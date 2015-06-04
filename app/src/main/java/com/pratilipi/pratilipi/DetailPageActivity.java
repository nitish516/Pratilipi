package com.pratilipi.pratilipi;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import org.json.JSONObject;

import java.text.NumberFormat;

public class DetailPageActivity extends ActionBarActivity {

    public static final String PID = "PId";
    public static final String POSITION = "Position";
    public static final String JSON = "JSON";
    private JSONObject obj;

    String title1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_detail_page);
        try{
            obj = new JSONObject(getIntent().getStringExtra(JSON));
            title1 = obj.getString("title");

            TextView title = (TextView) findViewById(R.id.titleTextView);

            String lan = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("selectedLanguage", "");
            Typeface typeFace = null;
            if(lan.equalsIgnoreCase("hi"))
                typeFace= Typeface.createFromAsset(getAssets(), "fonts/devanagari.ttf");
            else if(lan.equalsIgnoreCase("ta"))
                typeFace= Typeface.createFromAsset(getAssets(), "fonts/tamil.ttf");
            else if(lan.equalsIgnoreCase("gu"))
                typeFace= Typeface.createFromAsset(getAssets(), "fonts/gujarati.ttf");

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowCustomEnabled(true);

            LayoutInflater layoutInflate = LayoutInflater.from(this);
            View v = layoutInflate.inflate(R.layout.actionbar_custom_title, null);
            TextView actionBarTitleTextView = (TextView)v.findViewById(R.id.actionBarTitle);
            actionBarTitleTextView.setTypeface(typeFace);
            actionBarTitleTextView.setText(title1);

            getSupportActionBar().setCustomView(v);

            title.setTypeface(typeFace);

            title.setText(Html.fromHtml(obj.getString("title")));

            ImageLoader imageLoader = AppController.getInstance().getImageLoader();
            NetworkImageView imageView = (NetworkImageView) findViewById(R.id.detail_image);
            imageView.setImageUrl("http:" +obj.getString("coverImageUrl"), imageLoader);

            RatingBar ratingBar  = (RatingBar) findViewById(R.id.averageRatingBar);
            TextView averageRatingTextView = (TextView) findViewById(R.id.averageRatingTextView);
            TextView detailPageRate = (TextView)findViewById(R.id.detailPageRatingNumber);
            if(obj.getLong("ratingCount")> 0) {
                float val = (float)obj.getLong("starCount")/obj.getLong("ratingCount");
                ratingBar.setRating(val);

                NumberFormat numberformatter = NumberFormat.getNumberInstance();
                numberformatter.setMaximumFractionDigits(1);
                numberformatter.setMinimumFractionDigits(1);
                String rating = numberformatter.format(val);

                averageRatingTextView.setText("Average rating: " + String.valueOf(rating) + "/5");
                detailPageRate.setText(String.valueOf("("+obj.getLong("ratingCount"))+" rating)");
            }

            String summaryString = obj.getString("summary");
            if(null!= summaryString) {
                Spanned summary = Html.fromHtml(summaryString);
                if (null != summary) {
                    TextView summaryTextView = (TextView) findViewById(R.id.summaryTextView);
                    summaryTextView.setText(summary);
                    summaryTextView.setTypeface(typeFace);
                }
            }
            TextView authorTextView = (TextView) findViewById(R.id.authorTextView);
            JSONObject authorObj = obj.getJSONObject("author");
            if(null != authorObj){
                String name = authorObj.getString("name");
                if(null!= name) {
                    Spanned author = Html.fromHtml(name);
                   if (null != author) {
                       authorTextView.setText(author);
                        authorTextView.setTypeface(typeFace);
                    }
                    else
                        authorTextView.setVisibility(View.GONE);
                }
                else
                    authorTextView.setVisibility(View.GONE);
            }
            else
                authorTextView.setVisibility(View.GONE);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void launchReader(View view)
    {
        Intent i = new Intent(this, ReadActivity.class);
        i.putExtra(DetailPageActivity.JSON,  obj.toString());
        startActivity(i);
    }
}
