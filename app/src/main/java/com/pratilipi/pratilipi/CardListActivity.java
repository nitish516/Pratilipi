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

        String title = getIntent().getStringExtra("TITLE");
        toolbar_title.setText(title);
        if(!(title.equalsIgnoreCase("Featured")|| title.equalsIgnoreCase("New Releases")|| title.equalsIgnoreCase("Top Reads")
            || title.equalsIgnoreCase("Books")|| title.equalsIgnoreCase("Poems")|| title.equalsIgnoreCase("Stories"))){
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
                if (isOnline()) {
                    makeJsonArryReq();
                } else {
                    showNoConnectionDialog(this);
                }
            }
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
            if(pratilipiList != null) {

                progressBar.setVisibility(View.GONE);

                for (int i = 0; i < pratilipiList.length(); i++) {
                    final JSONObject obj = pratilipiList.getJSONObject(i);
                    if (obj.getLong("languageId") != lanId)
                        continue;
                    Metadata m = new Metadata();
                    m.set_title(obj.getString("title"));
                    m.set_authorFullName(obj.getJSONObject("author").getString("name"));
                    m.set_coverImageUrl(obj.getString("coverImageUrl"));
                    m.set_ratingCount(obj.getLong("ratingCount"));
                    m.set_starCount(obj.getLong("starCount"));
                    m.set_authorId(obj.getString("authorId"));
                    m.set_pageUrl(obj.getString("pageUrl"));
                    if(obj.has("summary"))
                        m.set_summary(obj.getString("summary"));
                    if(obj.has("index"))
                        m.set_index(obj.getString("index"));
                    m.set_contentType(obj.getString("contentType"));
                    m.set_pid(obj.getLong("id") + "");
                    m.set_page_count(obj.getInt("pageCount"));

                    metadata.add(m);
                    adapter.notifyDataSetChanged();
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