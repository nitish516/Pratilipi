
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
//import android.support.v7.app.AppCompatActivity;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.pratilipi.pratilipi.DataFiles.Metadata;
import com.pratilipi.pratilipi.adapter.GridViewImageAdapter;
import com.pratilipi.pratilipi.helper.AppConstant;
import com.pratilipi.pratilipi.helper.PratilipiProvider;
import com.software.shell.fab.ActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
    ViewPager mViewPager;
    private static String TAG = MainActivity.class.getSimpleName();
    SearchView searchViewButton;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.action_search, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.action_search_item:
                Intent searchIntent = new Intent(MainActivity.this, CardListActivity.class);
                searchIntent.putExtra("TITLE","Search");
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
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);

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
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
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
                case 3:
                    return new ProfileFragment();

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
                case 3:
                    return getResources().getString(R.string.title_profile);
            }

            return "Section " + (position + 1);
        }
    }

    public static class HomeFragment extends Fragment implements AsyncResponse{

        private ArrayList<Metadata> mMetaData ;
        private ProgressBar pBar;
        private LinearLayout featuredList;
        private LinearLayout newReleasesList;
        private LinearLayout topReadsList;
        private ProgressDialog pDialog;
        private float DecimalRating;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            RadioButton rb = (RadioButton) rootView.findViewById(R.id.radio_top);
            rb.setChecked(true);

            pBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);
            featuredList = (LinearLayout) rootView.findViewById(R.id.linear_layout_featured);
            newReleasesList = (LinearLayout)rootView.findViewById(R.id.linear_layout_new_releases);
            topReadsList = (LinearLayout)rootView.findViewById(R.id.linear_layout_top);

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


            final View viewAllFeatured = rootView.findViewById(R.id.view_more_featured_layout);
            viewAllFeatured.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LaunchCardView("Featured", viewAllFeatured);
                }
            });

            final View viewAllNewReleases = rootView.findViewById(R.id.view_more_newrelease_layout);

            viewAllNewReleases.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LaunchCardView("New Releases", viewAllNewReleases);
                }
            });

            final View viewAllTopReads = rootView.findViewById(R.id.view_more_top_layout);

            viewAllTopReads.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LaunchCardView("Top Reads", viewAllTopReads);
                }
            });

            return rootView;

        }

        private void LaunchCardView(String input, View view){

//            view.setBackgroundColor(getResources().getColor(R.color.Gray200));

            Intent NewReleaseIntent = new Intent(getActivity(), CardListActivity.class);
            NewReleaseIntent.putExtra("TITLE",input);
            startActivity(NewReleaseIntent);

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
            JsonArray featuredPratilipiDataList = null;
            JsonArray newReleasesPratilipiDataList = null;
            JsonArray topReadsPratilipiDataList = null;
            Gson gson = new GsonBuilder().create();

            try {
                String responseStr = response.getString("response");
                Log.d("responseStr",responseStr);
                JsonObject responseObj = gson.fromJson( responseStr, JsonElement.class ).getAsJsonObject();
                JsonArray elementArr  = responseObj.get("elements").getAsJsonArray();
                Log.d("element",""+elementArr);

                for (int i=0; i<elementArr.size(); i++) {
                    Log.d("parts at ", i + " " + elementArr.get(i));
                    JsonObject elementObj = gson.fromJson(elementArr.get(i), JsonElement.class).getAsJsonObject();
                    Log.d("elementObj", i + " " + elementObj);
                    JsonArray contentArr = elementObj.get("content").getAsJsonArray();
                    String type = elementObj.get("name").getAsString();
                    if(type.equalsIgnoreCase("Featured")){
                        featuredPratilipiDataList = contentArr;
                    }
                    else if(type.equalsIgnoreCase("New Releases")){
                        newReleasesPratilipiDataList = contentArr;
                    }
                    else if(type.equalsIgnoreCase("Top Reads")){
                        topReadsPratilipiDataList = contentArr;
                    }
                }

                for (int i = 0; i < featuredPratilipiDataList.size(); i++) {
                    final JsonObject obj = gson.fromJson( featuredPratilipiDataList.get(i), JsonElement.class ).getAsJsonObject();
                    if (!obj.get("state").getAsString().equalsIgnoreCase("PUBLISHED"))
                        continue;

                    CardView cardView = (CardView) getActivity().getLayoutInflater().inflate(R.layout.viewitem, null);
                    LinearLayout viewItemlayout = (LinearLayout)cardView.findViewById(R.id.view_item_layout);
                    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

                    NetworkImageView imageView = (NetworkImageView) viewItemlayout.findViewById(R.id.image);
                    RatingBar ratingBar = (RatingBar) viewItemlayout.findViewById(R.id.averageRatingRatingBar);
                    TextView ratingNum = (TextView) viewItemlayout.findViewById(R.id.ratingNumber);
                    if (obj.get("ratingCount").getAsLong() > 0) {
                        DecimalRating = ((float) obj.get("starCount").getAsLong() / obj.get("ratingCount").getAsLong());
                        ratingBar.setRating(DecimalRating);
                        ratingNum.setText((String.valueOf("(" + (obj.get("ratingCount").getAsLong() + ")"))));
                    }
                    // Populate the image
                    imageView.setImageUrl("http:" + obj.get("coverImageUrl").getAsString(), imageLoader);
                    featuredList.addView(cardView);

                    viewItemlayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getActivity(), DetailPageActivity.class);
                            i.putExtra(DetailPageActivity.JSON,  obj.toString());
                            getActivity().startActivity(i);
                        }
                    });
                }


                for (int i = 0; i < newReleasesPratilipiDataList.size(); i++) {
                    final JsonObject obj = gson.fromJson( newReleasesPratilipiDataList.get(i), JsonElement.class ).getAsJsonObject();
                    if (!obj.get("state").getAsString().equalsIgnoreCase("PUBLISHED"))
                        continue;

                    CardView cardView1 = (CardView) getActivity().getLayoutInflater().inflate(R.layout.viewitem, null);
                    LinearLayout layout =  (LinearLayout) cardView1.findViewById(R.id.view_item_layout);
                    ImageLoader imageLoader1 = AppController.getInstance().getImageLoader();
                    NetworkImageView imageView1 = (NetworkImageView) layout.findViewById(R.id.image);
                    RatingBar ratingBar1  = (RatingBar) layout.findViewById(R.id.averageRatingRatingBar);
                    TextView ratingNum1 = (TextView)layout.findViewById(R.id.ratingNumber);
                    if(obj.get("ratingCount").getAsLong()> 0) {
                        ratingBar1.setRating((float) obj.get("starCount").getAsLong() / obj.get("ratingCount").getAsLong());
                        ratingNum1.setText((String.valueOf("("+(obj.get("ratingCount").getAsLong()+")"))));
                    }
                    // Populate the image
                    imageView1.setImageUrl("http:" +obj.get("coverImageUrl").getAsString(), imageLoader1);
                    newReleasesList.addView((cardView1));

                    layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getActivity(), DetailPageActivity.class);
                            i.putExtra(DetailPageActivity.JSON,  obj.toString());
                            getActivity().startActivity(i);
                        }
                    });
                }

                for (int i = 0; i < topReadsPratilipiDataList.size(); i++) {
                    final JsonObject obj = gson.fromJson( topReadsPratilipiDataList.get(i), JsonElement.class ).getAsJsonObject();
                    if (!obj.get("state").getAsString().equalsIgnoreCase("PUBLISHED"))
                        continue;

                    CardView cardView1 = (CardView) getActivity().getLayoutInflater().inflate(R.layout.viewitem, null);
                    LinearLayout layout =  (LinearLayout) cardView1.findViewById(R.id.view_item_layout);
                    ImageLoader imageLoader1 = AppController.getInstance().getImageLoader();
                    NetworkImageView imageView1 = (NetworkImageView) layout.findViewById(R.id.image);
                    RatingBar ratingBar1  = (RatingBar) layout.findViewById(R.id.averageRatingRatingBar);
                    TextView ratingNum1 = (TextView)layout.findViewById(R.id.ratingNumber);
                    if(obj.get("ratingCount").getAsLong()> 0) {
                        ratingBar1.setRating((float) obj.get("starCount").getAsLong() / obj.get("ratingCount").getAsLong());
                        ratingNum1.setText((String.valueOf("("+(obj.get("ratingCount").getAsLong()+")"))));
                    }
                    // Populate the image
                    imageView1.setImageUrl("http:" +obj.get("coverImageUrl").getAsString(), imageLoader1);
                    topReadsList.addView((cardView1));

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

                final View moreBttn = morebtnlayout.findViewById(R.id.more_btn_click);

                moreBttn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LaunchCardView("Featured", moreBttn);
                    }
                });

                LinearLayout morebtnlayout1 = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.more_btn_layout, null);
                newReleasesList.addView((morebtnlayout1));
                View moreBttn1 = morebtnlayout1.findViewById(R.id.more_btn_click);

                moreBttn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LaunchCardView("New Releases", moreBttn);
                    }
                });

                LinearLayout morebtnlayout2 = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.more_btn_layout, null);
                topReadsList.addView((morebtnlayout2));
                View moreBttn2 = morebtnlayout2.findViewById(R.id.more_btn_click);

                moreBttn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LaunchCardView("Top Reads", moreBttn);
                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void processFinish(String output) {
            if(!(null == output || output.isEmpty())) {
                Log.d("Output", output);
                try {
                    parseJson(new JSONObject(output));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
            final String[] categoriesArray = {
                    "Books","Poems","Stories"
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
            linearLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String input = categoriesArray[position];
                    Intent NewReleaseIntent = new Intent(getActivity(), CardListActivity.class);
                    NewReleaseIntent.putExtra("TITLE",input);
                    startActivity(NewReleaseIntent);
                }
            });
            return rootView;
        }
    }
}
