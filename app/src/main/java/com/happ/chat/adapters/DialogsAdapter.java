package com.happ.chat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happ.R;
import com.happ.chat.model.ChatDialogs;

import java.util.ArrayList;

/**
 * Created by dante on 1/26/17.
 */
public class DialogsAdapter extends RecyclerView.Adapter<DialogsAdapter.DialogsListViewHolder> {

    private ArrayList<ChatDialogs> mChatDialogs;
    private final Context context;

    public DialogsAdapter(Context context, ArrayList<ChatDialogs> chatDialogses) {
        this.context = context;
        mChatDialogs = chatDialogses;
    }

    public void updateData(ArrayList<ChatDialogs> chatDialogses) {
        mChatDialogs = chatDialogses;
        this.notifyDataSetChanged();
    }

    @Override
    public DialogsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_dialog_item, parent, false);
        return new DialogsListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DialogsListViewHolder holder, int position) {
        final ChatDialogs chatDialogs = mChatDialogs.get(position);

        holder.mDialogName.setText(chatDialogs.getName());
        holder.mLastMessageDialog.setText(chatDialogs.getId());
    }

    @Override
    public int getItemCount() {
        return mChatDialogs.size();
    }

    public class DialogsListViewHolder extends RecyclerView.ViewHolder {

        private TextView mDialogName;
        private TextView mLastMessageDialog;

        public DialogsListViewHolder(View itemView) {
            super(itemView);

            mDialogName = (TextView) itemView.findViewById(R.id.text_dialog_name);
            mLastMessageDialog = (TextView) itemView.findViewById(R.id.text_dialog_last_message);
        }
    }
}
