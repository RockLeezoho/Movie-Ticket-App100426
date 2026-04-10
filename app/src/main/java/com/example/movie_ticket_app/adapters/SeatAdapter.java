package com.example.movie_ticket_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movie_ticket_app.R;

import java.util.List;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.ViewHolder> {

    public interface OnSeatClickListener {
        void onSeatClicked(String seatCode);
    }

    private final List<String> seats;
    private final OnSeatClickListener listener;
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
        holder.seatCode.setText(seatCode);

        boolean isSelected = seatCode.equals(selectedSeat);
        holder.itemView.setAlpha(1f);
        holder.seatCode.setTextColor(holder.itemView.getContext().getColor(
                isSelected ? android.R.color.white : R.color.movie_on_surface
        ));
        holder.itemView.setBackgroundColor(holder.itemView.getContext().getColor(
                isSelected ? R.color.movie_primary : android.R.color.transparent
        ));

        holder.itemView.setOnClickListener(v -> {
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView seatCode;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            seatCode = itemView.findViewById(R.id.text_seat_code);
        }
    }
}