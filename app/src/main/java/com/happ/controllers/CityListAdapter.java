package com.happ.controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happ.R;
import com.happ.models.City;
import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.util.ArrayList;

/**
 * Created by iztiev on 8/4/16.
 */
public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.CitiesListViewHolder> implements INameableAdapter {
    private ArrayList<City> mCities;
    private ArrayList<String> selectedCity;
    private final Context context;

    public CityListAdapter(Context context, ArrayList<City> cities) {
        this.context = context;
        this.mCities = cities;
    }


    public void updateItems(ArrayList<City> cities) {
        this.mCities = cities;
        notifyDataSetChanged();
    }

    public void updateData(ArrayList<City> cities) {
        this.updateItems(cities);
        Log.d("AAAAA", String.valueOf(cities.size()));
        this.notifyDataSetChanged();
    }


    @Override
    public CitiesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_list_item, parent, false);
        return new CitiesListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CitiesListViewHolder holder, int position) {
        final City city = mCities.get(position);
        String name = city.getName();
        if (name.equals(" ")) {
            name = "empty";
        }
        holder.mTitleCities.setText(city.getName());
        holder.bind(city.getId());
    }

    @Override
    public int getItemCount() {
        return mCities.size();
    }

    @Override
    public Character getCharacterForElement(int element) {
        return mCities.get(element).getName().charAt(0);
    }

    public class CitiesListViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitleCities;

        public CitiesListViewHolder(View itemView) {
            super(itemView);
            mTitleCities = (TextView)itemView.findViewById(R.id.city_title);
        }

        public void bind(final String cityId) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(">>CLICK<<","TRUE");

                }
            });
        }
    }

}