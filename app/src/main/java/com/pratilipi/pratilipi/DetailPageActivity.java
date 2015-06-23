package com.pratilipi.pratilipi;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.pratilipi.pratilipi.DataFiles.Metadata;
import com.pratilipi.pratilipi.helper.PratilipiProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.URI;
import java.text.NumberFormat;

public class DetailPageActivity extends ActionBarActivity implements AsyncResponse{

    public static final String PID = "PId";
    public static final String POSITION = "Position";
    public static final String METADATA = "METADATA";
    private Metadata metadata;
    URI mUri;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_detail_page);

        toolbar = (Toolbar)findViewById(R.id.tool_bar_detailpage_activity);
        setSupportActionBar(toolbar);

        TextView toolbar_title = (TextView)toolbar.findViewById(R.id.title_toolbar);

        Button addToShelf = (Button) findViewById(R.id.addToShelfButton);
        addToShelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.addToShelfButton: {
                        startActivity(new Intent(DetailPageActivity.this, LoginActivity.class));
                        break;
                    }
                }
            }
        });
        try{
            metadata = (Metadata) getIntent().getSerializableExtra(METADATA);

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
            toolbar_title.setTypeface(typeFace);
            toolbar_title.setText(" ");

            getSupportActionBar().setCustomView(v);

            title.setTypeface(typeFace);

            title.setText(Html.fromHtml(metadata.get_title()));

            ImageLoader imageLoader = AppController.getInstance().getImageLoader();
            NetworkImageView imageView = (NetworkImageView) findViewById(R.id.detail_page_image);
            imageView.setImageUrl("http:" +metadata.get_coverImageUrl(), imageLoader);

            RatingBar ratingBar  = (RatingBar) findViewById(R.id.averageRatingBar);
            TextView averageRatingTextView = (TextView) findViewById(R.id.averageRatingTextView);
            TextView detailPageRate = (TextView)findViewById(R.id.detailPageRatingNumber);
            if(metadata.get_ratingCount()> 0) {
                float val = (float)metadata.get_starCount()/metadata.get_ratingCount();
                ratingBar.setRating(val);

                NumberFormat numberformatter = NumberFormat.getNumberInstance();
                numberformatter.setMaximumFractionDigits(1);
                numberformatter.setMinimumFractionDigits(1);
                String rating = numberformatter.format(val);

//                averageRatingTextView.setText("Average rating: " + String.valueOf(rating) + "/5");
//                detailPageRate.setText(String.valueOf("("+metadata.get_ratingCount())+" rating)");
            }

            String summaryString = metadata.get_summary();
            if(null!= summaryString) {
                Spanned summary = Html.fromHtml(summaryString);
                if (null != summary) {
                    TextView summaryTextView = (TextView) findViewById(R.id.summaryTextView);
                    summaryTextView.setText(summary);
                    summaryTextView.setTypeface(typeFace);
                }
            }
            TextView authorTextView = (TextView) findViewById(R.id.authorTextView);

            authorTextView.setText(metadata.get_authorFullName());
            authorTextView.setTypeface(typeFace);

            makeRequest(1,metadata.get_contentType(),metadata.get_pid());



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
        i.putExtra(DetailPageActivity.METADATA, (Serializable) metadata);
        startActivity(i);
    }

    public void addMetaData(View view) {
        ContentValues values = new ContentValues();

        int pageCount = 0;
        String contentType = "";
        Long pId = 0l;
        try {
            contentType = metadata.get_contentType();
            values.put(PratilipiProvider.PID , metadata.get_pid());
            values.put(PratilipiProvider.TITLE , metadata.get_title());
            values.put(PratilipiProvider.CONTENT_TYPE ,contentType);
            values.put(PratilipiProvider.AUTHOR_ID , String.valueOf(metadata.get_authorId()));
            values.put(PratilipiProvider.AUTHOR_NAME , metadata.get_authorFullName());
            values.put(PratilipiProvider.CH_COUNT , metadata.get_page_count());
            values.put(PratilipiProvider.IMG_URL , metadata.get_coverImageUrl());
            values.put(PratilipiProvider.PG_URL , metadata.get_pageUrl());
            values.put(PratilipiProvider.INDEX , metadata.get_index());


            ContentResolver cv = getContentResolver();
            Uri uri = cv.insert(
                    PratilipiProvider.METADATA_URI, values);

            for(int i=1;i<=pageCount;i++){
    //            makeRequest(i,contentType,pId);
        }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void makeRequest(int pageNo,String type,String pId) {
        if(isOnline()) {
            if (type.equalsIgnoreCase("PRATILIPI")) {
                RequestTask task = new RequestTask();
                String url = "http://www.pratilipi.com/api.pratilipi/pratilipi/content?pratilipiId=";
                task.execute(url + pId + "&pageNo=" + pageNo);
                task.delegate = this;
            } else if (type.equalsIgnoreCase("IMAGE")) {

            }
        }
    }

    void parseJson(JSONObject obj) {
        ContentValues values = new ContentValues();
        try {
            values.put(PratilipiProvider.PID , String.valueOf(obj.getLong("pratilipiId")));
            values.put(PratilipiProvider.CONTENT , obj.getString("pageContent"));
            values.put(PratilipiProvider.CH_NO, String.valueOf(obj.getInt("pageNo")));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ContentResolver cv = getContentResolver();
        Uri uri = cv.insert(
                PratilipiProvider.CONTENT_URI, values);


    }

    @Override
    public void processFinish(String output) {
        if(!(null == output || output.isEmpty())) {
            Log.d("Output", output);
            try {
                parseJson(new JSONObject(output));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
