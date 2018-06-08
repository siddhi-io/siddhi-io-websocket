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

package org.wso2.extension.siddhi.io.websocket.sink;

import org.wso2.siddhi.core.exception.SiddhiAppRuntimeException;
import org.wso2.siddhi.query.api.definition.StreamDefinition;
import org.wso2.transport.http.netty.contract.websocket.HandshakeListener;
import org.wso2.transport.http.netty.contract.websocket.WebSocketConnection;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import javax.websocket.Session;

/**
 * Future listener for WebSocket handshake.
 */

public class WebSocketSinkHandshakeListener implements HandshakeListener {
    private StreamDefinition streamDefinition;
    private AtomicReference<Session> sessionAtomicReference = new AtomicReference<>();
    private Semaphore semaphore;

    public WebSocketSinkHandshakeListener(StreamDefinition streamDefinition, Semaphore semaphore) {
        this.streamDefinition = streamDefinition;
        this.semaphore = semaphore;
    }

    @Override
    public void onSuccess(WebSocketConnection webSocketConnection) {
        sessionAtomicReference.set(webSocketConnection.getSession());
        semaphore.release();
    }

    @Override public void onError(Throwable throwable) {
        semaphore.release();
        throw new SiddhiAppRuntimeException("Error while connecting with the websocket server defined in '"
                                                    + streamDefinition + "'.", throwable);
    }

    public AtomicReference<Session> getSessionAtomicReference() {
        return sessionAtomicReference;
    }
}
