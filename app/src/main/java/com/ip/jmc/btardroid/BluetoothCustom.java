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

import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BluetoothCustom extends MainActivity {
    ArduinoDroid ard;
    public static BluetoothDevice deviceConnected;
    public static boolean bo_first_found;
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private Intent intent_set_bluetooth = new Intent("get-param");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void BluetoothCustomOnCreate() {
        if (bt_manager == null) {
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
    }

    public void on() {
        if (!bt_adapter.isEnabled()) {
            bt_adapter.enable();
        }
    }

    public void off()
    {
        if(bt_adapter.isEnabled())
        bt_adapter.disable();
    }

    public boolean createBond(BluetoothDevice btDevice)
            throws Exception {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    private void connectDevice(BluetoothDevice device) {
        bt_adapter.cancelDiscovery();
        if(!al_bt_devices.contains(device.getName() + " - " + device.getAddress()))
        {
            try {
                createBond(device);
                al_bt_devices_discovered.remove(device.getName() + " - " + device.getAddress());
            } catch (Exception e) {
                intent_set_bluetooth.putExtra("set_bluetooth", "connection");
                intent_set_bluetooth.putExtra("set_device", device.getName());
                LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
            }
        }
        else
        {
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
    }

    public void onConnected(BluetoothSerialDevice connectedDevice) {
        intent_set_bluetooth.putExtra("set_bluetooth", "connecte");
        intent_set_bluetooth.putExtra("set_device", deviceConnected.getName());
        LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
        bo_serial_test = true;
        sbt_device_interface = connectedDevice.toSimpleDeviceInterface();
        sbt_device_interface.setListeners(this::onMessageReceived, this::onMessageSent, this::onError);
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

    public void listDevicesBT() {
        intent_set_bluetooth.putExtra("set_bluetooth", "appaire");
        List<BluetoothDevice> pairedDevices = bt_manager.getPairedDevicesList();
        while (pairedDevices.isEmpty()) {
            pairedDevices = bt_manager.getPairedDevicesList();
            break;
        }
        if (!pairedDevices.isEmpty()) {
            for (BluetoothDevice device : pairedDevices)
            {
                if (!al_bt_devices.contains(device.getName() + " - " + device.getAddress())) {
                    al_bt_devices.add(device.getName() + " - " + device.getAddress());
                    lv_bt_devices.setAdapter(aa_bt_paired);
                    lv_bt_devices.setOnItemClickListener((popup, lv1, position, id) -> {
                                String selLv = lv_bt_devices.getItemAtPosition(position).toString().trim();
                                String segments[] = selLv.split(" - ");
                                String macItem = segments[segments.length - 1];
                                BluetoothDevice mBluetoothDevice = bt_adapter.getRemoteDevice(macItem);
                                connectDevice(mBluetoothDevice);
                            }
                        );
                    }
                intent_set_bluetooth.putExtra("set_bluetooth", "appaire");
            }
        } else {
            intent_set_bluetooth.putExtra("set_bluetooth", "nonappaire");
        }
        LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
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
            } else {
                bt_adapter.startDiscovery();
                bo_first_found = true;
            }
        }
    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {

                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!al_bt_devices_discovered.contains(device.getName() + " - " + device.getAddress()) && !al_bt_devices.contains(device.getName() + " - " + device.getAddress())) {
                    if (bo_first_found == true) {
                        intent_set_bluetooth.putExtra("set_bluetooth", "trouve");
                        LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
                        bo_first_found = false;
                    }
                    al_bt_devices_discovered.add(device.getName() + " - " + device.getAddress());
                    lv_bt_discover.setAdapter(aa_bt_decouverte);
                    lv_bt_discover.setOnItemClickListener((popup, lv1, position, id) -> {
                                String selLv = lv_bt_discover.getItemAtPosition(position).toString().trim();
                                String segments[] = selLv.split(" - ");
                                String macItem = segments[segments.length - 1];
                                //Toast.makeText(con_main_activity, "Tentative de connexion avec l'appareil ...", Toast.LENGTH_LONG).show();
                                BluetoothDevice mBluetoothDevice = bt_adapter.getRemoteDevice(macItem);
                                connectDevice(mBluetoothDevice);
                            }
                    );
                    aa_bt_decouverte.notifyDataSetChanged();
                }
            }
            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                intent_set_bluetooth.putExtra("set_bluetooth", "finRecherche");
                LocalBroadcastManager.getInstance(con_main_activity).sendBroadcast(intent_set_bluetooth);
                bo_first_found = false;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bReceiver);
    }
}
