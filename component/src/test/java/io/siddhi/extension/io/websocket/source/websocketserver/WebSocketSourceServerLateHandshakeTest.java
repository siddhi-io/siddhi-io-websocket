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

package io.siddhi.extension.io.websocket.source.websocketserver;

import io.siddhi.extension.io.websocket.util.WebSocketStubs;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WebSocketSourceServerLateHandshakeTest {

    @Test
    public void testHandshakeCompletedBeforeCloseIsTerminatedOnClose() {
        WebSocketServerSourceConnectorListener listener =
                new WebSocketServerSourceConnectorListener(new String[0], 0, null);
        WebSocketStubs.Handshaker handshaker = new WebSocketStubs.Handshaker();
        listener.onHandshake(handshaker.proxy);
        WebSocketStubs.Connection connection = new WebSocketStubs.Connection();
        handshaker.listener.get().onSuccess(connection.proxy);
        Assert.assertFalse(connection.terminated.get());
        listener.closeConnections();
        Assert.assertTrue(connection.terminated.get());
    }

    @Test
    public void testHandshakeCompletedAfterCloseIsTerminated() {
        WebSocketServerSourceConnectorListener listener =
                new WebSocketServerSourceConnectorListener(new String[0], 0, null);
        WebSocketStubs.Handshaker handshaker = new WebSocketStubs.Handshaker();
        listener.onHandshake(handshaker.proxy);
        listener.closeConnections();
        WebSocketStubs.Connection lateConnection = new WebSocketStubs.Connection();
        handshaker.listener.get().onSuccess(lateConnection.proxy);
        Assert.assertTrue(lateConnection.terminated.get());
    }
}
