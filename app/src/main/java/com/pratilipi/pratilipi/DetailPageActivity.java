package com.pratilipi.pratilipi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.pratilipi.pratilipi.DataFiles.Metadata;

import org.json.JSONObject;
import org.w3c.dom.Text;


public class DetailPageActivity extends Activity {

    public static final String PID = "PId";
    public static final String POSITION = "Position";
    public static final String JSON = "JSON";
    private String _pId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
        try{
            JSONObject obj = new JSONObject(getIntent().getStringExtra(JSON));
            TextView title = (TextView) findViewById(R.id.titleTextView);
            title.setText(obj.getString("title"));
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void launchReader(View view)
    {
        Intent i = new Intent(this, ReadActivity.class);
        startActivity(i);
    }
}
