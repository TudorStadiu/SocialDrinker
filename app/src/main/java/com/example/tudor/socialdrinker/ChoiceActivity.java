package com.example.tudor.socialdrinker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

public class ChoiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);
        ScrollView scroll = (ScrollView) findViewById(R.id.scr); scroll.setFadingEdgeLength(400);
        Button showall = (Button) findViewById(R.id.show_all);
        showall.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(ChoiceActivity.this, MapsActivity.class));
            }
        });
    }
}
