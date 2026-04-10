package com.example.movie_ticket_app.models;

public class Showtime {
    private String id;
    private String movieId;
    private String theaterId;
    private String time;
    private double price;
    private String movieTitle;
    private String theaterName;
    private long startTimeMillis;

    public Showtime() {}

    public Showtime(String id, String movieId, String theaterId, String time, double price) {
        this.id = id;
        this.movieId = movieId;
        this.theaterId = theaterId;
        this.time = time;
        this.price = price;
    }

    public String getId() { return id; }
    public String getMovieId() { return movieId; }
    public String getTheaterId() { return theaterId; }
    public String getTime() { return time; }
    public double getPrice() { return price; }
    public String getMovieTitle() { return movieTitle; }
    public String getTheaterName() { return theaterName; }
    public long getStartTimeMillis() { return startTimeMillis; }

    public void setId(String id) { this.id = id; }
    public void setMovieId(String movieId) { this.movieId = movieId; }
    public void setTheaterId(String theaterId) { this.theaterId = theaterId; }
    public void setTime(String time) { this.time = time; }
    public void setPrice(double price) { this.price = price; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public void setTheaterName(String theaterName) { this.theaterName = theaterName; }
    public void setStartTimeMillis(long startTimeMillis) { this.startTimeMillis = startTimeMillis; }
}
