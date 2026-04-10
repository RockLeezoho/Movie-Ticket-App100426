package com.example.movie_ticket_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movie_ticket_app.R;
import com.google.android.material.card.MaterialCardView;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.ViewHolder> {

    public interface OnSeatClickListener {
        void onSeatClicked(String seatCode);
    }

    private final List<String> seats;
    private final OnSeatClickListener listener;
    private final Set<String> bookedSeats = new HashSet<>();
    private String selectedSeat;

    public SeatAdapter(List<String> seats, OnSeatClickListener listener) {
        this.seats = seats;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String seatCode = seats.get(position);
        String normalizedSeat = seatCode == null ? "" : seatCode.trim().toUpperCase(Locale.US);
        holder.seatCode.setText(seatCode);

        boolean isBooked = bookedSeats.contains(normalizedSeat);
        boolean isSelected = seatCode.equals(selectedSeat);
        holder.bookedBadge.setVisibility(isBooked ? View.VISIBLE : View.GONE);
        holder.itemView.setAlpha(isBooked ? 0.35f : 1f);
        holder.itemView.setEnabled(!isBooked);
        holder.itemView.setClickable(!isBooked);
        holder.seatCode.setTextColor(holder.itemView.getContext().getColor(
            isBooked ? android.R.color.darker_gray : (isSelected ? android.R.color.white : R.color.movie_on_surface)
        ));

        if (holder.itemView instanceof MaterialCardView) {
            MaterialCardView cardView = (MaterialCardView) holder.itemView;
            cardView.setStrokeColor(holder.itemView.getContext().getColor(
                    isBooked ? android.R.color.darker_gray : R.color.movie_primary
            ));
            cardView.setCardBackgroundColor(holder.itemView.getContext().getColor(
                    isSelected ? R.color.movie_primary : R.color.movie_surface
            ));
        }

        holder.itemView.setOnClickListener(v -> {
            if (bookedSeats.contains(normalizedSeat)) {
                return;
            }
            selectedSeat = seatCode;
            notifyDataSetChanged();
            listener.onSeatClicked(seatCode);
        });
    }

    @Override
    public int getItemCount() {
        return seats.size();
    }

    public void setSelectedSeat(String seatCode) {
        selectedSeat = seatCode;
        notifyDataSetChanged();
    }

    public void setBookedSeats(List<String> bookedSeats) {
        this.bookedSeats.clear();
        if (bookedSeats != null) {
            for (String seat : bookedSeats) {
                if (seat != null && !seat.trim().isEmpty()) {
                    this.bookedSeats.add(seat.trim().toUpperCase(Locale.US));
                }
            }
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView seatCode;
        final TextView bookedBadge;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            seatCode = itemView.findViewById(R.id.text_seat_code);
            bookedBadge = itemView.findViewById(R.id.text_seat_booked_badge);
        }
    }
}