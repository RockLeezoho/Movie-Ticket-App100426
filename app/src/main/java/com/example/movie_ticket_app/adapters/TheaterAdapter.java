package com.example.movie_ticket_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movie_ticket_app.R;
import com.example.movie_ticket_app.models.Theater;
import java.util.List;

public class TheaterAdapter extends RecyclerView.Adapter<TheaterAdapter.TheaterViewHolder> {

    private List<Theater> theaterList;

    public TheaterAdapter(List<Theater> theaterList) {
        this.theaterList = theaterList;
    }

    @NonNull
    @Override
    public TheaterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_theater, parent, false);
        return new TheaterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TheaterViewHolder holder, int position) {
        Theater theater = theaterList.get(position);
        holder.name.setText(theater.getName());
        holder.location.setText(theater.getLocation());
    }

    @Override
    public int getItemCount() {
        return theaterList.size();
    }

    static class TheaterViewHolder extends RecyclerView.ViewHolder {
        TextView name, location;

        public TheaterViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.theater_name);
            location = itemView.findViewById(R.id.theater_location);
        }
    }
}
