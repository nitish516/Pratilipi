package com.pratilipi.pratilipi;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;

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

public class ReadActivity extends ActionBarActivity {

    View mDecorView;
    View controlsView;
    int INITIAL_HIDE_DELAY = 100;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ArrayList<String> mTitles;
    private ArrayList<String> mContents;
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final String JSON = "JSON";
    private JSONObject obj;
    private static String TAG = MainActivity.class.getSimpleName();
    private String content;
    private Matcher matcher;
    private static final Gson gson = new GsonBuilder().create();
    WebView webView;
    float mStartDragX = 0;
    private int indexSize = 0;
    private int pageCount = 0;
    private int currentPage = 1;
    String url = "";
    Long pId;
    boolean scrollToLast;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitles = new ArrayList<>();
        try {
            obj = new JSONObject(getIntent().getStringExtra(JSON));
            pId = obj.getLong("id");
            String type = obj.getString("contentType");
            if(type.equalsIgnoreCase("PRATILIPI"))
                url = "http://www.pratilipi.com/api.pratilipi/pratilipi/content?pratilipiId=";
            else if(type.equalsIgnoreCase("IMAGE"))
                url ="http://www.pratilipi.com/api.pratilipi/pratilipi/content/image?pratilipiId=";

            Gson gson = new GsonBuilder().create();
            JsonArray indexArr = gson.fromJson( obj.getString("index"), JsonElement.class ).getAsJsonArray();
            if(null != indexArr) {
                indexSize = indexArr.size();
                for (int i = 0; i < indexSize; i++) {
                    JsonObject jsonObject = indexArr.get( i ).getAsJsonObject();
                    String title = jsonObject.get( "title" ).toString();
                    Log.d("TITLE",title);
                    mTitles.add(i,title.substring(1,title.length()-1));
                }
            }

            pageCount = obj.getInt("pageCount");

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
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       webView = (WebView)findViewById(R.id.webView);
       webView.setVerticalScrollBarEnabled(false);
       webView.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent ev) {
               final int action = ev.getAction();
               float x = ev.getX();
               switch(action & MotionEventCompat.ACTION_MASK){
                   case MotionEvent.ACTION_DOWN:
                       mStartDragX = x;
                       break;
                   case MotionEvent.ACTION_MOVE:
                   break;
                   case MotionEvent.ACTION_UP:
                       if (x>mStartDragX){
                           if( webView.getScrollY() > 1 )
                                webView.scrollBy(0,-webView.getHeight());
                           else
                               launchChapter(false);
                       }else if(x<mStartDragX){
                           if( webView.getScrollY() < webView.getContentHeight())
                               webView.scrollBy(0,webView.getHeight());
                           else
                               launchChapter(true);
                       }
                       else {
                           int b =  mDecorView.getSystemUiVisibility();
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
       launchChapter(0);
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
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url+pId+"&pageNo="+pageNo, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        parseJson(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq,
                "jobj_req");
    }

    void parseJson(JSONObject response) {
        try {
            webView.loadData(response.getString("pageContent"),"text/html",null);
            if(scrollToLast)
                webView.scrollTo(0,webView.getContentHeight());

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
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
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
//        if(isIncrease && fontSize < 30){
//            fontSize += 5;
////            selectItem(0);
//        }
//        else if(!isIncrease && fontSize > 10){
//            fontSize -=5;
////            selectItem(0);
//        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            launchChapter(position);
            hideSystemUI();
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
//        setTitle(mTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
        }
    }
}
