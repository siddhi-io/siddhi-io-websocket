/*
 * Copyright (c) 2026, WSO2 LLC. (http://www.wso2.org).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package io.siddhi.extension.io.websocket.util;

import org.wso2.transport.http.netty.contract.websocket.ServerHandshakeFuture;
import org.wso2.transport.http.netty.contract.websocket.ServerHandshakeListener;
import org.wso2.transport.http.netty.contract.websocket.WebSocketConnection;
import org.wso2.transport.http.netty.contract.websocket.WebSocketHandshaker;

import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Stubs for the transport interfaces used by the handshake race tests.
 */
public class WebSocketStubs {

    public static class Connection {
        public final AtomicBoolean terminated = new AtomicBoolean(false);
        public final WebSocketConnection proxy = (WebSocketConnection) Proxy.newProxyInstance(
                WebSocketConnection.class.getClassLoader(), new Class[]{WebSocketConnection.class},
                (p, method, args) -> {
                    if ("equals".equals(method.getName())) {
                        return p == args[0];
                    }
                    if ("hashCode".equals(method.getName())) {
                        return System.identityHashCode(p);
                    }
                    if ("terminateConnection".equals(method.getName())) {
                        terminated.set(true);
                    }
                    if ("isOpen".equals(method.getName())) {
                        return !terminated.get();
                    }
                    return null;
                });
    }

    public static class Handshaker {
        public final AtomicReference<ServerHandshakeListener> listener = new AtomicReference<>();
        public final WebSocketHandshaker proxy = (WebSocketHandshaker) Proxy.newProxyInstance(
                WebSocketHandshaker.class.getClassLoader(), new Class[]{WebSocketHandshaker.class},
                (p, method, args) -> {
                    if ("handshake".equals(method.getName())) {
                        return Proxy.newProxyInstance(ServerHandshakeFuture.class.getClassLoader(),
                                new Class[]{ServerHandshakeFuture.class}, (fp, fm, fargs) -> {
                                    if ("setHandshakeListener".equals(fm.getName())) {
                                        listener.set((ServerHandshakeListener) fargs[0]);
                                    }
                                    return null;
                                });
                    }
                    return null;
                });
    }
}
