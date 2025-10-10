package com.http.routing;

import java.util.HashMap;
import java.util.Map;

import com.http.model.common.HttpHeader;
import com.http.model.request.HttpRequest;
import com.http.model.request.HttpVerb;
import com.http.model.response.HttpResponse;
import com.http.model.response.HttpStatus;

public class Router {

    // Stores all registerd routes.
    // Key: "GET /route"
    // Value: Handler Function
    private final Map<String, RouteHandler> routes = new HashMap<>();

    private RouteHandler defaultGetHandler = null;

    public void setDefaultGetHandler(RouteHandler handler) {
        this.defaultGetHandler = handler;
    }

    /**
     * Registers a handler for a specific HTTP Verb and a resource path.
     */
    public void addRoute(HttpVerb verb, String resource, RouteHandler handler) {
        String routeKey = verb.toString() + " " + resource;
        this.routes.put(routeKey, handler);
    }

    public HttpResponse route(HttpRequest request) {
        HttpVerb verb = request.getVerb();
        String resource = request.getResource();
        String routeKey = request.getVerb().toString() + " " + request.getResource();
        RouteHandler handler = routes.get(routeKey);

        if(handler != null) {
            return handler.handle(request);
        }

        if(verb == HttpVerb.GET && defaultGetHandler != null) {
            return defaultGetHandler.handle(request);
        }

        if(verb == HttpVerb.GET) {
            System.out.println("No Route Handler found for route key: " + routeKey);
            return new HttpResponse.Builder(HttpStatus.NOT_FOUND_404).body("404 Not Found").build();
        } else {
            System.out.println("Unsupported method for resource: " + verb + " " + resource);
            return new HttpResponse.Builder(HttpStatus.METHOD_NOT_ALLOWED_405).body("405 Method Not Allowed").build();
        }
    }

}
