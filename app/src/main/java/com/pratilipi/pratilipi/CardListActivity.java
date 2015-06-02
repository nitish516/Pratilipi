package com.pratilipi.pratilipi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MOHIT KHAITAN on 31-05-2015.
 */
public class CardListActivity extends ActionBarActivity implements AsyncResponse{
    LinearLayout linearLayout;
    ProgressBar progressBar;
    Long lanId = null;
    Typeface typeFace = null;
    String url = "http://www.pratilipi.com/api.pratilipi/pratilipi/list?state=PUBLISHED&languageId=";
    boolean isSearch = false;
    SearchView searchView;

    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);
        linearLayout = (LinearLayout)findViewById(R.id.linear_layout_more_featured);
        progressBar = (ProgressBar)findViewById((R.id.progress_bar_more_featured));

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        String title = getIntent().getStringExtra("TITLE");
        actionBar.setTitle(title);
        if(!(title.equalsIgnoreCase("Featured")|| title.equalsIgnoreCase("New Releases"))){
            isSearch = true;
        }
        actionBar.setDisplayHomeAsUpEnabled(true);

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

        if(!isSearch){
            if(isOnline()) {
                makeJsonArryReq();
            }
            else
            {
                showNoConnectionDialog(this);
            }
        }
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

    progressBar.setVisibility(View.VISIBLE);
    RequestTask task =  new RequestTask();

    task.execute(url+lanId);
        task.delegate = this;
        }

        void parseJson(JSONObject response)
        {
        try {
            JSONArray pratilipiList;
            if(isSearch) {
                 pratilipiList = response.getJSONArray("pratilipiDataList");
            }
            else {
                 pratilipiList = response.getJSONArray("pratilipiList");
            }

            for (int i = 0; i < pratilipiList.length(); i++) {
                final JSONObject obj = pratilipiList.getJSONObject(i);
                if(obj.getLong("languageId") != lanId)
                    continue;
                final CardView cardView = (CardView) getLayoutInflater().inflate(R.layout.card_view, null);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(linearLayout.getContext(), DetailPageActivity.class);
                        i.putExtra(DetailPageActivity.JSON,  obj.toString());
                        startActivity(i);
                    }
                });
                ImageLoader imageLoader = AppController.getInstance().getImageLoader();

                NetworkImageView imageView = (NetworkImageView) cardView.findViewById(R.id.detail_image);
                RatingBar ratingBar = (RatingBar) cardView.findViewById(R.id.averageRatingBarFeatured);
                TextView ratingNum = (TextView) cardView.findViewById(R.id.featuredPageRatingNumber);

                TextView title = (TextView) cardView.findViewById(R.id.titleTextViewMoreFeatured);
                title.setTypeface(typeFace);
                title.setText(Html.fromHtml(obj.getString("title")));

                TextView author = (TextView) cardView.findViewById(R.id.authorTextViewMoreFeatured);
                author.setTypeface(typeFace);
                if(isSearch)
                    author.setVisibility(View.GONE);
                else
                    author.setText(Html.fromHtml(obj.getJSONObject("author").getString("name")));

                if (obj.getLong("ratingCount") > 0) {
                    ratingBar.setRating((float) obj.getLong("starCount") / obj.getLong("ratingCount"));
                    ratingNum.setText((String.valueOf("(" + (obj.getLong("ratingCount") + ")"))));
                }
                // Populate the image
                imageView.setImageUrl("http:" + obj.getString("coverImageUrl"), imageLoader);
                linearLayout.addView(cardView);

            }
            } catch (JSONException e1) {
            e1.printStackTrace();

            progressBar.setVisibility(View.GONE);
        }
        }
    @Override
    public void processFinish(String output) {
        Log.d("Output", output);
        try {
            parseJson(new JSONObject(output));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        if(isSearch){
            MenuInflater mi = getMenuInflater();
            mi.inflate(R.menu.menu_main, menu);

            searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            searchView.setIconified(true);
            searchView.setIconifiedByDefault(false);
            searchView.setActivated(true);
            searchView.setQueryHint("Search Pratilipi");
            searchView.setVisibility(View.VISIBLE);
            searchView.requestFocus();
            searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    if(isOnline()) {
                        url = "http://www.pratilipi.com/api.pratilipi/search?query="+s+"&languageId=";
                        makeJsonArryReq();
                        searchView.clearFocus();
                    }
                    else
                    {
                        showNoConnectionDialog(searchView.getContext());
                    }

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    linearLayout.removeAllViews();
                    return false;
                }
            });
        }
        return true;
    }
}