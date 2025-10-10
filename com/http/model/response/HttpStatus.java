package com.http.model.response;

public enum HttpStatus {
    // Success 2xx
    OK_200(200, "OK"),
    CREATED_201(201, "Created"),
    NO_CONTENT_204(204, "No Content"),

    // Client Errors 4xx
    BAD_REQUEST_400(400, "Bad Request"),
    UNAUTHORIZED_401(401, "Unauthorized"),
    FORBIDDEN_403(403, "Forbidden"),
    NOT_FOUND_404(404, "Not Found"),
    METHOD_NOT_ALLOWED_405(405, "Method Not Allowed"),
    UNSUPPORTED_MEDIA_TYPE_415(415, "Unsupported Media Type"),

    // Server Errors 5xx
    INTERNAL_SERVER_ERROR_500(500, "Internal Server Error");

    private final String statusMessage;
    private final int statusCode;

    private HttpStatus(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

}
