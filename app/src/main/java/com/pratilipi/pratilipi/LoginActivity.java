package com.pratilipi.pratilipi;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pratilipi.pratilipi.MainActivity;
import com.pratilipi.pratilipi.R;


public class LoginActivity extends ActionBarActivity implements View.OnClickListener {

    Button bLogin , bSignUp;
    EditText email , pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.editText_usr);
        pass = (EditText) findViewById(R.id.editText_pass);
        bLogin = (Button) findViewById(R.id.blogin);
        bSignUp = (Button) findViewById(R.id.bSignUp);
        bLogin.setOnClickListener(this);
        bSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()) {
            case R.id.blogin: {
                startActivity(new Intent(this, MainActivity.class));
                break;
            }
            case R.id.bSignUp: {
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            }
        }

    }

}
