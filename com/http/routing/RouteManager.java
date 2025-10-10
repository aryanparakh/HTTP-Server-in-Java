package com.http.routing;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.http.model.request.Request;
import com.http.model.request.HttpVerb;
import com.http.model.response.Response;
import com.http.model.response.Status;

/**
 * Routes incoming HTTP requests to appropriate handlers
 * Supports custom route registration, default GET handler,
 * path parameters, and wildcard routes.
 */
public class RouteManager 
{
    // Thread-safe map storing route patterns to their handlers
    private final Map<String, RouteHandler> handlerRegistry = new ConcurrentHashMap<>();
    
    private RouteHandler fallbackGetHandler = null;

    // Register default GET handler
    public void setDefaultGetHandler(RouteHandler handler) 
    {
        this.fallbackGetHandler = handler;
    }

    // Add route for specific HTTP method and path
    public void addRoute(HttpVerb verb, String resource, RouteHandler handler) 
    {
        String routeKey = buildRouteKey(verb, resource);
        handlerRegistry.put(routeKey, handler);
    }

    // Route incoming request
    public Response route(Request request) 
    {
        HttpVerb method = request.getVerb();
        String path = request.getResource();

        // Try exact match first
        String routeKey = buildRouteKey(method, path);
        RouteHandler handler = handlerRegistry.get(routeKey);
        if (handler != null) return handler.handle(request);

        // Try wildcard and path-parameter routes
        for (Map.Entry<String, RouteHandler> entry : handlerRegistry.entrySet()) 
        {
            String patternKey = entry.getKey();
            if (matchesPattern(patternKey, method, path)) 
            {
                return entry.getValue().handle(request);
            }
        }

        // Fallback GET handler
        if (method == HttpVerb.GET && fallbackGetHandler != null) 
        {
            return fallbackGetHandler.handle(request);
        }

        // Return error for unmatched routes
        return generateErrorResponse(method, path, routeKey);
    }

    // Build key as "METHOD /path"
    private String buildRouteKey(HttpVerb verb, String resource) 
    {
        return verb + " " + resource;
    }

    // Check if registered route pattern matches request path
    private boolean matchesPattern(String patternKey, HttpVerb method, String path) 
    {
        String[] parts = patternKey.split(" ", 2);
        if (parts.length != 2 || !parts[0].equals(method.toString())) return false;

        String routePattern = parts[1];
        // Convert path parameters to regex, e.g., /users/:id -> /users/[^/]+
        String regex = routePattern.replaceAll(":[^/]+", "[^/]+");
        regex = regex.replace("*", ".*"); // wildcard support
        Pattern p = Pattern.compile("^" + regex + "$");
        Matcher m = p.matcher(path);
        return m.matches();
    }

    private Response generateErrorResponse(HttpVerb method, String path, String routePattern) 
    {
        if (method == HttpVerb.GET) 
        {
            System.out.println("GET route not found: " + routePattern);
            return new Response.Builder(Status.NOT_FOUND_404)
                    .body("404 Not Found")
                    .build();
        } 
        else 
        {
            System.out.println("Method not allowed: " + method + " " + path);
            return new Response.Builder(Status.METHOD_NOT_ALLOWED_405)
                    .body("405 Method Not Allowed")
                    .build();
        }
    }
}
