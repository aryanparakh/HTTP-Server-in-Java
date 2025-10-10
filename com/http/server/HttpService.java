package com.http.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.http.routing.RouteManager;

/**
 * Lightweight Multi-threaded HTTP Service
 * - Manages incoming connections with a configurable thread pool
 * - Routes requests using the provided Router instance
 */
public class HttpService {

    private final int port;
    private final String host;
    private final RouteManager router;
    private final int threadCount;
    private final ExecutorService executor;

    /**
     * Initializes the HTTP Service with required parameters.
     */
    public HttpService(int port, String host, RouteManager router, int threadCount) {
        this.port = port;
        this.host = host;
        this.router = router;
        this.threadCount = threadCount;
        this.executor = Executors.newFixedThreadPool(threadCount);
    }

    /**
     * Activates the HTTP service and begins listening for client requests.
     */
    public void boot() {
        System.out.println("🟢 Starting HTTP service ...");

        InetAddress inetHost = resolveAddress();
        if (inetHost == null) {
            System.out.println("❌ Invalid host, terminating startup.");
            return;
        }

        int backlogLimit = 50;

        try (ServerSocket listener = new ServerSocket(port, backlogLimit, inetHost)) {
            System.out.println("🌍 Service active on " + host + ":" + port);
            System.out.println("🧵 Thread pool capacity: " + threadCount + " workers");

            listenForClients(listener);
        } catch (IOException e) {
            System.out.println("💥 Server I/O failure: " + e.getMessage());
        } finally {
            executor.shutdown();
        }

        System.out.println("🔴 HTTP Service stopped.");
    }

    /**
     * Resolves host string into a valid InetAddress instance.
     */
    private InetAddress resolveAddress() {
        try {
            return InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            System.out.println("⚠️ Could not resolve host: " + host + " | " + e.getMessage());
            return null;
        }
    }

    /**
     * Accepts incoming client sockets and dispatches them to worker threads.
     */
    private void listenForClients(ServerSocket listener) throws IOException {
        while (true) {
            Socket socket = listener.accept();
            System.out.println("🔗 Connection established from " + socket.getRemoteSocketAddress());

            // delegate connection processing to thread pool
            executor.execute(new ConnectionHandler(socket, router));
        }
    }
}
