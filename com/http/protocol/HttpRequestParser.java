package com.http.protocol;

import com.http.exception.InvalidHttpRequestException;
import com.http.model.common.Delimiter;
import com.http.model.common.HttpHeader;
import com.http.model.request.HttpRequest;
import com.http.model.request.HttpVerb;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {

    public HttpRequest parse(String statusLine, Map<String, String> rawHeaders, BufferedReader reader)
            throws InvalidHttpRequestException, IOException {
        String[] statuses = statusLine.split(Delimiter.HttpRequestStatusDelimiter.getDelimiterValue());
        if (statuses.length != 3) {
            throw new InvalidHttpRequestException(
                    "Status line should have exactly 3 parts. Status Line: " + statusLine);
        }

        HttpVerb verb;
        try {
            verb = HttpVerb.valueOf(statuses[0]);
        } catch (IllegalArgumentException e) {
            throw new InvalidHttpRequestException("Invalid HTTP Verb. Verb: " + statuses[0]);
        }

        // Parsing HTTP Resource and Version.
        String resource = statuses[1];
        String httpVersion = statuses[2];

        Map<HttpHeader, String> headers = new HashMap<>();
        for (Map.Entry<String, String> entry : rawHeaders.entrySet()) {
            try {
                HttpHeader header = HttpHeader.valueOf(entry.getKey().replace('-', '_'));
                headers.put(header, entry.getValue());
            } catch (IllegalArgumentException e) {
                System.out.println("Ignoring Unknown Header: Key: " + entry.getKey() + " | Value: " + entry.getValue());
            }
        }

        boolean containsBody = verb == HttpVerb.POST || verb == HttpVerb.PUT || verb == HttpVerb.PATCH;
        String body = null;

        if (containsBody) {
            if (!headers.containsKey(HttpHeader.Content_Length)) {
                throw new InvalidHttpRequestException("Missing Content-Length header for the request with body.");
            }

            int contentLength = Integer.parseInt(headers.get(HttpHeader.Content_Length));

            char bodyChars[] = new char[contentLength];
            int bytesRead = reader.read(bodyChars, 0, contentLength);
            if (bytesRead < contentLength) {
                throw new IOException("Mismatch in content length and actual body size.");
            }

            body = new String(bodyChars);
        }

        return new HttpRequest(verb, resource, httpVersion, headers, body);
    }
}
