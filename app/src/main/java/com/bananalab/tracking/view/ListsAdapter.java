package com.bananalab.tracking.view;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bananalab.tracking.R;
import com.bananalab.tracking.model.Tracking;

import java.util.ArrayList;

/**
 * Created by batmaster on 4/5/16 AD.
 */
public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.ViewHolder> {

    private ArrayList<Tracking> trackings;

    public ListsAdapter(ArrayList<Tracking> trackings) {
        this.trackings = trackings;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_tracking, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.textViewTitle.setText(trackings.get(position).getTitle());
        holder.textViewSession.setText(trackings.get(position).getSession());
        holder.textViewDateTime.setText(trackings.get(position).getDatetime());
        holder.textViewDistance.setText(trackings.get(position).getDistance());
    }

    @Override
    public int getItemCount() {
        return trackings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;

        public TextView textViewTitle;
        public TextView textViewSession;
        public TextView textViewDateTime;
        public TextView textViewDistance;

        public ViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.cardView);

            textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
            textViewSession = (TextView) view.findViewById(R.id.textViewSession);
            textViewDateTime = (TextView) view.findViewById(R.id.textViewDateTime);
            textViewDistance = (TextView) view.findViewById(R.id.textViewDistance);
        }
    }
}
