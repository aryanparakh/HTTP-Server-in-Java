package com.http.model.response;

import com.http.model.common.Header;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Represents an HTTP response with status, headers, and body
 * Uses builder pattern for construction
 */
public class Response 
{
    private final Status statusCode;
    private final Map<Header, String> headerMap;
    private final String protocolVersion;
    private final byte[] responseBody;

    private Response(Builder builder) 
    {
        this.statusCode = builder.responseStatus;
        this.headerMap = Collections.unmodifiableMap(builder.responseHeaders);
        this.protocolVersion = builder.protocolVersion;
        this.responseBody = builder.bodyContent;
    }

    public Status getStatus() 
    {
        return statusCode;
    }

    public Map<Header, String> getHeaders() 
    {
        return headerMap;
    }

    public String getHttpVersion() 
    {
        return protocolVersion;
    }

    public byte[] getBody() 
    {
        return responseBody;
    }

    /**
     * Builder class for constructing HttpResponse objects
     */
    public static class Builder 
    {
        private Status responseStatus;
        private Map<Header, String> responseHeaders;
        private byte[] bodyContent;
        private String protocolVersion = "HTTP/1.1";

        public Builder(Status status) 
        {
            this.responseStatus = status;
            this.responseHeaders = new HashMap<>();
        }

        public Builder protocolVersion(String version) 
        {
            this.protocolVersion = version;
            return this;
        }

        public Builder header(Header headerKey, String headerValue) 
        {
            this.responseHeaders.put(headerKey, headerValue);
            return this;
        }

        public Builder body(String textContent, Charset encoding) 
        {
            this.bodyContent = textContent.getBytes(encoding);
            return this;
        }

        public Builder body(String textContent) 
        {
            return this.body(textContent, StandardCharsets.UTF_8);
        }

        public Builder body(byte[] binaryContent) 
        {
            this.bodyContent = binaryContent;
            return this;
        }

        public Response build() 
        {
            // Standard HTTP headers
            this.header(Header.Date, getCurrentHttpDate());
            this.header(Header.Server, "My Custom HTTP Java Server v0.1");

            // Content-Length header
            if (this.bodyContent != null) 
            {
                this.header(Header.Content_Length, String.valueOf(this.bodyContent.length));
            } 
            else 
            {
                this.header(Header.Content_Length, "0");
            }

            // Default Content-Type if missing
            if (!this.responseHeaders.containsKey(Header.Content_Type)) 
            {
                this.header(Header.Content_Type, "text/plain; charset=utf-8");
            }

            // Default Connection header
            if (!this.responseHeaders.containsKey(Header.Connection)) 
            {
                this.header(Header.Connection, "close");
            }

            return new Response(this);
        }

        // Returns current date in HTTP format
        private String getCurrentHttpDate() 
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            return dateFormat.format(new Date());
        }
    }
}
