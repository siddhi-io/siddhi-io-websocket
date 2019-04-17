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

import io.siddhi.annotation.Example;
import io.siddhi.annotation.Extension;
import io.siddhi.annotation.Parameter;
import io.siddhi.annotation.util.DataType;
import io.siddhi.core.config.SiddhiAppContext;
import io.siddhi.core.exception.ConnectionUnavailableException;
import io.siddhi.core.exception.SiddhiAppCreationException;
import io.siddhi.core.stream.ServiceDeploymentInfo;
import io.siddhi.core.stream.input.source.Source;
import io.siddhi.core.stream.input.source.SourceEventListener;
import io.siddhi.core.util.config.ConfigReader;
import io.siddhi.core.util.snapshot.state.State;
import io.siddhi.core.util.snapshot.state.StateFactory;
import io.siddhi.core.util.transport.OptionHolder;
import org.wso2.extension.siddhi.io.websocket.util.WebSocketClientConnectorListener;
import org.wso2.extension.siddhi.io.websocket.util.WebSocketProperties;
import org.wso2.extension.siddhi.io.websocket.util.WebSocketUtil;
import org.wso2.transport.http.netty.contract.HttpWsConnectorFactory;
import org.wso2.transport.http.netty.contract.websocket.ClientHandshakeFuture;
import org.wso2.transport.http.netty.contract.websocket.WebSocketClientConnector;
import org.wso2.transport.http.netty.contract.websocket.WebSocketClientConnectorConfig;
import org.wso2.transport.http.netty.contractimpl.DefaultHttpWsConnectorFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;

/**
 * {@code WebsocketSource } Receiving the siddhi events from the WebSocket server.
 */

@Extension(
        name = "websocket",
        namespace = "source",
        description = "A Siddhi application can be configured to receive events via the WebSocket by adding " +
                "the @Source(type = 'websocket') annotation at the top of an event stream definition.\n" +
                "When this is defined the associated stream will receive events from the WebSocket server on " +
                "the url defined in the system.",
        parameters = {
                @Parameter(
                        name = "url",
                        description = "The URL of the remote endpoint.\n" +
                                "The url scheme should be either 'ws' or 'wss'.",
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
                ),
                @Parameter(
                        name = "truststore.path",
                        description = "The file path to the location of the truststore. If a custom truststore is" +
                                " not specified, then the system uses the default truststore file - wso2carbon.jks " +
                                "in the `${carbon.home}/resources/security` directory.",
                        type = {DataType.STRING},
                        optional = true,
                        defaultValue = "${carbon.home}/resources/security/client-truststore.jks"
                ),
                @Parameter(
                        name = "truststore.password",
                        description = "The password for the truststore. A custom password can be specified " +
                                "if required. If no custom password is specified, then the system uses " +
                                "`wso2carbon` as the default password.",
                        type = {DataType.STRING},
                        optional = true,
                        defaultValue = "wso2carbon"
                )
        },
        examples = {
                @Example(
                        syntax = "@Source(type = 'websocket', url = 'ws://localhost:8025/websockets/abc', \n" +
                                "   @map(type='xml'))\n" +
                                "define stream Foo (attribute1 string, attribute2 int);",
                        description = "" +
                                "Under this configuration, events are received via the WebSocket server and they are "
                                + "passed to `Foo` stream for processing. "
                )
        }
)

public class WebSocketSource extends Source {
    private String url;
    private String subProtocol;
    private String headers;
    private String idleTimeoutString;
    private int idleTimeout;
    private SourceEventListener sourceEventListener;
    private WebSocketClientConnectorListener connectorListener;
    private boolean sslEnabled = false;
    private String tlsstruststorePath;
    private String tlsstruststorePass;

    @Override
    public StateFactory init(SourceEventListener sourceEventListener, OptionHolder optionHolder, String[] strings,
                             ConfigReader configReader, SiddhiAppContext siddhiAppContext) {
        this.url = optionHolder.validateAndGetStaticValue(WebSocketProperties.URL);
        this.subProtocol = optionHolder.validateAndGetStaticValue
                (WebSocketProperties.SUB_PROTOCOL, null);
        this.headers = optionHolder.validateAndGetStaticValue
                (WebSocketProperties.HEADERS, null);
        this.idleTimeoutString = optionHolder.validateAndGetStaticValue
                (WebSocketProperties.IDLE_TIMEOUT, null);
        this.sourceEventListener = sourceEventListener;
        if (idleTimeoutString != null) {
            try {
                idleTimeout = Integer.parseInt(idleTimeoutString);
                if (idleTimeout < -1) {
                    throw new SiddhiAppCreationException("The idle timeout defined in '" + sourceEventListener +
                                                                 "' should be greater than 0.");
                }
            } catch (NumberFormatException e) {
                throw new SiddhiAppCreationException("The idle timeout defined in '" + sourceEventListener
                                                             + "' should be an Integer.");
            }
        }
        try {
            String scheme = (new URI(url)).getScheme();
            if (!Objects.equals("ws", scheme) && !Objects.equals("wss", scheme)) {
                throw new SiddhiAppCreationException("Invalid scheme in " + WebSocketProperties.URL + " = " +
                                                             url + ". The scheme of the " + WebSocketProperties.URL +
                                                             " for the websocket server should be either `ws` or "
                                                             + "`wss`.");
            }
            if (Objects.equals("wss", scheme)) {
                this.sslEnabled = true;
                this.tlsstruststorePath = optionHolder.validateAndGetStaticValue(
                        WebSocketProperties.TLS_TRUSTSTORE_PATH, configReader.readConfig
                                (WebSocketProperties.TLS_TRUSTSTORE_PATH,
                                        WebSocketProperties
                                                .DEFAULT_TRUSTSTORE_FILE_PATH));
                this.tlsstruststorePass = optionHolder.validateAndGetStaticValue(
                        WebSocketProperties.TLS_TRUSTSTORE_PASS, configReader.readConfig(
                                WebSocketProperties.TLS_TRUSTSTORE_PASS, WebSocketProperties.DEFAULT_TRUSTSTORE_PASS));
            }
        } catch (URISyntaxException e) {
            throw new SiddhiAppCreationException("There is an syntax error in the '" + WebSocketProperties.URL +
                                                         "' of the websocket server.", e);
        }
        connectorListener = new WebSocketClientConnectorListener();

        return null;
    }

    @Override
    protected ServiceDeploymentInfo exposeServiceDeploymentInfo() {
        return null;
    }

    @Override
    public Class[] getOutputEventClasses() {
        return new Class[]{String.class, ByteBuffer.class};
    }

    @Override
    public void connect(ConnectionCallback connectionCallback, State state) throws ConnectionUnavailableException {
        HttpWsConnectorFactory httpConnectorFactory = new DefaultHttpWsConnectorFactory();
        WebSocketClientConnectorConfig configuration = new WebSocketClientConnectorConfig(url);
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
        if (sslEnabled) {
            configuration.setTrustStoreFile(this.tlsstruststorePath);
            configuration.setTrustStorePass(this.tlsstruststorePass);
        }
        configuration.setAutoRead(true);
        WebSocketClientConnector clientConnector = httpConnectorFactory.createWsClientConnector(configuration);
        ClientHandshakeFuture handshakeFuture = clientConnector.connect();
        handshakeFuture.setWebSocketConnectorListener(connectorListener);
        WebSocketSourceHandshakeListener handshakeListener = new WebSocketSourceHandshakeListener
                (connectorListener, sourceEventListener);
        handshakeFuture.setClientHandshakeListener(handshakeListener);
    }

    @Override
    public void disconnect() {
        //Not applicable
    }

    @Override
    public void destroy() {
        //Not applicable
    }

    @Override
    public void pause() {
        //Not applicable
    }

    @Override
    public void resume() {
        //Not applicable
    }
}
