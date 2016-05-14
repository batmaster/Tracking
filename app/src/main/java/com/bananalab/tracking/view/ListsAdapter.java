package com.bananalab.tracking.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bananalab.tracking.R;
import com.bananalab.tracking.model.Tracking;
import com.bananalab.tracking.service.Preferences;

import java.util.ArrayList;

/**
 * Created by batmaster on 4/5/16 AD.
 */
public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Tracking> trackings;

    public ListsAdapter(Context context, ArrayList<Tracking> trackings) {
        this.context = context;
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

        holder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapActivity.class);
                intent.putExtra("t_id", Preferences.getInt(context, Preferences.TRACKING_ID_TEMP));
                context.startActivity(intent);
            }
        });

        holder.textViewTitle.setText(trackings.get(position).getTitle());
        holder.textViewElapse.setText(trackings.get(position).getElapseString());
        holder.textViewDateTime.setText(trackings.get(position).getDate());
        holder.textViewDistance.setText(trackings.get(position).getDistanceString());
    }

    @Override
    public int getItemCount() {
        return trackings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;

        public TextView textViewTitle;
        public TextView textViewElapse;
        public TextView textViewDateTime;
        public TextView textViewDistance;

        public ViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.cardView);

            textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
            textViewElapse = (TextView) view.findViewById(R.id.textViewElapse);
            textViewDateTime = (TextView) view.findViewById(R.id.textViewDateTime);
            textViewDistance = (TextView) view.findViewById(R.id.textViewDistance);
        }
    }
}
