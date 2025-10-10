package com.http.exception;

/**
 * Exception thrown when HTTP request parsing fails
 * Indicates malformed or invalid request format
 */
public class InvalidRequestException extends RuntimeException 
{
    private static final long serialVersionUID = 1L;

    // No-argument constructor
    public InvalidRequestException() 
    {
        super("Invalid HTTP request");
    }

    // Creates exception with error message
    public InvalidRequestException(String message) 
    {
        super(message);
    }

    // Creates exception with error message and underlying cause
    public InvalidRequestException(String message, Throwable cause) 
    {
        super(message, cause);
    }

    // Creates exception with underlying cause
    public InvalidRequestException(Throwable cause) 
    {
        super(cause);
    }
}
