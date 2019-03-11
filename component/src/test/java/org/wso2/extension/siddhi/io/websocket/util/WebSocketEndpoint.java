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
 */

package org.wso2.extension.siddhi.io.websocket.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.transport.http.netty.contract.websocket.WebSocketConnection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/chat/{name}")
public class WebSocketEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEndpoint.class);
    private List<WebSocketConnection> webSocketConnectionList = new LinkedList<>();

    @OnOpen
    public void onOpen(@PathParam("name") String name, WebSocketConnection websocketConnection) {
        webSocketConnectionList.add(websocketConnection);
    }

    @OnMessage
    public void onTextMessage(@PathParam("name") String name, String text, WebSocketConnection websocketConnection)
            throws IOException {
        webSocketConnectionList.forEach(
                webSocketConnection -> webSocketConnection.pushText(text)
        );
    }

    @OnMessage
    public void onBinaryMessage(@PathParam("name") String name, ByteBuffer message,
                                WebSocketConnection websocketConnection) {
        webSocketConnectionList.forEach(
                webSocketConnection -> webSocketConnection.pushBinary(message)
        );
    }

    @OnClose
    public void onClose(@PathParam("name") String name, CloseReason closeReason,
                        WebSocketConnection websocketConnection) {
        webSocketConnectionList.remove(websocketConnection);
    }

    @OnError
    public void onError(Throwable throwable, WebSocketConnection websocketConnection) {
        LOGGER.error("Error found in method : " + throwable.toString());
        webSocketConnectionList.remove(websocketConnection);
    }

}
