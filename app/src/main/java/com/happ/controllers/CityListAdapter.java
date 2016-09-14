package com.happ.controllers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
    SelectCityItemListener listener;

    public CityListAdapter(Context context, ArrayList<City> cities) {
        this.context = context;
        this.mCities = cities;
    }

    public void setOnCityItemSelectListener(SelectCityItemListener listener) {
        this.listener = listener;
    }

    public void updateData(ArrayList<City> cities) {
        this.mCities = cities;
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
        if (name.equals("")) {
            name = "empty";
        }
        holder.mTitleCities.setText(city.getName());
        holder.mCountry.setText(city.getCountry());
        holder.bind(city);
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
        public TextView mCountry;

        public CitiesListViewHolder(View itemView) {
            super(itemView);
            mTitleCities = (TextView)itemView.findViewById(R.id.city_title);
            mCountry = (TextView)itemView.findViewById(R.id.country);
        }

        public void bind(final City city) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCityItemSelected(city);
                }
            });
        }
    }

    public interface SelectCityItemListener {
        void onCityItemSelected(City city);
    }

}