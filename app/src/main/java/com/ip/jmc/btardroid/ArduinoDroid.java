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

package com.ip.jmc.btardroid;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import java.util.ArrayList;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class ArduinoDroid extends MainActivity {
    static ImageButton boutonMode, boutonDonnees, bt3, bt4, bt5, bt6;

    public static Context mContextArduinoDroid;

    public static Context getContext() {
        return mContextArduinoDroid;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContextArduinoDroid = getBaseContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino_droid);
        listViewParams = findViewById(R.id.listViewParams);


        JoystickView joystick = findViewById(R.id.joyStick);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                if(angle >= 46 && angle <= 135 && strength >= 40) {
                    deviceInterface.sendMessage("F");
                }
                else if(angle >= 136 && angle <= 225 && strength >= 40) {
                    deviceInterface.sendMessage("L");
                }
                else if(angle >= 226 && angle <= 315 && strength >= 40) {
                    deviceInterface.sendMessage("B");
                }
                else if(angle >= 316 && angle <= 360  && strength >= 40) {
                    deviceInterface.sendMessage("R");
                }
                else if(angle >= 0 && angle <= 45  && strength >= 40) {
                    deviceInterface.sendMessage("R");
                }
                if (angle == 0 && strength <= 39) {
                    deviceInterface.sendMessage("S");
                }
            }


        });
    }


    public void traitementReponse(String messageEnvoye, String messageRecu) {
        String cmdRetour;
        ArrayList<String> listParams = new ArrayList();
        //
        if (messageRecu != null) {
            if(messageRecu.length() == 1) {
                cmdRetour = messageRecu.toString();
            }
            else {
                for (String mess : messageRecu.split("/")) {
                    listParams.add(mess);
                }
                cmdRetour = listParams.get(0);
                listParams.remove(0);
            }
            switch (cmdRetour) {
                case "A" :
                    boutonMode.setImageResource(R.drawable.autonome);
                    break;
                case "M" :
                    boutonMode.setImageResource(R.drawable.human);
                    break;
                case "Z":
                    listParams(listParams);
                    break;
                case "C":
                    listParams(listParams);
                    break;
            }
        }
    }

    public void boutonModeClick(View v) {
        boutonMode = findViewById(R.id.boutonMode);
        Drawable drawable = boutonMode.getDrawable();
        if (!drawable.getConstantState().equals(getResources().getDrawable(R.drawable.autonome).getConstantState())) {

            deviceInterface.sendMessage("A");
        } else {

            deviceInterface.sendMessage("M");
        }
    }


    public void boutonDonneesClick(View v) {
        boutonDonnees = findViewById(R.id.boutonDonnees);
        Drawable drawableBtDonnees = boutonDonnees.getDrawable();
        deviceInterface.sendMessage("Z");
        if (!drawableBtDonnees.getConstantState().equals(getResources().getDrawable(R.drawable.empty).getConstantState())) {
            boutonDonnees.setImageResource(R.drawable.empty);

        } else {
            boutonDonnees.setImageResource(R.drawable.req_data);

        }
    }
    public void listParams(ArrayList<String> listParams) {

        adapterParams = new ArrayAdapter(mContextArduinoDroid, android.R.layout.simple_list_item_1, listParams);

        listViewParams.setAdapter(adapterParams);
        boutonDonnees.setImageResource(R.drawable.empty);

    }

    public void btn3Click(View v) {
        deviceInterface.sendMessage("3");
    }

    public void boutonConfigurationVhClick(View v) {

        deviceInterface.sendMessage("C");
        Intent intent = new Intent(ArduinoDroid.this, OptionVehicule.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
