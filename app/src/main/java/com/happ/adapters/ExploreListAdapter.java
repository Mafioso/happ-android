package com.happ.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.happ.App;
import com.happ.R;
import com.happ.Typefaces;
import com.happ.models.Event;

import java.util.ArrayList;

/**
 * Created by iztiev on 8/4/16.
 */
public class ExploreListAdapter extends RecyclerView.Adapter<ExploreListAdapter.ExploreListViewHolder> {
    private ArrayList<Event> mEvents;
    private final Context context;
    SelectEventExploreItemListener listener;

    private String[] urls = {
            "http://www.freedigitalphotos.net/images/img/homepage/87357.jpg",
            "http://assets.barcroftmedia.com.s3-website-eu-west-1.amazonaws.com/assets/images/recent-images-11.jpg",
            "http://7606-presscdn-0-74.pagely.netdna-cdn.com/wp-content/uploads/2016/03/Dubai-Photos-Images-Oicture-Dubai-Landmarks-800x600.jpg",
            "http://www.gettyimages.ca/gi-resources/images/Homepage/Hero/UK/CMS_Creative_164657191_Kingfisher.jpg",
            "http://www.w3schools.com/css/trolltunga.jpg",
            "http://i164.photobucket.com/albums/u8/hemi1hemi/COLOR/COL9-6.jpg",
            "http://www.planwallpaper.com/static/images/desktop-year-of-the-tiger-images-wallpaper.jpg",
            "http://www.gettyimages.pt/gi-resources/images/Homepage/Hero/PT/PT_hero_42_153645159.jpg",
            "http://www.planwallpaper.com/static/images/beautiful-sunset-images-196063.jpg",
            "http://www.w3schools.com/css/img_fjords.jpg"
    };

    public ExploreListAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        mEvents = events;
    }

    public void setOnSelectEventExploreListener(SelectEventExploreItemListener listener) {
        this.listener = listener;
    }

    public void updateData(ArrayList<Event> events) {
        mEvents = events;
        Log.d("EXPLORE ADAPTER", String.valueOf(events.size()));
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public ExploreListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_explore_item, parent, false);
        return new ExploreListViewHolder(view);
    }


//    public static void main(String... args) {
//        HashMap<String, String> companyDetails = new HashMap<String, String>();
//
//        // create hashmap with keys and values (CompanyName, #Employees)
//        companyDetails.put("http://www.kartinki24.ru/uploads/gallery/main/1/kartinki24_3d_0084.jpg", "#E5E500");
//        companyDetails.put("http://fotohomka.ru/images/Dec/12/5641e5041c64f57f768f779826bcdcf3/mini_3.jpg", "#E30026");
//        companyDetails.put("http://www.images.lesyadraw.ru/2015/03/risuem_sinego_kotenka7.jpg", "#D4D4D4");
//        companyDetails.put("http://kartinki-risunki.ru/sites/kartinki-risunki.ru/files/images/66/24199.jpg", "#57E46C");
//        companyDetails.put("https://okartinkah.ru/img/krasivye-kartinki-dlya-rabochego-stola-1946/krasivye-kartinki-dlya-rabochego-stola-7.jpg", "#AE03D0");
//
//        Iterator it = companyDetails.entrySet().iterator();
//        while (it.hasNext()) {
//            final Map.Entry pairs = (Map.Entry) it.next();
////            String url = pairs.getKey().toString();
////            String colorCode = pairs.getValue().toString()
//        }
//    }

    @Override
    public void onBindViewHolder(final ExploreListViewHolder holder, int position) {
        final Event events = mEvents.get(position);


//            if(events.getImages().size() > 0){
//                final String url = events.getImages().get(0).getUrl();
//
//                Glide.clear(holder.mImageView);
//                try {
//                    int viewWidth = holder.mImageView.getWidth();
//                    int viewHeight = holder.mImageView.getHeight();
//                    if (viewHeight > 0 && viewHeight > 0) {
//                        Glide.with(App.getContext())
//                                .load(url)
//                                .override(viewWidth, viewHeight)
//                                .centerCrop()
//                                .into(holder.mImageView);
//                    }
//                } catch (Exception ex) {
//                    ViewTreeObserver viewTreeObserver = holder.mImageView.getViewTreeObserver();
//                    if (viewTreeObserver.isAlive()) {
//                        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                            @Override
//                            public void onGlobalLayout() {
//                                holder.mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                                int viewWidth = holder.mImageView.getWidth();
//                                int viewHeight = holder.mImageView.getHeight();
//                                Log.d("HEIGHT_WIDTH", String.valueOf(viewWidth)+" "+String.valueOf(viewHeight));
//
//                                Glide.with(App.getContext())
//                                        .load(url)
//                                        .override(viewWidth, viewHeight)
//                                        .centerCrop()
//                                        .into(holder.mImageView);
//                            }
//                        });
//                    }
//                }
//            } else{
//                Glide.clear(holder.mImageView);
//                holder.mImageView.setImageDrawable(null);
//            }

        final String url = urls[position%10];

        if (holder.url == null || !holder.url.equals(url)) {
            holder.url = url;
            Glide.clear(holder.mImageView);
            try {
                ViewTreeObserver viewTreeObserver = holder.mImageView.getViewTreeObserver();
                if (viewTreeObserver.isAlive()) {
                    viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            holder.mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            int viewWidth = holder.mImageView.getWidth();
                            int viewHeight = holder.mImageView.getHeight();
                            Log.d("HEIGHT_WIDTH", String.valueOf(viewWidth) + " " + String.valueOf(viewHeight));

                            Glide.with(App.getContext())
                                    .load(url)
                                    .listener(new RequestListener<String, GlideDrawable>() {
                                        @Override
                                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                            Log.e("GLIDE_ERR", url + " " + e.getMessage());
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                            Log.d("GLIDE_OK", url);
                                            Bitmap bm = ((GlideBitmapDrawable)resource.getCurrent()).getBitmap();
                                            Palette p = Palette.from(bm).generate();
                                            holder.mBackground.setBackgroundColor(p.getMutedSwatch().getRgb());
//                                            setBlurs(holder,position);
                                            return false;
                                        }
                                    })
                                    .override(viewWidth, viewHeight)
                                    .centerCrop()
                                    .into(holder.mImageView);

//                            final Drawable image = holder.mImageView.getDrawable();
//                            if (holder.mImageView.getViewTreeObserver().isAlive()) {
//                                holder.mImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                                    @Override
//                                    public void onGlobalLayout() {
//                                        if (image != null && !image.equals(holder.mImageView.getDrawable())) {
//                                            holder.mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                                            setBlurs(holder, position);
//                                        }
//                                    }
//                                });
//
//                            }
                        }
                    });
                }
            } catch (Exception ex) {
                Log.e("INTERESTS", ex.getLocalizedMessage());
            }
        }

        holder.mTextView.setText(events.getTitle());

//            holder.mTextView.setTextColor(Color.parseColor(colorCode));
            Typeface tfcs = Typefaces.get(App.getContext(), "fonts/WienLight_Normal.ttf");
            holder.mTextView.setTypeface(tfcs);

            holder.bind(events);
    }


    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public class ExploreListViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;
        private ImageView mImageView;
        private String url;
        private LinearLayout mBackground;

        public ExploreListViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.explore_textview);
            mImageView = (ImageView) itemView.findViewById(R.id.imageview_explore);
            mBackground = (LinearLayout) itemView.findViewById(R.id.explore_item_bg);

        }

        public void bind(final Event event) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onExploreEventItemSelected(event);
                }
            });
        }
    }

    public interface SelectEventExploreItemListener {
        void onExploreEventItemSelected(Event event);
    }

}
