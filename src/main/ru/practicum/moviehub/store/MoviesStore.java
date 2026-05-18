package ru.practicum.moviehub.store;

import ru.practicum.moviehub.model.Movie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MoviesStore {

    private Map<Integer, Movie> movies = new HashMap<>();
    private AtomicInteger idGenerator = new AtomicInteger(1);

    public List<Movie> getAllMovies() {
        return new ArrayList<>(movies.values());
    }

    public Movie getMovieById(int id) {
        return movies.get(id);
    }

    public Movie addMovie(Movie movie) {
        int newId = idGenerator.getAndIncrement();
        movie.setId(newId);
        movies.put(newId, movie);
        return movie;
    }

    public boolean deleteMovie(int id) {
        return movies.remove(id) != null;
    }

    public List<Movie> getMoviesByYear(int year) {
        return movies.values().stream()
                .filter(m -> m.getYear() == year)
                .collect(Collectors.toList());
    }

    public void clear() {
        movies.clear();
        idGenerator.set(1);
    }
}