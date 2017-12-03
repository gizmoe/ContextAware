package com.maryland.cmsc436.contextaware;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent newIntent = new Intent(MainActivity.this, Context.class);
        startActivity(newIntent);
    }

//testing hello
}
