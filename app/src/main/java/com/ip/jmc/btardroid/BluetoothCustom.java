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
import java.util.List;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BluetoothCustom extends MainActivity {
    ArduinoDroid ard;
    private BluetoothManager bt_manager = BluetoothManager.getInstance();
    private BluetoothAdapter bt_adapter = BluetoothAdapter.getDefaultAdapter();
    public static SimpleBluetoothDeviceInterface sbt_device_interface;
    private BluetoothDevice deviceConnected;
    private boolean bo_first_found;
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private Intent intent_set_bluetooth = new Intent("get-param");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private String str_message_envoye;
    private String str_message_recu;
    public void BluetoothCustomOnCreate() {
        if (bt_manager == null) {
            intent_set_bluetooth.putExtra("set_bluetooth", "btPasSupporte");
            LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
            finish();
        }
        if (bt_adapter.isEnabled()) {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            con_main_activity.registerReceiver(bReceiver, filter);
            listDevicesBT();
        }
        else
        {
            if (bt_adapter.isEnabled()) {
                intent_set_bluetooth.putExtra("set_bluetooth", "btActive");
                LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
            } else {
                intent_set_bluetooth.putExtra("set_bluetooth", "btPasActive");
                LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
            }
        }
    }
    public BluetoothDevice device(String mac){
        BluetoothDevice device = bt_adapter.getRemoteDevice(mac);
        return device;
    }
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
    public void onOff() {
        if (!bt_adapter.isEnabled()) {
            bt_adapter.enable();
            intent_set_bluetooth.putExtra("set_bluetooth", "on");
        }else
        {
            bt_adapter.disable();
            intent_set_bluetooth.putExtra("set_bluetooth", "off");
        }
        LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
    }

    public boolean createBond(BluetoothDevice btDevice)
            throws Exception {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    public void appairage(BluetoothDevice device)
    {
        try {
            createBond(device);
            intent_set_bluetooth.putExtra("set_bluetooth", "connection");
            intent_set_bluetooth.putExtra("set_device", device.getName());
        } catch (Exception e) {
            intent_set_bluetooth.putExtra("set_bluetooth", "echecConnection");
            intent_set_bluetooth.putExtra("set_device", device.getName());
        }
        LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
    }

    public void connectDevice(BluetoothDevice device) {
        bt_adapter.cancelDiscovery();
        deviceConnected = device;
        bt_manager.openSerialDevice(device.getAddress())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onConnected, this::onError);
        /*try {
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
            socket.connect();
            if (socket.isConnected()) {
                tv_bluetooth.setTextColor(Color.rgb(0, 200, 0));
                tv_bluetooth.setText(device.getName() + " est connecté avec un socket");
            }
        } catch (IOException e) {
            //tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
            //tv_bluetooth.setText("Erreur : " + e.toString());
            tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
            tv_bluetooth.setText("Impossible d'établir un socket avec le périphérique ...");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    tv_bluetooth.setTextColor(Color.rgb(124, 124, 124));
                    tv_bluetooth.setText("L'équipe XXIITEAM vous souhaite la bienvenue sur l'application BTArdroid");
                }
            }, 2000);
        }*/
    }

    public void onConnected(BluetoothSerialDevice connectedDevice) {
        intent_set_bluetooth.putExtra("set_bluetooth", "connecte");
        intent_set_bluetooth.putExtra("set_device", deviceConnected.getName());
        LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
        bo_serial_test = true;
        sbt_device_interface = connectedDevice.toSimpleDeviceInterface();
        sbt_device_interface.setListeners(this::onMessageReceived, this::onMessageSent, this::onError);
    }
    public void lancementVoiture()
    {
        if(bt_adapter.isEnabled())
        {
            if (bo_serial_test == true) {
                Intent myIntent = new Intent(con_main_activity, ArduinoDroid.class);
                startActivity(myIntent);
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
    public void onMessageSent(String message) {
        str_message_envoye = message;
    }

    public void onMessageReceived(String message) {
        str_message_recu = message;
        new ArduinoDroid().traitementReponse(str_message_envoye, str_message_recu);
    }

    public void onError(Throwable error) {
        bo_serial_test = false;
        intent_set_bluetooth.putExtra("set_bluetooth", "erreurSerie");
        intent_set_bluetooth.putExtra("set_device", deviceConnected.getName());
        LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
    }
    public void refreshBT()
    {
        if(bt_adapter.isEnabled())
        {
            intent_set_bluetooth.putExtra("set_bluetooth", "majBT");
            LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
            listDevicesBT();
        }
        else
        {
            intent_set_bluetooth.putExtra("set_bluetooth", "iconeBt");
            LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
        }
    }
    public void listDevicesBT() {
        if(bt_adapter.isEnabled()) {
            intent_set_bluetooth.putExtra("set_bluetooth", "appaire");
            List<BluetoothDevice> pairedDevices = bt_manager.getPairedDevicesList();
            while (pairedDevices.isEmpty()) {
                pairedDevices = bt_manager.getPairedDevicesList();
                break;
            }
            if (!pairedDevices.isEmpty()) {
                for (BluetoothDevice device : pairedDevices) {
                    intent_set_bluetooth.putExtra("set_device", device.getName() + " - " + device.getAddress());

                    intent_set_bluetooth.putExtra("set_bluetooth", "appaire");
                }
            } else {
                intent_set_bluetooth.putExtra("set_bluetooth", "nonappaire");
            }
            LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
        }
    }

    public void deconnexion(View v) {
        bt_manager.closeDevice(sbt_device_interface);
        bt_manager.close();
    }

    @Override
    public void onBackPressed() {
        //getApplicationContext();
        //super.onBackPressed();
    }

    public void decouverteBluetooth() {
        if (bt_adapter.isEnabled()) {
            if (bt_adapter.isDiscovering()) {
                bt_adapter.cancelDiscovery();
                intent_set_bluetooth.putExtra("set_bluetooth", "stopDecouverte");
            } else {
                bt_adapter.startDiscovery();
                bo_first_found = true;
                intent_set_bluetooth.putExtra("set_bluetooth", "decouverte");
            }
        } else {
            intent_set_bluetooth.putExtra("set_bluetooth", "btDesactive");
        }

            LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {

                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if (bo_first_found == true) {
                        intent_set_bluetooth.putExtra("set_bluetooth", "trouve");
                        bo_first_found = false;
                        intent_set_bluetooth.putExtra("set_device", device.getName() + " - " + device.getAddress());
                }
            }
            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                if(bt_adapter.isEnabled()) {
                    intent_set_bluetooth.putExtra("set_bluetooth", "finRecherche");
                }
                bo_first_found = false;
            }
            LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bReceiver);
    }
}
