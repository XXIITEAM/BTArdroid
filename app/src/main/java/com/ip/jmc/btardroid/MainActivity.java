package com.ip.jmc.btardroid;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
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
    public static SimpleBluetoothDeviceInterface sbt_device_interface;
    public static BluetoothManager bt_manager = BluetoothManager.getInstance();
    public static BluetoothAdapter bt_adapter = BluetoothAdapter.getDefaultAdapter();
    private ListView lv_bt_devices;
    private ListView lv_bt_discover;
    private ArrayList al_bt_devices = new ArrayList();
    private ArrayList al_bt_devices_discovered = new ArrayList();
    private ArrayAdapter aa_bt_paired;
    private ArrayAdapter aa_bt_decouverte;
    private ImageButton btn_bt_connect;
    private ImageButton btn_bt_recherche;
    private TextView tv_bluetooth;
    private TextView tv_btn_recherche;
    private TextView tv_btn_bt;
    private TextView tv_discovered;
    private TextView tv_appaires;
    private TextView tv_btn_rafraichir;
    private TextView tv_btn_quitter;
    private TextView tv_btn_voiture;

    public static boolean bo_serial_test = false;

    public static Context getContext() {
        return con_main_activity;
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
        tv_btn_rafraichir.setTextColor(Color.rgb(104, 149, 197));
        tv_btn_recherche.setTextColor(Color.rgb(104, 149, 197));
        aa_bt_decouverte = new ArrayAdapter(con_main_activity, android.R.layout.simple_list_item_1, al_bt_devices_discovered);
        aa_bt_paired = new ArrayAdapter(con_main_activity, android.R.layout.simple_list_item_1, al_bt_devices);
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this).registerReceiver(mainMessageReceiver,
                new IntentFilter("get-param"));
        setContentView(R.layout.activity_main);
        con_main_activity = getBaseContext();
        initInterface();
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
        new BluetoothCustom().BluetoothCustomOnCreate();
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
                                            new BluetoothCustom().listDevicesBT();
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
    public void btnBTOn(View v) {
        new BluetoothCustom().onOff();
    }

    public void BtnRecherche(View v) {
        new BluetoothCustom().decouverteBluetooth();
    }

    public void BtnRafraichir(View v) {
        if(bt_adapter.isEnabled())
        {
            tv_bluetooth.setTextColor(Color.rgb(0,200,0));
            tv_bluetooth.setText("Mise à jour de la liste des périphériques appairés ...");
            handlerHome();
            tv_appaires.setText("");
            aa_bt_paired.clear();
            al_bt_devices.clear();
            new BluetoothCustom().listDevicesBT();
        }
        else
        {
            tv_bluetooth.setTextColor(Color.rgb(200,0,0));
            tv_bluetooth.setText("Veuiller activer le Bluetooth en cliquant sur l'icône ...");
            handlerHome();
        }

    }

    public void BtnVoiture(View v) {
        new BluetoothCustom().lancementVoiture();
    }

    public void btnSuivant(View v) {
        Intent intent = new Intent(con_main_activity, ArduinoDroid.class);
        startActivity(intent);
    }

    public void BtnQuitter(View v) {
        System.exit(0);
    }

    private void handlerHome()
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                tv_bluetooth.setTextColor(Color.rgb(124, 124, 124));
                tv_bluetooth.setText("L'équipe XXIITEAM vous souhaite la bienvenue sur l'application BTArdroid");
            }
        }, 2500);
    }

    private BroadcastReceiver mainMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s1= intent.getStringExtra("set_bluetooth");
            String device;
            switch (s1) {
                case "testBluetooth":
                    tv_discovered.setVisibility(TextView.INVISIBLE);
                    tv_appaires.setVisibility(TextView.INVISIBLE);
                    bt_adapter.disable();
                    btn_bt_connect.setImageResource(R.drawable.bt_off);
                    tv_btn_bt.setTextColor(Color.rgb(200, 0, 0));
                    tv_btn_bt.setText("Activer");
                    aa_bt_paired.clear();
                    aa_bt_decouverte.clear();
                    al_bt_devices.clear();
                    al_bt_devices_discovered.clear();
                    handlerHome();
                    break;
                case"btPasSupporte":
                    tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                    tv_bluetooth.setText("Le Bluetooth n'est pas supporté ...");
                    break;
                case "on":
                    tv_bluetooth.setTextColor(Color.rgb(0, 200, 0));
                    tv_bluetooth.setText("Activation du Bluetooth ...");
                    handlerHome();
                    btn_bt_connect.setImageResource(R.drawable.bt_on_2);
                    tv_btn_bt.setTextColor(Color.rgb(104, 149, 197));
                    tv_btn_bt.setText("Désactiver");
                    break;
                    case "off":
                    tv_discovered.setVisibility(TextView.INVISIBLE);
                    tv_appaires.setVisibility(TextView.INVISIBLE);
                    tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                    tv_bluetooth.setText("Déconnexion du Bluetooth ...");
                    handlerHome();
                    btn_bt_connect.setImageResource(R.drawable.bt_off);
                    tv_btn_bt.setTextColor(Color.rgb(200, 0, 0));
                    tv_btn_bt.setText("Activer");
                    aa_bt_paired.clear();
                    aa_bt_decouverte.clear();
                    al_bt_devices.clear();
                    al_bt_devices_discovered.clear();
                    break;
                case "btDesactive":
                    tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                    tv_bluetooth.setText("Veuiller activer le Bluetooth en cliquant sur l'icône ...");
                    handlerHome();
                    break;
                case "btPasActive":
                    btn_bt_connect.setImageResource(R.drawable.bt_off);
                    tv_btn_bt.setTextColor(Color.rgb(200, 0, 0));
                    tv_btn_bt.setText("Activer");
                    break;
                case "btActive":
                    btn_bt_connect.setImageResource(R.drawable.bt_on_2);
                    tv_btn_bt.setTextColor(Color.rgb(104, 149, 197));
                    tv_btn_bt.setText("Désactiver");
                    break;
                case "decouverte":
                    btn_bt_recherche.setImageResource(R.drawable.loupe_2);
                    al_bt_devices_discovered.clear();
                    aa_bt_decouverte.clear();
                    tv_btn_recherche.setTextColor(Color.rgb(255, 127, 80));
                    tv_bluetooth.setTextColor(Color.rgb(0, 200, 0));
                    tv_bluetooth.setText("Recherche en cours ...");
                    tv_btn_recherche.setText("Arrêter");
                    break;
                case "stopDecouverte":
                    btn_bt_recherche.setImageResource(R.drawable.loupe_1);
                    tv_btn_recherche.setTextColor(Color.rgb(104, 149, 197));
                    tv_btn_recherche.setText("Rechercher");
                    tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                    tv_bluetooth.setText("Fin de la recherche ...");
                    handlerHome();
                    break;
                case "appaire":
                    device = intent.getStringExtra("set_device");
                    tv_appaires.setTextColor(Color.rgb(104, 149, 197));
                    tv_appaires.setText("Liste des périphériques appairés :");
                    tv_appaires.setVisibility(TextView.VISIBLE);
                    aa_bt_paired.notifyDataSetChanged();
                    lv_bt_devices.setVisibility(TextView.VISIBLE);
                    if (!al_bt_devices.contains(device)) {
                        al_bt_devices.add(device);
                        lv_bt_devices.setAdapter(aa_bt_paired);
                        lv_bt_devices.setOnItemClickListener((popup, lv1, position, id) -> {
                                    String selLv = lv_bt_devices.getItemAtPosition(position).toString().trim();
                                    String segments[] = selLv.split(" - ");
                                    String macItem = segments[segments.length - 1];
                                    BluetoothDevice mBluetoothDevice = bt_adapter.getRemoteDevice(macItem);
                                    new BluetoothCustom().connectDevice(mBluetoothDevice);
                                }
                        );
                    }
                    break;
                case "nonappaire":
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            tv_appaires.setTextColor(Color.rgb(200, 0, 0));
                            tv_appaires.setVisibility(TextView.VISIBLE);
                            lv_bt_devices.setVisibility(TextView.INVISIBLE);
                            tv_appaires.setText("Aucun périphérique Bluetooth appairé");
                        }
                    }, 4000);
                    break;
                case "connecte":
                    device= intent.getStringExtra("set_device");
                    tv_bluetooth.setTextColor(Color.rgb(0, 200, 0));
                    tv_bluetooth.setText(device + " est connecté avec un port série");
                    break;
                case"echecConnection":
                    device= intent.getStringExtra("set_device");
                    tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                    tv_bluetooth.setText("Impossible d'appairer le périphérique "+device);
                    handlerHome();
                    break;
                case "connection":
                    device= intent.getStringExtra("set_device");
                    al_bt_devices_discovered.remove(device);
                    break;
                case "erreurSerie":
                    tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                    tv_bluetooth.setText("Impossible de connecter le périphérique en port série ...");
                    handlerHome();
                    break;
                case "trouve":
                    device = intent.getStringExtra("set_device");
                    if (!al_bt_devices_discovered.contains(device) && !al_bt_devices.contains(device)) {
                        tv_discovered.setVisibility(TextView.VISIBLE);
                        al_bt_devices_discovered.add(device);
                        lv_bt_discover.setAdapter(aa_bt_decouverte);
                        lv_bt_discover.setOnItemClickListener((popup, lv1, position, id) -> {
                                    String selLv = lv_bt_discover.getItemAtPosition(position).toString().trim();
                                    String segments[] = selLv.split(" - ");
                                    String macItem = segments[segments.length - 1];
                                    BluetoothDevice mBluetoothDevice = bt_adapter.getRemoteDevice(macItem);
                                    new BluetoothCustom().appairage(mBluetoothDevice);
                                }
                        );
                        aa_bt_decouverte.notifyDataSetChanged();
                    }
                    break;
                case "finRecherche":
                    tv_bluetooth.setText("");
                    tv_discovered.setVisibility(TextView.VISIBLE);
                    btn_bt_recherche.setImageResource(R.drawable.loupe_1);
                    tv_btn_recherche.setTextColor(Color.rgb(104, 149, 197));
                    tv_btn_recherche.setText("Rechercher");
                    tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                    tv_bluetooth.setText("Fin de la recherche ...");
                    if (al_bt_devices_discovered.isEmpty()) {
                        tv_discovered.setVisibility(TextView.INVISIBLE);
                        tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                    }
                    handlerHome();
                    break;
                case "nonVoiture":
                    tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                    tv_bluetooth.setText("Vous n'êtes pas connecté à une voiture arduino ...");
                    handlerHome();
                    break;
                case "btVoiture":
                    tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                    tv_bluetooth.setText("Le Bluetooth doit être activé pour utiliser cette application ...");
                    handlerHome();
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mainMessageReceiver);
        super.onDestroy();
    }

}