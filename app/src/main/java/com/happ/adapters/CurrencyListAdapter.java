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
import com.happ.models.Currency;
import com.happ.models.User;
import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.util.ArrayList;

/**
 * Created by iztiev on 8/4/16.
 */
public class CurrencyListAdapter extends RecyclerView.Adapter<CurrencyListAdapter.CurrenciesListViewHolder> implements INameableAdapter {
    private ArrayList<Currency> mCurrency;
    private final Context context;
    private User currentUser;

    public CurrencyListAdapter(Context context, ArrayList<Currency> currencies) {
        this.context = context;
        this.mCurrency = currencies;
        currentUser = App.getCurrentUser();
    }

    public void updateData(ArrayList<Currency> currencies) {
        this.mCurrency = currencies;
        this.notifyDataSetChanged();
    }

    public void updateData() {
        this.notifyDataSetChanged();
    }

    @Override
    public CurrenciesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // ?????????????
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_list_item, parent, false);
        return new CurrenciesListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CurrenciesListViewHolder holder, int position) {
        final Currency currency = mCurrency.get(position);

        if (currency.getId().equals(currentUser.getSettings().getCurrency())) {
            holder.mImageHappIcon.setVisibility(View.VISIBLE);
            holder.mTitleCurrencies.setTextColor(context.getResources().getColor(R.color.colorAccent));
        } else {
            holder.mImageHappIcon.setVisibility(View.INVISIBLE);
            holder.mTitleCurrencies.setTextColor(context.getResources().getColor(R.color.dark57));
        }

            holder.mTitleCurrencies.setText(currency.getName());

        holder.bind(currency);
    }

    @Override
    public int getItemCount() {
        return mCurrency.size();
    }

    @Override
    public Character getCharacterForElement(int element) {
        return mCurrency.get(element).getName().charAt(0);
    }



    public class CurrenciesListViewHolder extends RecyclerView.ViewHolder {

        public TextView mTitleCurrencies;
        private ImageView mImageHappIcon;

        public CurrenciesListViewHolder(View itemView) {
            super(itemView);
            mTitleCurrencies = (TextView)itemView.findViewById(R.id.city_title);
            mImageHappIcon = (ImageView)itemView.findViewById(R.id.city_fr_happ_icon);

        }

        public void bind(final Currency currency) {

        }
    }

}