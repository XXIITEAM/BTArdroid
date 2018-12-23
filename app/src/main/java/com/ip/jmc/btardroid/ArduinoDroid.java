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
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.ArrayList;

public class ArduinoDroid extends MainActivity {
    static Button boutonMode, boutonDonnees, bt3, bt4, bt5, bt6;

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
    }

    public void convertParams(String messageEnvoye, String messageRecu) {

        // We received a message! Handle it here.
        if (messageEnvoye != null) {
            switch (messageEnvoye) {
                case "F":
                    break;
                case "B":
                    break;
                case "Z":
                    ArrayList<String> listParams = new ArrayList();
                    for (String mess : messageRecu.split("/")) {
                        listParams.add(mess);
                    }

                    adapterParams = new ArrayAdapter(mContextArduinoDroid, android.R.layout.simple_list_item_1, listParams);

                    listViewParams.setAdapter(adapterParams);
                    break;

            }
        } else {

        }
    }

    public void boutonModeClick(View v) {
        boutonMode = findViewById(R.id.boutonMode);
        if (boutonMode.getText().equals(getString(R.string.strCommandeManuelle))) {
            boutonMode.setText(R.string.strCommandeAuto);
            deviceInterface.sendMessage("A");
        } else {
            boutonMode.setText(R.string.strCommandeManuelle);
            deviceInterface.sendMessage("S");
        }
    }


    public void boutonDonneesClick(View v) {
        deviceInterface.sendMessage("Z");

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
