package com.example.movie_ticket_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.movie_ticket_app.R;
import com.example.movie_ticket_app.models.Ticket;
import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private List<Ticket> ticketList;

    public TicketAdapter(List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);
        holder.movieTitle.setText(ticket.getMovieTitle());
        holder.theaterName.setText(ticket.getTheaterName());
        holder.time.setText("Giờ: " + ticket.getTime());
        holder.seat.setText("Ghế: " + ticket.getSeatNumber());
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView movieTitle, theaterName, time, seat;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            movieTitle = itemView.findViewById(R.id.ticket_movie_title);
            theaterName = itemView.findViewById(R.id.ticket_theater);
            time = itemView.findViewById(R.id.ticket_time);
            seat = itemView.findViewById(R.id.ticket_seat);
        }
    }
}
