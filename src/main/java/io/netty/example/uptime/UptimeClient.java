package io.netty.example.uptime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connects to a server periodically to measure and print the uptime of the
 * server.  This example demonstrates how to implement reliable reconnection
 * mechanism in Netty.
 *
 * @since 2019-06-09
 */
public final class UptimeClient {
    private static final Logger logger = LoggerFactory.getLogger(UptimeClient.class);

    private static final String HOST = System.getProperty("host", "127.0.0.1");
    private static final int PORT = Integer.parseInt(System.getProperty("port", "8080"));
    /**
     * Sleep 5 seconds before a reconnection attempt.
     */
    private static final int RECONNECT_DELAY = Integer.parseInt(System.getProperty("reconnectDelay", "5"));
    /**
     * Reconnect when the server sends nothing for 10 seconds.
     */
    private static final int READ_TIMEOUT = Integer.parseInt(System.getProperty("readTimeout", "10"));

    private static final UptimeClientHandler handler = new UptimeClientHandler();
}
