package com.pratilipi.pratilipi.helper;

import android.content.Context;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.pratilipi.pratilipi.R;

/**
 * Created by Nitish on 24-04-2015.
 */
public class FontProvider extends ActionProvider {
    protected final Context context;
    protected final int layout;
    protected final FontProvider self;
    protected View view;

    public FontProvider(Context context, int layout) {
        super(context);
        this.layout = layout;
        this.context = context;
        this.self = this;
    }

    @Override
    public View onCreateActionView() {
        // Inflate the action view to be shown on the action bar.
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.action_provider, null);
        Button button = (Button) view.findViewById(R.id.buttonInc);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do something...
            }
        });
        return view;
    }

    public boolean onItemClick(){
//        toggleDropdown();
        return true;
    }

//    protected void toggleDropdown(){
//        this.positionLeft = getRelativeLeft(view);
//        DropdownInflater.getInstance().toggleDropdown(this.dropdown,this.positionLeft);
//    }

    protected int getRelativeLeft(View view) {
        int[] loc = new int[2];
        view.getLocationOnScreen(loc);
        return loc[0];
    }
}
