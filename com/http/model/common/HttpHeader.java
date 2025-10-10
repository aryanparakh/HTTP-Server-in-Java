package com.http.model.common;

/**
 * <p>
 * Contains all headers which are compatible with the server, for all HTTP
 * requests and responses.
 * </p>
 *
 * <p>
 * HTTP/1.1 considers header values as case-insensitive, but future values like
 * HTTP/2 and HTTP/3 introduces a new rule that all header values should be
 * lowercase. Header values are in camel-case, but are checked by ignoring the
 * case also same while sending responses. Hence this server is compatible for
 * future HTTP versions.
 * </p>
 */
public enum HttpHeader {
    /* Request Headers */
    Host("Host"),
    User_Agent("User-Agent"),
    Accept("Accept"),
    Accept_Language("Accept-Language"),
    Accept_Encoding("Accept-Encoding"),
    Content_Encoding("Content-Encoding"),

    /* Response Headers */
    Server("Server"),
    Date("Date"),
    Content_Type("Content-Type"),
    Content_Length("Content-Length"),
    Location("Location"),
    Content_Disposition("Content-Disposition"),
    Keep_Alive("Keep-Alive"),

    /* Common Headers */
    Connection("Connection");

    private final String headerValue;

    HttpHeader(String headerValue) {
        this.headerValue = headerValue;
    }

    public String getHeaderValue() {
        return this.headerValue;
    }

}
