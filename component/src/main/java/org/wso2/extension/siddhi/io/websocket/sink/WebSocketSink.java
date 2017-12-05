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

import org.wso2.extension.siddhi.io.websocket.util.WebSocketClientConnectorListener;
import org.wso2.extension.siddhi.io.websocket.util.WebSocketConstants;
import org.wso2.extension.siddhi.io.websocket.util.WebSocketUtil;
import org.wso2.siddhi.annotation.Example;
import org.wso2.siddhi.annotation.Extension;
import org.wso2.siddhi.annotation.Parameter;
import org.wso2.siddhi.annotation.util.DataType;
import org.wso2.siddhi.core.config.SiddhiAppContext;
import org.wso2.siddhi.core.exception.ConnectionUnavailableException;
import org.wso2.siddhi.core.exception.SiddhiAppCreationException;
import org.wso2.siddhi.core.stream.output.sink.Sink;
import org.wso2.siddhi.core.util.config.ConfigReader;
import org.wso2.siddhi.core.util.transport.DynamicOptions;
import org.wso2.siddhi.core.util.transport.OptionHolder;
import org.wso2.siddhi.query.api.definition.StreamDefinition;
import org.wso2.transport.http.netty.contract.websocket.HandshakeFuture;
import org.wso2.transport.http.netty.contract.websocket.WebSocketClientConnector;
import org.wso2.transport.http.netty.contract.websocket.WsClientConnectorConfig;
import org.wso2.transport.http.netty.contractimpl.HttpWsConnectorFactoryImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;

/**
 * {@code WebsocketSink } Publishing the siddhi events to the WebSocket server.
 */

@Extension(
        name = "websocket",
        namespace = "sink",
        description = "A Siddhi application can be configured to publish events via the Websocket transport by " +
                "adding the @Sink(type = ‘Websocket’) annotation at the top of an event stream definition.",
        parameters = {
                @Parameter(
                        name = "url",
                        description = "The URL of the remote endpoint.\n" +
                                "The url scheme should be either ‘ws’ or ‘wss’.",
                        type = DataType.STRING
                ),
                @Parameter(
                        name = "sub.protocol",
                        description = "The negotiable sub-protocol if server is asking for it.\n" +
                                "The sub.protocol should adhere to `subprotocol1, subprotocol2,...` format.",
                        type = DataType.STRING,
                        optional = true,
                        defaultValue = "null"
                ),
                @Parameter(
                        name = "headers",
                        description = "Any specific headers which need to send to the server.\n" +
                                "The headers should adhere to `'key1:value1', 'key2:value2',...` format.",
                        type = DataType.STRING,
                        optional = true,
                        defaultValue = "null"
                ),
                @Parameter(
                        name = "idle.timeout",
                        description = "Idle timeout of the connection",
                        type = DataType.INT,
                        optional = true,
                        defaultValue = "-1"
                )
        },
        examples = {
                @Example(
                        syntax = "@Sink(type = ‘websocket’, url = 'ws://localhost:8025/abc', \n" +
                                "   @map(type='xml'))\n" +
                                "define stream Foo (attribute1 string, attribute2 int);",
                        description = "" +
                                "A sink of type 'websocket' has been defined.\n" +
                                "All events arriving at Foo stream via websocket will be sent " +
                                "to the url ws://localhost:8025/abc."
                )
        }
)

public class WebSocketSink extends Sink {
    private StreamDefinition streamDefinition;
    private String url;
    private String subProtocol;
    private String headers;
    private String idleTimeoutString;
    private int idleTimeout;
    private WebSocketClientConnectorListener connectorListener;
    private HandshakeFuture handshakeFuture = null;

    @Override
    public Class[] getSupportedInputEventClasses() {
        return new Class[]{String.class, ByteBuffer.class};
    }

    @Override
    public String[] getSupportedDynamicOptions() {
        return new String[0];
    }

    @Override
    protected void init(StreamDefinition streamDefinition, OptionHolder optionHolder,
                        ConfigReader configReader, SiddhiAppContext siddhiAppContext) {
        this.streamDefinition = streamDefinition;
        this.url = optionHolder.validateAndGetStaticValue(WebSocketConstants.URL);
        this.subProtocol = optionHolder.validateAndGetStaticValue
                (WebSocketConstants.SUB_PROTOCOL, null);
        this.headers = optionHolder.validateAndGetStaticValue
                (WebSocketConstants.HEADERS, null);
        this.idleTimeoutString = optionHolder.validateAndGetStaticValue
                (WebSocketConstants.IDLE_TIMEOUT, null);
        if (idleTimeoutString != null) {
            try {
                idleTimeout = Integer.parseInt(idleTimeoutString);
                if (idleTimeout < -1) {
                    throw new SiddhiAppCreationException("The idle timeout defined in '" + streamDefinition +
                                                                 "' should be greater than 0.");
                }
            } catch (NumberFormatException e) {
                throw new SiddhiAppCreationException("The idle timeout defined in '" + streamDefinition +
                                                             "' should be an Integer.");
            }
        }
        connectorListener = new WebSocketClientConnectorListener();
        try {
            String scheme = (new URI(url)).getScheme();
            if (!Objects.equals("ws", scheme) && !Objects.equals("wss", scheme)) {
                throw new SiddhiAppCreationException("Invalid scheme in " + WebSocketConstants.URL + " = " +
                                                             url + ". The scheme of the " + WebSocketConstants.URL +
                                                             " for the websocket server should be either `ws` or "
                                                             + "`wss`.");
            }
        } catch (URISyntaxException e) {
            throw new SiddhiAppCreationException("There is an syntax error in the '" + url + "' of the websocket "
                                                         + "server.", e);
        }
    }

    @Override
    public void connect() throws ConnectionUnavailableException {
        HttpWsConnectorFactoryImpl httpConnectorFactory = new HttpWsConnectorFactoryImpl();
        WsClientConnectorConfig configuration = new WsClientConnectorConfig(url);
        if (subProtocol != null) {
            String[] subProtocol1 = WebSocketUtil.getSubProtocol(subProtocol);
            configuration.setSubProtocols(subProtocol1);
        }
        if (headers != null) {
            Map<String, String> customHeaders = WebSocketUtil.getHeaders(headers);
            configuration.addHeaders(customHeaders);
        }
        if (idleTimeoutString != null) {
            configuration.setIdleTimeoutInMillis(idleTimeout);
        }
        WebSocketClientConnector clientConnector = httpConnectorFactory.createWsClientConnector(configuration);
        handshakeFuture = clientConnector.connect(connectorListener);
    }

    @Override
    public void publish(Object payload, DynamicOptions dynamicOptions) throws ConnectionUnavailableException {
        WebSocketSinkHandshakeListener handshakeListener = new WebSocketSinkHandshakeListener
                (payload, streamDefinition);
        handshakeFuture.setHandshakeListener(handshakeListener);
    }

    @Override
    public void disconnect() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public Map<String, Object> currentState() {
        return null;
    }

    @Override
    public void restoreState(Map<String, Object> map) {
    }
}
