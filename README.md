# HTTP Server in Java

## Table of Contents
1. [Project Overview](#project-overview)  
2. [Features](#features)  
3. [Technology Stack](#technology-stack)  
4. [Installation](#installation)  
5. [Usage](#usage)  
6. [Project Structure](#project-structure)  
7. [Core Classes & Modules](#core-classes--modules)  
   - [RouteHandler.java](#routeHandlerjava)  
   - [ConnectionHandler.java](#ConnectionHandlerjava)  
   - [Request & Response](#request--response)  
   - [RequestParser.java](#requestparserjava)  
   - [Header.java & HttpDelimiter.java](#headerjava--HttpDelimiterjava)  
8. [How the Server Works](#how-the-server-works)  
9. [Example Requests & Responses](#example-requests--responses)  
10. [Error Handling](#error-handling)  
11. [Future Enhancements](#future-enhancements)  
12. [Contributing](#contributing)  
13. [License](#license)  

---

## Project Overview
This project is a **custom HTTP server implemented in Java**, designed to handle HTTP requests and responses efficiently. It parses incoming HTTP requests, routes them to the appropriate handler using a custom router, and sends well-structured HTTP responses back to clients.  

It demonstrates core concepts of **network programming, multithreading, request parsing, and response handling** in Java without relying on external frameworks. Perfect for learning or small-scale server applications.

---

## Features
- Handle **HTTP GET and POST requests**.  
- Custom **Router** for routing requests to handlers.  
- Multi-threaded **ConnectionHandler** handling to serve multiple clients simultaneously.  
- Robust **HTTP request parsing** including headers, body, and query parameters.  
- **Structured HTTP responses** with status codes, headers, and body.  
- Modular and clean **OOP design** with separate classes for request, response, parsing, and routing.  
- **Error handling** for invalid requests and server-side exceptions.

---

## Technology Stack
- **Language:** Java (JDK 17+)  
- **Libraries:** Standard Java libraries (`java.io`, `java.net`, `java.util`)  
- **Concepts Covered:**  
  - Networking & Sockets  
  - Multithreading  
  - HTTP Protocol  
  - OOP (Classes, Objects, Encapsulation)  
  - Exception Handling  

---

## Installation
1. **Clone the repository:**
```bash
git clone https://github.com/aryanparakh/http-server-java.git
```
2. **Navigate to the project directory:**
```bash
cd HTTP-Server-in-Java
```
3. **Compile the source code:**
```bash
javac -d out src/com/http/server/*.java src/com/http/model/*.java
```
4. **Run the server:**
```bash
java -cp out com.http.server.Main
```
> Make sure your terminal is pointing to the project root folder.

---

## Usage
Start the server using the above command.  
Open a web browser or use **Postman/cURL** to send requests:

**GET request:**
```bash
curl http://localhost:9090/
```

**POST request:**
```bash
curl -X POST http://localhost:9090/ -d "name=Aryan&project=HTTPServer"
```

The server will process the request and send a structured HTTP response.

---

## Project Structure
```
HTTP-Server-in-Java/
│
├─ src/
│  ├─ com/http/server/
│  │  ├─ Main.java
│  │  ├─ Router.java
│  │  └─ ClientConnection.java
│  │
│  ├─ com/http/model/
│  │  ├─ request/
│  │  │  ├─ HttpRequest.java
│  │  │  └─ HttpRequestParser.java
│  │  ├─ response/
│  │  │  ├─ HttpResponse.java
│  │  │  └─ HttpStatus.java
│  │  └─ common/
│  │     ├─ HttpHeader.java
│  │     └─ Delimiter.java
│  │
│  └─ com/http/exception/
│     └─ InvalidHttpRequestException.java
│
├─ out/          # Compiled .class files
└─ README.md
```

---

## Core Classes & Modules

### Router.java
- Routes incoming HTTP requests to the appropriate handler method.  
- Maps **URL paths** to corresponding logic.  
- Supports **dynamic route handling** and default responses for unmatched paths.

### ClientConnection.java
- Handles a single client connection in a **separate thread**.  
- Reads the HTTP request from the socket, parses it, and sends the response.  
- Ensures **thread-safe handling** for multiple simultaneous clients.

### HttpRequest & HttpResponse
- **HttpRequest.java:** Represents all parts of an HTTP request: method, path, headers, query parameters, and body.  
- **HttpResponse.java:** Represents an HTTP response with status code, headers, and body.  
- Supports **sending formatted responses** according to HTTP protocol.

### HttpRequestParser.java
- Parses raw request strings into **HttpRequest objects**.  
- Validates request format and headers.  
- Throws **InvalidHttpRequestException** for malformed requests.

### HttpHeader.java & Delimiter.java
- **HttpHeader:** Represents a single HTTP header with key-value pair.  
- **Delimiter:** Contains constants for **CRLF, header separation, and line breaks** used in HTTP communication.

---

## How the Server Works
1. **Server starts** and listens on **port 9090**.  
2. For each client connection:
   - A **ClientConnection thread** is spawned.  
   - Reads the raw HTTP request.  
   - Parses the request using **HttpRequestParser**.  
   - Routes the request using **Router**.  
   - Generates **HttpResponse**.  
   - Sends the response back to the client.  
3. **Logging** is provided for requests and errors.

---

## Example Requests & Responses

**GET Request:**
```http
GET /hello HTTP/1.1
Host: localhost:9090
```
**Response:**
```http
HTTP/1.1 200 OK
Content-Type: text/plain
Content-Length: 13

Hello, World!
```

**POST Request:**
```http
POST /data HTTP/1.1
Host: localhost:9090
Content-Type: application/x-www-form-urlencoded
Content-Length: 15

name=Aryan
```
**Response:**
```http
HTTP/1.1 200 OK
Content-Type: text/plain
Content-Length: 12

Data Received
```

---

## Error Handling
- **400 Bad Request:** For malformed HTTP requests.  
- **404 Not Found:** For routes not handled by Router.  
- **500 Internal Server Error:** For unexpected server-side exceptions.  

All errors return **structured HTTP responses** with proper status codes and messages.

---

## Future Enhancements
- Support **HTTP PUT, DELETE, PATCH** methods.  
- Serve **static files** (HTML, CSS, JS).  
- Implement **HTTPS** support using SSL/TLS.  
- Add a **logging framework** for request/response tracking.  
- Enable **configurable routes** through external JSON or YAML.  
- Add **unit tests and integration tests** for reliability.

---

## Contributing
1. Fork the repository.  
2. Create a new branch:
```bash
git checkout -b feature/your-feature
```
3. Make your changes.  
4. Commit:
```bash
git commit -m "Add new feature"
```
5. Push:
```bash
git push origin feature/your-feature
```
6. Open a Pull Request.

---
