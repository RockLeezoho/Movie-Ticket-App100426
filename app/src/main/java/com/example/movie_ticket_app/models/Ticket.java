package com.example.movie_ticket_app.models;

public class Ticket {
    private String id;
    private String userId;
    private String showtimeId;
    private String paymentId;
    private String movieTitle;
    private String theaterName;
    private String time;
    private String seatNumber;
    private double price;
    private long showtimeMillis;

    public Ticket() {}

    public Ticket(String id, String userId, String showtimeId, String movieTitle, String theaterName, String time, String seatNumber, double price, long showtimeMillis) {
        this(id, userId, showtimeId, null, movieTitle, theaterName, time, seatNumber, price, showtimeMillis);
    }

    public Ticket(String id, String userId, String showtimeId, String paymentId, String movieTitle, String theaterName, String time, String seatNumber, double price, long showtimeMillis) {
        this.id = id;
        this.userId = userId;
        this.showtimeId = showtimeId;
        this.paymentId = paymentId;
        this.movieTitle = movieTitle;
        this.theaterName = theaterName;
        this.time = time;
        this.seatNumber = seatNumber;
        this.price = price;
        this.showtimeMillis = showtimeMillis;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getShowtimeId() { return showtimeId; }
    public String getPaymentId() { return paymentId; }
    public String getMovieTitle() { return movieTitle; }
    public String getTheaterName() { return theaterName; }
    public String getTime() { return time; }
    public String getSeatNumber() { return seatNumber; }
    public double getPrice() { return price; }
    public long getShowtimeMillis() { return showtimeMillis; }

    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setShowtimeId(String showtimeId) { this.showtimeId = showtimeId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public void setTheaterName(String theaterName) { this.theaterName = theaterName; }
    public void setTime(String time) { this.time = time; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public void setPrice(double price) { this.price = price; }
    public void setShowtimeMillis(long showtimeMillis) { this.showtimeMillis = showtimeMillis; }
}
