package com.pratilipi.pratilipi;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonParseException;
import com.pratilipi.pratilipi.DataFiles.Metadata;
import com.pratilipi.pratilipi.helper.PratilipiProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.text.NumberFormat;

import io.fabric.sdk.android.services.concurrency.Task;

public class DetailPageActivity extends ActionBarActivity implements AsyncResponse{

    public static final String PID = "PId";
    public static final String POSITION = "Position";
    public static final String JSON = "JSON";
    private JSONObject obj;
    URI mUri;

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

    public void addData(View view) {
        ContentValues values = new ContentValues();

        int pageCount = 0;
        String contentType = "";
        Long pId = 0l;
        try {
            pageCount = obj.getInt("pageCount");
            contentType = obj.getString("contentType");
            pId = obj.getLong("id");
            values.put(PratilipiProvider.PID , String.valueOf(pId));
            values.put(PratilipiProvider.TITLE , obj.getString("title"));
            values.put(PratilipiProvider.CONTENT_TYPE ,contentType);
            values.put(PratilipiProvider.AUTHOR_ID , String.valueOf(obj.getLong("authorId")));
            values.put(PratilipiProvider.AUTHOR_NAME , obj.getJSONObject("author").getString("name"));
            values.put(PratilipiProvider.CH_COUNT , String.valueOf(pageCount));
            values.put(PratilipiProvider.IMG_URL , obj.getString("coverImgUrl"));
            values.put(PratilipiProvider.PG_URL , obj.getString("pageUrl"));
            values.put(PratilipiProvider.INDEX , obj.getString("index"));


            ContentResolver cv = getContentResolver();
            Uri uri = cv.insert(
                    PratilipiProvider.METADATA_URI, values);

            for(int i=1;i<=pageCount;i++){
    //            makeRequest(i,contentType,pId);
        }
        } catch (JSONException e) {
            e.printStackTrace();
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

    private void makeRequest(int pageNo,String type,Long pId) {
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
