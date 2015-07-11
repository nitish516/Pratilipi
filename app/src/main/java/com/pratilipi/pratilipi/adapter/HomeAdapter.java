package com.pratilipi.pratilipi.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
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

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by MOHIT KHAITAN on 18-06-2015.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.DataViewHolder> {

    List<Metadata> metadata;
    ViewGroup mViewGroup;
    boolean random = true;

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

        final Metadata metadataObj = metadata.get(position);

        holder.bookTitle.setText(metadataObj.get_title());
//        holder.authorName.setText("- "+metadataObj.get_authorFullName());
        holder.bookCover.setImageUrl("http:" + metadataObj.get_coverImageUrl(), imageLoader);

//        if(random) {
            holder.freeButton.setText("FREE!");
//        }
//        else {
//            holder.freeButton.setText("\u20B9"+ "100");
//            holder.freeButton.setTextColor(Color.GRAY);
//            holder.freeButton.setPaintFlags(holder.freeButton.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//            holder.priceText.setText(" \u20B9" + "20");
//        }
//        random =!random;


        if (metadataObj.get_ratingCount() > 0) {

            float val = (float) metadataObj.get_starCount() / metadataObj.get_ratingCount();
            if (val != 0.0) {
//                holder.mRatingBar.setRating(val);

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
        TextView priceText;
        public DataViewHolder(View itemView) {
            super(itemView);

            mHomeCardView = (CardView)itemView.findViewById(R.id.home_card_view);
            bookCover = (NetworkImageView)itemView.findViewById(R.id.image);
//            mRatingBar = (RatingBar)itemView.findViewById(R.id.averageRatingRatingBar);
            bookTitle = (TextView)itemView.findViewById(R.id.overlay_book_title);
//            authorName = (TextView)itemView.findViewById(R.id.overlay_author_name);
            ratingCount = (TextView)itemView.findViewById(R.id.featuredPageRatingNumber);
            avgeragerating = (TextView)itemView.findViewById(R.id.averageRatingTextView);
            freeButton = (TextView)itemView.findViewById(R.id.freeBtn);
            priceText= (TextView)itemView.findViewById(R.id.priceText);
            bookTitle.setSelected(true);

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
