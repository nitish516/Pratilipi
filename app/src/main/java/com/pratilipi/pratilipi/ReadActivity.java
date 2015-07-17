package com.pratilipi.pratilipi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pratilipi.pratilipi.DataFiles.Metadata;
import com.pratilipi.pratilipi.helper.PratilipiProvider;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ReadActivity extends ActionBarActivity implements AsyncResponse {

    View mDecorView;
    View controlsView;
    int INITIAL_HIDE_DELAY = 100;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private ArrayList<String> mTitles;
    private ArrayList<Integer> mTitleChapters;
    private Metadata metadata;
    CustomWebView webView;
    SeekBar seekBar;
    TextView pageNoIndicator;
    private int indexSize = 0;
    private int pageCount = 0;
    private int currentPage = 1;
    String url = "http://www.pratilipi.com/api.pratilipi/pratilipi/content?pratilipiId=";
    private static final String HTML_FORMAT = "<img src=\"data:image/jpeg;base64,%1$s\" />";
    Long pId;
    boolean scrollToLast = false;
    JSONObject jsonObject;
    String type;
    boolean isLoading = false;
    ProgressDialog progressDialog;
    RequestTask task;
    String title;
    String pageContent;
    byte[] image;
    String lan;
    int initialScale = 30;
    int maxProgress = 0;
    int currentChapterPageCount = 0;
    int currentChapterCurrentPage = 0;
    String pratilipiId = "";
    long _time_stamp;
    TextView fixedPageIndicator;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitles = new ArrayList<>();
        mTitleChapters = new ArrayList<>();
        try {
            metadata = (Metadata) getIntent().getSerializableExtra(DetailPageActivity.METADATA);
            title = metadata.get_title();
            String id = metadata.get_pid();
            pId = Long.parseLong(id);
            pratilipiId = Long.toString(pId);
            type = metadata.get_contentType();
            pageCount = metadata.get_page_count();
            currentPage = metadata.get_current_chapter();
            currentChapterCurrentPage = metadata.get_current_page();
            _time_stamp = metadata.get_time_stamp();
            initialScale = metadata.get_font_size();

            Gson gson = new GsonBuilder().create();
            JsonArray indexArr = gson.fromJson(metadata.get_index(), JsonElement.class).getAsJsonArray();
            if (null != indexArr) {
                indexSize = indexArr.size();
                for (int i = 0; i < indexSize; i++) {
                    JsonObject jsonObject = indexArr.get(i).getAsJsonObject();
                    String title = jsonObject.get("title").toString();
                    Log.d("TITLE", title);
                    mTitles.add(i, title.substring(1, title.length() - 1));
                    mTitleChapters.add(i, Integer.parseInt(jsonObject.get("pageNo").toString()));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_read);
        controlsView = findViewById(R.id.main_layout);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_layout);
        mDrawerList = (ListView) findViewById(R.id.right_drawer);


        mDecorView = getWindow().getDecorView();
        mDecorView.setOnSystemUiVisibilityChangeListener(new
                                                                 View.OnSystemUiVisibilityChangeListener() {

                                                                     @Override
                                                                     public void onSystemUiVisibilityChange(int visibility) {
                                                                         boolean visible = (visibility & View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION) == 0;
                                                                         controlsView.setVisibility(visible
                                                                                 ? View.VISIBLE
                                                                                 : View.GONE);
                                                                     }

                                                                 });

        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mTitles));
        lan = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("selectedLanguage", "");
        Typeface typeFace = null;
        if (lan.equalsIgnoreCase("hi"))
            typeFace = Typeface.createFromAsset(getAssets(), "fonts/devanagari.ttf");
        else if (lan.equalsIgnoreCase("ta"))
            typeFace = Typeface.createFromAsset(getAssets(), "fonts/tamil.ttf");
        else if (lan.equalsIgnoreCase("gu"))
            typeFace = Typeface.createFromAsset(getAssets(), "fonts/gujarati.ttf");

        for (int i = 0; i < mDrawerList.getChildCount(); i++) {
            TextView tv = (TextView) mDrawerList.getChildAt(i);
            tv.setTypeface(typeFace);
        }

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setIcon(R.drawable.pratilipi_logo_vector);

        final LayoutInflater layoutInflate = LayoutInflater.from(this);
        View v = layoutInflate.inflate(R.layout.actionbar_custom_title, null);
        TextView actionBarTitleTextView = (TextView) v.findViewById(R.id.actionBarTitle);
        actionBarTitleTextView.setTextColor(getResources().getColor(R.color.fab_material_black));
        actionBarTitleTextView.setTypeface(typeFace);
        actionBarTitleTextView.setText(title);

        getSupportActionBar().setCustomView(v);

        webView = (CustomWebView) findViewById(R.id.webView);
        webView.setVerticalScrollBarEnabled(false);
        webView.setGestureDetector(new GestureDetector(new CustomeGestureDetector()));

        JavaScriptInterface jsInterface = new JavaScriptInterface(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(jsInterface, "JSInterface");

        fixedPageIndicator = (TextView)findViewById(R.id.fixed_page_indicator_text_view);

        seekBar = (SeekBar)findViewById(R.id.reader_seek_bar);
        pageNoIndicator = (TextView)findViewById(R.id.pageNo_indicator_textview);
        if(type.equalsIgnoreCase("IMAGE"))
            maxProgress = pageCount-1;
        else if(type.equalsIgnoreCase("PRATILIPI")){
            if(pageCount <= 1){

            }
            else
                maxProgress = (pageCount)*1000;
        }
        seekBar.setMax(maxProgress);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int count;
                if (type.equalsIgnoreCase("PRATILIPI")) {
                    if (pageCount > 1) {
                        count = progress / 1000 + 1;

                        //For last page
                        if (count >= pageCount)
                            count = pageCount;

                        pageNoIndicator.setText("Chapter " + count+ "/" + pageCount);
                        fixedPageIndicator.setText("Chapter " + count+ "/" + pageCount);
                    }
                    else{
                        count = progress+1;
                        pageNoIndicator.setText("Page " + count + "/"+currentChapterPageCount );
                        fixedPageIndicator.setText("Page " + count + "/"+currentChapterPageCount );
                    }
                }else {
                    count = progress+1;
                    pageNoIndicator.setText("Page " + count + "/" + pageCount);
                    fixedPageIndicator.setText("Page " + count + "/" + pageCount);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                if (toast != null) {
                    toast.cancel();
                    toast = null;
                }
                pageNoIndicator.setVisibility(View.VISIBLE);

//                pageNoIndicator.setBackgroundColor(R.color.Black);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int currentProgress = seekBar.getProgress();
                if (type.equalsIgnoreCase("IMAGE"))
                    launchChapter(currentProgress + 1);
                else if (pageCount <= 1) {
                    if (currentProgress + 1 > currentChapterCurrentPage) {
                        while (currentProgress + 1 != currentChapterCurrentPage) {

                            webView.loadUrl("javascript:next()");
                            currentChapterCurrentPage++;
                        }
                    } else if (currentProgress + 1 < currentChapterCurrentPage) {
                        while (currentProgress + 1 != currentChapterCurrentPage) {

                            webView.loadUrl("javascript:previous()");
                            currentChapterCurrentPage--;
                        }
                    }

                } else {
                    int base = currentProgress / 1000;
                    int factorVal = currentProgress - base * 1000;

                    launchChapter(base + 1);

                    // pages start from 1
                    int currentPageInChapter = ((factorVal * currentChapterPageCount) / 1000) + 1;

                    // go to page within chapter
                    while (currentPageInChapter != currentChapterCurrentPage) {

                        webView.loadUrl("javascript:next()");
                        currentChapterCurrentPage++;
                    }
                }
                pageNoIndicator.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String URL = "content://com.pratilipi.pratilipi.helper.PratilipiData/metadata";
        Uri pid = Uri.parse(URL);

        Cursor c = getContentResolver().query(pid, null, PratilipiProvider.PID + "=?",
                new String[]{pratilipiId}, PratilipiProvider.PID);

        if (c.moveToFirst()){
            currentPage = (c.getInt(c.getColumnIndex(PratilipiProvider.CURRENT_CHAPTER)));
            currentChapterCurrentPage = (c.getInt(c.getColumnIndex(PratilipiProvider.CURRENT_PAGE)));
            initialScale = (c.getInt(c.getColumnIndex(PratilipiProvider.FONT_SIZE)));
        }

        launchChapter(currentPage);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != task)
            task.cancel(true);

        try {

            ContentResolver cv = getContentResolver();
            ContentValues value = new ContentValues();
            value.put(PratilipiProvider.TIME_STAMP, System.currentTimeMillis() / 1000);
            value.put(PratilipiProvider.CURRENT_CHAPTER, currentPage);
            value.put(PratilipiProvider.CURRENT_PAGE, currentChapterCurrentPage);
            value.put(PratilipiProvider.FONT_SIZE, initialScale);

            cv.update(PratilipiProvider.METADATA_URI, value, PratilipiProvider.PID + "=?",
                    new String[]{metadata.get_pid()});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
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

    private void launchChapter(boolean isNext) {
        if (isNext && currentPage < pageCount) {
            scrollToLast = false;
            currentChapterCurrentPage =1;
            makeRequest(++currentPage);
        } else if (!isNext && currentPage > 1) {
            scrollToLast = true;
            makeRequest(--currentPage);
        }
    }

    private void launchChapter(int chapterNo) {
        currentPage = chapterNo;
        scrollToLast = false;
        makeRequest(chapterNo);
    }

    private void makeRequest(final int pageNo) {

        //Set seek bar
        if (type.equalsIgnoreCase("IMAGE"))
            seekBar.setProgress(pageNo - 1);
        else {
            if (pageNo <= 1) {
//                seekBar.setProgress(0);
            } else
                seekBar.setProgress((pageNo - 1) * 1000);

        }
        // Set index
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int index = mTitleChapters.indexOf(pageNo);
                    if (index >= 0) {
                        mDrawerList.setItemChecked(index, true);
                        mDrawerList.setSelector(R.drawable.drawer_select);
                        mDrawerList.setSelection(index);
                        mDrawerList.smoothScrollToPosition(index);
                        mDrawerList.setFastScrollEnabled(true);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        String URL = "content://com.pratilipi.pratilipi.helper.PratilipiData/content";
        Uri pid = Uri.parse(URL);

        Cursor c = getContentResolver().query(pid, null, PratilipiProvider.PID + "=? and " + PratilipiProvider.CH_NO + "=?",
                new String[]{pratilipiId, pageNo + ""}, PratilipiProvider.PID);


        if (!c.moveToFirst()) {
            makeNetworkRequest(pageNo);
        } else {
            pageContent = c.getString(c.getColumnIndex(PratilipiProvider.CONTENT));
            if (type.equalsIgnoreCase("IMAGE")) {
                try {
                    image = c.getBlob(c.getColumnIndex(PratilipiProvider.IMAGE));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            parseJson();
            makeNetworkRequestWithCheck(pageNo + 1);
            makeNetworkRequestWithCheck(pageNo - 1);
        }
    }

    private void makeNetworkRequestWithCheck(int pageNo) {
        if (pageNo > 1 && pageNo <= pageCount && isOnline()) {
            String URL = "content://com.pratilipi.pratilipi.helper.PratilipiData/content";
            Uri pid = Uri.parse(URL);

            Cursor c = getContentResolver().query(pid, null, PratilipiProvider.PID + "=? and " + PratilipiProvider.CH_NO + "=?",
                    new String[]{pratilipiId, pageNo + ""}, PratilipiProvider.PID);

            if (!c.moveToFirst()) {
                if (type.equalsIgnoreCase("PRATILIPI")) {

                    task = new RequestTask();
                    task.execute(url + pratilipiId + "&pageNo=" + pageNo); //5757183006343168l
                    task.delegate = this;
                } else if (type.equalsIgnoreCase("IMAGE")) {
                    ContentValues values = new ContentValues();
                    try {
                        URL imageUrl = new URL("http://www.pratilipi.com/api.pratilipi/pratilipi/content/image?pratilipiId="
                                + pratilipiId + "&pageNo=" + pageNo);
                        URLConnection ucon = imageUrl.openConnection();

                        InputStream is = ucon.getInputStream();
                        BufferedInputStream bis = new BufferedInputStream(is);

                        ByteArrayBuffer baf = new ByteArrayBuffer(500);
                        int current = 0;
                        while ((current = bis.read()) != -1) {
                            baf.append((byte) current);
                        }
                        values.put(PratilipiProvider.PID, pId);
                        values.put(PratilipiProvider.IMAGE, baf.toByteArray());
                        values.put(PratilipiProvider.CH_NO, 1);
                    } catch (Exception e) {
                        Log.d("ImageManager", "Error: " + e.toString());
                    }
                    ContentResolver cv = getContentResolver();
                    Uri uri = cv.insert(
                            PratilipiProvider.CONTENT_URI, values);
                }
            }
        }
    }

    void makeNetworkRequest(int pageNo) {
        if (isOnline()) {
            if (type.equalsIgnoreCase("PRATILIPI")) {
                progressDialog = new ProgressDialog(webView.getContext());
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                isLoading = true;

                task = new RequestTask();
                task.execute(url + pratilipiId + "&pageNo=" + pageNo); //5757183006343168l
                task.delegate = this;
            } else if (type.equalsIgnoreCase("IMAGE")) {
                webView.setInitialScale(30);
                WebSettings webSettings = webView.getSettings();
                webView.loadUrl("http://www.pratilipi.com/api.pratilipi/pratilipi/content/image?pratilipiId="
                        + pratilipiId + "&pageNo=" + pageNo);
//                webSettings.setBuiltInZoomControls(false);
//                webSettings.setDisplayZoomControls(false);
                webSettings.setUseWideViewPort(true);
            }
        } else {
            showNoConnectionDialog(this);
        }
    }

    void parseJson() {
        try {
            if (type.equalsIgnoreCase("PRATILIPI")) {
//            WebView methods must be called on the same thread.
                webView.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

                        if (lan.equalsIgnoreCase("hi"))
                            webView.loadUrl("file:///android_asset/htmlHi.html");
                        else if (lan.equalsIgnoreCase("ta"))
                            webView.loadUrl("file:///android_asset/htmlTa.html");
                        else if (lan.equalsIgnoreCase("gu"))
                            webView.loadUrl("file:///android_asset/htmlGu.html");

                        webView.getSettings().setTextZoom(initialScale);

                        webView.setWebViewClient(new WebViewClient() {
                            public void onPageFinished(WebView view, String url) {
                                webView.loadUrl("javascript:init('" + pageContent + "')");
                                if (scrollToLast) {
                                    webView.loadUrl("javascript:last()");
                                    currentChapterCurrentPage = currentChapterPageCount;
                                }
                                else {
                                    //go to page
                                    int i = 1;
                                    while (i++ < currentChapterCurrentPage) {
                                        webView.loadUrl("javascript:next()");
                                    }
                                }
                                if (null != progressDialog)
                                    progressDialog.dismiss();
                            }
                        });
                    }
                });

                isLoading = false;
            } else if (type.equalsIgnoreCase("IMAGE")) {
                openJpeg(webView, image);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void openJpeg(WebView web, byte[] image) {
        String b64Image = Base64.encodeToString(image, Base64.DEFAULT);
        web.loadData(b64Image, "image/jpeg", "base64");
    }

    private void hideSystemUI() {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        seekBar.setVisibility(View.GONE);
        pageNoIndicator.setVisibility(View.GONE);
   //     fixedPageIndicator.setVisibility(View.GONE);
    }

    private void showSystemUI() {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        seekBar.setVisibility(View.VISIBLE);
//        pageNoIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        delayHide(INITIAL_HIDE_DELAY);
    }

    Handler mHideSystemUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            hideSystemUI();
        }
    };

    private void delayHide(int delayMillis) {
        mHideSystemUiHandler.removeMessages(0);
        mHideSystemUiHandler.sendEmptyMessageDelayed(0, INITIAL_HIDE_DELAY);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            delayHide(INITIAL_HIDE_DELAY);
        else
            mHideSystemUiHandler.removeMessages(0);
    }

    private ShareActionProvider mShareActionProvider;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_read, menu);
//           final MenuItem mItem = (MenuItem) menu.findItem(R.id.action_font);
//                mItem.getActionView();

        if (indexSize < 1) {
            menu.findItem(R.id.action_index).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_font_dec:
                changeFont(false);
                return true;
            case R.id.action_font_inc:
                changeFont(true);
                return true;
//            case R.id.action_font:
//                return true;
            case R.id.action_index:
                openIndex();
                return true;
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openIndex() {
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            mDrawerLayout.openDrawer(Gravity.RIGHT);
        }
    }

    public void changeFont(boolean isIncrease) {
        WebSettings settings = webView.getSettings();
        if (type.equalsIgnoreCase("PRATILIPI")) {
            if (isIncrease) {
                settings.setTextZoom(settings.getTextZoom() + 5);
                loadHtml();

            } else if (!isIncrease) {
                settings.setTextZoom(settings.getTextZoom() - 5);
                loadHtml();
            }
        } else {
            if (isIncrease && initialScale < 100) {
                initialScale += 10;
                webView.setInitialScale(initialScale);

            } else if (!isIncrease && initialScale > 30) {
                initialScale -= 10;
                webView.setInitialScale(initialScale);
            }
        }
    }

    private void loadHtml() {
        String lan = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("selectedLanguage", "");
        if (lan.equalsIgnoreCase("hi"))
            webView.loadUrl("file:///android_asset/htmlHi.html");
        else if (lan.equalsIgnoreCase("ta"))
            webView.loadUrl("file:///android_asset/htmlTa.html");
        else if (lan.equalsIgnoreCase("gu"))
            webView.loadUrl("file:///android_asset/htmlGu.html");
    }

    @Override
    public void processFinish(String output) {
        if (!(null == output || output.isEmpty())) {
            Log.d("Output", output);
            try {
                jsonObject = new JSONObject(output);
                pageContent = jsonObject.getString("pageContent");
                if (isLoading)
                    parseJson();

                int ch_no = jsonObject.getInt("pageNo");
                ContentValues values = new ContentValues();
                values.put(PratilipiProvider.PID, pId);
                values.put(PratilipiProvider.CONTENT, pageContent);
                values.put(PratilipiProvider.CH_NO, ch_no);
                ContentResolver cv = getContentResolver();
                Uri uri = cv.insert(
                        PratilipiProvider.CONTENT_URI, values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            currentChapterCurrentPage =1;
            launchChapter(mTitleChapters.get(position));
            hideSystemUI();
//        setTitle(mTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    private class CustomeGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            boolean visibility = (mDecorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
            if (visibility) {
                hideSystemUI();
                if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT))
                    mDrawerLayout.closeDrawer(Gravity.RIGHT);
            } else
                showSystemUI();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (diffX > 0) {
                        if (type.equalsIgnoreCase("PRATILIPI")) {
                            webView.loadUrl("javascript:previous()");

                            if (currentChapterCurrentPage > 1)
                                currentChapterCurrentPage--;

                            if (pageCount > 1) {
                                int base = (currentPage - 1) * 1000;
                                float progressFactor = (float) (currentChapterCurrentPage - 1) / (float) currentChapterPageCount;
                                int progress = (int) (progressFactor * 1000);
                                seekBar.setProgress(base + progress);
                            } else {
                                seekBar.setProgress(currentChapterCurrentPage - 1);
                            }
                        } else {
                            if (currentPage == 1) {
                                if (toast != null) {
                                    toast.cancel();
                                    toast = null;
                                }
                                toast = Toast.makeText(getApplicationContext(), "First Page!", Toast.LENGTH_SHORT);
                                toast.show();
                            } else if (initialScale <= 30) {
                                launchChapter(false);
                            }
                        }
                    } else {
                        if (type.equalsIgnoreCase("PRATILIPI")) {
                            webView.loadUrl("javascript:next()");
                            if (currentChapterCurrentPage < currentChapterPageCount) {
                                currentChapterCurrentPage++;
                                if (pageCount > 1) {
                                    int base = (currentPage - 1) * 1000;
                                    float progressFactor = (float) (currentChapterCurrentPage - 1) / (float) currentChapterPageCount;
                                    int progress = (int) (progressFactor * 1000);
                                    seekBar.setProgress(base + progress);
                                } else
                                    seekBar.setProgress(currentChapterCurrentPage - 1);
                            }
                        } else {
                            if (pageCount == currentPage) {
                                if (toast != null) {
                                    toast.cancel();
                                    toast = null;
                                }
                                toast = Toast.makeText(getApplicationContext(), "Last Page!", Toast.LENGTH_SHORT);
                                toast.show();
                            } else if (initialScale <= 30) {
                                launchChapter(true);
                            }
                        }
                    }
                } else {
                    if (diffY > 0) {
                        if (toast != null) {
                            toast.cancel();
                            toast = null;
                        }
                        toast = Toast.makeText(getApplicationContext(), "Scroll right to go to next page", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        if (toast != null) {
                            toast.cancel();
                            toast = null;
                        }
                        toast = Toast.makeText(getApplicationContext(), "Scroll left to go to previous page", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }

    public class JavaScriptInterface {
        private Activity activity;

        public JavaScriptInterface(Activity activity) {
            this.activity = activity;
        }

        @JavascriptInterface
        public void launchNextChapter() {
            Log.d("launchNextChapter", " launchNextChapter");
            if (currentPage < pageCount) {
                scrollToLast = false;
                makeRequest(++currentPage);
            } else {
                toast = Toast.makeText(getApplicationContext(), "Last Page!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        @JavascriptInterface
        public void launchPrevChapter() {
            Log.d("launchPrevChapter", " launchPrevChapter");
            if (currentPage > 1) {
                scrollToLast = true;
                makeRequest(--currentPage);
            } else if (initialScale <= 30) {
                toast = Toast.makeText(getApplicationContext(), "First Page!", Toast.LENGTH_SHORT);
                toast.show();
            }

        }

        @JavascriptInterface
        public void fetchPageCount(int pages) {
            Log.d("Pages in capter " + currentPage + " = ", pages + "");
            currentChapterPageCount = pages + 1;
//            currentChapterCurrentPage = 1;
            if (pageCount <= 1)
                seekBar.setMax(pages);
        }
    }
}
