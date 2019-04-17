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

package org.wso2.extension.siddhi.io.websocket.source;

import io.siddhi.core.exception.SiddhiAppRuntimeException;
import io.siddhi.core.stream.input.source.SourceEventListener;
import org.wso2.extension.siddhi.io.websocket.util.WebSocketClientConnectorListener;
import org.wso2.transport.http.netty.contract.websocket.ClientHandshakeListener;
import org.wso2.transport.http.netty.contract.websocket.WebSocketConnection;
import org.wso2.transport.http.netty.message.HttpCarbonResponse;

/**
 * Future listener for WebSocket handshake.
 */

public class WebSocketSourceHandshakeListener implements ClientHandshakeListener {
    private SourceEventListener sourceEventListener;
    private WebSocketClientConnectorListener connectorListener;

    public WebSocketSourceHandshakeListener (WebSocketClientConnectorListener connectorListener,
                                            SourceEventListener sourceEventListener) {
        this.connectorListener = connectorListener;
        this.sourceEventListener = sourceEventListener;
    }

    @Override
    public void onSuccess(WebSocketConnection webSocketConnection, HttpCarbonResponse response) {
        connectorListener.setSourceEventListener(sourceEventListener);
    }

    @Override
    public void onError(Throwable t, HttpCarbonResponse response) {
        throw new SiddhiAppRuntimeException("Error while connecting with the websocket server defined in '"
                + sourceEventListener + "'.", t);
    }
}
