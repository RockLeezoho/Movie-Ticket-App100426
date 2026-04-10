package com.example.movie_ticket_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movie_ticket_app.R;
import com.example.movie_ticket_app.models.Payment;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

public class PaymentHistoryAdapter extends RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder> {

    private final List<Payment> paymentList;
    private final OnPaymentClickListener listener;

    public interface OnPaymentClickListener {
        void onPaymentClick(Payment payment);
    }

    public PaymentHistoryAdapter(List<Payment> paymentList, OnPaymentClickListener listener) {
        this.paymentList = paymentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Payment payment = paymentList.get(position);
        holder.movieTitle.setText(payment.getMovieTitle());
        holder.theaterName.setText(payment.getTheaterName());
        holder.seatNumber.setText(String.format(Locale.getDefault(), "Ghế: %s", payment.getSeatNumber()));
        holder.amount.setText(String.format(Locale.getDefault(), "$%.2f", payment.getAmount()));
        holder.method.setText(payment.getPaymentMethod());
        holder.status.setText(payment.getStatus());
        holder.time.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                .format(payment.getCreatedAtMillis()));
        holder.itemView.setOnClickListener(v -> listener.onPaymentClick(payment));
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView movieTitle, theaterName, seatNumber, amount, method, status, time;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            movieTitle = itemView.findViewById(R.id.payment_history_movie_title);
            theaterName = itemView.findViewById(R.id.payment_history_theater);
            seatNumber = itemView.findViewById(R.id.payment_history_seat);
            amount = itemView.findViewById(R.id.payment_history_amount);
            method = itemView.findViewById(R.id.payment_history_method);
            status = itemView.findViewById(R.id.payment_history_status);
            time = itemView.findViewById(R.id.payment_history_time);
        }
    }
}