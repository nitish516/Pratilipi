package com.pratilipi.pratilipi;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.pratilipi.pratilipi.DataFiles.Metadata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MOHIT KHAITAN on 31-05-2015.
 */
public class MoreFeaturedBooks extends ActionBarActivity implements AsyncResponse{
    LinearLayout linearLayout;
    Long lanId = null;
    Typeface typeFace = null;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more_featured_content);
        linearLayout = (LinearLayout)findViewById(R.id.linear_layout_more_featured);

        String lan = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("selectedLanguage", "");
        if(lan.equalsIgnoreCase("hi")) {
            lanId = 5130467284090880l;
            typeFace = Typeface.createFromAsset(getAssets(), "fonts/devanagari.ttf");
        }
        else if(lan.equalsIgnoreCase("ta")){
            lanId = 6319546696728576l;
            typeFace= Typeface.createFromAsset(getAssets(), "fonts/tamil.ttf");
        }
        else if(lan.equalsIgnoreCase("gu")) {
            lanId = 5965057007550464l;
            typeFace = Typeface.createFromAsset(getAssets(), "fonts/gujarati.ttf");
        }

        if(isOnline())
            makeJsonArryReq();
        else
        {
            showNoConnectionDialog(this);
        }
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getIntent().getStringExtra("TITLE"));
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

public boolean isOnline() {
        ConnectivityManager cm =
        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
        }
public static void showNoConnectionDialog(Context ctx1) {
final Context ctx = ctx1;
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(true);
        builder.setMessage(R.string.no_connection);
        builder.setTitle(R.string.no_connection_title);
        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
public void onClick(DialogInterface dialog, int which) {

        Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(dialogIntent);
        }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
public void onClick(DialogInterface dialog, int which) {
        return;
        }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
public void onCancel(DialogInterface dialog) {
        return;
        }
        });

        builder.show();
        }

/**
 * Making json array request
 * */
private void makeJsonArryReq() {

    RequestTask task =  new RequestTask();

    task.execute("http://www.pratilipi.com/api.pratilipi/pratilipi/list?state=PUBLISHED&languageId="+lanId);
        task.delegate = this;
        }

        void parseJson(JSONObject response, final Context context)
        {
        try {
            JSONArray pratilipiList = response.getJSONArray("pratilipiList");
            for (int i = 0; i < pratilipiList.length(); i++) {
                final JSONObject obj = pratilipiList.getJSONObject(i);
                CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.card_view, null);
                ImageLoader imageLoader = AppController.getInstance().getImageLoader();

                NetworkImageView imageView = (NetworkImageView) cardView.findViewById(R.id.detail_image);
                RatingBar ratingBar = (RatingBar) cardView.findViewById(R.id.averageRatingBarFeatured);
                TextView ratingNum = (TextView) cardView.findViewById(R.id.featuredPageRatingNumber);

                TextView title = (TextView) cardView.findViewById(R.id.titleTextViewMoreFeatured);
                title.setTypeface(typeFace);
                title.setText(Html.fromHtml(obj.getString("title")));

                TextView author = (TextView) cardView.findViewById(R.id.authorTextViewMoreFeatured);
                author.setTypeface(typeFace);
                author.setText(Html.fromHtml(obj.getJSONObject("author").getString("name")));

                if (obj.getLong("ratingCount") > 0) {
                    ratingBar.setRating((float) obj.getLong("starCount") / obj.getLong("ratingCount"));
                    ratingNum.setText((String.valueOf("(" + (obj.getLong("ratingCount") + ")"))));
                }
                // Populate the image
                imageView.setImageUrl("http:" + obj.getString("coverImageUrl"), imageLoader);
                linearLayout.addView(cardView);

                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, DetailPageActivity.class);
                        i.putExtra(DetailPageActivity.JSON,  obj.toString());
                        startActivity(i);
                    }
                });
            }
            } catch (JSONException e1) {
            e1.printStackTrace();
        }
        }
    @Override
    public void processFinish(String output) {
        Log.d("Output", output);
        try {
            parseJson(new JSONObject(output),this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}