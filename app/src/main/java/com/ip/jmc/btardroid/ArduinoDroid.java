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

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import java.util.ArrayList;
import io.github.controlwear.virtual.joystick.android.JoystickView;
import static com.ip.jmc.btardroid.OptionVehicule.tv_retour_voiture;

public class ArduinoDroid extends MainActivity {
    //Définition du contexte
    public static Context con_arduino_droid;
    public static Context getContext() {
        return con_arduino_droid;
    }
    static ImageButton bt_mode_vh, bt_donnees, bt3, bt4, bt5, bt6;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        con_arduino_droid = getBaseContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino_droid);
        //ListView getData
        lv_get_vh_data = findViewById(R.id.listViewParams);

        JoystickView joystick = findViewById(R.id.joyStick);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                if (angle >= 46 && angle <= 135 && strength >= 40) {
                    sbt_device_interface.sendMessage("F");
                } else if (angle >= 136 && angle <= 225 && strength >= 40) {
                    sbt_device_interface.sendMessage("L");
                } else if (angle >= 226 && angle <= 315 && strength >= 40) {
                    sbt_device_interface.sendMessage("B");
                } else if (angle >= 316 && angle <= 360 && strength >= 40) {
                    sbt_device_interface.sendMessage("R");
                } else if (angle >= 0 && angle <= 45 && strength >= 40) {
                    sbt_device_interface.sendMessage("R");
                }
                if (angle == 0 && strength <= 39) {
                    sbt_device_interface.sendMessage("S");
                }
            }


        });
    }


    public void traitementReponse(String messageEnvoye, String messageRecu) {
        String cmdRetour;
        ArrayList<String> listParams = new ArrayList();
        //
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
                    bt_mode_vh.setImageResource(R.drawable.autonome);
                    break;
                case "M":
                    bt_mode_vh.setImageResource(R.drawable.human);
                    break;
                case "Z":
                    listParams(listParams);
                    break;
                case "O":
                    Intent intent = new Intent(con_arduino_droid, OptionVehicule.class);
                    intent.putStringArrayListExtra("al_list_distances", listParams);
                    con_arduino_droid.startActivity(intent);
                    break;
                case "W":
                    tv_retour_voiture.setText("Application des paramètres");
                    Handler handlerW = new Handler();
                    handlerW.postDelayed(new Runnable() {
                        public void run() {
                            tv_retour_voiture.setText("");

                        }
                    }, 3000);
                    break;
                case "Q":
                    tv_retour_voiture.setText("Sauvegarde des paramètres actuels");
                    Handler handlerQ = new Handler();
                    handlerQ.postDelayed(new Runnable() {
                        public void run() {
                            tv_retour_voiture.setText("");

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
        } else {

            sbt_device_interface.sendMessage("M");
        }
    }


    public void boutonDonneesClick(View v) {
        bt_donnees = findViewById(R.id.boutonDonnees);
        Drawable drawableBtDonnees = bt_donnees.getDrawable();
        sbt_device_interface.sendMessage("Z");
        if (!drawableBtDonnees.getConstantState().equals(getResources().getDrawable(R.drawable.empty).getConstantState())) {
            bt_donnees.setImageResource(R.drawable.empty);

        } else {
            bt_donnees.setImageResource(R.drawable.req_data);

        }
    }

    public void listParams(ArrayList<String> listParams) {

        aa_vh_params = new ArrayAdapter(con_arduino_droid, android.R.layout.simple_list_item_1, listParams);

        lv_get_vh_data.setAdapter(aa_vh_params);
        bt_donnees.setImageResource(R.drawable.empty);

    }

    public void btn3Click(View v) {
        sbt_device_interface.sendMessage("3");
    }

    public void boutonConfigurationVhClick(View v) {

        sbt_device_interface.sendMessage("O");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
