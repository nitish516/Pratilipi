package com.pratilipi.pratilipi;

import android.content.Context;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.pratilipi.pratilipi.R;

/**
 * Created by MOHIT KHAITAN on 29-05-2015.
 */
public class CustomActionProvider extends ActionProvider {
    private Context context;

    public CustomActionProvider(Context context) {
        super(context);
    }

    @Override
    public View onCreateActionView() {
        return null;
    }

    @Override
    public View onCreateActionView(MenuItem forItem) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.action_provider,null);
        return null;
    }
}
