package com.bananalab.tracking.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bananalab.tracking.R;
import com.bananalab.tracking.model.Tracking;
import com.bananalab.tracking.service.FireBaseHelper;
import com.bananalab.tracking.service.Preferences;

import java.util.ArrayList;

/**
 * Created by batmaster on 4/5/16 AD.
 */
public class ListsAdapter extends RecyclerView.Adapter<ListsAdapter.ViewHolder> {

    private MainActivity activity;
    private Context context;
    private ArrayList<Tracking> trackings;
    private ArrayList<Integer> selected;

    public ListsAdapter(MainActivity activity, ArrayList<Tracking> trackings) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.trackings = trackings;
        selected = new ArrayList<Integer>();
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
                if (selected.size() == 0) {
                    Intent intent = new Intent(context, MapActivity.class);
                    intent.putExtra("t_id", trackings.get(position).getId());
                    activity.startActivity(intent);
                }
                else {
                    if (selected.contains(position)) {
                        selected.remove(new Integer(position));
                        holder.cardView.setCardBackgroundColor(Color.WHITE);
                    }
                    else {
                        selected.add(position);
                        holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                    }

                    activity.listChangedNotify();
                }
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (selected.contains(position)) {
                    selected.remove(new Integer(position));
                    holder.cardView.setCardBackgroundColor(Color.WHITE);
                }
                else {
                    selected.add(position);
                    holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                }

                activity.listChangedNotify();
                return true;
            }
        });
        if (selected.contains(position)) {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorAccent));
        }
        else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
        }

        holder.textViewTitle.setText(trackings.get(position).getSize() + " " + trackings.get(position).getTitle());
        holder.textViewElapse.setText(trackings.get(position).getElapseString());
        holder.textViewDateTime.setText(trackings.get(position).getDate());
        holder.textViewDistance.setText(trackings.get(position).getDistanceString());
        if (trackings.get(position).getDescription() == null || trackings.get(position).getDescription().equals("")) {
            holder.textViewDescription.setVisibility(View.GONE);
        }
        else {
            holder.textViewDescription.setVisibility(View.VISIBLE);
            holder.textViewDescription.setText(trackings.get(position).getDescription());
        }
        if (trackings.get(position).getHasSync() == 0 && trackings.get(position).getId() != Preferences.getInt(context, Preferences.TRACKING_ID_TEMP)) {
            holder.imageViewHasSync.setVisibility(View.VISIBLE);
            holder.imageViewHasSync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FireBaseHelper.saveTracking(context, trackings.get(position).getId(), position);
                }
            });
        }
        else {
            holder.imageViewHasSync.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return trackings.size();
    }

    public ArrayList<Integer> getSelected() {
        return selected;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;

        public TextView textViewTitle;
        public TextView textViewElapse;
        public TextView textViewDateTime;
        public TextView textViewDistance;
        public TextView textViewDescription;
        public ImageView imageViewHasSync;

        public ViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.cardView);

            textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
            textViewElapse = (TextView) view.findViewById(R.id.textViewElapse);
            textViewDateTime = (TextView) view.findViewById(R.id.textViewDateTime);
            textViewDistance = (TextView) view.findViewById(R.id.textViewDistance);
            textViewDescription = (TextView) view.findViewById(R.id.textViewDescription);
            imageViewHasSync = (ImageView) view.findViewById(R.id.imageViewHasSync);
        }
    }
}
