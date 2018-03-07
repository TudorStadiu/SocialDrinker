package com.example.tudor.socialdrinker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //startActivity(new Intent(MainActivity.this, LoginActivity.class));
        SharedPreferences settings = getSharedPreferences("pref", MODE_PRIVATE);
        boolean loggedIn = settings.getBoolean("logged",false);
        Log.d("log", String.valueOf(loggedIn));



        if(!loggedIn)
        startActivity(new Intent(MainActivity.this, LoginActivity.class));


        Button search_button = (Button) findViewById(R.id.search);
        Button follow_button = (Button) findViewById(R.id.facebook);
        search_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ChoiceActivity.class));
            }
        });

        follow_button.setOnClickListener(new View.OnClickListener()

    {
        public void onClick (View v){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://www.facebook.com/tudor.sky.1?ref=br_rs"));
        startActivity(intent);
    }
    });
}
    }

