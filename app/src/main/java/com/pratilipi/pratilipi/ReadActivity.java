package com.pratilipi.pratilipi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.regex.Matcher;

public class ReadActivity extends ActionBarActivity implements AsyncResponse {

    View mDecorView;
    View controlsView;
    int INITIAL_HIDE_DELAY = 100;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ArrayList<String> mTitles;
    private ArrayList<Integer> mTitleChapters;
    private ArrayList<String> mContents;
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final String JSON = "JSON";
    private JSONObject obj;
    private static String TAG = MainActivity.class.getSimpleName();
    private String content;
    private Matcher matcher;
    private static final Gson gson = new GsonBuilder().create();
    CustomWebView webView;
    float mStartDragX = 0;
    float mStartDragY = 0;
    private int indexSize = 0;
    private int pageCount = 0;
    private int currentPage = 1;
    String url = "http://www.pratilipi.com/api.pratilipi/pratilipi/content?pratilipiId=";
    Long pId;
    boolean scrollToLast;
    JSONObject jsonObject;
    String type;
    boolean isLoading = false;
    ProgressDialog progressDialog;
    RequestTask task;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitles = new ArrayList<>();
        mTitleChapters = new ArrayList<>();
        try {
            obj = new JSONObject(getIntent().getStringExtra(JSON));
            title = obj.getString("title");
            pId = obj.getLong("id");
            type = obj.getString("contentType");
            pageCount = obj.getInt("pageCount");

            Gson gson = new GsonBuilder().create();
            JsonArray indexArr = gson.fromJson( obj.getString("index"), JsonElement.class ).getAsJsonArray();
            if(null != indexArr) {
                indexSize = indexArr.size();
                for (int i = 0; i < indexSize; i++) {
                    JsonObject jsonObject = indexArr.get( i ).getAsJsonObject();
                    String title = jsonObject.get( "title" ).toString();
                    Log.d("TITLE",title);
                    mTitles.add(i,title.substring(1,title.length()-1));
                    mTitleChapters.add(i,Integer.parseInt(jsonObject.get("pageNo").toString()));
                }
            }

        }catch (JSONException e) {
                e.getCause();
                e.printStackTrace();
            }catch (Exception e){
        e.printStackTrace();
    }

        setContentView(R.layout.activity_read);
        controlsView = findViewById(R.id.main_layout);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_layout);
        mDrawerList = (ListView) findViewById(R.id.right_drawer);


        mDecorView  = getWindow().getDecorView();
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

        controlsView.setClickable(true);

        final GestureDetector clickDetector = new GestureDetector( this,
                new GestureDetector.SimpleOnGestureListener(){
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        boolean visibility = (mDecorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
                        if(visibility) {
                            hideSystemUI();
                            if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT))
                                mDrawerLayout.closeDrawer(Gravity.RIGHT);
                        }
                        else
                            showSystemUI();
                        return true;
                    }
                });

        controlsView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return clickDetector.onTouchEvent(event);
            }
        });


        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mTitles));
        String lan = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("selectedLanguage", "");
        Typeface typeFace = null;
        if(lan.equalsIgnoreCase("hi"))
            typeFace= Typeface.createFromAsset(getAssets(), "fonts/devanagari.ttf");
        else if(lan.equalsIgnoreCase("ta"))
            typeFace= Typeface.createFromAsset(getAssets(), "fonts/tamil.ttf");
        else if(lan.equalsIgnoreCase("gu"))
            typeFace= Typeface.createFromAsset(getAssets(), "fonts/gujarati.ttf");

        for(int i=0;i<mDrawerList.getChildCount();i++){
            TextView tv = (TextView)mDrawerList.getChildAt(i);
            tv.setTypeface(typeFace);
        }

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setIcon(R.drawable.pratilipi_logo_vector);

        LayoutInflater layoutInflate = LayoutInflater.from(this);
        View v = layoutInflate.inflate(R.layout.actionbar_custom_title, null);
        TextView actionBarTitleTextView = (TextView)v.findViewById(R.id.actionBarTitle);
        actionBarTitleTextView.setTextColor(getResources().getColor(R.color.fab_material_black));
        actionBarTitleTextView.setTypeface(typeFace);
        actionBarTitleTextView.setText(title);

        getSupportActionBar().setCustomView(v);

       webView = (CustomWebView)findViewById(R.id.webView);
       webView.setVerticalScrollBarEnabled(false);
       webView.setGestureDetector(new GestureDetector(new CustomeGestureDetector()));

        webView.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent ev) {
               final int action = ev.getAction();
               float x = ev.getX();
               float y = ev.getY();
               switch (action & MotionEventCompat.ACTION_MASK) {
                   case MotionEvent.ACTION_DOWN:
                       mStartDragX = x;
                       mStartDragY = y;
                       break;
                   case MotionEvent.ACTION_MOVE:
                   break;
                   case MotionEvent.ACTION_UP:
                       if (x > mStartDragX) {
                           if (type.equalsIgnoreCase("PRATILIPI") && (webView.getScrollY() > 1))
                               webView.scrollBy(0, -webView.getHeight());
                           else {
                               if (!isLoading) {
                                   launchChapter(false);
                               }
                           }
                       } else if (x < mStartDragX) {
                           if (type.equalsIgnoreCase("PRATILIPI") && (webView.getScrollY() < webView.getContentHeight()))
                               webView.scrollBy(0, webView.getHeight());
                           else {
                               if (!isLoading) {
                                   launchChapter(true);
                               }
                           }
                       } else
                           if (x == mStartDragX){
                           int b = mDecorView.getSystemUiVisibility();
                           int a = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

                           boolean visibility = (a & b) == 0;
                           if(visibility) {
                               hideSystemUI();
                               if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT))
                                   mDrawerLayout.closeDrawer(Gravity.RIGHT);
                           }
                           else
                               showSystemUI();
                       }
                       mStartDragX = 0;
                       break;
               }
               return true;
           }
       });

       launchChapter(1);
    }

    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }
    protected void onRestoreInstanceState(Bundle savedInstanceState){
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
        if(isNext && currentPage < pageCount) {
            makeRequest(++currentPage);
            scrollToLast = false;
        }
        else if (!isNext && currentPage > 1) {
            makeRequest(--currentPage);
            scrollToLast = true;
        }
    }

    private void launchChapter(int chapterNo) {
        currentPage = chapterNo;
        makeRequest(chapterNo);
        scrollToLast = false;
    }

    private void makeRequest(int pageNo) {
       if(isOnline()) {
           if (type.equalsIgnoreCase("PRATILIPI")) {
               progressDialog = new ProgressDialog(webView.getContext());
               progressDialog.setMessage("Loading...");
               progressDialog.show();
               isLoading = true;

               task = new RequestTask();
               task.execute(url + pId + "&pageNo=" + pageNo);
               task.delegate = this;
           } else if (type.equalsIgnoreCase("IMAGE")) {
               webView.setInitialScale(30);
               WebSettings webSettings = webView.getSettings();
               webSettings.setUseWideViewPort(true);
               webView.loadUrl("http://www.pratilipi.com/api.pratilipi/pratilipi/content/image?pratilipiId="
                       + pId + "&pageNo=" + pageNo);
               webSettings.setSupportZoom(true);
               webSettings.setBuiltInZoomControls(true);
           }
       }
       else {
            showNoConnectionDialog(this);
       }
        int index = mTitleChapters.indexOf(pageNo);
        if(index >= 0 ) {
            mDrawerList.setItemChecked(index, true);
            mDrawerList.setSelector(R.drawable.drawer_select);
            mDrawerList.setSelection(index);
        }
    }

    void parseJson() {
        try {
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

                String lan = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("selectedLanguage", "");
                if(lan.equalsIgnoreCase("hi"))
                    webView.loadUrl("file:///android_asset/htmlHi.html");
                else if(lan.equalsIgnoreCase("ta"))
                    webView.loadUrl("file:///android_asset/htmlTa.html");
                else if(lan.equalsIgnoreCase("gu"))
                    webView.loadUrl("file:///android_asset/htmlGu.html");

                webView.setWebViewClient(new WebViewClient() {

                    public void onPageFinished(WebView view, String url) {
                        try {
                            String pageContent = jsonObject.getString("pageContent");
                            Spanned parsedPageContent = Html.fromHtml(jsonObject.getString("pageContent"));
//                            Log.d("parsedPageContent",0+parsedPageContent);


                            webView.loadUrl("javascript:init('" + jsonObject.getString("pageContent") + "')");
                            if(scrollToLast) {
                                webView.postDelayed(new Runnable() {
                                    public void run() {
                                        if (webView.getProgress() == 100) {
                                            webView.postDelayed(new Runnable() {
                                                public void run() {
                                                    webView.scrollTo(0, webView.getBottom());
                                                }
                                            }, 10);
                                        }
                                    }
                                }, 10);
                            }
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                isLoading  = false;

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void hideSystemUI(){
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void showSystemUI() {
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        delayHide(INITIAL_HIDE_DELAY);
    }

    Handler mHideSystemUiHandler = new Handler()
    {
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
        if(hasFocus)
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

       if(indexSize<1){
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

    public void openIndex()
    {
        if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mDrawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            mDrawerLayout.openDrawer(Gravity.RIGHT);
        }
    }

    public void changeFont(boolean isIncrease)
    {
        if(isIncrease){
                 WebSettings settings = webView.getSettings();
                 settings.setTextZoom(settings.getTextZoom() + 5);
            webView.setInitialScale(100);
            WebSettings webSettings = webView.getSettings();
            webSettings.setSupportZoom(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setUseWideViewPort(true);
        }
        else if(!isIncrease){
                 WebSettings settings = webView.getSettings();
                 settings.setTextZoom(settings.getTextZoom() - 5);
            webView.setInitialScale(30);
        }
    }

    @Override
    public void processFinish(String output) {
        if(!(null == output || output.isEmpty())) {
            Log.d("Output", output);
            try {
                jsonObject = new JSONObject(output);
                parseJson();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(null != task)
            task.cancel(true);
    }
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            launchChapter(mTitleChapters.get(position));
            hideSystemUI();
//        setTitle(mTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    private class CustomeGestureDetector   extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                float distanceY) {
            if(distanceX > 0)
            {
                if (type.equalsIgnoreCase("PRATILIPI") && (webView.getScrollY() > 1))
                    webView.scrollBy(0, -webView.getHeight());
                else {
                    if (!isLoading) {
                        launchChapter(false);
                    }
                }
            }
            else if(distanceX < 0)
            {
                if (type.equalsIgnoreCase("PRATILIPI") && (webView.getScrollY() < webView.getContentHeight()))
                    webView.scrollBy(0, webView.getHeight());
                else {
                    if (!isLoading) {
                        launchChapter(true);
                    }
                }
            }
            return true;
        }

//        @Override
//        public boolean onTouch(View v, MotionEvent ev) {
//            final int action = ev.getAction();
//            float x = ev.getX();
//            float y = ev.getY();
//            switch (action & MotionEventCompat.ACTION_MASK) {
//                case MotionEvent.ACTION_DOWN:
//                    mStartDragX = x;
//                    mStartDragY = y;
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    break;
//                case MotionEvent.ACTION_UP:
//                   if (x > mStartDragX) {
//                       if (type.equalsIgnoreCase("PRATILIPI") && (webView.getScrollY() > 1))
//                           webView.scrollBy(0, -webView.getHeight());
//                       else {
//                           if (!isLoading) {
//                               launchChapter(false);
//                           }
//                       }
//                   } else if (x < mStartDragX) {
//                       if (type.equalsIgnoreCase("PRATILIPI") && (webView.getScrollY() < webView.getContentHeight()))
//                           webView.scrollBy(0, webView.getHeight());
//                       else {
//                           if (!isLoading) {
//                               launchChapter(true);
//                           }
//                       }
//                   } else
//                    if (x == mStartDragX){
//                        int b = mDecorView.getSystemUiVisibility();
//                        int a = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//
//                        boolean visibility = (a & b) == 0;
//                        if(visibility) {
//                            hideSystemUI();
//                            if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT))
//                                mDrawerLayout.closeDrawer(Gravity.RIGHT);
//                        }
//                        else
//                            showSystemUI();
//                    }
//                    mStartDragX = 0;
//                    break;
//            }
//            return true;
//        }
    }
}
