package com.ip.jmc.btardroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

//Classe boite à outils Bluetooth pour le MainActivity
public class BluetoothCustom extends MainActivity {
    public static SimpleBluetoothDeviceInterface sbt_device_interface;
    private static boolean bo_serial_test;
    private static BluetoothDevice deviceConnected;
    private BluetoothManager bt_manager = BluetoothManager.getInstance();
    private BluetoothAdapter bt_adapter = BluetoothAdapter.getDefaultAdapter();
    private Intent intent_set_bluetooth = new Intent("get-param");
    private String str_message_envoye;
    private String str_message_recu;
    private List<BluetoothDevice> pairedDevices;
    private ArrayList<String> deviceList = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Fonction lancée dans le OnCreate du MainActivity
    public void BluetoothCustomOnCreate() {
        //Si le Bluetooth n'est pas supporté
        if (bt_manager == null) {
            intent_set_bluetooth.putExtra("set_bluetooth", "btPasSupporte");
            LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
            finish();
        }
        //Si le Bluetooth est actif
        if (bt_adapter.isEnabled()) {
            //Nouveau receiver pour écouter les actions de l'adaptateur Bluetooth
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            con_main_activity.registerReceiver(bReceiver, filter);
            //On lance la mise à jour des devices appairés
            listDevicesBT();
        }
        //Si le Bluetooth est inactif
        else
        {
            //Revérifie s'il est inactif
            if (bt_adapter.isEnabled()) {
                //S'il est actif on envoi un message au receiver pour mettre à jour l'icône
                intent_set_bluetooth.putExtra("set_bluetooth", "btActive");
                LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
                //On lance la mise à jour des devices appairés
                listDevicesBT();
            } else {
                //Sinon on met à jour l'interface pour afficher l'icone Bluetooth inactif et vider les listes
                intent_set_bluetooth.putExtra("set_bluetooth", "btPasActive");
                LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
            }
        }
    }

    //Fonction pour récupérer un objet device selon une adresse mac
    public BluetoothDevice device(String mac){
        BluetoothDevice device = bt_adapter.getRemoteDevice(mac);
        return device;
    }
public BluetoothDevice deviceConnected(){
        if(deviceConnected != null ) {
            return deviceConnected;
        }
        else {
            return null;
        }
}
    //Fonction pour tester le Bluetooth en asynchrone
    public void testBluetooth() {
        if (!bt_adapter.isEnabled()) {
            intent_set_bluetooth.putExtra("set_bluetooth", "testBluetooth");
            LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
        }
        else
        {
            intent_set_bluetooth.putExtra("set_bluetooth", "testBluetoothActive");
            LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
        }
    }

    //Fonction pour activer ou désactiver le Bluetooth depuis l'application
    public void onOff() {
        if (!bt_adapter.isEnabled()) {
            bt_adapter.enable();
            intent_set_bluetooth.putExtra("set_bluetooth", "on");
            listDevicesBT();
        }else
        {
            bt_adapter.disable();
            deviceConnected = null;
            intent_set_bluetooth.putExtra("set_bluetooth", "off");
        }
        LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
    }

    //Appairage du périphérique
    public boolean createBond(BluetoothDevice btDevice)
            throws Exception {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    //Fonction appelée lors du clique sur un périphérique découvert
    public void appairage(BluetoothDevice device)
    {
        try {
            createBond(device);
        } catch (Exception e) {
            intent_set_bluetooth.putExtra("set_bluetooth", "echecConnection");
            intent_set_bluetooth.putExtra("set_device", device.getName());
            LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                listDevicesBT();
            }
        }, 2000);
    }

    //Fonction appelée lors du clique sur un périphérique appairé
    public void connectDevice(BluetoothDevice device) {
        if(bo_serial_test == false) {
            intent_set_bluetooth.putExtra("set_bluetooth", "connexion");
            intent_set_bluetooth.putExtra("set_device", device.getName());
            LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
            deviceConnected = device;
            //Connection en port série
             bt_manager.openSerialDevice(device.getAddress())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onConnected, this::onError);
        }
        else
        {
            deviceConnected = null;
            bo_serial_test = false;
            bt_adapter.disable();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    bt_adapter.enable();
                }
            }, 2000);
            intent_set_bluetooth.putExtra("set_bluetooth", "handlerHomeDeconnexion");
            intent_set_bluetooth.putExtra("set_device", device.getName());
            LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
        }
    }

    //Une fois connecté en port série
    public void onConnected(BluetoothSerialDevice connectedDevice) {
        bo_serial_test = true;
        intent_set_bluetooth.putExtra("set_bluetooth", "connecte");
        intent_set_bluetooth.putExtra("set_device", deviceConnected.getName());
        LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
        sbt_device_interface = connectedDevice.toSimpleDeviceInterface();
        //On écoute le périphérique
        sbt_device_interface.setListeners(this::onMessageReceived, this::onMessageSent, this::onErrorMessage);
    }

    //Lorsqu'on lance l'application ArduinoDroid lorsqu'on clique sur l'icone voiture
    public void lancementVoiture()
    {
        if(bt_adapter.isEnabled())
        {
            //Si on est connecté en port série on lance l'application
            if (bo_serial_test == true) {
                intent_set_bluetooth.putExtra("set_bluetooth", "voiture");
                LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
            } else {
                intent_set_bluetooth.putExtra("set_bluetooth", "nonVoiture");
                LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
            }
        }
        else
        {
            intent_set_bluetooth.putExtra("set_bluetooth", "btVoiture");
            LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
        }
    }

    //Lorsqu'un message est envoyé au périphérique connecté
    public void onMessageSent(String message) {
        str_message_envoye = message;
    }

    //Lorsqu'on reçoit un message du périphérique connecté
    public void onMessageReceived(String message) {
        str_message_recu = message;
        new ArduinoDroid().traitementReponse(str_message_envoye, str_message_recu);
    }

    //S'il y a une erreur de connexion en port série
    public void onError(Throwable error) {
        bo_serial_test = false;
        deviceConnected = null;
        intent_set_bluetooth.putExtra("set_bluetooth", "erreurSerie");
        intent_set_bluetooth.putExtra("set_device", deviceConnected.getName());
        LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
    }

    //S'il y a une erreur de connexion en port série
    public void onErrorMessage(Throwable error) {

    }

    //Fonction pour rafraichir la liste des périphériques appairés
    public void refreshBT()
    {
        if(bt_adapter.isEnabled())
        {
            intent_set_bluetooth.putExtra("set_bluetooth", "majBt");
            LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
        }
        else
        {
            listDevicesBT();
            intent_set_bluetooth.putExtra("set_bluetooth", "btDesactive");
            LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
        }
    }

    //Liste des périphériques appairés
    public void listDevicesBT() {
        if(bt_adapter.isEnabled()) {
            //On récupère la liste
            pairedDevices = bt_manager.getPairedDevicesList();
            //On vide l'ancienne liste
            deviceList.clear();
            //S'il y'a des périphériques appairés
            if (!pairedDevices.isEmpty()) {
                //Pour chaque périphérique on l'ajoute dans la liste
                for (BluetoothDevice device : pairedDevices) {
                    deviceList.add(device.getName() + " - " + device.getAddress());
                }
                //On envoi la liste au receiver
                intent_set_bluetooth.putStringArrayListExtra("set_deviceList", deviceList);
                intent_set_bluetooth.putExtra("set_bluetooth", "appaireDevice");
                LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
            } else {
                intent_set_bluetooth.putExtra("set_bluetooth", "nonappaire");
                LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
            }
        }
    }

    //Lors du lancement de la découverte des périphérique à proximité
    public void decouverteBluetooth() {
        if (bt_adapter.isEnabled()) {
            //Si la découverte est lancée on l'arrête
            if (bt_adapter.isDiscovering()) {
                bt_adapter.cancelDiscovery();
                intent_set_bluetooth.putExtra("set_bluetooth", "stopDecouverte");
            } else {
                //Sinon on la lance
                bt_adapter.startDiscovery();
                intent_set_bluetooth.putExtra("set_bluetooth", "decouverte");
            }
        } else {
            intent_set_bluetooth.putExtra("set_bluetooth", "btDesactive");
        }
            //Envoi au receiver du MainActivity
            LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
    }

    //Receiver pour écouter les actions du Bluetooth
    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //Si un périphérique est découvert
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                //On récupère les informations
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //On l'envoi au receiver du MainActivity
                intent_set_bluetooth.putExtra("set_bluetooth", "trouve");
                intent_set_bluetooth.putExtra("set_device", device.getName() + " - " + device.getAddress());
                LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
            }
            //Si la découverte est terminée
            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                if(bt_adapter.isEnabled()) {
                    intent_set_bluetooth.putExtra("set_bluetooth", "finRecherche");
                    LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Suppresion du receiver Bluetooth
        unregisterReceiver(bReceiver);
        //Fermeture de l'interface avec le périphérique Blutooth
        bt_manager.closeDevice(sbt_device_interface);
        //Fermeture du Bluetooth Manager
        bt_manager.close();
    }
    public void capteurs() {
        if(ArduinoDroid.testCenter == true && bo_serial_test == true)
        {
            sbt_device_interface.sendMessage("T");
        }
    }
}
