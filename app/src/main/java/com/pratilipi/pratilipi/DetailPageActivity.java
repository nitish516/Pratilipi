package com.pratilipi.pratilipi;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.pratilipi.pratilipi.DataFiles.Metadata;
import com.pratilipi.pratilipi.helper.AppConstant;
import com.pratilipi.pratilipi.helper.PratilipiProvider;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;

public class DetailPageActivity extends ActionBarActivity implements AsyncResponse{

    public static final String PID = "PId";
    public static final String POSITION = "Position";
    public static final String METADATA = "METADATA";
    private Metadata metadata;
    TextView summaryTextView;
    private String pId;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);

        summaryTextView = (TextView) findViewById(R.id.summaryTextView);

        toolbar = (Toolbar)findViewById(R.id.tool_bar_detailpage_activity);
        setSupportActionBar(toolbar);

        TextView toolbar_title = (TextView)toolbar.findViewById(R.id.title_toolbar);

        try{
            metadata = (Metadata) getIntent().getSerializableExtra(METADATA);

            final Button addToShelf = (Button) findViewById(R.id.addToShelfButton);
            if(metadata.get_is_downloaded()!= null && metadata.get_is_downloaded().equalsIgnoreCase("yes")){
                addToShelf.setBackgroundColor(Color.GRAY);
                addToShelf.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                addToShelf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(getApplicationContext(), "Already in shelf", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                    }
                });
            }
            else{
                addToShelf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        download(v);
                        addToShelf.setBackgroundColor(Color.GRAY);
                        addToShelf.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                        addToShelf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast toast = Toast.makeText(getApplicationContext(), "Already in shelf", Toast.LENGTH_SHORT);
                                        toast.show();
                                    }
                                });
                            }
                        });
                    }
                });
            }

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
//            TextView averageRatingTextView = (TextView) findViewById(R.id.averageRatingTextView);
//            TextView detailPageRate = (TextView)findViewById(R.id.detailPageRatingNumber);
            if(metadata.get_ratingCount()> 0) {
                float val = (float)metadata.get_starCount()/metadata.get_ratingCount();
                ratingBar.setRating(val);

                NumberFormat numberformatter = NumberFormat.getNumberInstance();
                numberformatter.setMaximumFractionDigits(1);
                numberformatter.setMinimumFractionDigits(1);
//                String rating = numberformatter.format(val);

//                averageRatingTextView.setText("Average rating: " + String.valueOf(rating) + "/5");
//                detailPageRate.setText(String.valueOf("("+metadata.get_ratingCount())+" rating)");
            }

            String summaryString = metadata.get_summary();
            if(null!= summaryString) {
                Spanned summary = Html.fromHtml(summaryString);
                if (null != summary) {
                    summaryTextView.setText(summary);
                    summaryTextView.setTypeface(typeFace);
                }
            }else{

                summaryTextView.setTypeface(typeFace);
//                summaryTextView.setText(Html.fromHtml(metadata.get_title())+", "+metadata.get_authorFullName()+" "+R.string.custom_summary);
            }

            TextView authorTextView = (TextView) findViewById(R.id.authorTextView);

            authorTextView.setText(metadata.get_authorFullName());
            authorTextView.setTypeface(typeFace);

            String URL = "content://com.pratilipi.pratilipi.helper.PratilipiData/content";
            Uri pid =  Uri.parse(URL);

            Cursor c = getContentResolver().query(pid, null, PratilipiProvider.PID +"=? and "+PratilipiProvider.CH_NO+"=?",
                    new String[] { pId+"", 1+"" }, PratilipiProvider.PID);

            if (!c.moveToFirst()) {

                makeRequest( metadata.get_contentType(), pId, 1);
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

    public void launchReader(View view) {
        Intent i = new Intent(this, ReadActivity.class);
        i.putExtra(DetailPageActivity.METADATA, (Serializable) metadata);
        startActivity(i);
    }

    public void download(View view){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), "Downloading ...", Toast.LENGTH_SHORT);
                toast.show();

            }
        });
        addMetaData();
        downloadImage();
        for(int i =1; i <= metadata.get_page_count(); i++ ){
            makeRequest(metadata.get_contentType(),metadata.get_pid(),i);
        }
    }

    private class AsyncTaskEx extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            File directory = new File(
                    android.os.Environment.getExternalStorageDirectory()
                            + File.separator + AppConstant.PHOTO_ALBUM);

            // make file if not exists
            if (!directory.exists()) {
                directory.mkdirs();
            }

            try {
                File file = new File(directory,  metadata.get_pid() + ".jpg");
                FileOutputStream out = new FileOutputStream(file);
                URL url = new URL("http:"+metadata.get_coverImageUrl());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private void downloadImage() {
        new AsyncTaskEx().execute();
    }

    public void addMetaData() {
        ContentValues values = new ContentValues();
        try {
            values.put(PratilipiProvider.PID , metadata.get_pid());
            values.put(PratilipiProvider.TITLE , metadata.get_title());
            values.put(PratilipiProvider.CONTENT_TYPE ,metadata.get_contentType());
            values.put(PratilipiProvider.AUTHOR_ID , String.valueOf(metadata.get_authorId()));
            values.put(PratilipiProvider.AUTHOR_NAME , metadata.get_authorFullName());
            values.put(PratilipiProvider.CH_COUNT , metadata.get_page_count());
            values.put(PratilipiProvider.IMG_URL , metadata.get_coverImageUrl());
            values.put(PratilipiProvider.PG_URL , metadata.get_pageUrl());
            if(metadata.get_index()!=null)
                values.put(PratilipiProvider.INDEX , metadata.get_index());
            values.put(PratilipiProvider.RATING_COUNT , metadata.get_ratingCount());
            values.put(PratilipiProvider.STAR_COUNT , metadata.get_starCount());
            if(null!=metadata.get_summary())
                values.put(PratilipiProvider.SUMMARY , metadata.get_summary());
            values.put(PratilipiProvider.LIST_TYPE , "download");
            values.put(PratilipiProvider.IS_DOWNLOADED , "yes");

            ContentResolver cv = getContentResolver();
            Uri uri = cv.insert(
                    PratilipiProvider.METADATA_URI, values);

            if(null!= uri) {
                ContentValues value = new ContentValues();
                value.put(PratilipiProvider.IS_DOWNLOADED , "yes");

                cv.update(PratilipiProvider.METADATA_URI,value, PratilipiProvider.PID + "=?",
                            new String[]{metadata.get_pid()});
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

    private void makeRequest(String type,String pId,int chapter) {
        String URL = "content://com.pratilipi.pratilipi.helper.PratilipiData/content";
        Uri pid =  Uri.parse(URL);
        Cursor c = getContentResolver().query(pid, null, PratilipiProvider.PID +"=? and "+PratilipiProvider.CH_NO+"=?",
                new String[] { pId+"", chapter+"" }, PratilipiProvider.PID);


        if (!c.moveToFirst() && isOnline()) {
            if (type.equalsIgnoreCase("PRATILIPI")) {
                RequestTask task = new RequestTask();
                String url = "http://www.pratilipi.com/api.pratilipi/pratilipi/content?pratilipiId=";
                task.execute(url + pId + "&pageNo=" + chapter);
                task.delegate = this;
            } else if (type.equalsIgnoreCase("IMAGE")) {
                insertImageToDb(pId,chapter);
            }
        }
    }

    private void insertImageToDb(String pId, int chapter) {
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
            values.put(PratilipiProvider.CH_NO, chapter);
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
            values.put(PratilipiProvider.CH_NO, obj.getInt("pageNo"));

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
