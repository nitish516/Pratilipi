package com.pratilipi.pratilipi;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pratilipi.pratilipi.helper.FontProvider;
import com.pratilipi.pratilipi.util.CustomViewPager;


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
    private String[] mPlanetTitles;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private CustomViewPager pagesView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        controlsView = findViewById(R.id.main_layout);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_layout);
        mDrawerList = (ListView) findViewById(R.id.right_drawer);

        mPlanetTitles = getResources().getStringArray(R.array.planets_array);

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
                R.layout.drawer_list_item, mPlanetTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        selectItem(0);
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
//        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
}
