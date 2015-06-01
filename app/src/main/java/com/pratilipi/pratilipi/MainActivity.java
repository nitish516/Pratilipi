
package com.pratilipi.pratilipi;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
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
import android.support.v7.internal.view.menu.MenuItemImpl;
import android.widget.Toast;
//import android.support.v7.app.AppCompatActivity;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.pratilipi.pratilipi.DataFiles.Metadata;
import com.pratilipi.pratilipi.adapter.GridViewImageAdapter;
import com.pratilipi.pratilipi.helper.AppConstant;
import com.pratilipi.pratilipi.helper.PratilipiProvider;
import com.software.shell.fab.ActionButton;

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
    SearchView searchViewButton;

//    public MainActivity() {
////        searchView = null;
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.action_search, menu);
//        searchView.setIconified(true);
//        searchView.setIconifiedByDefault(true);
//        searchView.setActivated(true);
//        searchView.setQueryHint("Search View Hint");
//        searchViewButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
//                startActivity(searchIntent);            }
//        });


//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                //Log.e("onQueryTextChange", "called");
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                Intent in = new Intent(getApplicationContext(), MoreFeaturedBooks.class);
//                in.putExtra("TITLE",query);
//                startActivity(in);
//
//                return true;
//            }
//
//        });
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_search_item:
                Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(searchIntent);
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        String lan = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("selectedLanguage", "");

            if (lan.length() < 1) {
                startActivity(new Intent(this, LanguageSelectionActivity.class));
                finish();
            }
        if(null!= searchViewButton)
            searchViewButton.clearFocus();
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
        actionBar.setLogo(R.drawable.pratilipi_logo_vector);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setHomeButtonEnabled(true);

//        actionBar.setDisplayHomeAsUpEnabled(true);

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

        //Floating Action Button
        final ActionButton actionButtonPrevious = (ActionButton)findViewById(R.id.action_fab);

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

        //Floating Action Button
        actionButtonPrevious.setType(ActionButton.Type.DEFAULT);
        actionButtonPrevious.setButtonColor(getResources().getColor(R.color.fab_material_white));
        actionButtonPrevious.setButtonColorPressed(getResources().getColor(R.color.fab_material_white));
        actionButtonPrevious.setImageResource(R.drawable.unnamed);
        actionButtonPrevious.cancelLongPress();
        actionButtonPrevious.setButtonColorRipple(getResources().getColor(R.color.fab_material_grey_500));
        actionButtonPrevious.setShadowResponsiveEffectEnabled(false);
        actionButtonPrevious.setRippleEffectEnabled(true);

//         actionButtonPrevious.setOnClickListener(new View.OnClickListener() {
//             @Override
//             public void onClick(View view) {
//                Intent nxt = new Intent(MainActivity.this, ReadPrevious.class);
//                startActivity(nxt);
//                 if (null != savedInstanceState) {
//                     ReadActivity read = new ReadActivity();
//                     read.onSaveInstanceState(savedInstanceState);
//                     read.onRestoreInstanceState(savedInstanceState);
//                 } else {
//                     Toast.makeText(getApplicationContext(), "No Previous Book Read", Toast.LENGTH_SHORT).show();
//                 }
//             }
//         });
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

    public static class HomeFragment extends Fragment implements AsyncResponse{

        private ArrayList<Metadata> mMetaData ;
        private ProgressBar pBar;
        private LinearLayout featuredList;
        private LinearLayout newReleasesList;
        private ProgressDialog pDialog;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            RadioButton rb = (RadioButton) rootView.findViewById(R.id.radio_top);
            rb.setChecked(true);

            pBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);
            featuredList = (LinearLayout) rootView.findViewById(R.id.linear_layout_featured);
            newReleasesList = (LinearLayout)rootView.findViewById(R.id.linear_layout_new_releases);

            if(isOnline())
                makeJsonArryReq();
            else
            {
                showNoConnectionDialog(getActivity());
            }
//            adapter = new CustomArrayAdapter(rootView.getContext(), mMetaData);
//            LinearLayout lv = (LinearLayout) rootView.findViewById(R.id.linear_layout);
//            lv.setAdapter(adapter);
//            ListView lv2 = (ListView) rootView.findViewById(R.id.listview_featured);
//            lv2.setAdapter(adapter);
            return rootView;
        }

        private void showProgressDialog() {
            pBar.setVisibility(View.VISIBLE);
        }
        private void hideProgressDialog() {
            pBar.setVisibility(View.GONE);
        }

        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
            String lan = getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("selectedLanguage", "");
            Long lanId = null;
            if(lan.equalsIgnoreCase("hi"))
                lanId = 5130467284090880l;
            else if(lan.equalsIgnoreCase("ta"))
                lanId = 6319546696728576l;
            else if(lan.equalsIgnoreCase("gu"))
                lanId = 5965057007550464l;

            task.execute("http://www.pratilipi.com/api.pratilipi/mobileinit?languageId="+lanId);
            task.delegate = this;
        }

        void parseJson(JSONObject response)
        {
            try {
                   JSONArray topReadPratilipiDataList = response.getJSONArray("topReadPratilipiDataList");
                   for (int i = 0; i < topReadPratilipiDataList.length(); i++) {
                       final JSONObject obj = topReadPratilipiDataList.getJSONObject(i);
                       if (!obj.getString("state").equalsIgnoreCase("PUBLISHED"))
                           continue;
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
                       RatingBar ratingBar = (RatingBar) viewItemlayout.findViewById(R.id.averageRatingRatingBar);
                       TextView ratingNum = (TextView) viewItemlayout.findViewById(R.id.ratingNumber);
                       if (obj.getLong("ratingCount") > 0) {
                           ratingBar.setRating((float) obj.getLong("starCount") / obj.getLong("ratingCount"));
                           ratingNum.setText((String.valueOf("(" + (obj.getLong("ratingCount") + ")"))));
                       }
                       // Populate the image
                       imageView.setImageUrl("http:" + metaData.get_coverImageUrl(), imageLoader);
                       featuredList.addView(viewItemlayout);

                       viewItemlayout.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               Intent i = new Intent(getActivity(), DetailPageActivity.class);
                               i.putExtra(DetailPageActivity.JSON,  obj.toString());
                               getActivity().startActivity(i);
                           }
                       });
                   }
                for (int i = topReadPratilipiDataList.length()-1; i >=0 ; i--) {

                    final JSONObject obj = topReadPratilipiDataList.getJSONObject(i);
                    if (!obj.getString("state").equalsIgnoreCase("PUBLISHED"))
                        continue;
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

                    LinearLayout layout =  (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.viewitem, null);
                       ImageLoader imageLoader1 = AppController.getInstance().getImageLoader();
                       NetworkImageView imageView1 = (NetworkImageView) layout.findViewById(R.id.image);
                       RatingBar ratingBar1  = (RatingBar) layout.findViewById(R.id.averageRatingRatingBar);
                       TextView ratingNum1 = (TextView)layout.findViewById(R.id.ratingNumber);
                       if(obj.getLong("ratingCount")> 0) {
                           ratingBar1.setRating((float) obj.getLong("starCount") / obj.getLong("ratingCount"));
                           ratingNum1.setText((String.valueOf("("+(obj.getLong("ratingCount")+")"))));
                       }
                       // Populate the image
                       imageView1.setImageUrl("http:" +metaData.get_coverImageUrl(), imageLoader1);
                       newReleasesList.addView((layout));

                       layout.setOnClickListener(new View.OnClickListener() {
                                                             @Override
                                                             public void onClick(View v) {
                                 Intent i = new Intent(getActivity(), DetailPageActivity.class);
                                 i.putExtra(DetailPageActivity.JSON,  obj.toString());
                                 getActivity().startActivity(i);
                             }
                         });
                    }

                LinearLayout morebtnlayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.more_btn_layout, null);
                featuredList.addView(morebtnlayout);

                View moreBttn = morebtnlayout.findViewById(R.id.more_btn_click);

                moreBttn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent in = new Intent(getActivity(), MoreFeaturedBooks.class);
                        in.putExtra("TITLE","Featured");
                        startActivity(in);
                    }
                });

                LinearLayout morebtnlayout1 = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.more_btn_layout, null);
                newReleasesList.addView((morebtnlayout1));
                View moreBttn1 = morebtnlayout1.findViewById(R.id.more_btn_click);

                moreBttn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent in = new Intent(getActivity(), MoreFeaturedBooks.class);
                        in.putExtra("TITLE","New Releases");
                        startActivity(in);
                    }
                });

                } catch (JSONException e) {
                e.printStackTrace();
            }
//            adapter.notifyDataSetChanged();
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
//            gridView = (GridView) rootView.findViewById(R.id.grid_view);
//            utils = new com.pratilipi.pratilipi.util.Utils(getActivity());
//
//            // Initilizing Grid View
//            InitilizeGridLayout();
//
//            // loading all image paths from SD card
//            imagePaths = utils.getFilePaths();
//
//            // Gridview adapter
//            adapter = new GridViewImageAdapter(getActivity(), imagePaths,
//                    columnWidth);
//
//            // setting grid view adapter
//            gridView.setAdapter(adapter);

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
}
