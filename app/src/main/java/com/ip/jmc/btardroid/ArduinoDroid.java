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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class ArduinoDroid extends MainActivity {
    static ImageButton boutonMode, boutonDonnees, bt3, bt4, bt5, bt6;

    public static Context mContextArduinoDroid;

    public static Context getContext() {
        return mContextArduinoDroid;
    }
    String strMessageRecu = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContextArduinoDroid = getBaseContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino_droid);
        listViewParams = findViewById(R.id.listViewParams);


        JoystickView joystick = (JoystickView) findViewById(R.id.joyStick);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                if(angle > 45 && angle < 135 && strength != 0) {
                    deviceInterface.sendMessage("F");
                }
                else if(angle > 136 && angle < 225 && strength != 0) {
                    deviceInterface.sendMessage("L");
                }
                else if(angle > 226 && angle < 315 && strength != 0) {
                    deviceInterface.sendMessage("B");
                }
                else if(angle < 45 && angle > 316 && strength != 0) {
                    deviceInterface.sendMessage("R");
                }
            }


        });
    }


    public void traitementReponse(String messageEnvoye, String messageRecu) {
        // We received a message! Handle it here.
        if (messageEnvoye != null && messageRecu != null) {
            switch (messageEnvoye) {
                case "F":
                    break;
                case "B":
                    break;

                case "Z":

                    listParams(messageRecu);
                    break;

            }
            switch (messageRecu) {
                case "A" :
                    boutonMode.setImageResource(R.drawable.autonome);
                    break;
                case "S" :
                    boutonMode.setImageResource(R.drawable.human);
                    break;
            }
        } else {

        }
    }

    public void boutonModeClick(View v) {
        boutonMode = findViewById(R.id.boutonMode);
        Drawable drawable = boutonMode.getDrawable();
        if (!drawable.getConstantState().equals(getResources().getDrawable(R.drawable.autonome).getConstantState())) {

            deviceInterface.sendMessage("A");
        } else {

            deviceInterface.sendMessage("S");
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
    public void listParams(String messageRecu) {
        ArrayList<String> listParams = new ArrayList();
        for (String mess : messageRecu.split("/")) {
            listParams.add(mess);
        }

        adapterParams = new ArrayAdapter(mContextArduinoDroid, android.R.layout.simple_list_item_1, listParams);

        listViewParams.setAdapter(adapterParams);
        boutonDonnees.setImageResource(R.drawable.empty);
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

}
