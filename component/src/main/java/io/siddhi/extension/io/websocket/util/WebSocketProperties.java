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
package io.siddhi.extension.io.websocket.util;

/**
 * This class represents WebSocket specific Constants.
 */
public class WebSocketProperties {
    private WebSocketProperties() {
    }

    public static final String URL = "url";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String HEADERS = "headers";
    public static final String SUB_PROTOCOL = "sub.protocol";
    public static final String IDLE_TIMEOUT = "idle.timeout";
    public static final String TLS_ENABLED = "tls.enabled";
    public static final String TLS_KEYSTORE_PATH = "keystore.path";
    public static final String TLS_KEYSTORE_PASS = "keystore.password";
    public static final String TLS_TRUSTSTORE_PATH = "truststore.path";
    public static final String TLS_TRUSTSTORE_PASS = "truststore.password";
    public static final String DEFAULT_KEYSTORE_FILE_PATH = "${carbon.home}/resources/security/wso2carbon.jks";
    public static final String DEFAULT_KEYSTORE_PASS = "wso2carbon";
    public static final String DEFAULT_TRUSTSTORE_FILE_PATH = "${carbon.home}/resources/security/client-truststore.jks";
    public static final String DEFAULT_TRUSTSTORE_PASS = "wso2carbon";
}
