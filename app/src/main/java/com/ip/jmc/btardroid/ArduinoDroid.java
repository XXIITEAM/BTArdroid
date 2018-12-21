package com.ip.jmc.btardroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;


public class ArduinoDroid extends MainActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino_droid);


        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.TV01);
        textView.setText(message);


    }



}
