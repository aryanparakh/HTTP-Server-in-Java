package com.http.routing;

import com.http.model.request.Request;
import com.http.model.response.Response;

/**
 * Functional interface for handling HTTP requests
 * Implementations process requests and return responses
 */
@FunctionalInterface
public interface RouteHandler 
{
    Response handle(Request request);
}
