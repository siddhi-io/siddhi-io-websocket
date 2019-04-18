/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.extension.siddhi.io.websocket.sink.websocketserver;

import io.siddhi.annotation.Example;
import io.siddhi.annotation.Extension;
import io.siddhi.annotation.Parameter;
import io.siddhi.annotation.util.DataType;
import io.siddhi.core.config.SiddhiAppContext;
import io.siddhi.core.exception.ConnectionUnavailableException;
import io.siddhi.core.stream.ServiceDeploymentInfo;
import io.siddhi.core.stream.output.sink.Sink;
import io.siddhi.core.util.config.ConfigReader;
import io.siddhi.core.util.snapshot.state.State;
import io.siddhi.core.util.snapshot.state.StateFactory;
import io.siddhi.core.util.transport.DynamicOptions;
import io.siddhi.core.util.transport.OptionHolder;
import io.siddhi.query.api.definition.StreamDefinition;
import org.wso2.extension.siddhi.io.websocket.util.WebSocketProperties;
import org.wso2.extension.siddhi.io.websocket.util.WebSocketUtil;

import java.nio.ByteBuffer;

/**
 * {@code WebsocketServerSink } Start the WebSocket server and publishing the siddhi events.
 */

@Extension(
        name = "websocket-server",
        namespace = "sink",
        description = "A Siddhi application can be configured to publish events via the WebSocket transport by " +
                "adding the @Sink(type = 'websocket-server') annotation at the top of an event stream definition.",
        parameters = {
                @Parameter(
                        name = "host",
                        description = "host of the WebSocket server",
                        type = DataType.STRING
                ),
                @Parameter(
                        name = "port",
                        description = "port of the WebSocket server",
                        type = DataType.STRING
                ),
                @Parameter(
                        name = "sub.protocol",
                        description = "Sub-Protocols which are allowed by the service.\n" +
                                "The sub.protocol should adhere to `subprotocol1, subprotocol2,...` format.",
                        type = DataType.STRING,
                        optional = true,
                        defaultValue = "null"
                ),
                @Parameter(
                        name = "idle.timeout",
                        description = "Idle timeout of the connection. If the idle.timeout = '-1' then the timer is "
                                + "disabled.",
                        type = DataType.INT,
                        optional = true,
                        defaultValue = "-1"
                ),
                @Parameter(
                        name = "tls.enabled",
                        description = "This parameter specifies whether a secure connection is enabled or not. When " +
                                "this parameter is set to `true`, the `keystore.path` and the `keystore.password` " +
                                "parameters are initialized.",
                        type = {DataType.BOOL},
                        optional = true,
                        defaultValue = "false"),
                @Parameter(
                        name = "keystore.path",
                        description = "The file path to the location of the keystore. If a custom keystore is not " +
                                "specified, then the system uses the default keystore file - wso2carbon.jks in the " +
                                "`${carbon.home}/resources/security` directory.",
                        type = {DataType.STRING},
                        optional = true,
                        defaultValue = "${carbon.home}/resources/security/wso2carbon.jks"
                ),
                @Parameter(
                        name = "keystore.password",
                        description = "The password for the keystore. A custom password can be specified " +
                                "if required. If no custom password is specified, then the system uses " +
                                "`wso2carbon` as the default password.",
                        type = {DataType.STRING},
                        optional = true,
                        defaultValue = "wso2carbon"
                )
        },
        examples = {
                @Example(
                        syntax = "@Sink(type = 'websocket-server', host='localhost', port='9025', \n" +
                                "   @map(type='xml'))\n" +
                                "define stream Foo (attribute1 string, attribute2 int);",
                        description = "" +
                                "A sink of type 'websocket-server' has been defined.\n" +
                                "All events arriving at Foo stream via websocket-server will be sent " +
                                "to the url ws://localhost:9025/abc."
                )
        }
)

public class WebSocketServerSink extends Sink {

    private String host;
    private String[] subProtocols = null;
    private int port;
    private boolean isTlsEnabled;
    private int idleTimeout;
    private String tlsKeystorePath;
    private String tlsKeystorePassword;
    private StreamDefinition streamDefinition;
    private WebSocketServer websocketServer = null;
    private static final String[] SUPPORTED_DYNAMIC_OPTIONS = new String[0];

    @Override
    public Class[] getSupportedInputEventClasses() {
        return new Class[]{String.class, ByteBuffer.class};
    }

    @Override
    public String[] getSupportedDynamicOptions() {
        return SUPPORTED_DYNAMIC_OPTIONS;
    }

    /**
     * The initialization method for {@link Sink}, will be called before other methods. It used to validate
     * all configurations and to get initial values.
     *
     * @param streamDefinition containing stream definition bind to the {@link Sink}
     * @param optionHolder     Option holder containing static and dynamic configuration related
     *                         to the {@link Sink}
     * @param configReader     to read the sink related system configuration.
     * @param siddhiAppContext the context of the {@link io.siddhi.query.api.SiddhiApp} used to
     *                         get siddhi related utility functions.
     */
    @Override
    protected StateFactory init(StreamDefinition streamDefinition, OptionHolder optionHolder,
                                ConfigReader configReader, SiddhiAppContext siddhiAppContext) {
        this.host = optionHolder.validateAndGetStaticValue(WebSocketProperties.HOST);
        this.port = Integer.parseInt(optionHolder.validateAndGetStaticValue(WebSocketProperties.PORT));
        String subProtocolString = optionHolder.validateAndGetStaticValue(WebSocketProperties.SUB_PROTOCOL, null);
        if (subProtocolString != null) {
            subProtocols = WebSocketUtil.getSubProtocol(subProtocolString);
        }
        this.idleTimeout = Integer.parseInt(optionHolder.validateAndGetStaticValue(WebSocketProperties.IDLE_TIMEOUT,
                                                                                   "-1"));
        this.isTlsEnabled = Boolean.parseBoolean(optionHolder.validateAndGetStaticValue
                (WebSocketProperties.TLS_ENABLED, "false"));
        this.tlsKeystorePath = optionHolder.validateAndGetStaticValue(WebSocketProperties.TLS_KEYSTORE_PATH,
                                                                      configReader.readConfig
                                                                              (WebSocketProperties.TLS_KEYSTORE_PATH,
                                                                               WebSocketProperties
                                                                                       .DEFAULT_KEYSTORE_FILE_PATH));
        this.tlsKeystorePassword = optionHolder.validateAndGetStaticValue(WebSocketProperties.TLS_KEYSTORE_PASS,
                                                                          configReader.readConfig(
                                                                                  WebSocketProperties.TLS_KEYSTORE_PASS,
                                                                                  WebSocketProperties.
                                                                                          DEFAULT_KEYSTORE_PASS));
        this.streamDefinition = streamDefinition;

        return null;
    }

    @Override
    protected ServiceDeploymentInfo exposeServiceDeploymentInfo() {
        return null;
    }

    @Override
    public void publish(Object payload,
                        DynamicOptions dynamicOptions,
                        State state) throws ConnectionUnavailableException {
        websocketServer.send(payload);
    }

    @Override
    public void connect() throws ConnectionUnavailableException {
        try {
            websocketServer = new WebSocketServer(host, port, subProtocols, idleTimeout, isTlsEnabled,
                                                  tlsKeystorePath, tlsKeystorePassword, streamDefinition);
            websocketServer.start();
        } catch (InterruptedException e) {
            throw new ConnectionUnavailableException("Error while starting the WebSocket server defined in "
                                                             + streamDefinition + ".", e);
        }
    }

    @Override
    public void disconnect() {
        if (websocketServer != null) {
            websocketServer.stop();
        }
    }

    @Override
    public void destroy() {
        //Not applicable
    }
}
