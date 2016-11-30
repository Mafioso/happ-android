package com.happ.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happ.R;
import com.happ.models.EventPhones;

import io.realm.RealmList;

/**
 * Created by dante on 11/30/16.
 */
public class EventPhoneListAdapter extends RecyclerView.Adapter<EventPhoneListAdapter.EventPhonesListViewHolder> {
    private RealmList<EventPhones> eventPhonesArrayList;
    private Context context;

    SelectEventPhoneItemListener listener;

    public interface SelectEventPhoneItemListener {
        void onEventPhoneItemSelected(EventPhones eventPhones);
    }

    public void setOnSelectEventExploreListener(SelectEventPhoneItemListener listener) {
        this.listener = listener;
    }

    public EventPhoneListAdapter(Context context, RealmList<EventPhones> eventPhones) {
        this.context = context;
        this.eventPhonesArrayList = eventPhones;
    }


    @Override
    public EventPhonesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_phones_list_item, parent, false);
        return new EventPhonesListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventPhonesListViewHolder holder, int position) {

        final EventPhones phone = eventPhonesArrayList.get(position);

        holder.mTVPhone.setText(phone.getPhone());
        holder.bind(phone);
    }

    @Override
    public int getItemCount() {
        return eventPhonesArrayList.size();
    }

    public class EventPhonesListViewHolder extends RecyclerView.ViewHolder{

        public TextView mTVPhone;
        public EventPhonesListViewHolder(View itemView) {
            super(itemView);

            mTVPhone = (TextView) itemView.findViewById(R.id.event_phone);
        }

        public void bind(final EventPhones eventPhones) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onEventPhoneItemSelected(eventPhones);
                }
            });
        }
    }
}
