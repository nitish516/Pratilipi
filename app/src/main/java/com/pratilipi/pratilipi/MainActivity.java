
package com.pratilipi.pratilipi;

import android.app.ActionBar;
import android.app.AlertDialog;
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
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pratilipi.pratilipi.DataFiles.Metadata;
import com.pratilipi.pratilipi.adapter.CardListAdapter;
import com.pratilipi.pratilipi.adapter.GridViewImageAdapter;
import com.pratilipi.pratilipi.adapter.HomeAdapter;
import com.pratilipi.pratilipi.adapter.ViewPagerAdapter;
import com.pratilipi.pratilipi.helper.AppConstant;
import com.pratilipi.pratilipi.helper.PratilipiProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import android.support.v7.app.AppCompatActivity;

public class MainActivity extends ActionBarActivity{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
//    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will display the three primary sections of the app, one at a
     * time.
     */
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
                searchIntent.putExtra("LAUNCHER", "search");
                searchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivityForResult(searchIntent,0);
                overridePendingTransition(0,0);
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


    private Toolbar toolbar;
    ViewPager mViewPager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[] = {"Home","Categories","Shelf","Profile"};
    int NumbOfTabs = 4;
    int tabPosition;

    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = (Toolbar) findViewById(R.id.tool_bar_main_activity);
        setSupportActionBar(toolbar);
        TextView toolbar_title = (TextView) toolbar.findViewById(R.id.title_toolbar);
        getSupportActionBar().setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        toolbar_title.setText("");

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, NumbOfTabs);

        mViewPager = (ViewPager) findViewById(R.id.pager_main_activity);
        mViewPager.setAdapter(adapter);

        tabs = (SlidingTabLayout) findViewById(R.id.tabs_main_activity);
//        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        tabs.setViewPager(mViewPager);
        Intent i = getIntent();
        tabPosition = i.getFlags();
        switch(tabPosition){
            case 0:
            mViewPager.setCurrentItem(tabPosition);
            break;
            case 1:
                mViewPager.setCurrentItem(tabPosition);
                break;
            case 2:
                mViewPager.setCurrentItem(tabPosition);
                break;
            case 3:
                mViewPager.setCurrentItem(tabPosition);
                break;
        }

    }

    public static class HomeFragment extends Fragment implements AsyncResponse{

        private ArrayList<Metadata> mMetaData ;
        private ProgressBar pBar,pBar1,pBar2;
        private LinearLayout featuredList;
        private LinearLayout newReleasesList;
        private LinearLayout topReadsList;
        private ProgressDialog pDialog;
        private float DecimalRating;
        RecyclerView mHomeFeaturedRecyclerView, mHomeNewReleasesRecyclerView, mHomeTopReadsRecyclerView;
        LinearLayoutManager mHomeFeaturedLayoutManager, mHomeNewReleasesLayoutManager, mHomeTopReadsLayoutManager;
        HomeAdapter mHomeFeaturedAdapter, mHomeNewReleasesAdapter, mHomeTopReadsAdapter;
        List<Metadata> featured_metadata = new ArrayList<Metadata>();
        List<Metadata> new_releases_metadata = new ArrayList<Metadata>();
        List<Metadata> top_reads_metadata = new ArrayList<Metadata>();

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home, container, false);
            RadioButton rb = (RadioButton) rootView.findViewById(R.id.radio_top);
            rb.setChecked(true);

            pBar = (ProgressBar)rootView.findViewById(R.id.progress_bar);
            pBar1 = (ProgressBar)rootView.findViewById(R.id.progress_bar1);
            pBar2 = (ProgressBar)rootView.findViewById(R.id.progress_bar2);
//            featuredList = (LinearLayout) rootView.findViewById(R.id.linear_layout_featured);
//            newReleasesList = (LinearLayout)rootView.findViewById(R.id.linear_layout_new_releases);
//            topReadsList = (LinearLayout)rootView.findViewById(R.id.linear_layout_top);

            //Recycler View START

            pBar.setVisibility(View.VISIBLE);
            pBar2.setVisibility(View.VISIBLE);
            pBar1.setVisibility(View.VISIBLE);

            mHomeFeaturedRecyclerView = (RecyclerView)rootView.findViewById(R.id.featured_recycler_view);
            mHomeNewReleasesRecyclerView = (RecyclerView)rootView.findViewById(R.id.new_releases_recycler_view);
            mHomeTopReadsRecyclerView = (RecyclerView)rootView.findViewById(R.id.top_reads_recycler_view);

            mHomeFeaturedRecyclerView.setHasFixedSize(true);
            mHomeNewReleasesRecyclerView.setHasFixedSize(true);
            mHomeTopReadsRecyclerView.setHasFixedSize(true);

            mHomeFeaturedLayoutManager = new LinearLayoutManager(getActivity());
            mHomeNewReleasesLayoutManager = new LinearLayoutManager((getActivity()));
            mHomeTopReadsLayoutManager = new LinearLayoutManager(getActivity());

            mHomeFeaturedRecyclerView.setLayoutManager(mHomeFeaturedLayoutManager);
            mHomeFeaturedLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

            mHomeNewReleasesRecyclerView.setLayoutManager(mHomeNewReleasesLayoutManager);
            mHomeNewReleasesLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

            mHomeTopReadsRecyclerView.setLayoutManager(mHomeTopReadsLayoutManager);
            mHomeTopReadsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

            mHomeFeaturedAdapter = new HomeAdapter(featured_metadata);
            mHomeNewReleasesAdapter = new HomeAdapter(new_releases_metadata);
            mHomeTopReadsAdapter = new HomeAdapter(top_reads_metadata);

            mHomeFeaturedRecyclerView.setAdapter(mHomeFeaturedAdapter);
            mHomeNewReleasesRecyclerView.setAdapter(mHomeNewReleasesAdapter);
            mHomeTopReadsRecyclerView.setAdapter(mHomeTopReadsAdapter);

            fetchData();

//            if(isOnline())
//                makeJsonArryReq();
//            else
//            {
//                showNoConnectionDialog(getActivity());
//            }
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

        private void fetchData() {
            String URL = "content://com.pratilipi.pratilipi.helper.PratilipiData/metadata";
            Uri pid = Uri.parse(URL);
            Cursor c = getActivity().getContentResolver().query(pid, null, PratilipiProvider.LIST_TYPE + "=?",
                    new String[]{"featured"}, PratilipiProvider.ID);


            if (!c.moveToFirst()) {
                if(isOnline())
                    makeJsonArryReq();
                else
                    showNoConnectionDialog(getActivity());
            }
            else{
                fetchDataFromDb();
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

        private void parseJson(JSONObject jsonObject) {
            JsonArray featuredPratilipiDataList = null;
            JsonArray newReleasesPratilipiDataList = null;
            JsonArray topReadsPratilipiDataList = null;
            Gson gson = new GsonBuilder().create();

            try {
                String responseStr = jsonObject.getString("response");
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
                addToDb(featuredPratilipiDataList,"featured");
                addToDb(newReleasesPratilipiDataList,"newReleases");
                addToDb(topReadsPratilipiDataList,"topReads");

            } catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private void addToDb(JsonArray list, String type) {

            Gson gson = new GsonBuilder().create();
            for (int i = 0; i < list.size(); i++) {
                final JsonObject obj = gson.fromJson( list.get(i), JsonElement.class ).getAsJsonObject();
                if (!obj.get("state").getAsString().equalsIgnoreCase("PUBLISHED"))
                    continue;

                ContentValues values = new ContentValues();
                try {
                    values.put(PratilipiProvider.PID , obj.get("id").getAsLong()+"");
                    values.put(PratilipiProvider.TITLE ,obj.get("title").getAsString());
                    values.put(PratilipiProvider.CONTENT_TYPE ,obj.get("contentType").getAsString());
                    values.put(PratilipiProvider.AUTHOR_ID , obj.get("authorId").getAsString());
                    values.put(PratilipiProvider.AUTHOR_NAME , obj.get("author").getAsJsonObject().get("name").getAsString());
                    values.put(PratilipiProvider.CH_COUNT , obj.get("pageCount").getAsInt());
                    values.put(PratilipiProvider.IMG_URL , obj.get("coverImageUrl").getAsString());
                    values.put(PratilipiProvider.PG_URL , obj.get("pageUrl").getAsString());
                    if(obj.get("index")!=null)
                        values.put(PratilipiProvider.INDEX , obj.get("index").getAsString());
                    values.put(PratilipiProvider.RATING_COUNT , obj.get("ratingCount").getAsLong());
                    values.put(PratilipiProvider.STAR_COUNT , obj.get("starCount").getAsLong());
                    if(null!=obj.get("summary"))
                        values.put(PratilipiProvider.SUMMARY , obj.get("summary").getAsString());
                    values.put(PratilipiProvider.LIST_TYPE , type);
                    values.put(PratilipiProvider.CURRENT_CHAPTER,1);
                    values.put(PratilipiProvider.CURRENT_PAGE,1);
                    values.put(PratilipiProvider.TIME_STAMP,System.currentTimeMillis()/1000);
                    values.put(PratilipiProvider.FONT_SIZE,30);

                    ContentResolver cv = getActivity().getContentResolver();
                    Uri uri = cv.insert(
                            PratilipiProvider.METADATA_URI, values);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            fetchDataFromDb();
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

        private void fetchDataFromDb() {
            String URL = "content://com.pratilipi.pratilipi.helper.PratilipiData/metadata";
            Uri pid = Uri.parse(URL);
            Cursor c = getActivity().getContentResolver().query(pid, null, null,
                    null, PratilipiProvider.ID);


            if (!c.moveToFirst()){
                //makeRequest();
            }else{
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
                    m.set_time_stamp(c.getLong(c.getColumnIndex(PratilipiProvider.TIME_STAMP)));
                    m.set_current_chapter(c.getInt(c.getColumnIndex(PratilipiProvider.CURRENT_CHAPTER)));
                    m.set_current_page(c.getInt(c.getColumnIndex(PratilipiProvider.CURRENT_PAGE)));

                    if((c.getString(c.getColumnIndex(PratilipiProvider.IS_DOWNLOADED)))!= null)
                        m.set_is_downloaded((c.getString(c.getColumnIndex(PratilipiProvider.IS_DOWNLOADED))));

                    if (c.getString(c.getColumnIndex(PratilipiProvider.LIST_TYPE)).equalsIgnoreCase("featured")) {
                        featured_metadata.add(m);
                        mHomeFeaturedAdapter.notifyDataSetChanged();
                        pBar.setVisibility(View.GONE);
                    } else if (c.getString(c.getColumnIndex(PratilipiProvider.LIST_TYPE)).equalsIgnoreCase("newReleases")) {
                        new_releases_metadata.add(m);
                        mHomeNewReleasesAdapter.notifyDataSetChanged();
                        pBar1.setVisibility(View.GONE);
                    } else if (c.getString(c.getColumnIndex(PratilipiProvider.LIST_TYPE)).equalsIgnoreCase("topReads")) {
                        top_reads_metadata.add(m);
                        mHomeTopReadsAdapter.notifyDataSetChanged();
                        pBar2.setVisibility(View.GONE);
                    }
                }
            }
        }

        private void LaunchCardView(String input, View view){

            Intent mIntent = new Intent(getActivity(), CardListActivity.class);
            mIntent.putExtra("TITLE",input);
            mIntent.putExtra("LAUNCHER","more");
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivityForResult(mIntent, 0);
//            overridePendingTransition(0,0);
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
    }

    /**
     * A fragment that launches shelf part of application.
     */
    public static class ShelfFragment extends Fragment {

        private com.pratilipi.pratilipi.util.Utils utils;
        private ArrayList<Metadata> imagePaths = new ArrayList<Metadata>();
//        private GridViewImageAdapter adapter;
        private GridView gridView;
        private int columnWidth;
        private RecyclerView mRecyclerView;
        private List<Metadata> metadata = new ArrayList<Metadata>();
        private CardListAdapter adapter;
        private LinearLayoutManager mShelfLayout;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_shelf, container, false);
//            gridView = (GridView) rootView.findViewById(R.id.grid_view);
            utils = new com.pratilipi.pratilipi.util.Utils(getActivity());

            // Initilizing Grid View
//            InitilizeGridLayout();
            mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view_shelf);
            mRecyclerView.setHasFixedSize(true);

            mShelfLayout = new LinearLayoutManager(this.getActivity());
            mRecyclerView.setLayoutManager(mShelfLayout);


            // loading all image paths from SD card
//            imagePaths =
            String URL = "content://com.pratilipi.pratilipi.helper.PratilipiData/metadata";
            Uri pid = Uri.parse(URL);
            Cursor c = getActivity().getContentResolver().query(pid, null, PratilipiProvider.LIST_TYPE + "=?",
                    new String[]{"download"}, PratilipiProvider.TIME_STAMP + " DESC");
            adapter = new CardListAdapter(metadata);

            // setting grid view adapter
            mRecyclerView.setAdapter(adapter);

            if (c.moveToFirst()) {
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
                    if((c.getString(c.getColumnIndex(PratilipiProvider.IS_DOWNLOADED)))!= null)
                        m.set_is_downloaded((c.getString(c.getColumnIndex(PratilipiProvider.IS_DOWNLOADED))));
                    m.set_current_chapter(c.getInt(c.getColumnIndex(PratilipiProvider.CURRENT_CHAPTER)));
                    m.set_current_page(c.getInt(c.getColumnIndex(PratilipiProvider.CURRENT_PAGE)));
                    m.set_time_stamp(System.currentTimeMillis()/1000);
//                    imagePaths.add(m);
                    metadata.add(m);
                    adapter.notifyDataSetChanged();
                }
            }

            // Gridview adapter
            return rootView;
        }

//        private void InitilizeGridLayout() {
//            Resources r = getResources();
//            float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//                    AppConstant.GRID_PADDING, r.getDisplayMetrics());
//
//            columnWidth = (int) ((utils.getScreenWidth() - ((AppConstant.NUM_OF_COLUMNS + 1) * padding)) / AppConstant.NUM_OF_COLUMNS);
//
//            gridView.setNumColumns(AppConstant.NUM_OF_COLUMNS);
//            gridView.setColumnWidth(columnWidth);
//            gridView.setStretchMode(GridView.NO_STRETCH);
//            gridView.setPadding((int) padding, (int) padding, (int) padding,
//                    (int) padding);
//            gridView.setHorizontalSpacing((int) padding);
//            gridView.setVerticalSpacing((int) padding);
//        }
    }

    public static class CategoriesFragment extends Fragment implements AsyncResponse{

        public static final String ARG_SECTION_NUMBER = "section_number";
        List<String> listCategories;
        List<String> listId;
        ArrayAdapter<String> mCategoriesAdapter;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            listCategories = new ArrayList<String>();
            listId = new ArrayList<String>();
            mCategoriesAdapter = new ArrayAdapter<String>(
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
                    String input = listCategories.get(position);
                    Intent CategoriesIntent = new Intent(getActivity(), CardListActivity.class);
                    CategoriesIntent.putExtra("TITLE", input);
                    CategoriesIntent.putExtra("LAUNCHER", "categories");
                    CategoriesIntent.putExtra("ID", listId.get(position));
                    startActivity(CategoriesIntent);
                }
            });
            fetchData();
            return rootView;
        }

        private void fetchData() {
            String URL = "content://com.pratilipi.pratilipi.helper.PratilipiData/categories";
            Uri pid = Uri.parse(URL);
            Cursor c = getActivity().getContentResolver().query(pid, null, null, null, PratilipiProvider.ID);

            if (!c.moveToFirst()) {
                if (isOnline())
                    makeJsonReq();
                else
                    showNoConnectionDialog(getActivity());
            } else {
                fetchDataFromDb();
            }
        }

        private void fetchDataFromDb() {
            String URL = "content://com.pratilipi.pratilipi.helper.PratilipiData/categories";
            Uri pid = Uri.parse(URL);
            Cursor c = getActivity().getContentResolver().query(pid, null, null,
                    null, PratilipiProvider.ID);


            if (!c.moveToFirst()) {
                //makeRequest();
            } else {
                for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                    listCategories.add(c.getString(c.getColumnIndex(PratilipiProvider.TITLE)));
                    listId.add(c.getString(c.getColumnIndex(PratilipiProvider.PID)));
                    mCategoriesAdapter.notifyDataSetChanged();
                    }
                }
            }

        @Override
        public void processFinish(String output) {
            if (!(null == output || output.isEmpty())) {
                Log.d("Output", output);
                try {
                    parseJson(new JSONObject(output));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private void makeJsonReq() {
            RequestTask task = new RequestTask();
            task.execute("http://www.pratilipi.com/api.pratilipi/category");
            task.delegate = (AsyncResponse) this;
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

        private void parseJson(JSONObject jsonObject) {
            Gson gson = new GsonBuilder().create();

            try {
                String responseStr = jsonObject.getString("categoryDataList");
                Log.d("responseStr", responseStr);
                JsonArray elementArr = gson.fromJson(responseStr, JsonElement.class).getAsJsonArray();
                Log.d("element", "" + elementArr);

                for (int i = 0; i < elementArr.size(); i++) {
                    Log.d("parts at ", i + " " + elementArr.get(i));
                    JsonObject elementObj = gson.fromJson(elementArr.get(i), JsonElement.class).getAsJsonObject();
                    Log.d("elementObj", i + " " + elementObj);

                    ContentValues values = new ContentValues();
                    values.put(PratilipiProvider.PID, elementObj.get("id").getAsLong() + "");
                    values.put(PratilipiProvider.TITLE, elementObj.get("name").getAsString());
                    ContentResolver cv = getActivity().getContentResolver();
                    Uri uri = cv.insert(
                            PratilipiProvider.CATEGORIES_URI, values);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            fetchDataFromDb();
        }
    }
}
