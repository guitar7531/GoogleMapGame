package com.example.xroms.main;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RulesActivity extends FullScreenActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        setMContentView(findViewById(R.id.llContentRules));

        Button back = findViewById(R.id.btnRulesBack);
        back.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                RulesActivity.super.onBackPressed();
            }
        });
    }
}
