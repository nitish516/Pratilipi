package com.pratilipi.pratilipi;

/**
 * Created by Nitish on 03-04-2015.
 */
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PageFragment extends Fragment {
    private final static String PAGE_TEXT = "PAGE_TEXT";
    private final static String FONT_SIZE = "FONT_SIZE";


    public static PageFragment newInstance(CharSequence pageText, float fontSize) {
        PageFragment frag = new PageFragment();
        Bundle args = new Bundle();
        args.putCharSequence(PAGE_TEXT, pageText);
        args.putFloat(FONT_SIZE, fontSize);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        CharSequence text = args.getCharSequence(PAGE_TEXT);
        TextView pageView = (TextView) inflater.inflate(R.layout.page, container, false);
        pageView.setTextSize(TypedValue.COMPLEX_UNIT_PX, args.getFloat(FONT_SIZE));

        String lan = getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("selectedLanguage", "hi");
        if(lan.equalsIgnoreCase("hi"))
        {
            Typeface typeFace= Typeface.createFromAsset(getActivity().getAssets(), "fonts/devanagari.ttf");
            pageView.setTypeface(typeFace);
        }
        else if(lan.equalsIgnoreCase("gu"))
        {
            Typeface typeFace= Typeface.createFromAsset(getActivity().getAssets(), "fonts/gujarati.ttf");
            pageView.setTypeface(typeFace);
        }
        else if(lan.equalsIgnoreCase("ta"))
        {
            Typeface typeFace= Typeface.createFromAsset(getActivity().getAssets(), "fonts/tamil.ttf");
            pageView.setTypeface(typeFace);
        }

        pageView.setText(text);
        return pageView;
    }
}
