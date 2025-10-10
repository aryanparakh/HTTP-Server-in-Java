package com.http.model.request;

import com.http.model.common.Header;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Immutable representation of an HTTP request
 */
public final class Request 
{
    private final HttpVerb httpMethod;
    private final String requestPath;
    private final String protocolVersion;
    private final Map<Header, String> headerMap;
    private final String requestBody;
    private final URI uriObject;
    private final Map<String, String> queryParams;

    public Request(HttpVerb verb, String resource, String httpVersion, Map<Header, String> headers, String body)
            throws IllegalArgumentException 
    {
        validateVerb(verb);
        this.httpMethod = verb;

        validateResource(resource);
        this.requestPath = resource;

        validateHttpVersion(httpVersion);
        this.protocolVersion = httpVersion;

        this.headerMap = (headers == null) ? Collections.emptyMap() : Map.copyOf(headers);

        // Validate body
        validateBody(body, this.httpMethod);
        this.requestBody = body;

        // Parse URI and query parameters
        try 
        {
            this.uriObject = new URI(resource);
        } 
        catch (URISyntaxException e) 
        {
            throw new IllegalArgumentException("Invalid resource path format: '" + resource + "'. " + e.getMessage());
        }

        this.queryParams = parseQueryParams(this.uriObject.getQuery());
    }

    private void validateBody(String body, HttpVerb verb) 
    {
        boolean bodyPresent = body != null && !body.trim().isEmpty();
        boolean bodyRequired = (verb == HttpVerb.POST || verb == HttpVerb.PUT || verb == HttpVerb.PATCH);

        if (bodyPresent && !bodyRequired) 
        {
            // Optional: allow body for DELETE but still log warning
            if (verb != HttpVerb.DELETE) 
            {
                throw new IllegalArgumentException("HTTP method: " + verb + " cannot have a body.");
            }
        }

        if (!bodyPresent && bodyRequired) 
        {
            throw new IllegalArgumentException("HTTP method: " + verb + " requires a body.");
        }
    }

    private void validateVerb(HttpVerb verb) 
    {
        if (verb == null) throw new IllegalArgumentException("HTTP verb cannot be null.");
    }

    private void validateHttpVersion(String httpVersion) 
    {
        if (httpVersion == null || httpVersion.trim().isEmpty()) 
        {
            throw new IllegalArgumentException("HTTP version cannot be null or empty.");
        }
    }

    private void validateResource(String resource) 
    {
        if (resource == null || !resource.startsWith("/")) 
        {
            throw new IllegalArgumentException("Resource path must start with '/' and cannot be null.");
        }
    }

    private Map<String, String> parseQueryParams(String query) 
    {
        if (query == null || query.isEmpty()) return Collections.emptyMap();

        Map<String, String> params = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) 
        {
            String[] keyVal = pair.split("=", 2);
            if (keyVal.length == 2) params.put(keyVal[0], keyVal[1]);
        }
        return Collections.unmodifiableMap(params);
    }

    // --- Public Getters ---
    public HttpVerb getVerb() { return this.httpMethod; }

    public String getResource() { return requestPath; }

    public String getHttpVersion() { return protocolVersion; }

    public Map<Header, String> getHeaders() { return headerMap; }

    public Optional<String> getBody() { return Optional.ofNullable(this.requestBody); }

    public Map<String, String> getQueryParams() { return queryParams; }

    public String getQueryParam(String key) { return queryParams.get(key); }

    public URI getUri() { return uriObject; }
}
