package com.ip.jmc.btardroid;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public static Context con_main_activity;
    public final static String EXTRA_MESSAGE = "com.ip.jmc.MESSAGE";
    public final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    public static SimpleBluetoothDeviceInterface sbt_device_interface;
    public static String str_message_envoye = "";
    public static String str_message_recu = "";
    public static BluetoothManager bt_manager = BluetoothManager.getInstance();
    public static BluetoothAdapter bt_adapter = BluetoothAdapter.getDefaultAdapter();
    public static ListView lv_bt_devices;
    public static ListView lv_bt_discover;
    public static ListView lv_get_vh_data;
    public static ArrayList al_bt_devices = new ArrayList();
    public static ArrayList al_bt_devices_discovered = new ArrayList();
    static ArrayAdapter aa_bt_paired;
    static ArrayAdapter aa_bt_decouverte;
    static ArrayAdapter aa_vh_params;
    static ImageButton btn_bt_connect;
    static ImageButton btn_bt_recherche;
    static TextView tv_bluetooth;
    static TextView tv_btn_recherche;
    static TextView tv_btn_bt;
    static TextView tv_discovered;
    static TextView tv_appaires;
    static TextView tv_btn_rafraichir;
    static TextView tv_btn_quitter;
    static TextView tv_btn_voiture;
    public static boolean bo_serial_test = false;
    public static void btnBTOn(View v) {
        new BluetoothCustom().btOnOff();
    }
    public void BtnRecherche(View v) {
        new BluetoothCustom().decouverteBluetooth();
    }
    public void BtnRafraichir(View v) {
        if(bt_adapter.isEnabled())
        {
            tv_bluetooth.setTextColor(Color.rgb(0,200,0));
            tv_bluetooth.setText("Mise à jour de la liste des périphériques appairés ...");
            tv_appaires.setText("");
            new BluetoothCustom().listDevicesBT();

        }
        else
        {
            tv_bluetooth.setTextColor(Color.rgb(200,0,0));
            tv_bluetooth.setText("Veuiller activer le Bluetooth en cliquant sur l'icône ...");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    tv_bluetooth.setTextColor(Color.rgb(124,124,124));
                    tv_bluetooth.setText("L'équipe XXIITEAM vous souhaite la bienvenue sur l'application BTArdroid");
                }
            }, 3000);
        }

    }
    public void BtnVoiture(View v) {
        if(bt_adapter.isEnabled())
        {
            if (bo_serial_test == true) {
                Intent myIntent = new Intent(con_main_activity, ArduinoDroid.class);
                startActivity(myIntent);
            } else {
                tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                tv_bluetooth.setText("Vous n'êtes pas connecté à une voiture arduino ...");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        tv_bluetooth.setTextColor(Color.rgb(124, 124, 124));
                        tv_bluetooth.setText("L'équipe XXIITEAM vous souhaite la bienvenue sur l'application BTArdroid");
                    }
                }, 3000);
            }
        }
        else
        {
            tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
            tv_bluetooth.setText("Le Bluetooth doit être activé pour utiliser cette application ...");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    tv_bluetooth.setTextColor(Color.rgb(124, 124, 124));
                    tv_bluetooth.setText("L'équipe XXIITEAM vous souhaite la bienvenue sur l'application BTArdroid");
                }
            }, 3000);
        }
    }
    public void BtnQuitter(View v) {
        System.exit(0);
    }
    public static Context getContext() {
        return con_main_activity;
    }

    public void btnSuivant(View v) {
        Intent intent = new Intent(con_main_activity, ArduinoDroid.class);
        startActivity(intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        con_main_activity = getBaseContext();
        initInterface();
        new BluetoothCustom().BluetoothCustomOnCreate();
        int MY_PERMISSIONS_REQUEST = 200;
        int permissions=ContextCompat.checkSelfPermission (this,Manifest.permission.ACCESS_FINE_LOCATION);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST);
        permissions=ContextCompat.checkSelfPermission (this,Manifest.permission.BLUETOOTH);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.BLUETOOTH},
                MY_PERMISSIONS_REQUEST);
        permissions=ContextCompat.checkSelfPermission (this,Manifest.permission.BLUETOOTH_ADMIN);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.BLUETOOTH_ADMIN},
                MY_PERMISSIONS_REQUEST);
       AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                    while(true) {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(bt_adapter.isEnabled()) {
                                            new BluetoothCustom().listDevicesBTThread();
                                        }
                                        else
                                        {
                                            new BluetoothCustom().testBluetooth();
                                        }
                                    }
                                });
                    }
            }
       });
    }

    private void initInterface()
    {
        lv_bt_devices = findViewById(R.id.listviewbt);
        lv_bt_discover = findViewById(R.id.listviewbtdiscover);
        btn_bt_connect = findViewById(R.id.BtnBT);
        btn_bt_recherche = findViewById(R.id.BtnRecherche);
        tv_bluetooth = findViewById(R.id.textViewBT);
        tv_btn_recherche = findViewById(R.id.textViewBtnRecherche);
        tv_btn_bt = findViewById(R.id.textViewBtnBt);
        tv_appaires = findViewById(R.id.textViewAppaires);
        tv_discovered = findViewById(R.id.textViewDiscovered);
        tv_btn_rafraichir = findViewById(R.id.textViewBtnRafraichir);
        tv_btn_voiture = findViewById(R.id.textViewBtnVoiture);
        tv_btn_quitter = findViewById(R.id.textViewBtnQuitter);
        tv_btn_quitter.setTextColor(Color.rgb(104,149,197));
        tv_btn_voiture.setTextColor(Color.rgb(104,149,197));
    }

}