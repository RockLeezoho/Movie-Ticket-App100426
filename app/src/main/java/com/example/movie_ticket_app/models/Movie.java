package com.example.movie_ticket_app.models;

import java.util.List;

public class Movie {
    private String id;
    private String title;
    private String description;
    private String posterUrl;
    private String genre;
    private double rating;
    private int duration; // in minutes

    public Movie() {}

    public Movie(String id, String title, String description, String posterUrl, String genre, double rating, int duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.posterUrl = posterUrl;
        this.genre = genre;
        this.rating = rating;
        this.duration = duration;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPosterUrl() { return posterUrl; }
    public String getGenre() { return genre; }
    public double getRating() { return rating; }
    public int getDuration() { return duration; }

    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setRating(double rating) { this.rating = rating; }
    public void setDuration(int duration) { this.duration = duration; }
}
