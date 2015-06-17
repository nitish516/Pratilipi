package com.pratilipi.pratilipi.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by MOHIT KHAITAN on 17-06-2015.
 */
public class CardListAdapter extends RecyclerView.Adapter<CardListAdapter.DataViewHolder> {

    List<Metadata> metadata;
    ViewGroup mViewGroup;

    public CardListAdapter(List<Metadata> metadata){
        this.metadata = metadata;
    }

    @Override
    public DataViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        this.mViewGroup = viewGroup;
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
        DataViewHolder mDataViewHolder = new DataViewHolder(v);

        return mDataViewHolder;
    }

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    @Override
    public void onBindViewHolder(DataViewHolder holder, int position) {

        final Context context = mViewGroup.getContext();
        String lan = context.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("selectedLanguage", "");
        Long lanId = null;
        if(lan.equalsIgnoreCase("hi"))
            lanId = 5130467284090880l;
        else if(lan.equalsIgnoreCase("ta"))
            lanId = 6319546696728576l;
        else if(lan.equalsIgnoreCase("gu"))
            lanId = 5965057007550464l;
        Typeface typeFace = null;
        if(lan.equalsIgnoreCase("hi"))
            typeFace= Typeface.createFromAsset(context.getAssets(), "fonts/devanagari.ttf");
        else if(lan.equalsIgnoreCase("ta"))
            typeFace= Typeface.createFromAsset(context.getAssets(), "fonts/tamil.ttf");
        else if(lan.equalsIgnoreCase("gu"))
            typeFace= Typeface.createFromAsset(context.getAssets(), "fonts/gujarati.ttf");

        holder.bookTitle.setTypeface(typeFace);
        holder.authorName.setTypeface(typeFace);

        final Metadata metadataObj = metadata.get(position);
        holder.bookTitle.setText(metadataObj.get_title());
        holder.authorName.setText(metadataObj.get_authorFullName());
        holder.bookCover.setImageUrl("http:" + metadataObj.get_coverImageUrl(), imageLoader);
        if (metadataObj.get_ratingCount() > 0) {

            float val = (float) metadataObj.get_starCount() / metadataObj.get_ratingCount();
            if (val != 0.0) {
                holder.mRatingBar.setRating(val);

                NumberFormat numberformatter = NumberFormat.getNumberInstance();
                numberformatter.setMaximumFractionDigits(1);
                numberformatter.setMinimumFractionDigits(1);
                String rating = numberformatter.format(val);

                holder.ratingCount.setText(String.valueOf("("+metadataObj.get_ratingCount() + " ratings)"));
                holder.avgeragerating.setText("Average rating: " + rating + "/5");

            }else{
                Log.d("Val is Null", "");
            }
        }

        holder.mCardVeiw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, DetailPageActivity.class);
                i.putExtra(DetailPageActivity.JSON,metadataObj.toString());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return metadata.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder{

        CardView mCardVeiw;
        TextView bookTitle;
        TextView authorName;
        NetworkImageView bookCover;
        RatingBar mRatingBar;
        TextView ratingCount;
        TextView avgeragerating;

        public DataViewHolder(View itemView) {
            super(itemView);

            mCardVeiw = (CardView)itemView.findViewById(R.id.more_card_feature);
            bookTitle = (TextView)itemView.findViewById(R.id.titleTextViewMoreFeatured);
            authorName = (TextView)itemView.findViewById(R.id.authorTextViewMoreFeatured);
            bookCover = (NetworkImageView)itemView.findViewById(R.id.detail_image);
            mRatingBar = (RatingBar)itemView.findViewById(R.id.averageRatingBarFeatured);
            ratingCount = (TextView)itemView.findViewById(R.id.featuredPageRatingNumber);
            avgeragerating = (TextView)itemView.findViewById(R.id.averageRatingTextView);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
