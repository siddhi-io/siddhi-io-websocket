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

package org.wso2.extension.siddhi.io.websocket.sink.websocketserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.siddhi.query.api.definition.StreamDefinition;
import org.wso2.transport.http.netty.contract.websocket.ServerHandshakeFuture;
import org.wso2.transport.http.netty.contract.websocket.WebSocketBinaryMessage;
import org.wso2.transport.http.netty.contract.websocket.WebSocketCloseMessage;
import org.wso2.transport.http.netty.contract.websocket.WebSocketConnection;
import org.wso2.transport.http.netty.contract.websocket.WebSocketConnectorListener;
import org.wso2.transport.http.netty.contract.websocket.WebSocketControlMessage;
import org.wso2.transport.http.netty.contract.websocket.WebSocketHandshaker;
import org.wso2.transport.http.netty.contract.websocket.WebSocketTextMessage;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * {@code WebSocketServerSinkConnectorListener } Handle the WebSocket connector listener tasks..
 */

public class WebSocketServerSinkConnectorListener implements WebSocketConnectorListener {

    private static final Logger log = LoggerFactory.getLogger(
            WebSocketServerSinkConnectorListener.class);

    private List<WebSocketConnection> webSocketConnectionList = new CopyOnWriteArrayList<>();
    private String[] subProtocol = null;
    private int idleTimeout;

    WebSocketServerSinkConnectorListener(String[] subProtocol, int idleTimeout, StreamDefinition streamDefinition) {
        this.subProtocol = subProtocol;
        this.idleTimeout = idleTimeout;
    }

    @Override
    public void onHandshake(WebSocketHandshaker webSocketHandshaker) {
        WebSocketServerHandshakeListener serverHandshakeListener =
                new WebSocketServerHandshakeListener(webSocketConnectionList);
        ServerHandshakeFuture handshake = webSocketHandshaker.handshake(subProtocol, idleTimeout);
        handshake.setHandshakeListener(serverHandshakeListener);
    }

    void send(Object message) {
        webSocketConnectionList.forEach(
                currentWebSocketConnection -> {
                    if (message instanceof ByteBuffer) {
                        byte[] byteMessage = ((ByteBuffer) message).array();
                        ByteBuffer binaryMessage = ByteBuffer.wrap(byteMessage);
                        currentWebSocketConnection.pushBinary(binaryMessage);
                    } else {
                        currentWebSocketConnection.pushText((String) message);
                    }
                });
    }

    @Override
    public void onMessage(WebSocketTextMessage textMessage) {
        //Not applicable
    }

    @Override
    public void onMessage(WebSocketBinaryMessage binaryMessage) {
        //Not applicable
    }

    @Override
    public void onMessage(WebSocketControlMessage controlMessage) {
        //Not applicable
    }

    @Override
    public void onMessage(WebSocketCloseMessage closeMessage) {
        //Not applicable
    }

    @Override
    public void onError(WebSocketConnection webSocketConnection, Throwable throwable) {
        webSocketConnectionList.remove(webSocketConnection);
    }

    @Override
    public void onClose(WebSocketConnection webSocketConnection) {
        webSocketConnectionList.remove(webSocketConnection);
    }

    @Override
    public void onIdleTimeout(WebSocketControlMessage controlMessage) {
        WebSocketConnection webSocketConnection = controlMessage.getWebSocketConnection();
        webSocketConnection.terminateConnection(1001, "Connection timeout");
    }
}
