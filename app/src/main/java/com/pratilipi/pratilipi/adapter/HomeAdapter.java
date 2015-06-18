package com.pratilipi.pratilipi.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.pratilipi.pratilipi.AppController;
import com.pratilipi.pratilipi.DataFiles.Metadata;
import com.pratilipi.pratilipi.DetailPageActivity;
import com.pratilipi.pratilipi.R;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by MOHIT KHAITAN on 18-06-2015.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.DataViewHolder> {

    List<Metadata> metadata;
    ViewGroup mViewGroup;

    public HomeAdapter(List<Metadata> metadata){
        this.metadata = metadata;
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        this.mViewGroup = viewGroup;
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.viewitem, viewGroup, false);
        DataViewHolder mDataViewHolder = new DataViewHolder(v);

        return mDataViewHolder;
    }

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    @Override
    public void onBindViewHolder(DataViewHolder holder, int position) {

        final Context context = mViewGroup.getContext();
        final Metadata metadataObj = metadata.get(position);
        holder.bookCover.setImageUrl("http:" + metadataObj.get_coverImageUrl(), imageLoader);

        holder.freeButton.setText("  FREE!");

        if (metadataObj.get_ratingCount() > 0) {

            float val = (float) metadataObj.get_starCount() / metadataObj.get_ratingCount();
            if (val != 0.0) {
                holder.mRatingBar.setRating(val);

                NumberFormat numberformatter = NumberFormat.getNumberInstance();
                numberformatter.setMaximumFractionDigits(1);
                numberformatter.setMinimumFractionDigits(1);
                String rating = numberformatter.format(val);

//                holder.ratingCount.setText(String.valueOf("("+metadataObj.get_ratingCount() + " ratings)"));
//                holder.avgeragerating.setText("Average rating: " + rating + "/5");

            }else{
                Log.d("Val is Null", "");
            }
        }

        holder.mHomeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, DetailPageActivity.class);
                i.putExtra(DetailPageActivity.METADATA, (Serializable) metadataObj);
                context.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return metadata.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder{

        CardView mHomeCardView;
        NetworkImageView bookCover;
        RatingBar mRatingBar;
        TextView bookTitle;
        TextView authorName;
        TextView ratingCount;
        TextView avgeragerating;
        TextView freeButton;

        public DataViewHolder(View itemView) {
            super(itemView);

            mHomeCardView = (CardView)itemView.findViewById(R.id.home_card_view);
            bookCover = (NetworkImageView)itemView.findViewById(R.id.image);
            mRatingBar = (RatingBar)itemView.findViewById(R.id.averageRatingRatingBar);
            bookTitle = (TextView)itemView.findViewById(R.id.titleTextViewMoreFeatured);
            authorName = (TextView)itemView.findViewById(R.id.authorTextViewMoreFeatured);
            ratingCount = (TextView)itemView.findViewById(R.id.featuredPageRatingNumber);
            avgeragerating = (TextView)itemView.findViewById(R.id.averageRatingTextView);
            freeButton = (TextView)itemView.findViewById(R.id.freeBtn);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
