package com.happ.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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


    public ExploreListAdapter(Context context, ArrayList<Event> events) {
        this.context = context;
        mEvents = events;
    }

    public void setOnSelectEventExploreListener(SelectEventExploreItemListener listener) {
        this.listener = listener;
    }

    public void updateData(ArrayList<Event> events) {
        mEvents = events;
        Log.d("AAAAA", String.valueOf(events.size()));
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


            if(events.getImages().size() > 0){
                final String url = events.getImages().get(0).getUrl();

                Glide.clear(holder.mImageView);
                try {
                    int viewWidth = holder.mImageView.getWidth();
                    int viewHeight = holder.mImageView.getHeight();
                    if (viewHeight > 0 && viewHeight > 0) {
                        Glide.with(App.getContext())
                                .load(url)
                                .override(viewWidth, viewHeight)
                                .centerCrop()
                                .into(holder.mImageView);
                    }
                } catch (Exception ex) {
                    ViewTreeObserver viewTreeObserver = holder.mImageView.getViewTreeObserver();
                    if (viewTreeObserver.isAlive()) {
                        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                holder.mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                int viewWidth = holder.mImageView.getWidth();
                                int viewHeight = holder.mImageView.getHeight();
                                Log.d("HEIGHT_WIDTH", String.valueOf(viewWidth)+" "+String.valueOf(viewHeight));

                                Glide.with(App.getContext())
                                        .load(url)
                                        .override(viewWidth, viewHeight)
                                        .centerCrop()
                                        .into(holder.mImageView);
                            }
                        });
                    }
                }
            } else{
                Glide.clear(holder.mImageView);
                holder.mImageView.setImageDrawable(null);
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

        public TextView mTextView;
        public ImageView mImageView;

        public ExploreListViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.explore_textview);
            mImageView = (ImageView) itemView.findViewById(R.id.imageview_explore);

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
