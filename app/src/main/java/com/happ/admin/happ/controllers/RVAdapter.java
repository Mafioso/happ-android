package com.happ.admin.happ.controllers;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happ.admin.happ.R;
import com.happ.admin.happ.models.Events;

import java.util.ArrayList;


/**
 * Created by dante on 8/2/16.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.EventsViewHolder> {
    public static class EventsViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView tw;
        EventsViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            tw = (TextView)itemView.findViewById(R.id.textView2);
        }
    }

    public ArrayList<Events> events;
    RVAdapter(ArrayList<Events> events) {
        this.events = events;
    }

    public void updateData(ArrayList<Events> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public EventsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        EventsViewHolder evh = new EventsViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(EventsViewHolder personViewHolder, int i) {
            personViewHolder.tw.setText(events.get(i).getTitle());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }
}
