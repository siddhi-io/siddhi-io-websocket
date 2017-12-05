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

import java.nio.ByteBuffer;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/abc", subprotocols = {"chat", "superchat"})
public class WebSocketEndpoint {

    @OnMessage
    public void onTextMessage(Session sess, String message) {
        for (Session session : sess.getOpenSessions()) {
            session.getAsyncRemote().sendText(message);
        }
    }

    @OnMessage
    public void onBinaryMessage(Session sess, byte[] message) {
        for (Session session : sess.getOpenSessions()) {
            session.getAsyncRemote().sendBinary(ByteBuffer.wrap(message));
        }
    }
}
