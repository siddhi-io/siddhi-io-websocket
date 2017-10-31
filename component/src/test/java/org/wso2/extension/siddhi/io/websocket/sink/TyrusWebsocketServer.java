
package org.wso2.extension.siddhi.io.websocket.sink;

import org.glassfish.tyrus.server.Server;

public class TyrusWebsocketServer {

    private static final String HOST = "localhost";
    private static final int WEB_SOCKET_PORT = 8025;
    private static final String WEB_SOCKET_CONTEXT = "/websockets";
    private static Server server = new Server(HOST, WEB_SOCKET_PORT, WEB_SOCKET_CONTEXT,
                                              null, WebsocketEndpoint.class);

    public static void start() {
        try {
            server.start();
        } catch (Exception ignored) {
        }
    }

    public static void stop() {
        server.stop();
    }
}
