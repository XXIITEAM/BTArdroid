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

import static com.ip.jmc.btardroid.BluetoothCustom.sbt_device_interface;
public class OptionVehicule extends MainActivity {

    TextInputEditText ti_zone_max;
    TextInputEditText ti_zone_4;
    TextInputEditText ti_zone_3;
    TextInputEditText ti_zone_2;
    TextInputEditText ti_zone_1;
    ArrayList<String> al_list_distances = new ArrayList<>();
    TextView tv_retour_voiture;
    static public Context con_option_vehicule;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_vehicule);
        con_option_vehicule = this;
        //al_list_distances.isEmpty();
        tv_retour_voiture = findViewById(R.id.tvRetour);
        ti_zone_max = findViewById(R.id.et_zone_max);
        ti_zone_4 = findViewById(R.id.et_zone_4);
        ti_zone_3 = findViewById(R.id.et_zone_3);
        ti_zone_2 = findViewById(R.id.et_zone_2);
        ti_zone_1 = findViewById(R.id.et_zone_1);

        //al_list_distances = getIntent().getStringArrayListExtra("al_list_distances");
        LocalBroadcastManager.getInstance(con_option_vehicule).registerReceiver(option_vehicule_message_receiver,
                new IntentFilter("get-param-opt"));
        LocalBroadcastManager.getInstance(con_option_vehicule).registerReceiver(option_vehicule_param_receiver,
                new IntentFilter("get-param-dist"));
        if(al_list_distances.isEmpty()) {

            al_list_distances.add("10");
            al_list_distances.add("30");
            al_list_distances.add("50");
            al_list_distances.add("80");
            al_list_distances.add("100");
        }
        updateListParamVehicule();

    }
    private BroadcastReceiver option_vehicule_message_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s1= intent.getStringExtra("set_tv_retour_voiture");
            //al_list_distances = intent.getStringArrayListExtra("al_list_distances");
            tv_retour_voiture.setText(s1);
        }
    };
    private BroadcastReceiver option_vehicule_param_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            al_list_distances = intent.getStringArrayListExtra("al_list_distances");
            updateListParamVehicule();
        }
    };

    public void updateListParamVehicule() {
        ti_zone_1.setText(al_list_distances.get(0));
        ti_zone_2.setText(al_list_distances.get(1));
        ti_zone_3.setText(al_list_distances.get(2));
        ti_zone_4.setText(al_list_distances.get(3));
        ti_zone_max.setText(al_list_distances.get(4));
        al_list_distances.clear();
    }

    public void envoiParamVehicule(View v) {

        String strParam = "W" + "/" + ti_zone_1.getText().toString() + "/" +
                ti_zone_2.getText().toString() + "/" +
                ti_zone_3.getText().toString() + "/" +
                ti_zone_4.getText().toString() + "/" +
                ti_zone_max.getText().toString() + "/" + "X";

        sbt_device_interface.sendMessage(strParam);


    }

    public void sauvegardeParametres(View v) {
        sbt_device_interface.sendMessage("Q");
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(con_option_vehicule).unregisterReceiver(option_vehicule_message_receiver);
        LocalBroadcastManager.getInstance(con_option_vehicule).unregisterReceiver(option_vehicule_param_receiver);

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
