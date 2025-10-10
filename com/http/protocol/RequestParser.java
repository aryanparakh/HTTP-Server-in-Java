package com.http.protocol;

import com.http.exception.InvalidRequestException;
import com.http.model.common.HttpDelimiter;
import com.http.model.common.Header;
import com.http.model.request.Request;
import com.http.model.request.HttpVerb;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses raw HTTP request data into structured HttpRequest objects
 * Handles request line, headers, and body parsing
 */
public class RequestParser 
{
    /**
     * Parses HTTP request components into an HttpRequest object
     * @param requestLine First line of HTTP request (e.g., "GET /path HTTP/1.1")
     * @param rawHeaderMap Map of raw header strings
     * @param bodyReader Reader for reading request body
     * @return Parsed HttpRequest object
     * @throws InvalidRequestException if request format is invalid
     * @throws IOException if reading fails
     */
    public Request parse(String requestLine, Map<String, String> rawHeaderMap, BufferedReader bodyReader)
            throws InvalidRequestException, IOException 
    {
        String[] requestComponents = requestLine.trim().split(HttpDelimiter.HttpRequestStatusDelimiter.getDelimiterValue());

        validateRequestLine(requestComponents, requestLine);

        HttpVerb httpMethod = parseHttpMethod(requestComponents[0].trim());
        String urlPath = requestComponents[1].trim();
        String protocolVersion = requestComponents[2].trim();

        Map<Header, String> parsedHeaders = convertRawHeaders(rawHeaderMap);
        String requestBody = extractRequestBody(httpMethod, parsedHeaders, bodyReader);

        return new Request(httpMethod, urlPath, protocolVersion, parsedHeaders, requestBody);
    }

    // Validates that the request line has exactly 3 components
    private void validateRequestLine(String[] components, String originalLine) 
            throws InvalidRequestException 
    {
        if (components.length != 3) 
        {
            throw new InvalidRequestException(
                    "Request line must contain exactly 3 components. Received: " + originalLine);
        }
    }

    // Parses and validates the HTTP method (case-insensitive)
    private HttpVerb parseHttpMethod(String methodString) throws InvalidRequestException 
    {
        try 
        {
            return HttpVerb.valueOf(methodString.toUpperCase());
        } 
        catch (IllegalArgumentException e) 
        {
            throw new InvalidRequestException("Unsupported HTTP method: " + methodString);
        }
    }

    // Converts raw header strings to HttpHeader enum map
    private Map<Header, String> convertRawHeaders(Map<String, String> rawHeaders) 
    {
        Map<Header, String> headerMap = new HashMap<>();

        for (Map.Entry<String, String> headerEntry : rawHeaders.entrySet()) 
        {
            try 
            {
                String key = headerEntry.getKey().trim().replace('-', '_').toUpperCase();
                Header headerKey = Header.valueOf(key);
                headerMap.put(headerKey, headerEntry.getValue().trim());
            } 
            catch (IllegalArgumentException e) 
            {
                System.out.println("Skipping unrecognized header: " + headerEntry.getKey() + " = " + headerEntry.getValue());
            }
        }

        return headerMap;
    }

    // Extracts request body for methods that support it
    private String extractRequestBody(HttpVerb method, Map<Header, String> headers, BufferedReader reader) 
            throws InvalidRequestException, IOException 
    {
        boolean methodSupportsBody = (method == HttpVerb.POST || method == HttpVerb.PUT || method == HttpVerb.PATCH);

        if (!methodSupportsBody) return null;

        int bodyLength = 0;
        if (headers.containsKey(Header.Content_Length)) 
        {
            try 
            {
                bodyLength = Integer.parseInt(headers.get(Header.Content_Length));
            } 
            catch (NumberFormatException e) 
            {
                throw new InvalidRequestException("Invalid Content-Length value.");
            }
        } 
        else 
        {
            // Optional: allow empty body if Content-Length missing
            return null;
        }

        char[] bodyBuffer = new char[bodyLength];
        int totalRead = 0;
        while (totalRead < bodyLength) 
        {
            int read = reader.read(bodyBuffer, totalRead, bodyLength - totalRead);
            if (read == -1) break;
            totalRead += read;
        }

        if (totalRead != bodyLength) 
        {
            throw new IOException("Body size mismatch: expected " + bodyLength + " bytes, got " + totalRead);
        }

        return new String(bodyBuffer);
    }
}
