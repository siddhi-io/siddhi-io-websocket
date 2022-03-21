/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package io.siddhi.extension.io.websocket.sink;

import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.SiddhiManager;
import io.siddhi.core.exception.SiddhiAppCreationException;
import io.siddhi.core.stream.input.InputHandler;
import io.siddhi.extension.io.websocket.sink.util.ResultContainer;
import io.siddhi.extension.io.websocket.sink.util.WebSocketReceiver;
import io.siddhi.extension.io.websocket.util.LoggerAppender;
import io.siddhi.extension.io.websocket.util.WebSocketServer;
import io.siddhi.query.api.exception.SiddhiAppValidationException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class WebSocketSinkTest {
    private AtomicInteger eventCount = new AtomicInteger(0);
    private boolean isLogEventArrived = false;

    @BeforeMethod
    public void init() {
        eventCount.set(0);
        WebSocketServer.start();
    }

    @AfterMethod
    public void serverClose() throws Exception {
        WebSocketServer.stop();
    }

    @Test
    public void testWebSocketSinkXmlMapTestCase() throws InterruptedException {
        SiddhiManager siddhiManager = new SiddhiManager();
        ResultContainer resultContainer = new ResultContainer(2);
        new WebSocketReceiver("ws://localhost:7070/chat/wso2", resultContainer);
        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(
                "@App:name('TestExecutionPlan') " +
                        "define stream FooStream1 (symbol string, age int, country string); " +
                        "@info(name = 'query1') " +
                        "@sink(type='websocket', url = 'ws://localhost:7070/chat/wso2', " +
                        "@map(type='xml'))" +
                        "Define stream BarStream1 (symbol string, age int, country string);" +
                        "from FooStream1 select symbol, age, country insert into BarStream1;");
        InputHandler fooStream = executionPlanRuntime.getInputHandler("FooStream1");
        executionPlanRuntime.start();
        fooStream.send(new Object[]{"JAMES", 23, "USA"});
        fooStream.send(new Object[]{"MIKE", 23, "Germany"});
        Assert.assertTrue(resultContainer.assertMessageContent("JAMES"));
        Assert.assertTrue(resultContainer.assertMessageContent("MIKE"));
        executionPlanRuntime.shutdown();
    }

    @Test(expectedExceptions = SiddhiAppValidationException.class,
          dependsOnMethods = "testWebSocketSinkXmlMapTestCase")
    public void testWebSocketSinkWithoutUri() {
        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.createSiddhiAppRuntime(
                "@App:name('TestExecutionPlan') " +
                        "define stream FooStream1 (symbol string, price float, volume long); " +
                        "@info(name = 'query1') " +
                        "@sink(type='websocket', " +
                        "@map(type='xml'))" +
                        "Define stream BarStream1 (symbol string, price float, volume long);" +
                        "from FooStream1 select symbol, price, volume insert into BarStream1;");
    }

    // TODO Should be fixed. Git issue: https://github.com/siddhi-io/siddhi-io-websocket/issues/28
//    @Test(dependsOnMethods = "testWebSocketSinkWithoutUri")
    public void testWebSocketSinkInvalidUri() throws InterruptedException {
        String regexPattern = "Error starting Siddhi App 'TestExecutionPlan'";
        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(
                "@App:name('TestExecutionPlan') " +
                        "define stream FooStream1 (symbol string, price float, volume long); " +
                        "@info(name = 'query1') " +
                        "@sink(type='websocket', url = 'ws://localhost:6060/websockets/abc'," +
                        "@map(type='xml'))" +
                        "Define stream BarStream1 (symbol string, price float, volume long);" +
                        "from FooStream1 select symbol, price, volume insert into BarStream1;");

        LoggerAppender appender = new LoggerAppender("LoggerAppender", null);
        final Logger logger = (Logger) LogManager.getRootLogger();
        logger.setLevel(Level.ALL);
        logger.addAppender(appender);
        appender.start();
        executionPlanRuntime.start();
        Thread.sleep(1000);
        Assert.assertTrue(
                ((LoggerAppender) logger.getAppenders().get("LoggerAppender")).getMessages().contains(regexPattern),
                "Matching log event not found for pattern: '" + regexPattern + "'");
        executionPlanRuntime.shutdown();
    }

    @Test(dependsOnMethods = "testWebSocketSinkWithoutUri")
    public void testWebSocketSinkInvalidHeaderFormat() throws InterruptedException {
        String regexPattern = "Error starting Siddhi App 'TestExecutionPlan'";
        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(
                "@App:name('TestExecutionPlan') " +
                        "define stream FooStream1 (symbol string, price float, volume long); " +
                        "@info(name = 'query1') " +
                        "@sink(type='websocket', url = 'ws://localhost:7070/chat/abc'," +
                        "headers=\"'message-type-websocket','message-sender:wso2'\", @map(type='xml'))" +
                        "Define stream BarStream1 (symbol string, price float, volume long);" +
                        "from FooStream1 select symbol, price, volume insert into BarStream1;");

        LoggerAppender appender = new LoggerAppender("LoggerAppender", null);
        final Logger logger = (Logger) LogManager.getRootLogger();
        logger.setLevel(Level.ALL);
        logger.addAppender(appender);
        appender.start();
        executionPlanRuntime.start();
        Thread.sleep(1000);
        Assert.assertTrue(
                ((LoggerAppender) logger.getAppenders().get("LoggerAppender")).getMessages().contains(regexPattern),
                "Matching log event not found for pattern: '" + regexPattern + "'");
        executionPlanRuntime.shutdown();
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class,
          dependsOnMethods = "testWebSocketSinkInvalidHeaderFormat")
    public void testWebSocketSinkInvalidUrlScheme() throws InterruptedException {
        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.createSiddhiAppRuntime(
                "@App:name('TestExecutionPlan') " +
                        "define stream FooStream1 (symbol string, price float, volume long); " +
                        "@info(name = 'query1') " +
                        "@sink(type='websocket', url = 'tcp://localhost:8025/abc'," +
                        "@map(type='xml'))" +
                        "Define stream BarStream1 (symbol string, price float, volume long);" +
                        "from FooStream1 select symbol, price, volume insert into BarStream1;");
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class,
          dependsOnMethods = "testWebSocketSinkInvalidUrlScheme")
    public void testWebSocketSinkInvalidIdleTimeout() throws InterruptedException {
        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.createSiddhiAppRuntime(
                "@App:name('TestExecutionPlan') " +
                        "define stream FooStream1 (symbol string, price float, volume long); " +
                        "@info(name = 'query1') " +
                        "@sink(type='websocket', url = 'ws://localhost:7070/chat/abc', idle.timeout = '-10'," +
                        "@map(type='xml'))" +
                        "Define stream BarStream1 (symbol string, price float, volume long);" +
                        "from FooStream1 select symbol, price, volume insert into BarStream1;");
    }
}
