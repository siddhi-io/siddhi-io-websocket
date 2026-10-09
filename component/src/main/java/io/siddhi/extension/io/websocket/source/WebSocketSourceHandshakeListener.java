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

package io.siddhi.extension.io.websocket.source;

import io.siddhi.core.stream.input.source.Source;
import io.siddhi.core.stream.input.source.SourceEventListener;
import io.siddhi.extension.io.websocket.util.WebSocketClientConnectorListener;
import org.wso2.transport.http.netty.contract.websocket.ClientHandshakeListener;
import org.wso2.transport.http.netty.contract.websocket.WebSocketConnection;
import org.wso2.transport.http.netty.message.HttpCarbonResponse;

import java.util.concurrent.CountDownLatch;

/**
 * Future listener for WebSocket handshake.
 */

public class WebSocketSourceHandshakeListener implements ClientHandshakeListener {
    private SourceEventListener sourceEventListener;
    private WebSocketClientConnectorListener connectorListener;
    private Source.ConnectionCallback connectionCallback;
    private final CountDownLatch handshakeCompleted = new CountDownLatch(1);
    private volatile Throwable error;
    private volatile boolean succeeded = false;

    public WebSocketSourceHandshakeListener (WebSocketClientConnectorListener connectorListener,
                                            SourceEventListener sourceEventListener,
                                            Source.ConnectionCallback connectionCallback) {
        this.connectionCallback = connectionCallback;
        this.connectorListener = connectorListener;
        this.sourceEventListener = sourceEventListener;
    }

    @Override
    public void onSuccess(WebSocketConnection webSocketConnection, HttpCarbonResponse response) {
        connectorListener.setSourceEventListener(sourceEventListener);
        connectorListener.setCurrentConnection(webSocketConnection);
        connectorListener.setConnectionCallback(connectionCallback);
        succeeded = true;
        handshakeCompleted.countDown();
    }

    @Override
    public void onError(Throwable t, HttpCarbonResponse response) {
        error = t;
        handshakeCompleted.countDown();
    }

    public void awaitHandshake() throws InterruptedException {
        handshakeCompleted.await();
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public Throwable getError() {
        return error;
    }
}
