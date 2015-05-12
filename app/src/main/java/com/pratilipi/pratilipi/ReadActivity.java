package com.pratilipi.pratilipi;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pratilipi.pratilipi.helper.FontProvider;
import com.pratilipi.pratilipi.util.CustomViewPager;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadActivity extends ActionBarActivity implements ReaderFragment.OnSwipeListener{

    View mDecorView;
    View controlsView;
    int INITIAL_HIDE_DELAY = 100;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private FontProvider mFontProvider;
    private float fontSize = 20;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ArrayList<String> mTitles;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private CustomViewPager pagesView;
    public static final String JSON = "JSON";
    private JSONObject obj;
    private static String TAG = MainActivity.class.getSimpleName();
    private String content;
    private Matcher matcher;
    private static final Gson gson = new GsonBuilder().create();


    private static final String pageBreak = "<div style=\"page-break-after:always\"></div>";
    private static final Pattern pageBreakPattern = Pattern.compile(
            "<div style=\"page-break-after:always\"></div>" // Pratilipi
                    + "|"
                    + "<div\\s+style=\"page-break-(before|after).+?>(.+?)</div>" // CK Editor
                    + "|"
                    + "<hr\\s+style=\"page-break-(before|after).+?>" // MS Word
    );
//
//    private static final Pattern titlePattern = Pattern.compile(
//            "<h1.*?>(<.+?>)*(?<title>.+?)(</.+?>)*</h1>"
//                    + "|"
//                    + "<h2.*?>(<.+?>)*(?<subTitle>.+?)(</.+?>)*</h2>" );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitles = new ArrayList<>();
        try {
            obj = new JSONObject(getIntent().getStringExtra(JSON));
            Long pId = obj.getLong("id");
            String type = obj.getString("contentType");
            String url = "";
            if(type.equalsIgnoreCase("PRATILIPI"))
                url = "http://www.pratilipi.com/api.pratilipi/pratilipi/content?pratilipiId=";
            else if(type.equalsIgnoreCase("IMAGE"))
                url ="http://www.pratilipi.com/api.pratilipi/pratilipi/content/image?pratilipiId=";
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, url+pId, null,
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
            Gson gson = new GsonBuilder().create();
            JsonArray indexArr = gson.fromJson( obj.getString("index"), JsonElement.class ).getAsJsonArray();
            for (int i = 0; i < indexArr.size(); i++) {
                JsonObject jsonObject = indexArr.get( i ).getAsJsonObject();
                String title = jsonObject.get( "title" ).toString();
                Log.d("TITLE",title);
                mTitles.add(i,title.substring(1,title.length()-1));
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
        View.OnSystemUiVisibilityChangeListener(){

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
        selectItem(0);
    }

    void parseJson(JSONObject response) {
        try {
            content = response.getString("pageContent");

            matcher = pageBreakPattern.matcher( content );
            Log.d(TAG,content);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getContent( int pageNo ) {

        matcher.reset();

        int pageCount = 0;
        int startIndex = 0;
        int endIndex = 0;
        String pageContent = null;

        while( endIndex < content.length() ) {
            pageCount++;
            startIndex = endIndex;

            if( matcher.find() ) {
                endIndex = matcher.end();
                pageContent = content.substring( startIndex, matcher.start() );
            } else {
                endIndex = content.length();
                pageContent = content.substring( startIndex );
            }

            if( pageCount == pageNo )
                break;
        }

        return pageContent;
    }

    public int getPageCount() {

        matcher.reset();

        int pageCount = 0;
        int startIndex = 0;
        int endIndex = 0;

        while( endIndex < content.length() ) {
            pageCount++;
            startIndex = endIndex;

            if( matcher.find() )
                endIndex = matcher.end();
            else
                endIndex = content.length();
        }

        return pageCount;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_read, menu);
        if(mTitles.size()<1){
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
            case R.id.action_index:
                openIndex();
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
        if(isIncrease && fontSize < 30){
            fontSize += 5;
            selectItem(0);
        }
        else if(!isIncrease && fontSize > 10){
            fontSize -=5;
            selectItem(0);
        }
    }

    @Override
    public void onSwipeOut(boolean isEnd, String position) {
        selectItem(0);
    }

    @Override
    public void onTouch() {
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

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
            hideSystemUI();
        }
    }
    private void selectItem(int position) {
        Fragment fragment = new ReaderFragment();
        Bundle args = new Bundle();
        args.putFloat("fontSize", fontSize);
        args.putInt("position", position);
        fragment.setArguments(args);

        android.app.FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.content_frame, ReaderFragment.newInstance(position + 1))
//                .commit();
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
//        setTitle(mTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
}
