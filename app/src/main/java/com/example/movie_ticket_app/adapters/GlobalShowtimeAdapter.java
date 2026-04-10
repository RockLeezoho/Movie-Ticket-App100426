package com.example.movie_ticket_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movie_ticket_app.R;
import com.example.movie_ticket_app.models.Showtime;

import java.util.Locale;
import java.util.List;

public class GlobalShowtimeAdapter extends RecyclerView.Adapter<GlobalShowtimeAdapter.ViewHolder> {

    private List<Showtime> showtimeList;

    public GlobalShowtimeAdapter(List<Showtime> showtimeList) {
        this.showtimeList = showtimeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_global_showtime, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Showtime showtime = showtimeList.get(position);
        holder.movieTitle.setText(showtime.getMovieTitle() == null ? "Unknown movie" : showtime.getMovieTitle());
        holder.theaterName.setText(showtime.getTheaterName() == null ? "Unknown theater" : showtime.getTheaterName());
        holder.time.setText(showtime.getTime());
        holder.price.setText(String.format(Locale.getDefault(), "$%.2f", showtime.getPrice()));
    }

    @Override
    public int getItemCount() {
        return showtimeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView movieTitle, theaterName, time, price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            movieTitle = itemView.findViewById(R.id.gs_movie_title);
            theaterName = itemView.findViewById(R.id.gs_theater_name);
            time = itemView.findViewById(R.id.gs_time);
            price = itemView.findViewById(R.id.gs_price);
        }
    }
}
