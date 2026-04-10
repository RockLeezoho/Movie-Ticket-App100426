package com.example.movie_ticket_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movie_ticket_app.R;
import com.example.movie_ticket_app.models.Showtime;
import java.util.List;

public class ShowtimeAdapter extends RecyclerView.Adapter<ShowtimeAdapter.ShowtimeViewHolder> {

    private List<Showtime> showtimeList;
    private OnShowtimeClickListener listener;

    public interface OnShowtimeClickListener {
        void onShowtimeClick(Showtime showtime);
    }

    public ShowtimeAdapter(List<Showtime> showtimeList, OnShowtimeClickListener listener) {
        this.showtimeList = showtimeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShowtimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_showtime, parent, false);
        return new ShowtimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowtimeViewHolder holder, int position) {
        Showtime showtime = showtimeList.get(position);
        holder.btnShowtime.setText(showtime.getTime());
        holder.btnShowtime.setOnClickListener(v -> listener.onShowtimeClick(showtime));
    }

    @Override
    public int getItemCount() {
        return showtimeList.size();
    }

    static class ShowtimeViewHolder extends RecyclerView.ViewHolder {
        Button btnShowtime;

        public ShowtimeViewHolder(@NonNull View itemView) {
            super(itemView);
            btnShowtime = itemView.findViewById(R.id.btn_showtime);
        }
    }
}
