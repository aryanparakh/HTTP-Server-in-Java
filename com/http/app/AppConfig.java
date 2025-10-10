package com.http.app;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import com.http.model.common.Header;
import com.http.model.request.HttpVerb;
import com.http.model.response.Response;
import com.http.model.response.Status;
import com.http.routing.StaticFileHandler;
import com.http.routing.RouteManager;

public class AppConfig 
{
    private static final String RESOURCES_DIR = "../../resources";
    private static final String UPLOADS_DIR = RESOURCES_DIR + "/uploads";

    public RouteManager configureRouter() 
    {
        RouteManager applicationRouter = new RouteManager();

        // Default GET handler to serve static files
        applicationRouter.setDefaultGetHandler(new StaticFileHandler());

        // Custom GET route for home page (so browser shows a message)
        applicationRouter.addRoute(HttpVerb.GET, "/", request -> {
            return new Response.Builder(Status.OK_200)
                    .body("✅ Server is running! Use POST /upload to upload JSON files.")
                    .build();
        });

        // Custom POST route for uploading JSON files
        applicationRouter.addRoute(HttpVerb.POST, "/upload", request -> handleUpload(request));

        return applicationRouter;
    }

    private Response handleUpload(com.http.model.request.Request request) 
    {
        String contentType = request.getHeaders().get(Header.Content_Type);
        if (contentType == null || !contentType.equalsIgnoreCase("application/json")) 
        {
            return jsonError(Status.UNSUPPORTED_MEDIA_TYPE_415, 
                    "Content-Type must be application/json");
        }

        Optional<String> bodyOpt = request.getBody();
        if (bodyOpt.isEmpty() || bodyOpt.get().trim().isEmpty()) 
        {
            return jsonError(Status.BAD_REQUEST_400, "JSON body is missing or empty");
        }

        String jsonContent = bodyOpt.get().trim();
        if (!jsonContent.startsWith("{") || !jsonContent.endsWith("}")) 
        {
            return jsonError(Status.BAD_REQUEST_400, "Invalid JSON format");
        }

        return saveUploadedFile(jsonContent);
    }

    private Response saveUploadedFile(String jsonData) 
    {
        try 
        {
            Files.createDirectories(Paths.get(UPLOADS_DIR));

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String uniqueId = java.util.UUID.randomUUID().toString().substring(0, 4);
            String fileName = "upload_" + timeStamp + "_" + uniqueId + ".json";

            String relativePath = "/uploads/" + fileName;
            String absolutePath = UPLOADS_DIR + "/" + fileName;

            Files.write(Paths.get(absolutePath), jsonData.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE_NEW);

            System.out.println("File uploaded successfully: " + relativePath);

            String successJson = String.format(
                    "{ \"status\": \"success\", \"message\": \"File created successfully\", \"filepath\": \"%s\" }",
                    relativePath);

            return new Response.Builder(Status.CREATED_201)
                    .header(Header.Content_Type, "application/json")
                    .body(successJson)
                    .build();

        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            return jsonError(Status.INTERNAL_SERVER_ERROR_500, "Could not save file");
        }
    }

    private Response jsonError(Status status, String message) 
    {
        String json = String.format("{ \"status\": \"error\", \"message\": \"%s\" }", message);
        return new Response.Builder(status)
                .header(Header.Content_Type, "application/json")
                .body(json)
                .build();
    }
}
