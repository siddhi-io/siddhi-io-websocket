/*
 *  Copyright (c) 2017 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.extension.siddhi.io.websocket.sink.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.transport.http.netty.contract.websocket.HandshakeListener;
import org.wso2.transport.http.netty.contract.websocket.WebSocketConnection;

public class WebSocketHandshakeListener implements HandshakeListener {
    private static Logger log = LoggerFactory.getLogger(WebSocketHandshakeListener.class);

    private WebSocketClientConnectorListener connectorListener;
    private ResultContainer resultContainer;

    public WebSocketHandshakeListener(WebSocketClientConnectorListener connectorListener,
                                      ResultContainer resultContainer) {
        this.connectorListener = connectorListener;
        this.resultContainer = resultContainer;
    }

    @Override
    public void onSuccess(WebSocketConnection webSocketConnection) {
        connectorListener.setResultContainer(resultContainer);
    }

    @Override
    public void onError(Throwable throwable) {
        log.error("Error while connecting with the WebSocket server.");
    }
}
