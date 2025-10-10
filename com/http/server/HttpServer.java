package com.http.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.http.routing.Router;

public class HttpServer {

    private final int PORT;
    private final Router router;
    private final String HOST;
    private final ExecutorService threadPool;
    private final int THREAD_POOL_SIZE;

    public HttpServer(int PORT, String HOST, Router router, int THREAD_POOL_SIZE) {
        this.PORT = PORT;
        this.HOST = HOST;
        this.router = router;
        this.THREAD_POOL_SIZE = THREAD_POOL_SIZE;
        this.threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public void start() {
        System.out.println("Server about to start...");

        int backlog = 50;
        InetAddress serverBindAddress = null;
        try {
            serverBindAddress = InetAddress.getByName(this.HOST);
        } catch (UnknownHostException e) {
            System.out.println("No Host found for the provided Host address. " + e.getMessage());
            System.exit(1);
        }

        try (
                ServerSocket serverSocket = new ServerSocket(this.PORT, backlog, serverBindAddress);) {
            System.out.println("Server now listening for clients on: " + this.HOST + ":" + this.PORT);
            System.out.println("Number of threads in the pool: " + this.THREAD_POOL_SIZE);

            while (true) {
                // Receive from Socket object after client connection.
                Socket clientSocket = serverSocket.accept();

                threadPool.submit(new ClientConnection(clientSocket, router));
            }
        } catch (IOException e) {
            System.out.println("Server error occured due to I/O error. " + e.getMessage());
        } finally {
            threadPool.shutdown();
        }

        System.out.println("Server shutting down...");
    }
}
