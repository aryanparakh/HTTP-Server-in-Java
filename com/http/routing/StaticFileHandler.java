package com.http.routing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.http.model.common.Header;
import com.http.model.request.Request;
import com.http.model.response.Response;
import com.http.model.response.Status;

/**
 * Handles static file serving from the resources directory
 * Supports HTML, TXT, PNG, JPG, CSS, JS, GIF file types
 */
public class StaticFileHandler implements RouteHandler 
{
    private static final Path STATIC_FILES_DIRECTORY = Paths.get("resources").toAbsolutePath().normalize();

    @Override
    public Response handle(Request request) 
    {
        String requestedPath = request.getResource();

        // Default to index.html for root
        if (requestedPath.equals("/")) 
        {
            requestedPath = "/index.html";
        }

        Path targetPath = STATIC_FILES_DIRECTORY.resolve(requestedPath.substring(1)).normalize();

        // Security check
        if (!isPathSecure(targetPath)) 
        {
            System.out.println("SECURITY ALERT: Path traversal blocked: " + requestedPath);
            return new Response.Builder(Status.FORBIDDEN_403)
                    .body("403 Forbidden")
                    .build();
        }

        File targetFile = targetPath.toFile();
        if (!targetFile.exists() || targetFile.isDirectory()) 
        {
            return new Response.Builder(Status.NOT_FOUND_404)
                    .body("404 Not Found")
                    .build();
        }

        return serveFile(targetFile);
    }

    private boolean isPathSecure(Path path) 
    {
        return path.startsWith(STATIC_FILES_DIRECTORY);
    }

    private Response serveFile(File file) 
    {
        try 
        {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            String mimeType = determineMimeType(file.getName());

            if (mimeType == null) 
            {
                return new Response.Builder(Status.UNSUPPORTED_MEDIA_TYPE_415)
                        .body("415 Unsupported Media Type")
                        .build();
            }

            Response.Builder builder = new Response.Builder(Status.OK_200)
                    .header(Header.Content_Type, mimeType);

            // Add download header for non-displayable types
            if (mimeType.equals("application/octet-stream")) 
            {
                builder.header(Header.Content_Disposition,
                        "attachment; filename=\"" + file.getName() + "\"");
            }

            builder.body(fileContent);
            System.out.println("Serving file: " + file.getAbsolutePath());
            return builder.build();
        } 
        catch (IOException e) 
        {
            System.out.println("Error reading file: " + e.getMessage());
            return new Response.Builder(Status.INTERNAL_SERVER_ERROR_500)
                    .body("500 Internal Server Error")
                    .build();
        }
    }

    private String determineMimeType(String filename) 
    {
        filename = filename.toLowerCase();
        if (filename.endsWith(".html") || filename.endsWith(".htm")) return "text/html; charset=utf-8";
        if (filename.endsWith(".txt")) return "text/plain; charset=utf-8";
        if (filename.endsWith(".css")) return "text/css; charset=utf-8";
        if (filename.endsWith(".js")) return "application/javascript";
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
        if (filename.endsWith(".gif")) return "image/gif";

        // Unknown file type
        return "application/octet-stream";
    }
}
