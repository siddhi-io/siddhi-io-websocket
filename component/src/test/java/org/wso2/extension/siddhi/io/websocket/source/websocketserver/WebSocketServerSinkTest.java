/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.extension.siddhi.io.websocket.source.websocketserver;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.stream.output.StreamCallback;
import org.wso2.siddhi.core.util.SiddhiTestHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class WebSocketServerSinkTest {
    private AtomicInteger eventCount = new AtomicInteger(0);
    private AtomicInteger eventCount1 = new AtomicInteger(0);
    private List<String> receivedEventNameList;
    private int waitTime = 50;
    private int timeout = 30000;

    @BeforeMethod
    public void init() {
        eventCount.set(0);
        eventCount1.set(0);
    }

    @Test
    public void testWebSocketServerSink() throws InterruptedException {
        receivedEventNameList = new ArrayList<>(3);
        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager
                .createSiddhiAppRuntime(
                        "@App:name('TestExecutionPlan') " +
                                "define stream FooStream1 (symbol string); " +
                                "@info(name = 'query1') " +
                                "@sink(type='websocket-server', host='localhost', port='9025', " +
                                "@map(type='xml'))" +
                                "Define stream BarStream1 (symbol string);" +
                                "from FooStream1 select symbol insert into BarStream1;");
        InputHandler fooStream = siddhiAppRuntime.getInputHandler("FooStream1");

        siddhiAppRuntime.start();
        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(
                "@App:name('TestExecutionPlan') " +
                        "define stream FooStream1 (symbol string); " +
                        "@info(name = 'query1') " +
                        "@source(type='websocket', url = 'ws://localhost:9025/wso2', " +
                        "@map(type='xml'))" +
                        "Define stream BarStream1 (symbol string);" +
                        "from FooStream1 select symbol insert into BarStream1;");

        executionPlanRuntime.addCallback("BarStream1", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                for (Event event : events) {
                    eventCount.incrementAndGet();
                    receivedEventNameList.add(event.getData(0).toString());
                }
            }
        });

        executionPlanRuntime.start();
        ArrayList<Event> arrayList = new ArrayList<>();
        arrayList.add(new Event(System.currentTimeMillis(), new Object[]{"WSO2"}));
        arrayList.add(new Event(System.currentTimeMillis(), new Object[]{"IBM"}));
        arrayList.add(new Event(System.currentTimeMillis(), new Object[]{"WSO2"}));
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

    @Test(dependsOnMethods = "testWebSocketServerSink")
    public void testWebSocketServerSecureSink() throws InterruptedException {
        receivedEventNameList = new ArrayList<>(3);
        File keyStoreFilePath = new File("src/test");
        String keyStorePath = keyStoreFilePath.getAbsolutePath();
        System.setProperty("carbon.home", keyStorePath);
        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager
                .createSiddhiAppRuntime(
                        "@App:name('TestExecutionPlan') " +
                                "define stream FooStream1 (symbol string); " +
                                "@info(name = 'query1') " +
                                "@sink(type='websocket-server', host='localhost', port='9026', " +
                                "tls.enabled = 'true', keystore.path ='${carbon.home}/resources/conf/transports" +
                                "/wso2carbon.jks' , keystore.password='wso2carbon'," +
                                "@map(type='xml'))" +
                                "Define stream BarStream1 (symbol string);" +
                                "from FooStream1 select symbol insert into BarStream1;");
        InputHandler fooStream = siddhiAppRuntime.getInputHandler("FooStream1");

        siddhiAppRuntime.start();
        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(
                "@App:name('TestExecutionPlan') " +
                        "define stream FooStream1 (symbol string); " +
                        "@info(name = 'query1') " +
                        "@source(type='websocket', url = 'wss://localhost:9026/wso2', " +
                        "@map(type='xml'))" +
                        "Define stream BarStream1 (symbol string);" +
                        "from FooStream1 select symbol insert into BarStream1;");

        executionPlanRuntime.addCallback("BarStream1", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                for (Event event : events) {
                    eventCount.incrementAndGet();
                    receivedEventNameList.add(event.getData(0).toString());
                }
            }
        });

        executionPlanRuntime.start();
        ArrayList<Event> arrayList = new ArrayList<>();
        arrayList.add(new Event(System.currentTimeMillis(), new Object[]{"WSO2"}));
        arrayList.add(new Event(System.currentTimeMillis(), new Object[]{"IBM"}));
        arrayList.add(new Event(System.currentTimeMillis(), new Object[]{"WSO2"}));
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

    @Test(dependsOnMethods = "testWebSocketServerSecureSink")
    public void testWebSocketServerSinkWithOptional() throws InterruptedException {
        receivedEventNameList = new ArrayList<>(3);
        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager
                .createSiddhiAppRuntime(
                        "@App:name('TestExecutionPlan') " +
                                "define stream FooStream1 (symbol string); " +
                                "@info(name = 'query1') " +
                                "@sink(type='websocket-server', host='localhost', port='9025', " +
                                "sub.protocol='chat', idle.timeout = '10'," +
                                "@map(type='xml'))" +
                                "Define stream BarStream1 (symbol string);" +
                                "from FooStream1 select symbol insert into BarStream1;");
        InputHandler fooStream = siddhiAppRuntime.getInputHandler("FooStream1");

        siddhiAppRuntime.start();
        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(
                "@App:name('TestExecutionPlan') " +
                        "define stream FooStream1 (symbol string); " +
                        "@info(name = 'query1') " +
                        "@source(type='websocket', url = 'ws://localhost:9025/wso2', sub.protocol='chat', " +
                        "@map(type='xml'))" +
                        "Define stream BarStream1 (symbol string);" +
                        "from FooStream1 select symbol insert into BarStream1;");

        executionPlanRuntime.addCallback("BarStream1", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                for (Event event : events) {
                    eventCount.incrementAndGet();
                    receivedEventNameList.add(event.getData(0).toString());
                }
            }
        });

        executionPlanRuntime.start();
        ArrayList<Event> arrayList = new ArrayList<>();
        arrayList.add(new Event(System.currentTimeMillis(), new Object[]{"WSO2"}));
        arrayList.add(new Event(System.currentTimeMillis(), new Object[]{"IBM"}));
        arrayList.add(new Event(System.currentTimeMillis(), new Object[]{"WSO2"}));
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

    @Test(dependsOnMethods = "testWebSocketServerSinkWithOptional")
    public void testWebSocketServerSinkBinaryMap() throws InterruptedException {
        receivedEventNameList = new ArrayList<>(3);
        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager
                .createSiddhiAppRuntime(
                        "@App:name('TestExecutionPlan') " +
                                "define stream FooStream1 (symbol string); " +
                                "@info(name = 'query1') " +
                                "@sink(type='websocket-server', host='localhost', port='9010', " +
                                "@map(type='binary'))" +
                                "Define stream BarStream1 (symbol string);" +
                                "from FooStream1 select symbol insert into BarStream1;");
        InputHandler fooStream = siddhiAppRuntime.getInputHandler("FooStream1");

        siddhiAppRuntime.start();
        SiddhiAppRuntime executionPlanRuntime = siddhiManager.createSiddhiAppRuntime(
                "@App:name('TestExecutionPlan') " +
                        "define stream FooStream1 (symbol string); " +
                        "@info(name = 'query1') " +
                        "@source(type='websocket', url = 'ws://localhost:9010/wso2', " +
                        "@map(type='binary'))" +
                        "Define stream BarStream1 (symbol string);" +
                        "from FooStream1 select symbol insert into BarStream1;");

        executionPlanRuntime.addCallback("BarStream1", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                for (Event event : events) {
                    eventCount.incrementAndGet();
                    receivedEventNameList.add(event.getData(0).toString());
                }
            }
        });

        executionPlanRuntime.start();
        ArrayList<Event> arrayList = new ArrayList<>();
        arrayList.add(new Event(System.currentTimeMillis(), new Object[]{"WSO2"}));
        arrayList.add(new Event(System.currentTimeMillis(), new Object[]{"IBM"}));
        arrayList.add(new Event(System.currentTimeMillis(), new Object[]{"WSO2"}));
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


    @Test(dependsOnMethods = "testWebSocketServerSinkBinaryMap")
    public void testWebSocketServerMultipleConsumer() throws InterruptedException {
        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager
                .createSiddhiAppRuntime(
                        "@App:name('TestExecutionPlan') " +
                                "define stream BarStream1 (symbol string, price float, volume long); " +
                                "@info(name = 'query1') " +
                                "@sink(type='websocket-server', host='localhost', port='7070'," +
                                "@map(type='xml'))" +
                                "Define stream FooStream1 (symbol string, price float, volume long); " +
                                "from FooStream1 select symbol, price, volume insert into BarStream1; ");
        InputHandler fooStream = siddhiAppRuntime.getInputHandler("FooStream1");

        siddhiAppRuntime.start();
        SiddhiAppRuntime executionPlanRuntime = siddhiManager
                .createSiddhiAppRuntime(
                        "@App:name('TestExecutionPlan') " +
                                "define stream BarStream (symbol string, price float, volume long); " +
                                "define stream BarStream2 (symbol string, price float, volume long); " +

                                "@info(name = 'query1') " +
                                "@source(type='websocket', url = 'ws://localhost:7070/wso2', " +
                                "@map(type='xml'))" +
                                "Define stream FooStream (symbol string, price float, volume long); " +

                                "@info(name = 'query2') " +
                                "@source(type='websocket', url = 'ws://localhost:7070/wso2', " +
                                "@map(type='xml'))" +
                                "Define stream FooStream2 (symbol string, price float, volume long); " +

                                "from FooStream select symbol, price, volume insert into BarStream; " +
                                "from FooStream2 select symbol, price, volume insert into BarStream2; ");
        executionPlanRuntime.addCallback("BarStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                for (Event ignored : events) {
                    eventCount.incrementAndGet();
                }
            }
        });

        executionPlanRuntime.addCallback("BarStream2", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                for (Event ignored : events) {
                    eventCount1.incrementAndGet();
                }
            }
        });
        executionPlanRuntime.start();
        fooStream.send(new Object[]{"WSO2", 55.6f, 100L});
        fooStream.send(new Object[]{"IBM", 75.6f, 100L});
        SiddhiTestHelper.waitForEvents(waitTime, 2, eventCount1, timeout);
        Assert.assertEquals(eventCount.get(), 2);
        SiddhiTestHelper.waitForEvents(waitTime, 2, eventCount1, timeout);
        Assert.assertEquals(eventCount1.get(), 2);
        executionPlanRuntime.shutdown();
        siddhiAppRuntime.shutdown();
    }
}
