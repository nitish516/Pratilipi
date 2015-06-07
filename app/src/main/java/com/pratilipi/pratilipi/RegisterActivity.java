package com.pratilipi.pratilipi;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pratilipi.pratilipi.R;


public class RegisterActivity extends ActionBarActivity implements View.OnClickListener {

    Button bregister;
    EditText name, email , pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText) findViewById(R.id.editText_nam_r);
        email = (EditText) findViewById(R.id.editText_usr_r);
        pass = (EditText) findViewById(R.id.editText_pass_r);
        bregister = (Button) findViewById(R.id.bregister);

        bregister.setOnClickListener(this);


    }

    @Override
    public void onClick(View v){
        switch(v.getId()) {
            case R.id.bregister:

                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
}
