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
    public static Context con_option_vehicule;

    public static Context getContext() {
        return con_option_vehicule;
    }

    static TextInputEditText ti_zone_max;
    static TextInputEditText ti_zone_4;
    static TextInputEditText ti_zone_3;
    static TextInputEditText ti_zone_2;
    static TextInputEditText ti_zone_1;
    static ArrayList<String> al_list_distances;
    static TextView tv_retour_voiture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_vehicule);
        tv_retour_voiture = findViewById(R.id.tvRetour);
        ti_zone_max = findViewById(R.id.et_zone_max);
        ti_zone_4 = findViewById(R.id.et_zone_4);
        ti_zone_3 = findViewById(R.id.et_zone_3);
        ti_zone_2 = findViewById(R.id.et_zone_2);
        ti_zone_1 = findViewById(R.id.et_zone_1);

        con_option_vehicule = getBaseContext();
        al_list_distances = getIntent().getStringArrayListExtra("al_list_distances");

        receptionParamVehicule();

    }

    public static void receptionParamVehicule() {
        ti_zone_1.setText(al_list_distances.get(0));
        ti_zone_2.setText(al_list_distances.get(1));
        ti_zone_3.setText(al_list_distances.get(2));
        ti_zone_4.setText(al_list_distances.get(3));
        ti_zone_max.setText(al_list_distances.get(4));
    }

    public void envoyerParamVehicule(View v) {

        String strParam = "W" + "/" + ti_zone_1.getText().toString() + "/" +
                ti_zone_2.getText().toString() + "/" +
                ti_zone_3.getText().toString() + "/" +
                ti_zone_4.getText().toString() + "/" +
                ti_zone_max.getText().toString() + "/" + "X";

        sbt_device_interface.sendMessage(strParam);


    }

    public void sauvegarderParametres(View v) {
        sbt_device_interface.sendMessage("Q");

    }

}
