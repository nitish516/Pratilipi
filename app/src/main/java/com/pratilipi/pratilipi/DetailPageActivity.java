package com.pratilipi.pratilipi;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.pratilipi.pratilipi.DataFiles.Metadata;
import com.pratilipi.pratilipi.helper.PratilipiProvider;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;

public class DetailPageActivity extends ActionBarActivity implements AsyncResponse{

    public static final String PID = "PId";
    public static final String POSITION = "Position";
    public static final String METADATA = "METADATA";
    private Metadata metadata;
    URI mUri;
private String pId;
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

            pId = metadata.get_pid();
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

            String URL = "content://com.pratilipi.pratilipi.helper.PratilipiData/content";
            Uri pid =  Uri.parse(URL);

            Cursor c = getContentResolver().query(pid, null, PratilipiProvider.PID +"=? and "+PratilipiProvider.CH_NO+"=?",
                    new String[] { pId+"", 1+"" }, PratilipiProvider.PID);

            if (!c.moveToFirst()) {

                makeRequest( metadata.get_contentType(), pId);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        ImageView home_direct = (ImageView)findViewById(R.id.home_img_button);

        home_direct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent direct = new Intent(DetailPageActivity.this, MainActivity.class);
                startActivity(direct);
            }
        });



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

    private void makeRequest(String type,String pId) {
        String URL = "content://com.pratilipi.pratilipi.helper.PratilipiData/content";
        Uri pid =  Uri.parse(URL);
        Cursor c = getContentResolver().query(pid, null, PratilipiProvider.PID +"=? and "+PratilipiProvider.CH_NO+"=?",
                new String[] { pId+"", 1+"" }, PratilipiProvider.PID);


        if (!c.moveToFirst() && isOnline()) {
            if (type.equalsIgnoreCase("PRATILIPI")) {
                RequestTask task = new RequestTask();
                String url = "http://www.pratilipi.com/api.pratilipi/pratilipi/content?pratilipiId=";
                task.execute(url + pId + "&pageNo=" + 1);
                task.delegate = this;
            } else if (type.equalsIgnoreCase("IMAGE")) {
                insertImageToDb(pId);
            }
        }
    }

    private void insertImageToDb(String pId) {
        ContentValues values = new ContentValues();
        try {
            URL imageUrl = new URL("http://www.pratilipi.com/api.pratilipi/pratilipi/content/image?pratilipiId="
                    + pId+"&pageNo=1");
            URLConnection ucon = imageUrl.openConnection();

            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(500);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            values.put(PratilipiProvider.PID , pId);
            values.put(PratilipiProvider.IMAGE ,baf.toByteArray());
            values.put(PratilipiProvider.CH_NO, 1);
        } catch (Exception e) {
            Log.d("ImageManager", "Error: " + e.toString());
        }
        ContentResolver cv = getContentResolver();
        Uri uri = cv.insert(
                PratilipiProvider.CONTENT_URI, values);
    }

    void parseJson(JSONObject obj) {
        ContentValues values = new ContentValues();
        try {
            values.put(PratilipiProvider.PID , String.valueOf(obj.getLong("pratilipiId")));
            values.put(PratilipiProvider.CONTENT , obj.getString("pageContent"));
            values.put(PratilipiProvider.CH_NO, 1);

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
