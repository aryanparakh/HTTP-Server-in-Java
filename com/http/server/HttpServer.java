package com.http.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.http.routing.Router;

public class HttpServer {

    private final int PORT;
    private final Router router;
    private final String HOST;

    public HttpServer(int PORT, String HOST, Router router) {
        this.PORT = PORT;
        this.HOST = HOST;
        this.router = router;
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

            while (true) {
                // Receive from Socket object after client connection.
                Socket clientSocket = serverSocket.accept();

                // Assign new thread for handling client.
                Thread handleClient = new Thread(new ClientConnection(clientSocket, router));

                // Run the separate thread from the main thread.
                handleClient.start();
            }
        } catch (IOException e) {
            System.out.println("Server error occured due to I/O error. " + e.getMessage());
        }

        System.out.println("Server shutting down...");
    }
}
