package com.happ.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.happ.App;
import com.happ.R;
import com.happ.controllers.EditCreateActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by dante on 10/27/16.
 */
public class EcImagesAdapter extends RecyclerView.Adapter<EcImagesAdapter.EcImagesViewHolder> {

    private ArrayList<EditCreateActivity.EventEditImage> mImagesItems;
    private final Context context;
    private EcItemActionLintener listener;

    public EcImagesAdapter(Context context, ArrayList<EditCreateActivity.EventEditImage> mImagesItems) {
        this.context = context;
        this.mImagesItems = mImagesItems;
    }

    public void setItemActionListener(EcItemActionLintener listener) {
        this.listener = listener;
    }

    @Override
    public EcImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ec_image_item, parent, false);
        return new EcImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final EcImagesViewHolder holder, int position) {
        EditCreateActivity.EventEditImage event = mImagesItems.get(position);

        if (event.isLast()) {
            holder.mProgress.setVisibility(View.GONE);
            holder.mEmptyPlaceholder.setVisibility(View.VISIBLE);
            holder.mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.dashed_bg_darker));
            holder.mDeleteImageButton.setVisibility(View.GONE);
        } else {
            holder.mProgress.setVisibility(View.VISIBLE);
            holder.mEmptyPlaceholder.setVisibility(View.GONE);
            holder.mImageView.setImageDrawable(null);
            holder.mDeleteImageButton.setVisibility(View.GONE);

            if (event.isLocal() && !event.isUploading()) {
                holder.mDeleteImageButton.setVisibility(View.VISIBLE);
                holder.mProgress.setVisibility(View.GONE);
                Bitmap bmp = null;
                try {
                    bmp = MediaStore.Images.Media
                            .getBitmap(this.context.getContentResolver(), event.getUri());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                holder.mImageView.setImageBitmap(bmp);
            }

            if (!event.isLocal()) {
                final String url = event.getImage().getUrl();
                Picasso.with(context)
                    .load(url)
                    .fit()
                    .centerCrop()
                    .into(holder.mImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.mProgress.setVisibility(View.GONE);
                            holder.mDeleteImageButton.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            holder.mProgress.setVisibility(View.GONE);
                            holder.mDeleteImageButton.setVisibility(View.GONE);
                        }
                    });
            }
        }

        holder.bind(position);

    }

    @Override
    public int getItemCount() {
        return mImagesItems.size();
    }

    public void updateList(ArrayList<EditCreateActivity.EventEditImage> imagesList) {
        mImagesItems = imagesList;
        notifyDataSetChanged();
    }

    public class EcImagesViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout mEmptyPlaceholder;
        public ImageView mImageView;
        public ImageButton mDeleteImageButton;
        public ProgressBar mProgress;

        public EcImagesViewHolder(View itemView) {
            super(itemView);
            mEmptyPlaceholder = (LinearLayout) itemView.findViewById(R.id.ec_item_placeholder);
            mImageView = (ImageView) itemView.findViewById(R.id.ec_item_image);
            mDeleteImageButton = (ImageButton) itemView.findViewById(R.id.ec_item_delete);
            mProgress = (ProgressBar) itemView.findViewById(R.id.ec_item_image_preloader);
        }

        public void bind(final int position) {
            mEmptyPlaceholder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) listener.onImageSelectRequested();
                }
            });

            mDeleteImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mImagesItems.get(position).isLast() && listener != null) {
                        listener.onImageDeleteRequested(mImagesItems.get(position).getImageId());
                    }
                }
            });
        }
    }


    public interface EcItemActionLintener {
        void onImageSelectRequested();
        void onImageDeleteRequested(String imageId);
    }

}
