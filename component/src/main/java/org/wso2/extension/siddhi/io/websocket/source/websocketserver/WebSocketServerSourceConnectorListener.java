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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.siddhi.core.stream.input.source.SourceEventListener;
import org.wso2.transport.http.netty.contract.websocket.HandshakeListener;
import org.wso2.transport.http.netty.contract.websocket.WebSocketBinaryMessage;
import org.wso2.transport.http.netty.contract.websocket.WebSocketCloseMessage;
import org.wso2.transport.http.netty.contract.websocket.WebSocketConnection;
import org.wso2.transport.http.netty.contract.websocket.WebSocketConnectorListener;
import org.wso2.transport.http.netty.contract.websocket.WebSocketControlMessage;
import org.wso2.transport.http.netty.contract.websocket.WebSocketInitMessage;
import org.wso2.transport.http.netty.contract.websocket.WebSocketTextMessage;

import java.io.IOException;
import javax.websocket.CloseReason;
import javax.websocket.Session;

/**
 * {@code WebSocketServerSourceConnectorListener } Handle the websocket connector listener tasks..
 */

public class WebSocketServerSourceConnectorListener implements WebSocketConnectorListener {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServerSourceConnectorListener.class);

    private String[] subProtocols = null;
    private int idleTimeout;
    private SourceEventListener sourceEventListener = null;
    private static WebSocketSourceHandShakeListener webSocketSourceHandShakeListener = new
            WebSocketSourceHandShakeListener();

    WebSocketServerSourceConnectorListener(String[] subProtocols, int idleTimeout,
                                           SourceEventListener sourceEventListener) {
        this.subProtocols = subProtocols;
        this.idleTimeout = idleTimeout;
        this.sourceEventListener = sourceEventListener;
    }

    @Override
    public void onMessage(WebSocketInitMessage initMessage) {
        initMessage.handshake(subProtocols, true, idleTimeout).setHandshakeListener(webSocketSourceHandShakeListener);
    }

    @Override
    public void onMessage(WebSocketTextMessage textMessage) {
        String receivedTextToClient = textMessage.getText();
        if (sourceEventListener != null) {
            sourceEventListener.onEvent(receivedTextToClient, null);
        }
    }

    @Override
    public void onMessage(WebSocketBinaryMessage binaryMessage) {
        byte[] receivedBinaryMessage = binaryMessage.getByteArray();
        if (sourceEventListener != null) {
            sourceEventListener.onEvent(receivedBinaryMessage, null);
        }
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
    public void onError(Throwable throwable) {
        //Not applicable
    }

    @Override
    public void onIdleTimeout(WebSocketControlMessage controlMessage) {
        try {
            Session session = controlMessage.getWebSocketConnection().getSession();
            session.close(new CloseReason(() -> 1001, "Connection timeout"));
        } catch (IOException e) {
            log.error("Error occurred while closing the connection: " + e.getMessage());
        }
    }

    private static class WebSocketSourceHandShakeListener implements HandshakeListener {

        @Override
        public void onSuccess(WebSocketConnection webSocketConnection) {
            webSocketConnection.startReadingFrames();
        }

        @Override
        public void onError(Throwable throwable) {
            log.error("Error occurred while receiving messages : " + throwable.getMessage());
        }
    }
}
