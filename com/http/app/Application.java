package com.http.app;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import com.http.model.common.HttpHeader;
import com.http.model.request.HttpVerb;
import com.http.model.response.HttpResponse;
import com.http.model.response.HttpStatus;
import com.http.routing.FileHandler;
import com.http.routing.Router;

public class Application {

    /**
     * Defines Application Routes.
     */
    public Router configureRouter() {
        Router router = new Router();

        router.setDefaultGetHandler(new FileHandler());

        router.addRoute(HttpVerb.POST, "/upload", (request) -> {
            // Check for correct content type
            String contentType = request.getHeaders().get(HttpHeader.Content_Type);
            if (contentType == null || !contentType.equalsIgnoreCase("application/json")) {
                return new HttpResponse.Builder(HttpStatus.UNSUPPORTED_MEDIA_TYPE_415)
                        .body("415 Unsupported Media Type: Content-Type must be application/json").build();
            }

            // Get and validate JSON body
            Optional<String> body = request.getBody();
            if (body.isEmpty() || body.get().trim().isEmpty()) {
                return new HttpResponse.Builder(HttpStatus.BAD_REQUEST_400)
                        .body("400 Bad Request: JSON body is missing or empty.").build();
            }
            // A simple validation: check if it starts and ends with {}
            String jsonBody = body.get().trim();
            if (!jsonBody.startsWith("{") || !jsonBody.endsWith("}")) {
                return new HttpResponse.Builder(HttpStatus.BAD_REQUEST_400)
                        .body("400 Bad Request: Invalid JSON format.").build();
            }

            // Create the file
            try {
                // Ensure the uploads directory exists
                Files.createDirectories(Paths.get("../../resources/uploads"));

                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String randomId = java.util.UUID.randomUUID().toString().substring(0, 4);
                String fileName = "upload_" + timestamp + "_" + randomId + ".json";
                String relativePath = "/uploads/" + fileName;
                String filePath = "../../resources" + relativePath;

                Files.write(Paths.get(filePath), jsonBody.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE_NEW);

                // Create success response
                String responseJson = String.format(
                        "{\n  \"status\": \"success\",\n  \"message\": \"File created successfully\",\n  \"filepath\": \"%s\"\n}",
                        relativePath);

                return new HttpResponse.Builder(HttpStatus.CREATED_201)
                        .header(HttpHeader.Content_Type, "application/json")
                        .body(responseJson)
                        .build();

            } catch (IOException e) {
                e.printStackTrace();
                return new HttpResponse.Builder(HttpStatus.INTERNAL_SERVER_ERROR_500)
                        .body("500 Internal Server Error: Could not save file.").build();
            }
        });

        return router;
    }
}
