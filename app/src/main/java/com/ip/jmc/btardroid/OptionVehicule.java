/**
 * @desc Activity Arduino CAR Options
 * Configuration véhicule
 * @author XXIITEAM xxiiteam@gmail.com
 * @class com.ip.jmc.btardroid.OptionVehicule
 */
package com.ip.jmc.btardroid;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;


public class OptionVehicule extends MainActivity {
    public static Context mContextOptionVehicule;

    public static Context getContext() {
        return mContextOptionVehicule;
    }

    static TextInputEditText ti_zone_max;
    static TextInputEditText ti_zone_4;
    static TextInputEditText ti_zone_3;
    static TextInputEditText ti_zone_2;
    static TextInputEditText ti_zone_1;
    static ArrayList<String> listDistances;
    static TextView tvRetour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_vehicule);
        tvRetour = findViewById(R.id.tvRetour);
        ti_zone_max = findViewById(R.id.et_zone_max);
        ti_zone_4 = findViewById(R.id.et_zone_4);
        ti_zone_3 = findViewById(R.id.et_zone_3);
        ti_zone_2 = findViewById(R.id.et_zone_2);
        ti_zone_1 = findViewById(R.id.et_zone_1);

        mContextOptionVehicule = getBaseContext();
        listDistances = getIntent().getStringArrayListExtra("listDistances");

        receptionParamVehicule();

    }

    public static void receptionParamVehicule() {
        ti_zone_1.setText(listDistances.get(0));
        ti_zone_2.setText(listDistances.get(1));
        ti_zone_3.setText(listDistances.get(2));
        ti_zone_4.setText(listDistances.get(3));
        ti_zone_max.setText(listDistances.get(4));
    }

    public void envoyerParamVehicule(View v) {

        String strParam = "W" + "/" + ti_zone_1.getText().toString() + "/" +
                ti_zone_2.getText().toString() + "/" +
                ti_zone_3.getText().toString() + "/" +
                ti_zone_4.getText().toString() + "/" +
                ti_zone_max.getText().toString() + "/" + "X";

        deviceInterface.sendMessage(strParam);


    }

    public void sauvegarderParametres(View v) {
        deviceInterface.sendMessage("Q");

    }

}
