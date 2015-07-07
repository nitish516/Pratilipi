package com.pratilipi.pratilipi;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pratilipi.pratilipi.DataFiles.Metadata;
import com.pratilipi.pratilipi.adapter.CardListAdapter;
import com.pratilipi.pratilipi.helper.PratilipiProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import android.support.v7.app.ActionBarActivity;

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
    String output ="";
    Toolbar toolbar;
    List<Metadata> metadata = new ArrayList<Metadata>();
    CardListAdapter adapter;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayout;
    String mTitle = "";
    String selectionArgs = "";

    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        progressBar = (ProgressBar)findViewById((R.id.progress_bar_more_featured));

        toolbar = (Toolbar)findViewById(R.id.tool_bar_card_activity);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView)toolbar.findViewById(R.id.title_toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        progressBar.setVisibility(View.VISIBLE);

        //Recycler View STARTS

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayout = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayout);

        adapter = new CardListAdapter(metadata);
        progressBar.setVisibility(View.GONE);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        //Recycler View END

        linearLayout = (LinearLayout)findViewById(R.id.card_activity_linear_layout);

        mTitle = getIntent().getStringExtra("TITLE");
        toolbar_title.setText(mTitle);

        if(!(mTitle.equalsIgnoreCase("Featured")|| mTitle.equalsIgnoreCase("New Releases")|| mTitle.equalsIgnoreCase("Top Reads")
            || mTitle.equalsIgnoreCase("Books")|| mTitle.equalsIgnoreCase("Poems")|| mTitle.equalsIgnoreCase("Stories"))){
            isSearch = true;
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            try {
                this.output = savedInstanceState.getString("Output");
               parseJson(new JSONObject(output));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String lan = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("selectedLanguage", "");
            if (lan.equalsIgnoreCase("hi")) {
                lanId = 5130467284090880l;
                typeFace = Typeface.createFromAsset(getAssets(), "fonts/devanagari.ttf");
            } else if (lan.equalsIgnoreCase("ta")) {
                lanId = 6319546696728576l;
                typeFace = Typeface.createFromAsset(getAssets(), "fonts/tamil.ttf");
            } else if (lan.equalsIgnoreCase("gu")) {
                lanId = 5965057007550464l;
                typeFace = Typeface.createFromAsset(getAssets(), "fonts/gujarati.ttf");
            }

            if (!isSearch) {
                    fetchData();
            }
        }
    }
    
    private void fetchData() {
        String URL = "content://com.pratilipi.pratilipi.helper.PratilipiData/metadata";
        Uri pid = Uri.parse(URL);
        url +=lanId+"&category=";
        switch(mTitle){
            case "Featured":
                selectionArgs = "featuredMore";
                url +="featuredPratilipiDataList";
                break;
            case "New Releases":
                selectionArgs = "newReleasesMore";
                url +="newReleasesPratilipiDataList";
                break;
            case "Top Reads":
                selectionArgs = "topReadsMore";
                url +="topReadsPratilipiDataList";
                break;
        }

        Cursor c = getContentResolver().query(pid, null, PratilipiProvider.LIST_TYPE + "=?",
                new String[]{selectionArgs}, PratilipiProvider.PID);


        if (!c.moveToFirst()) {
            if(isOnline())
                makeJsonArryReq();
            else
                showNoConnectionDialog(this);
        }
        else{
            fetchDataFromDb(c);
        }
    }

    private void fetchDataFromDb(Cursor c) {
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Metadata m = new Metadata();
            m.set_title(c.getString(c.getColumnIndex(PratilipiProvider.TITLE)));
            m.set_authorFullName(c.getString(c.getColumnIndex(PratilipiProvider.AUTHOR_NAME)));
            m.set_coverImageUrl(c.getString(c.getColumnIndex(PratilipiProvider.IMG_URL)));
            m.set_ratingCount(c.getLong(c.getColumnIndex(PratilipiProvider.RATING_COUNT)));
            m.set_starCount(c.getLong(c.getColumnIndex(PratilipiProvider.STAR_COUNT)));
            m.set_authorId(c.getString(c.getColumnIndex(PratilipiProvider.AUTHOR_ID)));
            m.set_pageUrl(c.getString(c.getColumnIndex(PratilipiProvider.PG_URL)));
            m.set_summary(c.getString(c.getColumnIndex(PratilipiProvider.SUMMARY)));
            m.set_index(c.getString(c.getColumnIndex(PratilipiProvider.INDEX)));
            m.set_contentType(c.getString(c.getColumnIndex(PratilipiProvider.CONTENT_TYPE)));
            m.set_pid(c.getString(c.getColumnIndex(PratilipiProvider.PID)));
            m.set_page_count(c.getInt(c.getColumnIndex(PratilipiProvider.CH_COUNT)));
            metadata.add(m);
            adapter.notifyDataSetChanged();
        }
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("Output",output);
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

        task.execute(url);
        task.delegate = this;
    }

    void parseJson(JSONObject response) {
        try {
            JSONArray pratilipiList;
            if(isSearch) {
                 pratilipiList = response.getJSONArray("pratilipiDataList");
            }
            else {
                 pratilipiList = response.getJSONArray("pratilipiList");
            }
            if(pratilipiList != null) {

                progressBar.setVisibility(View.GONE);

                for (int i = 0; i < pratilipiList.length(); i++) {
                    final JSONObject obj = pratilipiList.getJSONObject(i);

                    ContentValues values = new ContentValues();
                    Metadata m = new Metadata();

                    Long id  = obj.getLong("languageId");
                    if (!id.equals(lanId))
                        continue;

                    values.put(PratilipiProvider.PID ,id+"");
                    m.set_pid(id + "");


                    String title = obj.getString("title");
                    m.set_title(title);
                    values.put(PratilipiProvider.TITLE, title);

                    String authorName = obj.getJSONObject("author").getString("name");
                    m.set_authorFullName(authorName);
                    values.put(PratilipiProvider.AUTHOR_NAME, authorName);

                    String coverImageUrl = obj.getString("coverImageUrl");
                    m.set_coverImageUrl(coverImageUrl);
                    values.put(PratilipiProvider.IMG_URL, coverImageUrl);

                    long ratingCount = obj.getLong("ratingCount");
                    m.set_ratingCount(ratingCount);
                    values.put(PratilipiProvider.RATING_COUNT, ratingCount);

                    long starCount = obj.getLong("starCount");
                    m.set_starCount(starCount);
                    values.put(PratilipiProvider.STAR_COUNT, starCount);

                    String authorId = obj.getString("authorId");
                    m.set_authorId(authorId);
                    values.put(PratilipiProvider.AUTHOR_ID, authorId);

                    String pageUrl = obj.getString("pageUrl");
                    m.set_pageUrl(pageUrl);
                    values.put(PratilipiProvider.PG_URL,pageUrl);
                    if(obj.has("summary")) {
                        String summary = obj.getString("summary");
                        m.set_summary(summary);
                        values.put(PratilipiProvider.SUMMARY,summary);
                    }
                    if(obj.has("index")){
                        String index = obj.getString("index");
                        m.set_index(index);
                        values.put(PratilipiProvider.INDEX,index);
                    }

                    String contentType = obj.getString("contentType");
                    m.set_contentType(contentType);
                    values.put(PratilipiProvider.CONTENT_TYPE, contentType);

                    int pageCont = obj.getInt("pageCount");
                    m.set_page_count(pageCont);
                    values.put(PratilipiProvider.CH_COUNT, pageCont);

                    values.put(PratilipiProvider.LIST_TYPE, selectionArgs);

                    metadata.add(m);
                    adapter.notifyDataSetChanged();

                   try {
                        ContentResolver cv = getContentResolver();
                        Uri uri = cv.insert(PratilipiProvider.METADATA_URI, values);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                if(linearLayout.getChildAt(0) == null){
                    TextView tv = new TextView(linearLayout.getContext());
                    tv.setText("No results");
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextSize(32);
                    linearLayout.setGravity(Gravity.CENTER);
                    linearLayout.addView(tv);
                }

            }else {
                TextView tv = new TextView(linearLayout.getContext());
                tv.setText("No results");
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(32);
                linearLayout.setGravity(Gravity.CENTER);
                linearLayout.addView(tv);

            }
            } catch (JSONException e1) {
            e1.printStackTrace();
        }
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public void processFinish(String output) {
        if(null == output || output.isEmpty()){
            Log.d("Output", output);

            TextView tv = new TextView(linearLayout.getContext());
            tv.setText("No results");
            tv.setTextSize(32);
            tv.setGravity(Gravity.CENTER);
            linearLayout.setGravity(Gravity.CENTER);
            linearLayout.addView(tv);

        }
        else {
            try {
                this.output = output;
               parseJson(new JSONObject(output));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        if(isSearch){
            MenuInflater mi = getMenuInflater();
            mi.inflate(R.menu.menu_main, menu);

            searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
            searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if(hasFocus){
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                }
            });
            searchView.setIconified(true);
            searchView.setIconifiedByDefault(false);
            searchView.setActivated(true);
            searchView.setQueryHint("Search Pratilipi");
            searchView.setVisibility(View.VISIBLE);
            searchView.requestFocus();
            searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
            if(output.isEmpty()){
                searchView.requestFocus();
            }


            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    if(isOnline()) {
                        s = s.replace(" ", "%20");
                        url = "http://www.pratilipi.com/api.pratilipi/search?query="+s+"&languageId=";
                        makeJsonArryReq();
                        searchView.clearFocus();
                        toolbar.setTitle(s);
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

            searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        metadata.clear();
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }
        return true;
    }
}