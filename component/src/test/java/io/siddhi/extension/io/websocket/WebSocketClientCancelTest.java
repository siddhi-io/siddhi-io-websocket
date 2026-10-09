/*
 * Copyright (c) 2026, WSO2 LLC. (http://www.wso2.org).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.siddhi.extension.io.websocket;

import io.siddhi.extension.io.websocket.sink.WebSocketSinkHandshakeListener;
import io.siddhi.extension.io.websocket.source.WebSocketSourceHandshakeListener;
import io.siddhi.extension.io.websocket.util.WebSocketClientConnectorListener;
import io.siddhi.extension.io.websocket.util.WebSocketStubs;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.Semaphore;

public class WebSocketClientCancelTest {

    @Test
    public void testSinkHandshakeCompletedAfterCancelIsTerminated() {
        WebSocketSinkHandshakeListener listener = new WebSocketSinkHandshakeListener(new Semaphore(0));
        listener.cancel();
        WebSocketStubs.Connection connection = new WebSocketStubs.Connection();
        listener.onSuccess(connection.proxy, null);
        Assert.assertTrue(connection.terminated.get());
        Assert.assertNull(listener.getWebSocketConnectionAtomicReference().get());
    }

    @Test
    public void testSinkConnectionAlreadyDeliveredIsTerminatedOnCancel() {
        WebSocketSinkHandshakeListener listener = new WebSocketSinkHandshakeListener(new Semaphore(0));
        WebSocketStubs.Connection connection = new WebSocketStubs.Connection();
        listener.onSuccess(connection.proxy, null);
        listener.cancel();
        Assert.assertTrue(connection.terminated.get());
    }

    @Test
    public void testSourceHandshakeCompletedAfterCancelIsNotInstalled() {
        WebSocketClientConnectorListener connectorListener = new WebSocketClientConnectorListener();
        WebSocketSourceHandshakeListener listener =
                new WebSocketSourceHandshakeListener(connectorListener, null, null);
        listener.cancel();
        WebSocketStubs.Connection connection = new WebSocketStubs.Connection();
        listener.onSuccess(connection.proxy, null);
        Assert.assertTrue(connection.terminated.get());
        Assert.assertNull(connectorListener.getCurrentConnection());
        Assert.assertFalse(listener.isSucceeded());
    }
}
