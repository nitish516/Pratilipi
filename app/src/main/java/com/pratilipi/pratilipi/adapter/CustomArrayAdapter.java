package com.pratilipi.pratilipi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pratilipi.pratilipi.DataFiles.Metadata;
import com.pratilipi.pratilipi.R;

import java.util.ArrayList;

/** An array adapter that knows how to render views when given CustomData classes */
public class CustomArrayAdapter extends ArrayAdapter<Metadata> {
    private LayoutInflater mInflater;

    public CustomArrayAdapter(Context context, ArrayList<Metadata> values) {
        super(context, R.layout.viewitem, values);
        mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            // Inflate the view since it does not exist
            convertView = mInflater.inflate(R.layout.viewitem, parent, false);

            // Create and save off the holder in the tag so we get quick access to inner fields
            // This must be done for performance reasons
            holder = new Holder();
            holder.textView = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        // Populate the text
        holder.textView.setText(getItem(position).get_title());


        return convertView;
    }

    /** View holder for the views we need access to */
    private static class Holder {
        public TextView textView;
    }
}
