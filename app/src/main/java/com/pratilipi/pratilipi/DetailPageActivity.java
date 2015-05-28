package com.pratilipi.pratilipi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import org.json.JSONObject;

public class DetailPageActivity extends Activity {

    public static final String PID = "PId";
    public static final String POSITION = "Position";
    public static final String JSON = "JSON";
    private JSONObject obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
        try{
            obj = new JSONObject(getIntent().getStringExtra(JSON));

            TextView title = (TextView) findViewById(R.id.titleTextView);
            final Typeface typeFace= Typeface.createFromAsset(getAssets(), "fonts/Hindi.ttf");
            title.setTypeface(typeFace);
            // Note: This flag is required for proper typeface rendering
            title.setPaintFlags(title.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            title.setText(Html.fromHtml(obj.getString("title")));

            ImageLoader imageLoader = AppController.getInstance().getImageLoader();
            NetworkImageView imageView = (NetworkImageView) findViewById(R.id.detail_image);
            imageView.setImageUrl("http:" +obj.getString("coverImageUrl"), imageLoader);

            RatingBar ratingBar  = (RatingBar) findViewById(R.id.averageRatingBar);
            TextView averageRatingTextView = (TextView) findViewById(R.id.averageRatingTextView);
            TextView detailPageRate = (TextView)findViewById(R.id.detailPageRatingNumber);
            if(obj.getLong("ratingCount")> 0) {
                float val = (float)obj.getLong("starCount")/obj.getLong("ratingCount");
                ratingBar.setRating(val);
                averageRatingTextView.setText("Average rating: " + String.valueOf(val) + "/5");
                detailPageRate.setText(String.valueOf("("+obj.getLong("ratingCount"))+" rating)");
            }

            TextView authorTextView = (TextView) findViewById(R.id.authorTextView);
            authorTextView.setText(Html.fromHtml(obj.getJSONObject("author").getString("name")));
            authorTextView.setTypeface(typeFace);
            authorTextView.setPaintFlags(title.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

            TextView summaryTextView = (TextView) findViewById(R.id.summaryTextView);
            summaryTextView.setText(Html.fromHtml(obj.getString("summary")));
            summaryTextView.setTypeface(typeFace);
            summaryTextView.setPaintFlags(title.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void launchReader(View view)
    {
        Intent i = new Intent(this, ReadActivity.class);
        i.putExtra(DetailPageActivity.JSON,  obj.toString());
        startActivity(i);
    }
}
