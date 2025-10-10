package com.http;

import com.http.app.AppConfig;
import com.http.routing.RouteManager;
import com.http.server.HttpService;

/**
 * Launches the Custom Multi-threaded HTTP Service.
 * Handles basic setup using optional command-line parameters.
 */
public class MainLauncher {

    // Default configuration setup
    private static int PORT = 9090;
    private static String HOST = "127.0.0.1";
    private static int MAX_THREADS = 10;

    /**
     * Main entry point – prepares router and starts the HTTP server.
     * Command-line arguments (optional): [port] [host] [thread_count]
     */
    public static void main(String[] args) {
        configureServer(args);

        AppConfig app = new AppConfig();
        RouteManager router = app.configureRouter();

        HttpService server = new HttpService(PORT, HOST, router, MAX_THREADS);
        System.out.println("🚀 Server initialized on " + HOST + ":" + PORT + " | Threads: " + MAX_THREADS);

        server.boot();
    }

    /**
     * Reads user input parameters and overrides defaults.
     */
    private static void configureServer(String[] params) {
        try {
            if (params.length > 0) PORT = Integer.parseInt(params[0]);
            if (params.length > 1) HOST = params[1];
            if (params.length > 2) MAX_THREADS = Integer.parseInt(params[2]);
        } catch (NumberFormatException e) {
            System.out.println("⚠️ Invalid input detected, using default settings.");
        }
    }
}
