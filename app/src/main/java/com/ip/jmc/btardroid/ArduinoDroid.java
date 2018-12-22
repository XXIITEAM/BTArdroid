package com.ip.jmc.btardroid;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static com.ip.jmc.btardroid.MainActivity.deviceInterface;
import static com.ip.jmc.btardroid.MainActivity.strMessageEnvoye;
import static com.ip.jmc.btardroid.MainActivity.strMessageRecu;

public class ArduinoDroid extends AppCompatActivity {
    ListView listViewParams;
    public static Context mContextArduinoDroid;

    public static Context getContext() {
        return mContextArduinoDroid;
    }

    void convertParams() {
        // We received a message! Handle it here.
        if (strMessageEnvoye != null) {
            switch (strMessageEnvoye) {
                case "F":
                    break;
                case "B":
                    break;
                case "Z":
                    ArrayList<String> listParams = new ArrayList();
                    listViewParams = findViewById(R.id.listViewParams);
                    //  listViewParams.setVisibility(View.VISIBLE);
                    //List listParam = findViewById(R.id.listViewParam);
                    for (String mess : strMessageRecu.split("/")) {
                        listParams.add(mess);
                    }

                    final ArrayAdapter adapterParams = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listParams);

                    listViewParams.setAdapter(adapterParams);
                    break;

            }
        } else {

        }
    }

    public void btn1Click(View v) {


        //deviceInterface.sendMessage("A");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContextArduinoDroid = getBaseContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino_droid);
        listViewParams = findViewById(R.id.listViewParams);
    }

    public void btn3Click(View v) {
        deviceInterface.sendMessage("3");
    }

    public void btn4Click(View v) {
        deviceInterface.sendMessage("4");
    }

    public void btn5Click(View v) {
        deviceInterface.sendMessage("4");

    }

    public void btn6Click(View v) {
        deviceInterface.sendMessage("6");
    }

    public void btn7Click(View v) {
        deviceInterface.sendMessage("6");
    }

    public void btn8Click(View v) {
        deviceInterface.sendMessage("6");
    }

    public void btnHautClick(View v) {
        deviceInterface.sendMessage("F");
    }

    public void btnBasClick(View v) {
        deviceInterface.sendMessage("B");
    }

    public void btnGaucheClick(View v) {
        deviceInterface.sendMessage("G");
    }

    public void btnDroiteClick(View v) {
        deviceInterface.sendMessage("D");
    }

    public void btnAccelClick(View v) {

        deviceInterface.sendMessage("F");
    }

    public void btnFreinClick(View v) {
        deviceInterface.sendMessage("S");
    }

    public void btn2Click(View v) {
        deviceInterface.sendMessage("Z");
        convertParams();
    }
}
