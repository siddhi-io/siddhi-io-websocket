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

package io.siddhi.extension.io.websocket.sink;

import io.siddhi.core.exception.SiddhiAppRuntimeException;
import io.siddhi.query.api.definition.StreamDefinition;
import org.wso2.transport.http.netty.contract.websocket.ClientHandshakeListener;
import org.wso2.transport.http.netty.contract.websocket.WebSocketConnection;
import org.wso2.transport.http.netty.message.HttpCarbonResponse;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Future listener for WebSocket handshake.
 */

public class WebSocketSinkHandshakeListener implements ClientHandshakeListener {
    private StreamDefinition streamDefinition;
    private AtomicReference<WebSocketConnection> webSocketConnectionAtomicReference = new AtomicReference<>();
    private Semaphore semaphore;

    public WebSocketSinkHandshakeListener(StreamDefinition streamDefinition, Semaphore semaphore) {
        this.streamDefinition = streamDefinition;
        this.semaphore = semaphore;
    }

    @Override
    public void onSuccess(WebSocketConnection webSocketConnection, HttpCarbonResponse response) {
        webSocketConnectionAtomicReference.set(webSocketConnection);
        semaphore.release();
    }

    @Override
    public void onError(Throwable t, HttpCarbonResponse response) {
        semaphore.release();
        throw new SiddhiAppRuntimeException("Error while connecting with the websocket server defined in '"
                + streamDefinition + "'.", t);
    }

    public AtomicReference<WebSocketConnection> getWebSocketConnectionAtomicReference() {
        return webSocketConnectionAtomicReference;
    }
}
