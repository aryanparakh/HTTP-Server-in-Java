package com.http.server;

import com.http.model.common.Header;
import com.http.model.request.Request;
import com.http.model.response.Response;
import com.http.routing.RouteManager;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles an individual client connection in its own thread.
 * Reads the request, routes it, and writes the response.
 */
public class ConnectionHandler implements Runnable {

    private final Socket clientSocket;
    private final RouteManager router;

    public ConnectionHandler(Socket socket, RouteManager router) {
        this.clientSocket = socket;
        this.router = router;
    }

    @Override
    public void run() {
        try (InputStream input = clientSocket.getInputStream();
             OutputStream output = clientSocket.getOutputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {

            System.out.println("🆕 New client connected: " + clientSocket.getRemoteSocketAddress());

            // Read request line
            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                clientSocket.close();
                return;
            }

            // Read headers
            Map<String, String> rawHeaders = new HashMap<>();
            String headerLine;
            while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
                String[] parts = headerLine.split(": ", 2);
                if (parts.length == 2) {
                    rawHeaders.put(parts[0], parts[1]);
                }
            }

            // Parse request
            Request request = new com.http.protocol.RequestParser()
                    .parse(requestLine, rawHeaders, reader);

            // Handle connection headers safely
            Map<Header, String> mutableHeaders = new HashMap<>(request.getHeaders());
            mutableHeaders.put(Header.Connection, "keep-alive");

            // Route the request
            Response response = router.route(request);

            // Send response
            writeResponse(output, response);

        } catch (Exception e) {
            System.out.println("⚠️ Error handling client: " + e.getMessage());
            try {
                if (!clientSocket.isClosed()) clientSocket.close();
            } catch (IOException ignored) {}
        }
    }

    // Writes the response to the client socket
    private void writeResponse(OutputStream output, Response response) throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));

        // Status line
        writer.write(response.getHttpVersion() + " " + response.getStatus().getStatusCode() + " " +
                response.getStatus().getStatusMessage() + "\r\n");

        // Headers
        for (Map.Entry<Header, String> entry : response.getHeaders().entrySet()) {
            writer.write(entry.getKey().getHeaderValue() + ": " + entry.getValue() + "\r\n");
        }
        writer.write("\r\n"); // End of headers
        writer.flush();

        // Body
        byte[] body = response.getBody();
        if (body != null && body.length > 0) {
            output.write(body);
            output.flush();
        }
    }
}
