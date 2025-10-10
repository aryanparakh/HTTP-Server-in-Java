package com.http.model.common;

/**
 * HTTP protocol delimiters used for parsing
 */
public enum HttpDelimiter 
{
    HttpRequestLineDelimiter("\r\n"),  // Line endings
    HttpRequestStatusDelimiter(" "),    // Space in request line
    HttpHeaderDelimiter(": "),          // Colon-space in headers
    QueryParamDelimiter("&"),           // For parsing query string
    KeyValueDelimiter("=");             // For parsing key=value pairs

    private final String value;

    private HttpDelimiter(String value) 
    {
        this.value = value;
    }

    public String getDelimiterValue() 
    {
        return this.value;
    }

    @Override
    public String toString() 
    {
        return this.value;
    }
}
