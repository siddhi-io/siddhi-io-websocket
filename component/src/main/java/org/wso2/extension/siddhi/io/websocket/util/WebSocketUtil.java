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

import org.wso2.siddhi.core.exception.SiddhiAppRuntimeException;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code WebSocketUtil } responsible for input formatting of WebSocket.
 */
public class WebSocketUtil {
    private static final String SUB_PROTOCOL_SPLITTER = ",";
    private static final String HEADER_NAME_VALUE_SPLITTER = ":";
    private static final String HEADER_SPLITTER = "','";

    public static String[] getSubProtocol(String subProtocol) {
        subProtocol = subProtocol.trim();
        return subProtocol.split(SUB_PROTOCOL_SPLITTER);
    }

    public static Map<String, String> getHeaders(String headers) {
        headers = headers.trim();
        headers = headers.substring(1, headers.length() - 1);
        Map<String, String> header = new HashMap<>();
        if (!headers.isEmpty()) {
            String[] headerData = headers.split(HEADER_SPLITTER);
            for (String aHeaderData : headerData) {
                String[] values = aHeaderData.split(HEADER_NAME_VALUE_SPLITTER, 2);
                if (values.length > 1) {
                    header.put(values[0], values[1]);
                } else {
                    throw new SiddhiAppRuntimeException(
                            "Invalid " + WebSocketProperties.HEADERS + " format. Please include as 'key1:value1',"
                                    + "'key2:value2',..");
                }
            }
        }
        return header;
    }
}
