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

package org.wso2.extension.siddhi.io.websocket.source;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.extension.siddhi.io.websocket.util.WebSocketServer;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.exception.SiddhiAppCreationException;
import org.wso2.siddhi.core.exception.SiddhiAppRuntimeException;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.stream.output.StreamCallback;
import org.wso2.siddhi.core.util.SiddhiTestHelper;
import org.wso2.siddhi.query.api.exception.SiddhiAppValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WebSocketSourceTest {
    private AtomicInteger eventCount = new AtomicInteger(0);
    private AtomicInteger eventCount1 = new AtomicInteger(0);
    private List<String> receivedEventNameList;
    private int waitTime = 50;
    private int timeout = 30000;

    @BeforeMethod
    public void init() {
        eventCount.set(0);
        eventCount1.set(0);
        WebSocketServer.start();
    }

    @AfterMethod
    public void serverClose() throws Exception {
        WebSocketServer.stop();
    }

    @Test
    public void testWebSocketSinkAndSourceBinaryMap() throws InterruptedException {
        receivedEventNameList = new ArrayList<>(3);
        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager
                .createSiddhiAppRuntime(
                        "@App:name('TestExecutionPlan') " +
                                "define stream FooStream1 (symbol string, price float, volume long); " +
                                "@info(name = 'query1') " +
                                "@source(type='websocket', url = 'ws://localhost:7070/chat/abc', " +
                                "@map(type='binary'))" +
                                "Define stream BarStream1 (symbol string, price float, volume long);" +
                                "from FooStream1 select symbol, price, volume insert into BarStream1;");
        siddhiAppRuntime.addCallback("BarStream1", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                for (Event event : events) {
                    eventCount.incrementAndGet();
                    receivedEventNameList.add(event.getData(0).toString());
                }
            }
        });
        siddhiAppRuntime.start();
        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(
                "@App:name('TestExecutionPlan') " +
                        "define stream FooStream1 (symbol string, price float, volume long); " +
                        "@info(name = 'query1') " +
                        "@sink(type='websocket', url = 'ws://localhost:7070/chat/abc', @map(type='binary'))" +
                        "Define stream BarStream1 (symbol string, price float, volume long);" +
                        "from FooStream1 select symbol, price, volume insert into BarStream1;");
        InputHandler fooStream = executionPlanRuntime.getInputHandler("FooStream1");
        executionPlanRuntime.start();
        ArrayList<Event> arrayList = new ArrayList<Event>();
        arrayList.add(new Event(System.currentTimeMillis(), new Object[]{"WSO2", 55.6f, 100L}));
        arrayList.add(new Event(System.currentTimeMillis(), new Object[]{"IBM", 75.6f, 100L}));
        arrayList.add(new Event(System.currentTimeMillis(), new Object[]{"WSO2", 57.6f, 100L}));
        fooStream.send(arrayList.toArray(new Event[3]));
        List<String> expected = new ArrayList<>(2);
        expected.add("WSO2");
        expected.add("IBM");
        expected.add("WSO2");
        SiddhiTestHelper.waitForEvents(waitTime, 3, eventCount, timeout);
        Assert.assertEquals(receivedEventNameList, expected);
        Assert.assertEquals(eventCount.get(), 3);
        executionPlanRuntime.shutdown();
        siddhiAppRuntime.shutdown();
    }

    @Test(dependsOnMethods = "testWebSocketSinkAndSourceBinaryMap")
    public void testWebSocketSinkAndSourceXmlMap() throws InterruptedException {
        receivedEventNameList = new ArrayList<>(3);
        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager
                .createSiddhiAppRuntime(
                        "@App:name('TestExecutionPlan') " +
                                "define stream FooStream1 (symbol string, price float, volume long); " +
                                "@info(name = 'query1') " +
                                "@source(type='websocket', url = 'wss://localhost:7443/chat/abc', idle.timeout = "
                                + "'1000', " +
                                "@map(type='xml'))" +
                                "Define stream BarStream1 (symbol string, price float, volume long);" +
                                "from FooStream1 select symbol, price, volume insert into BarStream1;");
        siddhiAppRuntime.addCallback("BarStream1", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                for (Event event : events) {
                    eventCount.incrementAndGet();
                    receivedEventNameList.add(event.getData(0).toString());
                }
            }
        });
        siddhiAppRuntime.start();
        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(
                "@App:name('TestExecutionPlan') " +
                        "define stream FooStream1 (symbol string, price float, volume long); " +
                        "@info(name = 'query1') " +
                        "@sink(type='websocket', url = 'wss://localhost:7443/chat/abc', idle.timeout = '1000', " +
                        "@map(type='xml'))" +
                        "Define stream BarStream1 (symbol string, price float, volume long);" +
                        "from FooStream1 select symbol, price, volume insert into BarStream1;");
        InputHandler fooStream = executionPlanRuntime.getInputHandler("FooStream1");
        executionPlanRuntime.start();
        ArrayList<Event> arrayList = new ArrayList<Event>();
        arrayList.add(new Event(System.currentTimeMillis(), new Object[]{"WSO2", 55.6f, 100L}));
        arrayList.add(new Event(System.currentTimeMillis(), new Object[]{"IBM", 75.6f, 100L}));
        arrayList.add(new Event(System.currentTimeMillis(), new Object[]{"WSO2", 57.6f, 100L}));
        fooStream.send(arrayList.toArray(new Event[3]));
        List<String> expected = new ArrayList<>(2);
        expected.add("WSO2");
        expected.add("IBM");
        expected.add("WSO2");
        SiddhiTestHelper.waitForEvents(waitTime, 3, eventCount, timeout);
        Assert.assertEquals(receivedEventNameList, expected);
        Assert.assertEquals(eventCount.get(), 3);
        executionPlanRuntime.shutdown();
        siddhiAppRuntime.shutdown();
    }

    @Test(expectedExceptions = SiddhiAppValidationException.class,
          dependsOnMethods = "testWebSocketSinkAndSourceXmlMap")
    public void testWebSocketSourceWithoutUri() {
        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.createSiddhiAppRuntime(
                "@App:name('TestExecutionPlan') " +
                        "define stream FooStream1 (symbol string, price float, volume long); " +
                        "@info(name = 'query1') " +
                        "@source(type='websocket', " +
                        "@map(type='xml'))" +
                        "Define stream BarStream1 (symbol string, price float, volume long);" +
                        "from FooStream1 select symbol, price, volume insert into BarStream1;");
    }

    @Test(expectedExceptions = SiddhiAppRuntimeException.class, dependsOnMethods = "testWebSocketSourceWithoutUri")
    public void testWebSocketSourceInvalidUri() throws InterruptedException {
        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(
                "@App:name('TestExecutionPlan') " +
                        "define stream FooStream1 (symbol string, price float, volume long); " +
                        "@info(name = 'query1') " +
                        "@source(type='websocket', url = 'ws://localhost:6060/websockets/abc'," +
                        "@map(type='xml'))" +
                        "Define stream BarStream1 (symbol string, price float, volume long);" +
                        "from FooStream1 select symbol, price, volume insert into BarStream1;");
        InputHandler fooStream = executionPlanRuntime.getInputHandler("FooStream1");
        executionPlanRuntime.start();
        fooStream.send(new Object[]{"WSO2", 55.6f, 100L});
        executionPlanRuntime.shutdown();
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class, dependsOnMethods = "testWebSocketSourceInvalidUri")
    public void testWebSocketSourceInvalidIdleTimeout() throws InterruptedException {
        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.createSiddhiAppRuntime(
                "@App:name('TestExecutionPlan') " +
                        "define stream FooStream1 (symbol string, price float, volume long); " +
                        "@info(name = 'query1') " +
                        "@source(type='websocket', url = 'ws://localhost:7070/chat/abc', idle.timeout = '-10'," +
                        "@map(type='xml'))" +
                        "Define stream BarStream1 (symbol string, price float, volume long);" +
                        "from FooStream1 select symbol, price, volume insert into BarStream1;");
    }

    @Test(expectedExceptions = SiddhiAppCreationException.class,
          dependsOnMethods = "testWebSocketSourceInvalidIdleTimeout")
    public void testWebSocketSourceInvalidUrlScheme() throws InterruptedException {
        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.createSiddhiAppRuntime(
                "@App:name('TestExecutionPlan') " +
                        "define stream FooStream1 (symbol string, price float, volume long); " +
                        "@info(name = 'query1') " +
                        "@source(type='websocket', url = 'tcp://localhost:7070/abc'," +
                        "@map(type='xml'))" +
                        "Define stream BarStream1 (symbol string, price float, volume long);" +
                        "from FooStream1 select symbol, price, volume insert into BarStream1;");
    }

    @Test(dependsOnMethods = "testWebSocketSourceInvalidUrlScheme")
    public void testWebSocketMultipleConsumer() throws InterruptedException {
        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager
                .createSiddhiAppRuntime(
                        "@App:name('TestExecutionPlan') " +
                                "define stream BarStream (symbol string, price float, volume long); " +
                                "define stream BarStream2 (symbol string, price float, volume long); " +

                                "@info(name = 'query1') " +
                                "@source(type='websocket', url = 'ws://localhost:7070/chat/abc', " +
                                "@map(type='xml'))" +
                                "Define stream FooStream (symbol string, price float, volume long); " +

                                "@info(name = 'query2') " +
                                "@source(type='websocket', url = 'ws://localhost:7070/chat/abc', " +
                                "@map(type='xml'))" +
                                "Define stream FooStream2 (symbol string, price float, volume long); " +

                                "from FooStream select symbol, price, volume insert into BarStream; " +
                                "from FooStream2 select symbol, price, volume insert into BarStream2; ");
        siddhiAppRuntime.addCallback("BarStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                for (Event ignored : events) {
                    eventCount.incrementAndGet();
                }
            }
        });

        siddhiAppRuntime.addCallback("BarStream2", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                for (Event ignored : events) {
                    eventCount1.incrementAndGet();
                }
            }
        });

        siddhiAppRuntime.start();
        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(
                "@App:name('TestExecutionPlan') " +
                        "define stream FooStream1 (symbol string, price float, volume long); " +
                        "@info(name = 'query1') " +
                        "@sink(type ='websocket', url = 'ws://localhost:7070/chat/abc', " +
                        "@map(type='xml'))" +
                        "Define stream BarStream1 (symbol string, price float, volume long);" +
                        "from FooStream1 select symbol, price, volume insert into BarStream1;");
        InputHandler fooStream = executionPlanRuntime.getInputHandler("FooStream1");

        executionPlanRuntime.start();
        fooStream.send(new Object[]{"WSO2", 55.6f, 100L});
        fooStream.send(new Object[]{"IBM", 75.6f, 100L});
        SiddhiTestHelper.waitForEvents(waitTime, 2, eventCount, timeout);
        SiddhiTestHelper.waitForEvents(waitTime, 2, eventCount1, timeout);
        Assert.assertEquals(eventCount.get(), 2);
        Assert.assertEquals(eventCount.get(), 2);
        executionPlanRuntime.shutdown();
        siddhiAppRuntime.shutdown();
    }
}
