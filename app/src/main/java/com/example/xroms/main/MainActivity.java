package com.example.xroms.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends FullScreenActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setMContentView(findViewById(R.id.llContentView));

        Button rules = findViewById(R.id.btnRules);
        rules.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, RulesActivity.class);
                startActivity(myIntent);
            }
        });

        Button play = findViewById(R.id.btnPlay);
        play.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, NewJoinActivity.class);
                startActivity(myIntent);
            }
        });
    }
}
