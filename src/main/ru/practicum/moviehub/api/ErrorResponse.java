package ru.practicum.moviehub.api;

import com.google.gson.Gson;
import java.util.List;

public class ErrorResponse {

    private static final Gson gson = new Gson();
    private String error;
    private List<String> details;

    public ErrorResponse() {
    }

    public ErrorResponse(String error, List<String> details) {
        this.error = error;
        this.details = details;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public String toJson() {
        return gson.toJson(this);
    }
}