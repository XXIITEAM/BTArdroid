/* COMMANDES ARDUINO
#define CMD_FORWARD     'F'
#define CMD_RIGHT_FRONT 'R'
#define CMD_RIGHT_FORWARD	'D'
#define CMD_LEFT_FORWARD	'G'
#define CMD_RIGHT_BACK		'J'
#define CMD_LEFT_BACK		'H'
#define CMD_BACKWARD		'B'
#define CMD_AUTONOME		'A'
#define CMD_LEFT_FRONT		'L'
#define CMD_STOP			'S'
#define CMD_DIST			'Z'
 */
/**
 * @desc Activity Arduino CAR View
 * Gestion du véhicule autonome Arduino
 * @author XXIITEAM xxiiteam@gmail.com
 * @package com.ip.jmc.btardroid.ArduinoDroid.class
 */
package com.ip.jmc.btardroid;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import io.github.controlwear.virtual.joystick.android.JoystickView;

import static com.ip.jmc.btardroid.BluetoothCustom.sbt_device_interface;

public class ArduinoDroid extends MainActivity {
    //Définition du contexte
    static public Context con_arduino_droid;
    //public static Context getContext() {
        //return con_arduino_droid;
    //}
    static public Context con_app;
    static ImageButton bt_mode_vh, bt_donnees, bt3, bt4;
    Intent intent_set_tv_retour_voiture = new Intent("get-param-opt");
    Intent intent_list_distance = new Intent("get-param-dist");
    static ListView lv_get_vh_data;
    static ListView lv_capteurs;
    static ArrayAdapter aa_vh_params;
    static  ArrayAdapter aa_capteurs;
    static public boolean testCenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testCenter = true;
        setContentView(R.layout.activity_arduino_droid);
        con_arduino_droid = this;
        con_app = getApplicationContext();
        //ListView getData
        //lv_get_vh_data = findViewById(R.id.listViewParams);
        lv_capteurs = findViewById(R.id.listViewCapteurs);
        JoystickView joystick = findViewById(R.id.joyStick);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                testCenter = true;
                if (angle >= 46 && angle <= 135 && strength >= 40) {
                    sbt_device_interface.sendMessage("F");
                    testCenter = false;
                } else if (angle >= 136 && angle <= 225 && strength >= 40) {
                    sbt_device_interface.sendMessage("L");
                    testCenter = false;
                } else if (angle >= 226 && angle <= 315 && strength >= 40) {
                    sbt_device_interface.sendMessage("B");
                    testCenter = false;
                } else if (angle >= 316 && angle <= 360 && strength >= 40) {
                    sbt_device_interface.sendMessage("R");
                    testCenter = false;
                } else if (angle >= 0 && angle <= 45 && strength >= 40) {
                    sbt_device_interface.sendMessage("R");
                    testCenter = false;
                }
                if (angle == 0 && strength <= 39) {
                    sbt_device_interface.sendMessage("S");
                    testCenter = true;
                }
            }
        });
        sbt_device_interface.sendMessage("T");
    }
    public void traitementReponse(String messageEnvoye, String messageRecu) {
        String cmdRetour;
        ArrayList<String> listParams = new ArrayList();
        if (messageRecu != null) {
            if (messageRecu.length() == 1) {
                cmdRetour = messageRecu;
            } else {
                for (String mess : messageRecu.split("/")) {
                    listParams.add(mess);
                }
                cmdRetour = listParams.get(0);
                listParams.remove(0);
            }
            switch (cmdRetour) {
                case "A":
                    bt_mode_vh.setImageResource(R.drawable.auto_mode);
                    break;
                case "M":
                    bt_mode_vh.setImageResource(R.drawable.manuel_mode);
                    break;
                case "Z":
                    listParams(listParams);
                    break;
                case "O":
                    intent_list_distance.putStringArrayListExtra("al_list_distances", listParams);
                    LocalBroadcastManager.getInstance(con_arduino_droid).sendBroadcast(intent_list_distance);
                    break;
                case"T" :
                    listCapteurs(listParams);
                    break;
                case "W":

                    intent_set_tv_retour_voiture.putExtra("set_tv_retour_voiture", "Application des paramètres");
                    LocalBroadcastManager.getInstance(con_arduino_droid).sendBroadcast(intent_set_tv_retour_voiture);
                    Handler handlerW = new Handler();
                    handlerW.postDelayed(new Runnable() {
                        public void run() {
                            intent_set_tv_retour_voiture.putExtra("set_tv_retour_voiture", "");
                            LocalBroadcastManager.getInstance(con_arduino_droid).sendBroadcast(intent_set_tv_retour_voiture);

                        }
                    }, 3000);

                    break;
                case "Q":
                    intent_set_tv_retour_voiture.putExtra("set_tv_retour_voiture", "Sauvegarde des paramètres actuels");
                    LocalBroadcastManager.getInstance(con_arduino_droid).sendBroadcast(intent_set_tv_retour_voiture);
                    Handler handlerQ = new Handler();
                    handlerQ.postDelayed(new Runnable() {
                        public void run() {
                            intent_set_tv_retour_voiture.putExtra("set_tv_retour_voiture", "");
                            LocalBroadcastManager.getInstance(con_arduino_droid).sendBroadcast(intent_set_tv_retour_voiture);
                        }
                    }, 3000);

                    break;
            }
        }
    }

    public void boutonModeClick(View v) {
        bt_mode_vh = findViewById(R.id.boutonMode);
        Drawable drawable = bt_mode_vh.getDrawable();
        if (!drawable.getConstantState().equals(getResources().getDrawable(R.drawable.autonome).getConstantState())) {

            sbt_device_interface.sendMessage("A");
            testCenter = true;
        } else {

            sbt_device_interface.sendMessage("M");
        }
    }
    public void boutonDonneesClick(View v) {
        /*bt_donnees = findViewById(R.id.boutonDonnees);
        Drawable drawableBtDonnees = bt_donnees.getDrawable();
        sbt_device_interface.sendMessage("Z");
        if (!drawableBtDonnees.getConstantState().equals(getResources().getDrawable(R.drawable.empty).getConstantState())) {
            bt_donnees.setImageResource(R.drawable.empty);

        } else {
            bt_donnees.setImageResource(R.drawable.req_data);

        }*/
            sbt_device_interface.sendMessage("T");

    }

    public void listCapteurs(ArrayList<String> listCapteurs) {
        aa_capteurs = new ArrayAdapter(con_arduino_droid, android.R.layout.simple_list_item_1, listCapteurs);
        lv_capteurs.setAdapter(aa_capteurs);
    }
    public void listParams(ArrayList<String> listParams) {

        //lv_get_vh_data = findViewById(R.id.listViewParams);

        aa_vh_params = new ArrayAdapter(con_arduino_droid, android.R.layout.simple_list_item_1, listParams);

        lv_get_vh_data.setAdapter(aa_vh_params);
        bt_donnees.setImageResource(R.drawable.empty);

    }

    public void btn3Click(View v) {
        Intent i_options = new Intent(con_app, OptionVehicule.class);
        con_app.startActivity(i_options);
    }

    public void boutonConfigurationVhClick(View v) {

        sbt_device_interface.sendMessage("O");
        Intent i_options = new Intent(con_app, OptionVehicule.class);
        con_app.startActivity(i_options);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        finish();
    }
}
