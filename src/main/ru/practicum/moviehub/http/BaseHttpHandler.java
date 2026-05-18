package ru.practicum.moviehub.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.practicum.moviehub.api.ErrorResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class BaseHttpHandler implements HttpHandler {
    protected static final String JSON_TYPE = "application/json; charset=UTF-8";
    protected static final Gson gson = new Gson();

    protected void sendJson(HttpExchange ex, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", JSON_TYPE);
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }

    protected void sendJson(HttpExchange ex, int code, Object obj) throws IOException {
        sendJson(ex, code, gson.toJson(obj));
    }

    protected void sendNoContent(HttpExchange ex) throws IOException {
        ex.sendResponseHeaders(204, -1);
    }

    protected void sendError(HttpExchange ex, int code, String message) throws IOException {
        ErrorResponse resp = new ErrorResponse(message, null);
        sendJson(ex, code, resp);
    }

    protected void sendErrorWithDetails(HttpExchange ex, int code, String error, List<String> details) throws IOException {
        ErrorResponse resp = new ErrorResponse(error, details);
        sendJson(ex, code, resp);
    }
}