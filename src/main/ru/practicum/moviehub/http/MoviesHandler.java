package ru.practicum.moviehub.http;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.moviehub.model.Movie;
import ru.practicum.moviehub.store.MoviesStore;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MoviesHandler extends BaseHttpHandler {

    private MoviesStore store;

    public MoviesHandler(MoviesStore store) {
        this.store = store;
    }

    @Override
    public void handle(HttpExchange ex) throws IOException {
        String method = ex.getRequestMethod();
        String path = ex.getRequestURI().getPath();

        if (!path.startsWith("/movies")) {
            sendError(ex, 404, "Not Found");
            return;
        }

        if (method.equalsIgnoreCase("GET")) {
            handleGet(ex, path);
        } else if (method.equalsIgnoreCase("POST")) {
            handlePost(ex);
        } else if (method.equalsIgnoreCase("DELETE")) {
            handleDelete(ex, path);
        } else {
            sendError(ex, 405, "Method Not Allowed");
        }
    }

    private void handleGet(HttpExchange ex, String path) throws IOException {
        String query = ex.getRequestURI().getQuery();

        if (query != null && query.startsWith("year=")) {
            try {
                int year = Integer.parseInt(query.replace("year=", ""));
                List<Movie> list = store.getMoviesByYear(year);
                sendJson(ex, 200, list);
            } catch (NumberFormatException e) {
                sendError(ex, 400, "Некорректный параметр запроса — 'year'");
            }
            return;
        }

        if (path.startsWith("/movies/")) {
            try {
                int id = Integer.parseInt(path.replace("/movies/", ""));
                Movie movie = store.getMovieById(id);
                if (movie != null) {
                    sendJson(ex, 200, movie);
                } else {
                    sendError(ex, 404, "Фильм не найден");
                }
            } catch (NumberFormatException e) {
                sendError(ex, 400, "Некорректный ID");
            }
            return;
        }

        List<Movie> all = store.getAllMovies();
        sendJson(ex, 200, all);
    }

    private void handlePost(HttpExchange ex) throws IOException {
        String ct = ex.getRequestHeaders().getFirst("Content-Type");
        if (ct == null || !ct.contains("application/json")) {
            sendError(ex, 415, "Unsupported Media Type");
            return;
        }

        InputStream is = ex.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);

        try {
            Movie incoming = gson.fromJson(body, Movie.class);
            String title = incoming.getTitle();
            int year = incoming.getYear();
            List<String> errors = new ArrayList<>();

            if (title == null || title.trim().isEmpty()) {
                errors.add("название не должно быть пустым");
            } else if (title.length() > 100) {
                errors.add("название не должно превышать 100 символов");
            }
            int curYear = java.time.Year.now().getValue();
            if (year < 1888 || year > curYear + 1) {
                errors.add("год должен быть между 1888 и " + (curYear + 1));
            }

            if (!errors.isEmpty()) {
                sendErrorWithDetails(ex, 422, "Ошибка валидации", errors);
                return;
            }

            Movie m = new Movie(title, year);
            store.addMovie(m);
            sendJson(ex, 201, m);
        } catch (Exception e) {
            sendError(ex, 400, "Некорректный JSON");
        }
    }

    private void handleDelete(HttpExchange ex, String path) throws IOException {
        try {
            int id = Integer.parseInt(path.replace("/movies/", ""));
            if (store.deleteMovie(id)) {
                sendNoContent(ex);
            } else {
                sendError(ex, 404, "Фильм не найден");
            }
        } catch (NumberFormatException e) {
            sendError(ex, 400, "Некорректный ID");
        }
    }
}