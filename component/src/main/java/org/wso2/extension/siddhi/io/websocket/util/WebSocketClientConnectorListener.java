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

package org.wso2.extension.siddhi.io.websocket.util;

import io.siddhi.core.stream.input.source.SourceEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.transport.http.netty.contract.websocket.WebSocketBinaryMessage;
import org.wso2.transport.http.netty.contract.websocket.WebSocketCloseMessage;
import org.wso2.transport.http.netty.contract.websocket.WebSocketConnection;
import org.wso2.transport.http.netty.contract.websocket.WebSocketConnectorListener;
import org.wso2.transport.http.netty.contract.websocket.WebSocketControlMessage;
import org.wso2.transport.http.netty.contract.websocket.WebSocketHandshaker;
import org.wso2.transport.http.netty.contract.websocket.WebSocketTextMessage;

/**
 * {@code WebSocketClientConnectorListener } Handle the websocket connector listener tasks.
 */
public class WebSocketClientConnectorListener implements WebSocketConnectorListener {
    private static final Logger log = LoggerFactory.getLogger(WebSocketConnectorListener.class);

    private SourceEventListener sourceEventListener = null;

    public void setSourceEventListener(SourceEventListener eventListener) {
        sourceEventListener = eventListener;
    }

    @Override
    public void onHandshake(WebSocketHandshaker webSocketHandshaker) {
    }

    @Override
    public void onMessage(WebSocketTextMessage textMessage) {
        String receivedTextMessage = textMessage.getText();
        if (sourceEventListener != null) {
            sourceEventListener.onEvent(receivedTextMessage, null);
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
        //Not Applicable
    }

    @Override
    public void onMessage(WebSocketCloseMessage closeMessage) {
        //Not Applicable
    }

    @Override
    public void onClose(WebSocketConnection webSocketConnection) {
    }

    @Override
    public void onError(WebSocketConnection webSocketConnection, Throwable throwable) {
        log.error("There is an error in the message format.", throwable);
    }

    @Override
    public void onIdleTimeout(WebSocketControlMessage webSocketControlMessage) {
        //Not Applicable
    }
}
