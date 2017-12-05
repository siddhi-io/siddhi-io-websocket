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

package org.wso2.extension.siddhi.io.websocket.util;

import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import javax.websocket.server.ServerContainer;

public class WebSocketServer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);
   private static Server server = null;
   private static  File file = new File("src/test/resources/client-truststore.jks");
   private static  String truststorePath = file.getAbsolutePath();
   private static  File file1 = new File("src/test/resources/keycert.p12");
   private static String keystorePath = file1.getAbsolutePath();

    public static void start() {
        server = new Server();
        //For the normal connection (ws)
        ServerConnector connector = new ServerConnector(server);
        connector.setHost("localhost");
        connector.setPort(8025);
        server.addConnector(connector);

        //For the secure connection (wss)
        SslContextFactory sslContextFactory = new SslContextFactory(keystorePath);
        sslContextFactory.setTrustStorePath(truststorePath);
        sslContextFactory.setKeyStorePassword("MySecretPassword");
        sslContextFactory.setNeedClientAuth(false);
        sslContextFactory.setKeyStoreType("PKCS12");
        sslContextFactory.setTrustStorePassword("wso2carbon");
        sslContextFactory.setTrustStoreType("JKS");
        sslContextFactory.setMaxCertPathLength(100000000);
        HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSecureScheme("https");
        httpConfiguration.setSecurePort(5050);
        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfiguration);
        ServerConnector sslConnector = new ServerConnector(server, sslContextFactory, httpConnectionFactory);
        sslConnector.setHost("localhost");
        sslConnector.setPort(5050);
        server.addConnector(sslConnector);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        try {
            ServerContainer wscontainer = WebSocketServerContainerInitializer.configureContext(context);
            wscontainer.addEndpoint(WebSocketEndpoint.class);
            server.start();
        } catch (Throwable t) {
            log.error("Error while starting the server", t);
        }
    }

    public static void stop() throws Exception {
        server.stop();
    }
}
