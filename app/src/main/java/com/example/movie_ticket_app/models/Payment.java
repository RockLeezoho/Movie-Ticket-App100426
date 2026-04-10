package com.example.movie_ticket_app.models;

public class Payment {
    private String id;
    private String userId;
    private String ticketId;
    private String showtimeId;
    private String movieTitle;
    private String theaterName;
    private String seatNumber;
    private String paymentMethod;
    private String status;
    private double amount;
    private long createdAtMillis;

    public Payment() {
    }

    public Payment(String id, String userId, String ticketId, String showtimeId, String movieTitle,
                   String theaterName, String seatNumber, String paymentMethod, String status,
                   double amount, long createdAtMillis) {
        this.id = id;
        this.userId = userId;
        this.ticketId = ticketId;
        this.showtimeId = showtimeId;
        this.movieTitle = movieTitle;
        this.theaterName = theaterName;
        this.seatNumber = seatNumber;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.amount = amount;
        this.createdAtMillis = createdAtMillis;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getTicketId() { return ticketId; }
    public String getShowtimeId() { return showtimeId; }
    public String getMovieTitle() { return movieTitle; }
    public String getTheaterName() { return theaterName; }
    public String getSeatNumber() { return seatNumber; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getStatus() { return status; }
    public double getAmount() { return amount; }
    public long getCreatedAtMillis() { return createdAtMillis; }

    public void setId(String id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }
    public void setShowtimeId(String showtimeId) { this.showtimeId = showtimeId; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public void setTheaterName(String theaterName) { this.theaterName = theaterName; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setStatus(String status) { this.status = status; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCreatedAtMillis(long createdAtMillis) { this.createdAtMillis = createdAtMillis; }
}