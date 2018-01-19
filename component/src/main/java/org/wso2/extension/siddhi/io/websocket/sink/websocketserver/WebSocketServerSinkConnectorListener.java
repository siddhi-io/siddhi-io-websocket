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
import org.wso2.siddhi.core.exception.SiddhiAppRuntimeException;
import org.wso2.siddhi.query.api.definition.StreamDefinition;
import org.wso2.transport.http.netty.contract.websocket.HandshakeFuture;
import org.wso2.transport.http.netty.contract.websocket.WebSocketBinaryMessage;
import org.wso2.transport.http.netty.contract.websocket.WebSocketCloseMessage;
import org.wso2.transport.http.netty.contract.websocket.WebSocketConnectorListener;
import org.wso2.transport.http.netty.contract.websocket.WebSocketControlMessage;
import org.wso2.transport.http.netty.contract.websocket.WebSocketInitMessage;
import org.wso2.transport.http.netty.contract.websocket.WebSocketTextMessage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.websocket.CloseReason;
import javax.websocket.Session;

/**
 * {@code WebSocketServerSinkConnectorListener } Handle the WebSocket connector listener tasks..
 */

public class WebSocketServerSinkConnectorListener implements WebSocketConnectorListener {

    private static final Logger log = LoggerFactory.getLogger(
            WebSocketServerSinkConnectorListener.class);

    private List<Session> sessionList = new CopyOnWriteArrayList<>();
    private String[] subProtocol = null;
    private int idleTimeout;
    private StreamDefinition streamDefinition;

    WebSocketServerSinkConnectorListener(String[] subProtocol, int idleTimeout, StreamDefinition streamDefinition) {
        this.subProtocol = subProtocol;
        this.idleTimeout = idleTimeout;
        this.streamDefinition = streamDefinition;
    }

    @Override
    public void onMessage(WebSocketInitMessage initMessage) {
        WebSocketServerHandshakeListener serverHandshakeListener = new WebSocketServerHandshakeListener(sessionList);
        HandshakeFuture future = initMessage.handshake(subProtocol, true, idleTimeout);
        future.setHandshakeListener(serverHandshakeListener);
    }

    void send(Object message) {
        sessionList.forEach(
                currentSession -> {
                    if (currentSession.isOpen()) {
                        try {
                            if (message instanceof ByteBuffer) {
                                byte[] byteMessage = ((ByteBuffer) message).array();
                                ByteBuffer binaryMessage = ByteBuffer.wrap(byteMessage);
                                currentSession.getBasicRemote().sendBinary(binaryMessage);
                            } else {
                                currentSession.getBasicRemote().sendText((String) message);
                            }
                        } catch (IOException e) {
                            throw new SiddhiAppRuntimeException("Error while sending the events defined in " +
                                                                        streamDefinition + ".", e);
                        }
                    } else {
                        sessionList.remove(currentSession);
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
    public void onError(Throwable throwable) {
        //Not applicable
    }

    @Override
    public void onIdleTimeout(WebSocketControlMessage controlMessage) {
        try {
            Session session = controlMessage.getChannelSession();
            session.close(new CloseReason(() -> 1001, "Connection timeout"));
        } catch (IOException e) {
            log.error("Error occurred while closing the connection: ", e);
        }
    }
}
