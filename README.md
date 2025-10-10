# **HTTP Server in Java**

A lightweight, multi-threaded HTTP/1.1 server built from scratch in Java. This project demonstrates core concepts of network programming, including socket communication, request parsing, routing, and concurrent request handling using a thread pool. It's designed to be simple, extensible, and educational.

## **✨ Features**

* **Multi-threaded Architecture:** Utilizes a fixed-size thread pool to handle multiple client connections concurrently, ensuring responsiveness under load.  
* **HTTP/1.1 Keep-Alive:** Supports persistent connections to reduce latency by allowing multiple requests and responses over a single TCP connection.  
* **Static File Serving:** Serves static files (HTML, text, images) from a designated resources directory.  
* **Dynamic Routing:** A flexible routing system to map specific HTTP methods and URL paths to custom handlers.  
* **POST Request Handling:** Capable of processing POST requests, including reading the request body. Includes an example endpoint for JSON file uploads.  
* **Robust Error Handling:** Gracefully handles common errors, sending appropriate HTTP status codes like 400 Bad Request, 404 Not Found, and 500 Internal Server Error.  
* **Security:** Includes basic security measures to prevent path traversal attacks.  
* **Customizable Configuration:** Server host, port, and thread pool size can be easily configured via command-line arguments.

## **📂 Project Structure**

The project is organized into logical packages to separate concerns:  
com/http/  
├── app/            \# Contains the main application logic and route configuration.  
│   └── Application.java  
├── exception/      \# Custom exceptions for request handling.  
│   └── InvalidHttpRequestException.java  
├── model/          \# Data models for requests, responses, headers, and status codes.  
│   ├── common/  
│   ├── request/  
│   └── response/  
├── protocol/       \# Logic for parsing the HTTP protocol.  
│   └── HttpRequestParser.java  
├── routing/        \# Classes for handling routing and file serving.  
│   ├── FileHandler.java  
│   ├── RouteHandler.java  
│   └── Router.java  
├── server/         \# Core server and client connection management.  
│   ├── ClientConnection.java  
│   └── HttpServer.java  
└── Main.java       \# Entry point for the application.

## **🚀 Getting Started**

Follow these instructions to get the server up and running on your local machine.

### **Prerequisites**

* Java Development Kit (JDK) 11 or higher.

### **Compilation**

1. Navigate to the root directory of the project (the one containing the com folder).  
2. Compile all the Java source files using the following command:  
   javac com/http/\*.java com/http/app/\*.java com/http/exception/\*.java com/http/model/common/\*.java com/http/model/request/\*.java com/http/model/response/\*.java com/http/protocol/\*.java com/http/routing/\*.java com/http/server/\*.java

### **Running the Server**

1. Create a resources directory in the same root directory. This is where the server will look for static files.  
   mkdir resources

2. Add some files to it, for example, an index.html file:  
   echo "\<h1\>Hello, World\!\</h1\>" \> resources/index.html

3. Run the server from the root directory using the java command:  
   java com.http.Main

4. The server will start with default settings. You can now access it at http://127.0.0.1:8080 in your web browser.

### **Command-Line Arguments**

You can customize the server's configuration by providing command-line arguments:  
java com.http.Main \[PORT\] \[HOST\] \[THREAD\_POOL\_SIZE\]

* PORT: The port number the server will listen on (Default: 8080).  
* HOST: The host address to bind to (Default: 127.0.0.1).  
* THREAD\_POOL\_SIZE: The number of threads in the worker pool (Default: 10).

**Example:**  
\# Run the server on port 3000 with 20 threads  
java com.http.Main 3000 127.0.0.1 20

## **🛠️ Usage**

### **1\. Serving Static Files**

Place any static files (.html, .txt, .png, etc.) inside the resources directory. The server will automatically serve them.

* **Root:** Accessing http://127.0.0.1:8080/ will serve resources/index.html.  
* **Other Files:** Accessing http://127.0.0.1:8080/my-image.png will serve resources/my-image.png.

### **2\. Uploading a JSON File**

The server has a pre-configured endpoint at POST /upload that accepts a JSON body and saves it as a file in resources/uploads/.  
You can test this endpoint using a tool like curl:  
curl \-X POST \\  
  \[http://127.0.0.1:8080/upload\](http://127.0.0.1:8080/upload) \\  
  \-H 'Content-Type: application/json' \\  
  \-d '{  
    "username": "test",  
    "data": "This is some example data"  
  }'

**Expected Response (201 Created):**  
{  
  "status": "success",  
  "message": "File created successfully",  
  "filepath": "/uploads/upload\_20231027\_103000\_abcd.json"  
}

## **⚙️ How It Works**

1. **Initialization:** The main method initializes the HttpServer with a specified port, host, a Router instance, and a thread pool size.  
2. **Listening:** The HttpServer creates a ServerSocket and enters an infinite loop, waiting for client connections on the specified port.  
3. **Connection Handling:** When a client connects, the ServerSocket.accept() method returns a Socket. This socket is passed to a new ClientConnection task, which is immediately submitted to the ExecutorService (the thread pool).  
4. **Request Parsing:** Inside the ClientConnection thread, the HttpRequestParser reads the input stream from the socket. It parses the request line (e.g., GET /index.html HTTP/1.1), headers, and body (if present) into an immutable HttpRequest object.  
5. **Routing:** The HttpRequest object is passed to the Router. The router looks up the combination of the HTTP verb and the resource path in its routes map.  
   * If a specific handler is found, it is executed.  
   * If it's a GET request with no specific handler, the FileHandler is used as a default to serve a static file.  
   * If no handler matches, an appropriate error response (404 Not Found or 405 Method Not Allowed) is generated.  
6. **Response Generation:** The handler creates an HttpResponse object using a builder pattern. The builder automatically adds required headers like Date, Server, and Content-Length.  
7. **Sending Response:** The ClientConnection serializes the HttpResponse object back into the standard HTTP response format and writes it to the socket's output stream.  
8. **Connection Management:** If the client requested a keep-alive connection, the server keeps the socket open for subsequent requests until a timeout or a max request limit is reached. Otherwise, the connection is closed.