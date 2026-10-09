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

package io.siddhi.extension.io.websocket;

import io.siddhi.core.SiddhiAppRuntime;
import io.siddhi.core.SiddhiManager;
import io.siddhi.core.event.Event;
import io.siddhi.core.stream.input.InputHandler;
import io.siddhi.core.stream.output.StreamCallback;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class WebSocketReconnectTest {
    private static final long TIMEOUT_MILLIS = 60000;

    private SiddhiAppRuntime serverSourceApp(SiddhiManager manager, int port, AtomicInteger counter) {
        SiddhiAppRuntime runtime = manager.createSiddhiAppRuntime(
                "@App:name('ReconnectServer" + port + "') " +
                        "@source(type='websocket-server', host='localhost', port='" + port + "', " +
                        "@map(type='xml')) " +
                        "define stream InStream (symbol string);");
        runtime.addCallback("InStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                counter.addAndGet(events.length);
            }
        });
        return runtime;
    }

    private boolean sendUntilReceived(InputHandler handler, AtomicInteger counter) throws InterruptedException {
        long deadline = System.currentTimeMillis() + TIMEOUT_MILLIS;
        while (counter.get() == 0 && System.currentTimeMillis() < deadline) {
            handler.send(new Object[]{"WSO2"});
            Thread.sleep(500);
        }
        return counter.get() > 0;
    }

    @Test
    public void testSinkConnectsWhenServerStartsAfterClient() throws InterruptedException {
        SiddhiManager manager = new SiddhiManager();
        AtomicInteger received = new AtomicInteger(0);
        SiddhiAppRuntime client = manager.createSiddhiAppRuntime(
                "@App:name('ReconnectClient7031') " +
                        "define stream FooStream (symbol string); " +
                        "@sink(type='websocket', url='ws://localhost:7031/wso2', @map(type='xml')) " +
                        "define stream BarStream (symbol string); " +
                        "from FooStream select symbol insert into BarStream;");
        InputHandler fooStream = client.getInputHandler("FooStream");
        client.start();
        SiddhiAppRuntime server = serverSourceApp(manager, 7031, received);
        server.start();
        Assert.assertTrue(sendUntilReceived(fooStream, received),
                "Sink did not connect after the server became available");
        client.shutdown();
        server.shutdown();
    }

    @Test
    public void testSinkReconnectsAfterServerRedeploy() throws InterruptedException {
        SiddhiManager manager = new SiddhiManager();
        AtomicInteger firstReceived = new AtomicInteger(0);
        SiddhiAppRuntime server = serverSourceApp(manager, 7032, firstReceived);
        server.start();
        SiddhiAppRuntime client = manager.createSiddhiAppRuntime(
                "@App:name('ReconnectClient7032') " +
                        "define stream FooStream (symbol string); " +
                        "@sink(type='websocket', url='ws://localhost:7032/wso2', @map(type='xml')) " +
                        "define stream BarStream (symbol string); " +
                        "from FooStream select symbol insert into BarStream;");
        InputHandler fooStream = client.getInputHandler("FooStream");
        client.start();
        Assert.assertTrue(sendUntilReceived(fooStream, firstReceived));
        server.shutdown();

        AtomicInteger secondReceived = new AtomicInteger(0);
        SiddhiManager secondManager = new SiddhiManager();
        SiddhiAppRuntime redeployed = serverSourceApp(secondManager, 7032, secondReceived);
        redeployed.start();
        Assert.assertTrue(sendUntilReceived(fooStream, secondReceived),
                "Sink did not reconnect to the redeployed server");
        client.shutdown();
        redeployed.shutdown();
    }

    @Test
    public void testSourceReconnectsAfterServerRedeploy() throws InterruptedException {
        SiddhiManager manager = new SiddhiManager();
        AtomicInteger received = new AtomicInteger(0);
        SiddhiAppRuntime client = manager.createSiddhiAppRuntime(
                "@App:name('ReconnectSourceClient7033') " +
                        "@source(type='websocket', url='ws://localhost:7033/wso2', @map(type='xml')) " +
                        "define stream InStream (symbol string);");
        client.addCallback("InStream", new StreamCallback() {
            @Override
            public void receive(Event[] events) {
                received.addAndGet(events.length);
            }
        });
        client.start();

        for (int round = 0; round < 2; round++) {
            received.set(0);
            SiddhiAppRuntime server = manager.createSiddhiAppRuntime(
                    "@App:name('ReconnectPush7033_" + round + "') " +
                            "define stream FooStream (symbol string); " +
                            "@sink(type='websocket-server', host='localhost', port='7033', @map(type='xml')) " +
                            "define stream BarStream (symbol string); " +
                            "from FooStream select symbol insert into BarStream;");
            InputHandler fooStream = server.getInputHandler("FooStream");
            server.start();
            Assert.assertTrue(sendUntilReceived(fooStream, received),
                    "Source did not receive events in round " + round);
            server.shutdown();
        }
        client.shutdown();
    }
}
