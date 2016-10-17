package com.happ.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.happ.App;
import com.happ.R;
import com.happ.models.City;
import com.happ.models.User;
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
    private User currentUser;

    public CityListAdapter(Context context, ArrayList<City> cities) {
        this.context = context;
        this.mCities = cities;
        currentUser = App.getCurrentUser();
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
        String name_city = city.getName();

        if (city.getId().equals(currentUser.getSettings().getCity())) {
            holder.mImageHappIcon.setVisibility(View.VISIBLE);
            holder.mTitleCities.setTextColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            holder.mImageHappIcon.setVisibility(View.INVISIBLE);
            holder.mTitleCities.setTextColor(context.getResources().getColor(R.color.dark57));
        }
        if (name_city.equals("")) {
//             city.getName() = "";
            holder.mTitleCities.setText(context.getResources().getString(R.string.empty_city));
        } else {
            holder.mTitleCities.setText(city.getName());
        }
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
        private ImageView mImageHappIcon;

        public CitiesListViewHolder(View itemView) {
            super(itemView);
            mTitleCities = (TextView)itemView.findViewById(R.id.city_title);
            mImageHappIcon = (ImageView)itemView.findViewById(R.id.city_fr_happ_icon);

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