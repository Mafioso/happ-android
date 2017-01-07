package com.happ.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happ.R;
import com.happ.models.RejectionReason;

import io.realm.RealmList;

/**
 * Created by iztiev on 8/4/16.
 */
public class RejectionReasonsListAdapter extends RecyclerView.Adapter<RejectionReasonsListAdapter.RejectionReasonsListViewHolder> {
    private RealmList<RejectionReason> mRejectionReasons;
    private final Context context;


    public RejectionReasonsListAdapter(Context context, RealmList<RejectionReason> rejectionReasonses) {
        this.context = context;
        this.mRejectionReasons = rejectionReasonses;
    }

    public void updateData(RealmList<RejectionReason> rejectionReasonses) {
        this.mRejectionReasons = rejectionReasonses;
        this.notifyDataSetChanged();
    }

    @Override
    public RejectionReasonsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_rejection_reason_list_item, parent, false);
        return new RejectionReasonsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RejectionReasonsListViewHolder holder, int position) {
        final RejectionReason rejectionReason = mRejectionReasons.get(position);
        holder.mTextViewRejectionReasons.setText(rejectionReason.getText());
    }

    @Override
    public int getItemCount() {
        return mRejectionReasons.size();
    }


    public class RejectionReasonsListViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextViewRejectionReasons;

        public RejectionReasonsListViewHolder(View itemView) {
            super(itemView);

            mTextViewRejectionReasons = (TextView) itemView.findViewById(R.id.event_rejection_reason_item_text);

        }

    }

}