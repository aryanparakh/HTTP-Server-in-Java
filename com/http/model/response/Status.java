package com.http.model.response;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of HTTP status codes supported by the server
 */
public enum Status 
{
    // 2xx Success codes
    OK_200(200, "OK"),
    CREATED_201(201, "Created"),
    ACCEPTED_202(202, "Accepted"),
    NO_CONTENT_204(204, "No Content"),

    // 4xx Client error codes
    BAD_REQUEST_400(400, "Bad Request"),
    UNAUTHORIZED_401(401, "Unauthorized"),
    FORBIDDEN_403(403, "Forbidden"),
    NOT_FOUND_404(404, "Not Found"),
    METHOD_NOT_ALLOWED_405(405, "Method Not Allowed"),
    CONFLICT_409(409, "Conflict"),
    UNSUPPORTED_MEDIA_TYPE_415(415, "Unsupported Media Type"),
    PAYLOAD_TOO_LARGE_413(413, "Payload Too Large"),

    // 5xx Server error codes
    INTERNAL_SERVER_ERROR_500(500, "Internal Server Error"),
    SERVICE_UNAVAILABLE_503(503, "Service Unavailable");

    private final String reasonPhrase;
    private final int code;

    private static final Map<Integer, Status> CODE_MAP = new HashMap<>();

    static {
        for (Status status : values()) {
            CODE_MAP.put(status.code, status);
        }
    }

    private Status(int code, String reasonPhrase) 
    {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
    }

    public String getStatusMessage() 
    {
        return this.reasonPhrase;
    }

    public int getStatusCode() 
    {
        return this.code;
    }

    /**
     * Get HttpStatus enum by numeric code
     * @param code HTTP status code
     * @return Corresponding HttpStatus or null if not found
     */
    public static Status fromCode(int code) 
    {
        return CODE_MAP.get(code);
    }

    @Override
    public String toString() 
    {
        return code + " " + reasonPhrase;
    }
}
