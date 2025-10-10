package com.http.routing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.http.model.common.HttpHeader;
import com.http.model.request.HttpRequest;
import com.http.model.response.HttpResponse;
import com.http.model.response.HttpStatus;

public class FileHandler implements RouteHandler {
    private static final String RESOURCE_DIR = "../../resources";

    @Override
    public HttpResponse handle(HttpRequest request) {
        String path = request.getResource();
        if (path.equals("/")) {
            path = "/index.html";
        }

        File file = new File(RESOURCE_DIR + path);

        // Prevent path traversals using ..
        try {
            String canonicalPath = file.getCanonicalPath();
            String resourceCanonicalPath = new File(RESOURCE_DIR).getCanonicalPath();

            if (!canonicalPath.startsWith(resourceCanonicalPath)) {
                System.out.println("ALERT: path traversal attempted. blocked for path: " + path);
                return new HttpResponse.Builder(HttpStatus.FORBIDDEN_403)
                        .body("403 Forbidden")
                        .build();
            }
            System.out.println(file.getCanonicalPath());
        } catch (IOException e) {
            return new HttpResponse.Builder(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }

        if (!file.exists() || file.isDirectory()) {
            return new HttpResponse.Builder(HttpStatus.NOT_FOUND_404)
                    .body("404 Not Found")
                    .build();
        }

        try {
            byte fileBytes[] = Files.readAllBytes(file.toPath());
            String contentType = getContentType(file.getName());

            if (contentType == null) {
                return new HttpResponse.Builder(HttpStatus.UNSUPPORTED_MEDIA_TYPE_415)
                        .body("415 Unsupported Media Type")
                        .build();
            }

            HttpResponse.Builder responseBuilder = new HttpResponse.Builder(HttpStatus.OK_200)
                    .header(HttpHeader.Content_Type, contentType);

            if (contentType.equals("application/octet-stream")) {
                responseBuilder.header(HttpHeader.Content_Disposition,
                        "attachment; filename=\"" + file.getName() + "\"");
            }

            responseBuilder.body(fileBytes);

            return responseBuilder.build();
        } catch (IOException e) {
            return new HttpResponse.Builder(HttpStatus.INTERNAL_SERVER_ERROR_500).build();
        }
    }

    private String getContentType(String fileName) {
        if (fileName.endsWith(".html"))
            return "text/html; charset=utf-8";
        if (fileName.endsWith(".txt"))
            return "application/octet-stream";
        if (fileName.endsWith(".png"))
            return "application/octet-stream";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg"))
            return "application/octet-stream";

        // Return null for unsupported types
        return null;
    }

}
