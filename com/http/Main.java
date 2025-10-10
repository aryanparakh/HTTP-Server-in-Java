package com.http;

import com.http.app.Application;
import com.http.routing.Router;
import com.http.server.HttpServer;

public class Main {
    private static int PORT = 8080;
    private static String HOST = "127.0.0.1";

    public static void main(String[] args) {
        if (args.length > 0) {
            PORT = Integer.parseInt(args[0]);
        }
        if (args.length > 1) {
            HOST = args[1];
        }

        Application app = new Application();

        Router router = app.configureRouter();

        HttpServer server = new HttpServer(PORT, HOST, router);

        server.start();
    }
}
