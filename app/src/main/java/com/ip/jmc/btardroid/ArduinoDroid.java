package com.ip.jmc.btardroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class ArduinoDroid extends MainActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino_droid);
        //  bt1 = findViewById(R.id.bt1);
        // bt2 = findViewById(R.id.bt2);
        // bt3 = findViewById(R.id.bt3);
        // bt4 = findViewById(R.id.bt4);
        // bt5 = findViewById(R.id.bt5);
        //bt6 = findViewById(R.id.bt6);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        //TextView textView = findViewById(R.id.TV01);
        //textView.setText(message);

    }

    public void bt2Click(View v) {
        deviceInterface.sendMessage("2");
        msgSent = "2";
    }

    public void bt3Click(View v) {
        deviceInterface.sendMessage("3");
        msgSent = "3";
    }

    public void bt4Click(View v) {
        deviceInterface.sendMessage("4");
        msgSent = "4";
    }

    public void bt5Click(View v) {
        //deviceInterface.sendMessage("5");
        //msgSent = "5";
        Intent intent = new Intent(this, ArduinoDroid.class);
        //TextView editText = findViewById(R.id.editText);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

    }

    public void bt6Click(View v) {
        deviceInterface.sendMessage("6");
        msgSent = "6";
    }

}
