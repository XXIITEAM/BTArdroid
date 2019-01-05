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
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public static Context con_main_activity;
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
    private TextView tv_bas;

    //Récupération du contexte MainActivity
    public static Context getContext() {
        return con_main_activity;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Local Receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mainMessageReceiver,
                new IntentFilter("get-param"));
        setContentView(R.layout.activity_main);
        con_main_activity = getBaseContext();
        //Initialisation de l'interface
        initInterface();
        //Demande des permissions
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
        //Tâche asynchrone toutes les 5 secondes pour tester la connectivité Bluetooth et la liste des devices appairés
        asyncTask();
    }

    private void asyncTask()
    {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BluetoothDevice bd = new BluetoothCustom().deviceConnected();
                            if(bd != null && ArduinoDroid.testCenter == true)
                            {
                                new BluetoothCustom().capteurs();
                            }
                            else
                            {   //Liste des périphériques appairés
                                new BluetoothCustom().listDevicesBT();
                            }
                            //Test de la connectivité bluetooth
                            new BluetoothCustom().testBluetooth();
                        }
                    });
                }
            }
        });
    }

    private void initInterface()
    {
        //Initialisation de l'interface
        lv_bt_devices = findViewById(R.id.listviewbt);
        lv_bt_discover = findViewById(R.id.listviewbtdiscover);
        btn_bt_connect = findViewById(R.id.BtnBT);
        btn_bt_recherche = findViewById(R.id.BtnRecherche);
        tv_bluetooth = findViewById(R.id.textViewBT);
        tv_btn_recherche = findViewById(R.id.textViewBtnRecherche);
        tv_btn_bt = findViewById(R.id.textViewBtnBt);
        tv_appaires = findViewById(R.id.textViewAppaires);
        tv_discovered = findViewById(R.id.textViewDiscovered);
        tv_bas = findViewById(R.id.textViewBas);
        tv_bas.setMovementMethod(LinkMovementMethod.getInstance());
        //Nouvels ArrayAdapter pour les listes devices découverts et devices appairés
        aa_bt_decouverte = new ArrayAdapter(con_main_activity, android.R.layout.simple_list_item_1, al_bt_devices_discovered);
        aa_bt_paired = new ArrayAdapter(con_main_activity, android.R.layout.simple_list_item_1, al_bt_devices);
    }

    //Bouton Bluetooth (icone Bluetooth)
    public void btnBTOn(View v) {
        //Activation ou désactivation du Bluetooth
        new BluetoothCustom().onOff();
    }

    //Bouton recherche (icone loupe)
    public void BtnRecherche(View v) {
        //Découverte des périphériques Bluetooth à proximité
        new BluetoothCustom().decouverteBluetooth();
    }

    //Bouton rafraichir
    public void BtnRafraichir(View v) {
        //On rafraichit la liste des devices appairés
        new BluetoothCustom().refreshBT();
    }

    public void BtnVoiture(View v) {
        //Lancement de l'activité ArduinoDroid
        new BluetoothCustom().lancementVoiture();
    }

    public void BtnQuitter(View v) {
        //Quitte l'application
        System.exit(0);
    }

    //Handler pour réafficher le message d'accueil après 2500 millisecondes
    private void handlerHome()
    {
        BluetoothDevice deviceConnected = new BluetoothCustom().deviceConnected();
        if(deviceConnected == null) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    tv_bluetooth.setTextColor(Color.rgb(124, 124, 124));
                    tv_bluetooth.setText("L'équipe XXII Team vous souhaite la bienvenue sur l'application BTArdroid\nCette application nécessite une carte Arduino avec un module Bluetooth. Pour ce projet nous utilisons une voiture autonome avec une carte Arduino Mega 2650. Le Buetooth de votre téléphone doit être activé.");
                }
            }, 3000);
        }
        else
        {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                        tv_bluetooth.setTextColor(Color.rgb(124, 124, 124));
                        tv_bluetooth.setText(deviceConnected.getName() + " est connecté");
                }
            }, 3000);
        }
    }

    //Receiver local pour traiter la partie affichage dynamique. Il est utilisé dans les méthodes de BluetoothCustom.
    private BroadcastReceiver mainMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Variable principale pour switcher dans les cas
            String s1= intent.getStringExtra("set_bluetooth");
            //Variable pour récupérer le device passé en paramètre
            String device;
            //Variable pour récupérer la liste des devices appairés
            ArrayList<String> deviceList;
            //Switch sur la variable principale
            switch (s1) {
                //Si le Blue tooth est inactif
                case "testBluetooth":
                    tv_bluetooth.setTextColor(Color.rgb(124, 124, 124));
                    tv_bluetooth.setText("L'équipe XXII Team vous souhaite la bienvenue sur l'application BTArdroid\nCette application nécessite une carte Arduino avec un module Bluetooth. Pour ce projet nous utilisons une voiture autonome avec une carte Arduino Mega 2650. Le Buetooth de votre téléphone doit être activé.");
                    tv_discovered.setVisibility(TextView.INVISIBLE);
                    tv_appaires.setVisibility(TextView.INVISIBLE);
                    btn_bt_connect.setImageResource(R.drawable.bt_off);
                    tv_btn_bt.setTextColor(Color.rgb(200, 0, 0));
                    tv_btn_bt.setText("Activer");
                    clearIHM();
                    handlerHome();
                    break;
                //Si le Bluetooth est actif
                case "testBluetoothActive":
                    tv_appaires.setVisibility(TextView.VISIBLE);
                    btn_bt_connect.setImageResource(R.drawable.bt_on_2);
                    tv_btn_bt.setTextColor(Color.rgb(255, 255, 255));
                    tv_btn_bt.setText("Désactiver");
                    break;
                 //Si le Bluetooth n'est aps supporté
                case"btPasSupporte":
                    tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                    tv_bluetooth.setText("Le Bluetooth n'est pas supporté ...");
                    clearIHM();
                    break;
                //Si on active le Bluetooth en cliquant sur l'icone Bluetooth
                case "on":
                    tv_bluetooth.setTextColor(Color.rgb(0, 200, 0));
                    tv_bluetooth.setText("Activation du Bluetooth ...");
                    //btn_bt_connect.setImageResource(R.drawable.bt_on_2);
                    //tv_btn_bt.setTextColor(Color.rgb(104, 149, 197));
                    //tv_btn_bt.setText("Désactiver");
                    handlerHome();
                    break;
                //Si on désactive le Bluetooth en cliquant sur l'icone Bluetooth
                case "off":
                    tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                    tv_bluetooth.setText("Déconnexion du Bluetooth ...");
                    btn_bt_connect.setImageResource(R.drawable.bt_off);
                    tv_btn_bt.setTextColor(Color.rgb(200, 0, 0));
                    tv_btn_bt.setText("Activer");
                    clearIHM();
                    handlerHome();
                    break;
                //Si on utilise l'application mais que le Bluetooth est désactivé
                case "btDesactive":
                    tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                    tv_bluetooth.setText("Veuiller activer le Bluetooth en cliquant sur l'icône ...");
                    clearIHM();
                    handlerHome();
                    break;
                //Changement de l'icone Bluetooth selon si il est actif ou inactif
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
                //Si on lance une découverte de périphériques à proximité
                case "decouverte":
                    btn_bt_recherche.setImageResource(R.drawable.loupe_2);
                    aa_bt_decouverte.clear();
                    lv_bt_discover.setAdapter(aa_bt_decouverte);
                    aa_bt_decouverte.notifyDataSetChanged();
                    tv_btn_recherche.setTextColor(Color.rgb(255, 200, 80));
                    tv_bluetooth.setTextColor(Color.rgb(0, 200, 0));
                    tv_bluetooth.setText("Découverte des périphériques à proximité ...");
                    tv_btn_recherche.setText("Arrêter");
                    break;
                 //Si on arrête la découverte de périphériques à proximité en cliquant sur l'icone loupe
                case "stopDecouverte":
                    btn_bt_recherche.setImageResource(R.drawable.loupe_1);
                    tv_btn_recherche.setTextColor(Color.rgb(104, 149, 197));
                    tv_btn_recherche.setText("Rechercher");
                    tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                    tv_bluetooth.setText("Fin de la recherche ...");
                    handlerHome();
                    break;
                //Liste des périphériques appairés
                case "appaireDevice":
                    deviceList = intent.getStringArrayListExtra("set_deviceList");
                    al_bt_devices.clear();
                    tv_appaires.setTextColor(Color.rgb(104, 149, 197));
                    tv_appaires.setText("Liste des périphériques appairés :");
                    tv_appaires.setVisibility(TextView.VISIBLE);
                    lv_bt_devices.setVisibility(TextView.VISIBLE);
                    for (String pairedDevices : deviceList) {
                        if (!al_bt_devices.contains(pairedDevices)) {
                            al_bt_devices.add(pairedDevices);
                            al_bt_devices_discovered.remove(pairedDevices);
                            if(al_bt_devices_discovered.isEmpty())
                            {
                                tv_discovered.setVisibility(TextView.INVISIBLE);
                            }
                        }
                    }
                    lv_bt_devices.setAdapter(aa_bt_paired);
                    lv_bt_devices.setOnItemClickListener((popup, lv1, position, id) -> {
                                String selLv = lv_bt_devices.getItemAtPosition(position).toString().trim();
                                String segments[] = selLv.split(" - ");
                                String macItem = segments[segments.length - 1];
                                BluetoothDevice mBluetoothDevice = new BluetoothCustom().device(macItem);
                                new BluetoothCustom().connectDevice(mBluetoothDevice);
                            }
                    );
                    aa_bt_paired.notifyDataSetChanged();
                    lv_bt_discover.setAdapter(aa_bt_decouverte);
                    aa_bt_decouverte.notifyDataSetChanged();
                    break;
                //S'il n'y a pas de périphériques appairés
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
                 //Si on parvient à se connecter à un périphérique en port série
                case "connecte":
                    device= intent.getStringExtra("set_device");
                    tv_bluetooth.setTextColor(Color.rgb(124, 124, 124));
                    tv_bluetooth.setText(device+" est connecté");
                    break;
                //Après une déconnexiona vec un périphérique Bluetooth
                case "handlerHomeDeconnexion":
                    device= intent.getStringExtra("set_device");
                    tv_bluetooth.setTextColor(Color.rgb(0, 200, 0));
                    tv_bluetooth.setText("Déconnexion du périphérique : "+device);
                    handlerHome();
                    break;
                //Si on essai de se connecter à un périphérique en port série
                case "connexion":
                    device= intent.getStringExtra("set_device");
                    tv_bluetooth.setTextColor(Color.rgb(0, 200, 0));
                    tv_bluetooth.setText("Connexion en port série avec le périphérique : "+device);
                    break;
                //Si l'application ne peut pas appairer le périphérique
                case"echecConnection":
                    device= intent.getStringExtra("set_device");
                    tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                    tv_bluetooth.setText("Impossible d'appairer le périphérique "+device);
                    handlerHome();
                    break;
                //Si le périphérique ne peut pas être connecté en port série
                case "erreurSerie":
                    device= intent.getStringExtra("set_device");
                    tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                    tv_bluetooth.setText("Impossible de connecter le périphérique "+device+" en port série ...");
                    handlerHome();
                    break;
                //Si un périphérique à proximité est découvert
                case "trouve":
                    device = intent.getStringExtra("set_device");
                    if(!al_bt_devices.contains(device)) {
                        tv_discovered.setVisibility(TextView.VISIBLE);
                        if (!al_bt_devices_discovered.contains(device)) {
                            al_bt_devices_discovered.add(device);
                            lv_bt_discover.setAdapter(aa_bt_decouverte);
                            lv_bt_discover.setOnItemClickListener((popup, lv1, position, id) -> {
                                        String selLv = lv_bt_discover.getItemAtPosition(position).toString().trim();
                                        String segments[] = selLv.split(" - ");
                                        String macItem = segments[segments.length - 1];
                                        BluetoothDevice mBluetoothDevice = new BluetoothCustom().device(macItem);
                                        new BluetoothCustom().appairage(mBluetoothDevice);
                                    }
                            );
                            aa_bt_decouverte.notifyDataSetChanged();
                        }
                    }
                    break;
                //Lorsque la découverte des périphériques à proximité est terminé
                case "finRecherche":
                    tv_bluetooth.setText("");
                    tv_discovered.setVisibility(TextView.VISIBLE);
                    btn_bt_recherche.setImageResource(R.drawable.loupe_1);
                    tv_btn_recherche.setTextColor(Color.rgb(255, 255, 255));
                    tv_btn_recherche.setText("Rechercher");
                    tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                    tv_bluetooth.setText("Fin de la recherche ...");
                    if (al_bt_devices_discovered.isEmpty()) {
                        tv_discovered.setVisibility(TextView.INVISIBLE);
                        tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                    }
                    handlerHome();
                    break;
                //Si on rafraichit la liste des périphériques appairés
                case "majBt":
                    tv_bluetooth.setTextColor(Color.rgb(0,200,0));
                    tv_bluetooth.setText("Mise à jour de la liste des périphériques appairés ...");
                    tv_appaires.setText("");
                    aa_bt_paired.clear();
                    aa_bt_paired.notifyDataSetChanged();
                    handlerHome();
                    break;
                 //Si on n'est aps connecté en port série à un périphérique
                case "nonVoiture":
                    tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                    tv_bluetooth.setText("Vous n'êtes pas connecté à une voiture arduino ...");
                    handlerHome();
                    break;
                //Si on n'est aps connecté en Bluetooth et qu'on veut lancer l'application ArduinoDroid
                case "btVoiture":
                    tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                    tv_bluetooth.setText("Le Bluetooth doit être activé pour utiliser cette application ...");
                    handlerHome();
                    break;
                //Lancement de l'activité ArduinoDroid en cliquant sur l'icone voiture
                case "voiture":
                    Intent myIntent = new Intent(con_main_activity, ArduinoDroid.class);
                    startActivity(myIntent);
                    break;
            }
        }
    };

    //On vide les listes des périphériques appairés et découverts
    private void clearIHM()
    {
        tv_discovered.setVisibility(TextView.INVISIBLE);
        tv_appaires.setVisibility(TextView.INVISIBLE);
        al_bt_devices.clear();
        al_bt_devices_discovered.clear();
        aa_bt_paired.clear();
        aa_bt_decouverte.clear();
        aa_bt_decouverte.notifyDataSetChanged();
        lv_bt_discover.setAdapter(aa_bt_decouverte);
        lv_bt_devices.setAdapter(aa_bt_paired);
        aa_bt_paired.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        //Suppression du receiver local
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mainMessageReceiver);
        super.onDestroy();
    }

}