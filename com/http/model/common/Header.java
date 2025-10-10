package com.http.model.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of HTTP headers supported by the server
 */
public enum Header 
{
    // Request-specific headers
    Host("Host"),
    User_Agent("User-Agent"),
    Accept("Accept"),
    Accept_Language("Accept-Language"),
    Accept_Encoding("Accept-Encoding"),
    Content_Encoding("Content-Encoding"),
    Authorization("Authorization"),

    // Response-specific headers
    Server("Server"),
    Date("Date"),
    Content_Type("Content-Type"),
    Content_Length("Content-Length"),
    Location("Location"),
    Content_Disposition("Content-Disposition"),
    Keep_Alive("Keep-Alive"),
    ETag("ETag"),
    Last_Modified("Last-Modified"),
    Cache_Control("Cache-Control"),

    // Headers used in both requests and responses
    Connection("Connection");

    private final String headerName;
    private static final Map<String, Header> LOOKUP_MAP = new HashMap<>();

    static {
        for (Header header : values()) {
            LOOKUP_MAP.put(header.headerName.toLowerCase(), header);
        }
    }

    Header(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderValue() {
        return this.headerName;
    }

    @Override
    public String toString() {
        return this.headerName;
    }

    /**
     * Case-insensitive lookup of HttpHeader from string
     * @param headerName Header name (any case)
     * @return HttpHeader if recognized, null otherwise
     */
    public static Header fromString(String headerName) {
        if (headerName == null) return null;
        return LOOKUP_MAP.get(headerName.toLowerCase());
    }
}
