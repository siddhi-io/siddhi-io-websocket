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

import org.wso2.msf4j.MicroservicesRunner;

import java.io.File;

public class WebSocketServer {
    private static File transportNettyFilePath = new File("src/test/resources/conf/transports/"
                                                                  + "netty-transports.yml");
    private static String transportNettyFile = transportNettyFilePath.getAbsolutePath();
    private static File keyStoreFilePath = new File("src/test");
    private static String keyStorePath = keyStoreFilePath.getAbsolutePath();
    private static MicroservicesRunner microservicesRunner = null;

    public static void start() {
        System.setProperty("carbon.home", keyStorePath);
        System.setProperty("transports.netty.conf", transportNettyFile);
        microservicesRunner = new MicroservicesRunner();
        microservicesRunner.deployWebSocketEndpoint(new WebSocketEndpoint()).start();
    }

    public static void stop() throws Exception {
        if (microservicesRunner != null) {
            microservicesRunner.stop();
        }
    }
}
