
package com.pratilipi.pratilipi;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.pratilipi.pratilipi.DataFiles.Metadata;
import com.pratilipi.pratilipi.adapter.CustomArrayAdapter;
import com.pratilipi.pratilipi.adapter.GridViewImageAdapter;
import com.pratilipi.pratilipi.helper.AppConstant;
import com.pratilipi.pratilipi.helper.PratilipiProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;
    private static String TAG = MainActivity.class.getSimpleName();


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.action_search, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()) {

            case R.id.action_search_item:
                 Intent inti = new Intent(MainActivity.this, SearchActivity.class);
                 startActivity(inti);
                 return true;
            default: Toast.makeText(getApplicationContext(), "Wrong Input", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        String lan = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("selectedLanguage", "");

            if (lan.length() < 1) {
                startActivity(new Intent(this, LanguageSelectionActivity.class));
                finish();
            }
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(new android.support.v7.app.ActionBar.TabListener() {
                                @Override
                                public void onTabSelected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
                                    mViewPager.setCurrentItem(tab.getPosition());
                                }

                                @Override
                                public void onTabUnselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

                                }

                                @Override
                                public void onTabReselected(android.support.v7.app.ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

                                }
                            }));
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new HomeFragment();
                case 1:
                    return new CategoriesFragment();
                case 2:
                    // The first section of the app is the most interesting -- it offers
                    // a launchpad into the other demonstrations in this example application.
                    return new ShelfFragment();

                default:
                    // The other sections of the app are dummy placeholders.
                    Fragment fragment = new CategoriesFragment();
                    Bundle args = new Bundle();
                    args.putInt(CategoriesFragment.ARG_SECTION_NUMBER, i + 1);
                    fragment.setArguments(args);
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position)
            {
                case 0:
                    return getResources().getString(R.string.title_home);
                case 1:
                    return getResources().getString(R.string.title_categories);
                case 2:
                    return getResources().getString(R.string.title_shelf);
            }

            return "Section " + (position + 1);
        }
    }

    public static class HomeFragment extends Fragment {


        //      String _pid, String _title, String _contentType, String _authorId, String _authorFullName, String _ch_count, String _index, String _coverImageUrl, String _pageUrl
        private ArrayList<Metadata> mMetaData ;
        CustomArrayAdapter adapter;
        private ProgressBar pBar;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            RadioButton rb = (RadioButton) rootView.findViewById(R.id.radio_top);
            rb.setChecked(true);

            pBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);
            mMetaData = new ArrayList<Metadata>();
            for(int i=0;i<6;i++)
                mMetaData.add(new Metadata());
            makeJsonArryReq((LinearLayout) rootView.findViewById(R.id.linear_layout_featured) );
//            adapter = new CustomArrayAdapter(rootView.getContext(), mMetaData);
//            LinearLayout lv = (LinearLayout) rootView.findViewById(R.id.linear_layout);
//            lv.setAdapter(adapter);
//            ListView lv2 = (ListView) rootView.findViewById(R.id.listview_featured);
//            lv2.setAdapter(adapter);
            return rootView;
        }
        private ProgressDialog pDialog;

        private void showProgressDialog() {
                pBar.setVisibility(View.VISIBLE);
        }

        private void hideProgressDialog() {
                pBar.setVisibility(View.GONE);
        }
        /**
         * Making json array request
         * */
        private void makeJsonArryReq(final LinearLayout layout) {
          showProgressDialog();
            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    "http://www.pratilipi.com/api.pratilipi/mobileinit?languageId=5130467284090880", null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
                            parseJson(response,layout);
                            hideProgressDialog();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    hideProgressDialog();
                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(jsonObjReq,
                    "jobj_req");

            // Cancelling request
            // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_arry);
        }

        void parseJson(JSONObject response, LinearLayout layout)
        {
            try {
                   JSONArray topReadPratilipiDataList = response.getJSONArray("topReadPratilipiDataList");
                   for (int i = 0; i < topReadPratilipiDataList.length(); i++) {
                        final JSONObject obj = topReadPratilipiDataList.getJSONObject(i);
                        //      String _pid, String _title, String _contentType, String _authorId, String _authorFullName, String _ch_count, String _index, String _coverImageUrl, String _pageUrl
                        final Metadata metaData = new Metadata(
                                obj.getString("id"),
                                obj.getString("title"),
                                obj.getString("type"),
                                obj.getString("authorId"),
                                obj.getJSONObject("author").getString("name"),
                                "",
                                "",
                                obj.getString("coverImageUrl"),
                                obj.getString("pageUrl")
                                );
//                              mMetaData.add(metaData);

                         LinearLayout viewItemlayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.viewitem, null);
                         ImageLoader imageLoader = AppController.getInstance().getImageLoader();

                         NetworkImageView imageView = (NetworkImageView) viewItemlayout.findViewById(R.id.image);
                         RatingBar ratingBar  = (RatingBar) viewItemlayout.findViewById(R.id.averageRatingRatingBar);
                         TextView ratingNum = (TextView)viewItemlayout.findViewById(R.id.ratingNumber);
                         if(obj.getLong("ratingCount")> 0) {
                             ratingBar.setRating((float) obj.getLong("starCount") / obj.getLong("ratingCount"));
                             ratingNum.setText((String.valueOf("("+(obj.getLong("ratingCount")+")"))));
                         }
                         // Populate the image
                         imageView.setImageUrl("http:" +metaData.get_coverImageUrl(), imageLoader);
                         layout.addView(viewItemlayout);
                       viewItemlayout.setOnClickListener(new View.OnClickListener() {
                                                             @Override
                                                             public void onClick(View v) {
                                                                 Intent i = new Intent(getActivity(), DetailPageActivity.class);
                                                                 i.putExtra(DetailPageActivity.JSON,  obj.toString());
                                                                 getActivity().startActivity(i);
                                                             }
                                                         }
                       );
                    }
                } catch (JSONException e) {
                e.printStackTrace();
            }
//            adapter.notifyDataSetChanged();
        }
    }

    /**
     * A fragment that launches shelf part of application.
     */
    public static class ShelfFragment extends Fragment {

        private com.pratilipi.pratilipi.util.Utils utils;
        private ArrayList<String> imagePaths = new ArrayList<String>();
        private GridViewImageAdapter adapter;
        private GridView gridView;
        private int columnWidth;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_shelf, container, false);
            gridView = (GridView) rootView.findViewById(R.id.grid_view);
            utils = new com.pratilipi.pratilipi.util.Utils(getActivity());

            // Initilizing Grid View
            InitilizeGridLayout();

            // loading all image paths from SD card
            imagePaths = utils.getFilePaths();

            // Gridview adapter
            adapter = new GridViewImageAdapter(getActivity(), imagePaths,
                    columnWidth);

            // setting grid view adapter
            gridView.setAdapter(adapter);

            return rootView;
        }

        private void InitilizeGridLayout() {
            Resources r = getResources();
            float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    AppConstant.GRID_PADDING, r.getDisplayMetrics());

            columnWidth = (int) ((utils.getScreenWidth() - ((AppConstant.NUM_OF_COLUMNS + 1) * padding)) / AppConstant.NUM_OF_COLUMNS);

            gridView.setNumColumns(AppConstant.NUM_OF_COLUMNS);
            gridView.setColumnWidth(columnWidth);
            gridView.setStretchMode(GridView.NO_STRETCH);
            gridView.setPadding((int) padding, (int) padding, (int) padding,
                    (int) padding);
            gridView.setHorizontalSpacing((int) padding);
            gridView.setVerticalSpacing((int) padding);
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class CategoriesFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            String[] categoriesArray = {
                    "Classic","Horror","Poems","Romance","Stories","Gazals"
            };
            List<String> listCategories = new ArrayList<String>(
                    Arrays.asList(categoriesArray));

            ArrayAdapter<String> mCategoriesAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.listitem_categories_textview,
                    R.id.textview_categories,
                    listCategories
            );

            View rootView = inflater.inflate(R.layout.fragment_categories, container, false);
            ListView linearLayout = (ListView) rootView.findViewById(R.id.listview_categories);
            linearLayout.setAdapter(mCategoriesAdapter);
            return rootView;
        }
    }

    public void addData(View view) {
        ContentValues values = new ContentValues();
        values.put(PratilipiProvider.PID , "6288435027378176");
        values.put(PratilipiProvider.CH_NO, "1");
        values.put(PratilipiProvider.CONTENT, "हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।");

        ContentResolver cv = getContentResolver();
        Uri uri = cv.insert(
                PratilipiProvider.CONTENT_URI, values);

        values.put(PratilipiProvider.PID , "6288435027378176");
        values.put(PratilipiProvider.CH_NO, "2");
        values.put(PratilipiProvider.CONTENT, "हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।");
        uri = cv.insert(PratilipiProvider.CONTENT_URI, values);
        values.put(PratilipiProvider.PID , "6288435027378176");
        values.put(PratilipiProvider.CH_NO, "3");
        values.put(PratilipiProvider.CONTENT, "हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।हिन्दी साहित्य के सबसे उम्दा व्यंगकार श्री जितेन्द्र 'जीतू' जी का ये व्यंग्य संग्रह समसामयिक विषयों पे एक नयी तरह से प्रकाश डालता है। हर एक व्यंग्य ना केवल पाठकों को मुस्कुराने पर मजबूर करता है, बल्कि साथ ही साथ सोचने के लिये बाध्य भी करता है।");
        uri = cv.insert(PratilipiProvider.CONTENT_URI, values);
        values = new ContentValues();
        values.put(PratilipiProvider.PID , "6288435027378176");
        values.put(PratilipiProvider.CH_COUNT, "3");
        values.put(PratilipiProvider.INDEX, "abc,def,ghi");
        cv.insert(PratilipiProvider.METADATA_URI,values);
    }


    public void showAllContent(View view) {
        // Show all the birthdays sorted by friend's name
        String URL = "content://com.pratilipi.pratilipi.helper.PratilipiData/content";
        Uri pid = Uri.parse(URL);
        Cursor c = getContentResolver().query(pid, null, null, null, PratilipiProvider.PID);
        String result = "Results:";

        if (!c.moveToFirst()) {
            Toast.makeText(this, result + " no content yet!", Toast.LENGTH_LONG).show();
        }else{
            do{
                result = result + "\n" + c.getString(c.getColumnIndex(PratilipiProvider.PID));
            } while (c.moveToNext());
            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        }
    }
}
