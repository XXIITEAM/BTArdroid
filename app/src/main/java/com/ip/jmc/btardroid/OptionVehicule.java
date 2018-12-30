/**
 * @desc Activity Arduino CAR Options
 * Configuration véhicule
 * @author XXIITEAM xxiiteam@gmail.com
 * @class com.ip.jmc.btardroid.OptionVehicule
 */
package com.ip.jmc.btardroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;


public class OptionVehicule extends MainActivity {
    public static Context con_option_vehicule;

    public static Context getContext() {
        return con_option_vehicule;
    }

    TextInputEditText ti_zone_max;
    TextInputEditText ti_zone_4;
    TextInputEditText ti_zone_3;
    TextInputEditText ti_zone_2;
    TextInputEditText ti_zone_1;
    ArrayList<String> al_list_distances;
    TextView tv_retour_voiture;

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
        LocalBroadcastManager.getInstance(con_option_vehicule).registerReceiver(mMessageReceiver,
                new IntentFilter("get-param"));

        receptionParamVehicule();
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s1= intent.getStringExtra("set_tv_retour_voiture");
            getIt(s1);
        }
    };
    public void getIt(String s)
    {

        System.out.println(s);
        tv_retour_voiture.setText(s);

    }
    public void receptionParamVehicule() {
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

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(con_option_vehicule).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }


}
