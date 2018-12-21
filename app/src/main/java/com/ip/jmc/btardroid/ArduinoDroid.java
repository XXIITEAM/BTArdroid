package com.ip.jmc.btardroid;

import android.os.Bundle;
import android.view.View;


public class ArduinoDroid extends MainActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino_droid);

        //Intent intent = getIntent();
        //String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        // Capture the layout's TextView and set the string as its text
        //TextView textView = findViewById(R.id.TV01);
        //textView.setText(message);

    }

    public void btn1Click(View v) {
        deviceInterface.sendMessage("2");
        msgSent = "2";
    }

    public void btn2Click(View v) {
        deviceInterface.sendMessage("2");
        msgSent = "2";
    }

    public void btn3Click(View v) {
        deviceInterface.sendMessage("3");
        msgSent = "3";
    }

    public void btn4Click(View v) {
        deviceInterface.sendMessage("4");
        msgSent = "4";
    }

    public void btn5Click(View v) {
        deviceInterface.sendMessage("4");
        msgSent = "4";

    }

    public void btn6Click(View v) {
        deviceInterface.sendMessage("6");
        msgSent = "6";
    }

    public void btn7Click(View v) {
        deviceInterface.sendMessage("6");
        msgSent = "6";
    }

    public void btn8Click(View v) {
        deviceInterface.sendMessage("6");
        msgSent = "6";
    }

    public void btnHautClick(View v) {
        deviceInterface.sendMessage("6");
        msgSent = "6";
    }

    public void btnBasClick(View v) {
        deviceInterface.sendMessage("6");
        msgSent = "6";
    }

    public void btnGaucheClick(View v) {
        deviceInterface.sendMessage("6");
        msgSent = "6";
    }

    public void btnDroiteClick(View v) {
        deviceInterface.sendMessage("6");
        msgSent = "6";
    }

    public void btnAccelClick(View v) {
        deviceInterface.sendMessage("6");
        msgSent = "6";
    }

}
