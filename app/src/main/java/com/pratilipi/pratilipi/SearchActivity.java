package com.pratilipi.pratilipi;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

/**
 * Created by MOHIT KHAITAN on 14-05-2015.
 */


public class SearchActivity extends ActionBarActivity {
    TextView txtqury;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);

        txtqury = (TextView)findViewById(R.id.txtQuery);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.menu_main, menu);

        SearchView searchVeiw = (SearchView)menu.findItem(R.id.action_search).getActionView();
        searchVeiw.setIconified(true);

        return true;
    }
}
