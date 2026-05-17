package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    protected static final String JSON_TYPE = "application/json; charset=UTF-8";

    protected void sendJson(HttpExchange ex, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", JSON_TYPE);
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    protected void sendNoContent(HttpExchange ex) throws IOException {
        ex.sendResponseHeaders(204, -1);
    }

    protected void sendError(HttpExchange ex, int code, String message) throws IOException {
        String errorJson = "{\"error\": \"" + message + "\"}";
        sendJson(ex, code, errorJson);
    }

    protected void sendErrorWithDetails(HttpExchange ex, int code, String error, java.util.List<String> details) throws IOException {
        ru.practicum.moviehub.api.ErrorResponse resp = new ru.practicum.moviehub.api.ErrorResponse(error, details);
        sendJson(ex, code, resp.toJson());
    }
}