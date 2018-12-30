package com.ip.jmc.btardroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
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
    public static Context con_bluetooth_custom;

    public static Context getContext() {
        return con_bluetooth_custom;
    }

    public static boolean bo_first_found;

    private static final UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a22");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void BluetoothCustomOnCreate() {
        tv_btn_rafraichir.setTextColor(Color.rgb(104, 149, 197));
        con_bluetooth_custom = getBaseContext();
        tv_btn_recherche.setTextColor(Color.rgb(104, 149, 197));
        aa_bt_decouverte = new ArrayAdapter(con_main_activity, android.R.layout.simple_list_item_1, al_bt_devices_discovered);
        aa_bt_paired = new ArrayAdapter(con_main_activity, android.R.layout.simple_list_item_1, al_bt_devices);
        //Test si le Bluetooth est supporté
        if (bt_manager == null) {
            Toast.makeText(con_main_activity, "Le Bluetooth n'est pas supporté", Toast.LENGTH_LONG).show(); // Replace context with your context instance.
            finish();
        }
        //Affichage de l'icône Bluetooth activé ou désactivé
        if (bt_adapter.isEnabled()) {
            btn_bt_connect.setImageResource(R.drawable.bt_on_2);
            tv_btn_bt.setTextColor(Color.rgb(104, 149, 197));
            tv_btn_bt.setText("Désactiver");
            listDevicesBT();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            con_main_activity.registerReceiver(bReceiver, filter);
        } else {
            btn_bt_connect.setImageResource(R.drawable.bt_off);
            tv_btn_bt.setTextColor(Color.rgb(200, 0, 0));
            tv_btn_bt.setText("Activer");
        }
    }

    public void testBluetooth() {
        if (!bt_adapter.isEnabled()) {
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
        }
    }

    public void btOnOff() {
        //Si le Bluetooth n'est pas activé on l'active
        if (!bt_adapter.isEnabled()) {
            tv_bluetooth.setTextColor(Color.rgb(0, 200, 0));
            tv_bluetooth.setText("Activation du Bluetooth ...");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    tv_bluetooth.setTextColor(Color.rgb(124, 124, 124));
                    tv_bluetooth.setText("L'équipe XXIITEAM vous souhaite la bienvenue sur l'application BTArdroid");
                }
            }, 3000);
            bt_adapter.enable();
            btn_bt_connect.setImageResource(R.drawable.bt_on_2);
            tv_btn_bt.setTextColor(Color.rgb(104, 149, 197));
            tv_btn_bt.setText("Désactiver");
            listDevicesBT();
        }
        //Sinon on le désactive et on modifie l'icône
        else {
            tv_discovered.setVisibility(TextView.INVISIBLE);
            tv_appaires.setVisibility(TextView.INVISIBLE);
            tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
            tv_bluetooth.setText("Déconnexion du Bluetooth ...");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    tv_bluetooth.setTextColor(Color.rgb(124, 124, 124));
                    tv_bluetooth.setText("L'équipe XXIITEAM vous souhaite la bienvenue sur l'application BTArdroid");
                }
            }, 2000);
            bt_adapter.disable();
            btn_bt_connect.setImageResource(R.drawable.bt_off);
            tv_btn_bt.setTextColor(Color.rgb(200, 0, 0));
            tv_btn_bt.setText("Activer");
            aa_bt_paired.clear();
            aa_bt_decouverte.clear();
            al_bt_devices.clear();
            al_bt_devices_discovered.clear();
        }
    }

    public boolean createBond(BluetoothDevice btDevice)
            throws Exception {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    private void connectDevice(BluetoothDevice device) {
        try {
            try {
                createBond(device);
            } catch (Exception e) {
                tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                tv_bluetooth.setText("Impossible d'appairer le périphérique ...");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        tv_bluetooth.setTextColor(Color.rgb(124, 124, 124));
                        tv_bluetooth.setText("L'équipe XXIITEAM vous souhaite la bienvenue sur l'application BTArdroid");
                    }
                }, 2000);
            }
            /*if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                Toast.makeText(con_main_activity, "Appairé", Toast.LENGTH_LONG).show();
            }*/
            device.createRfcommSocketToServiceRecord(MY_UUID);
            tv_bluetooth.setTextColor(Color.rgb(0, 200, 0));
            tv_bluetooth.setText(device.getName() + " est connecté");
            aa_bt_decouverte.clear();
            al_bt_devices_discovered.clear();
            tv_discovered.setVisibility(TextView.INVISIBLE);
            bt_manager.openSerialDevice(device.getAddress())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onConnected, this::onError);
        } catch (IOException e) {
            tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
            tv_bluetooth.setText("Erreur : " + e.toString());
        }
    }

    public void onConnected(BluetoothSerialDevice connectedDevice) {
        bo_serial_test = true;
        // You are now connected to this device!
        // Here you may want to retain an instance to your device:
        sbt_device_interface = connectedDevice.toSimpleDeviceInterface();
        // Listen to bluetooth events
        sbt_device_interface.setListeners(this::onMessageReceived, this::onMessageSent, this::onError);
        //Intent myIntent = new Intent(con_main_activity, ArduinoDroid.class);
        //con_main_activity.startActivity(myIntent);

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
        // Handle the error
        //Toast.makeText(con_main_activity, "Erreur : " + error, Toast.LENGTH_LONG).show();
    }

    public void listDevicesBT() {
        try {
            Thread.sleep(1800);
        } catch (InterruptedException ex) {
            android.util.Log.d("BTArdroid Erreur", ex.toString());
        }
        List<BluetoothDevice> pairedDevices = bt_manager.getPairedDevicesList();
        aa_bt_paired.clear();
        al_bt_devices.clear();
        while (pairedDevices.isEmpty()) {
            pairedDevices = bt_manager.getPairedDevicesList();
            break;
        }
        if (!pairedDevices.isEmpty()) {
            tv_appaires.setTextColor(Color.rgb(104, 149, 197));
            tv_appaires.setText("Liste des périphériques appairés :");
            tv_appaires.setVisibility(TextView.VISIBLE);

            for (BluetoothDevice device : pairedDevices) {
                if (!al_bt_devices.contains(device.getName() + " - " + device.getAddress())) {
                    al_bt_devices.add(device.getName() + " - " + device.getAddress());
                    //al_bt_devices.add(device);
                    lv_bt_devices.setAdapter(aa_bt_paired);
                    lv_bt_devices.setVisibility(TextView.VISIBLE);
                    lv_bt_devices.setOnItemClickListener((popup, lv1, position, id) -> {
                                String selLv = lv_bt_devices.getItemAtPosition(position).toString().trim();
                                String segments[] = selLv.split(" - ");
                                String macItem = segments[segments.length - 1];
                                //Toast.makeText(con_main_activity, "Tentative de connexion avec l'appareil ...", Toast.LENGTH_LONG).show();
                                BluetoothDevice mBluetoothDevice = bt_adapter.getRemoteDevice(macItem);
                                connectDevice(mBluetoothDevice);
                            }
                    );
                }
            }

            aa_bt_paired.notifyDataSetChanged();
            tv_bluetooth.setTextColor(Color.rgb(124, 124, 124));
            tv_bluetooth.setText("L'équipe XXIITEAM vous souhaite la bienvenue sur l'application BTArdroid");
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    tv_appaires.setTextColor(Color.rgb(200, 0, 0));
                    //tv_bluetooth.setText("Aucun périphérique Bluetooth appairé ...");
                    tv_bluetooth.setTextColor(Color.rgb(124, 124, 124));
                    tv_bluetooth.setText("L'équipe XXIITEAM vous souhaite la bienvenue sur l'application BTArdroid");
                    tv_appaires.setVisibility(TextView.VISIBLE);
                    lv_bt_devices.setVisibility(TextView.INVISIBLE);
                    tv_appaires.setText("Aucun périphérique Bluetooth appairé");
                }
            }, 4000);

        }
    }

    public void listDevicesBTThread() {
        try {
            Thread.sleep(1800);
        } catch (InterruptedException ex) {
            android.util.Log.d("BTArdroid Erreur", ex.toString());
        }
        tv_appaires.setTextColor(Color.rgb(104, 149, 197));
        tv_appaires.setText("Liste des périphériques appairés :");
        tv_appaires.setVisibility(TextView.VISIBLE);
        List<BluetoothDevice> pairedDevices = bt_manager.getPairedDevicesList();
        aa_bt_paired.clear();
        al_bt_devices.clear();
        while (pairedDevices.isEmpty()) {
            pairedDevices = bt_manager.getPairedDevicesList();
            break;
        }
        if (!pairedDevices.isEmpty()) {
            for (BluetoothDevice device : pairedDevices) {
                if (!al_bt_devices.contains(device.getName() + " - " + device.getAddress())) {
                    al_bt_devices.add(device.getName() + " - " + device.getAddress());
                    lv_bt_devices.setAdapter(aa_bt_paired);
                    lv_bt_devices.setVisibility(TextView.VISIBLE);
                    lv_bt_devices.setOnItemClickListener((popup, lv1, position, id) -> {
                                String selLv = lv_bt_devices.getItemAtPosition(position).toString().trim();
                                String segments[] = selLv.split(" - ");
                                String macItem = segments[segments.length - 1];
                                //Toast.makeText(con_main_activity, "Tentative de connexion avec l'appareil ...", Toast.LENGTH_LONG).show();
                                BluetoothDevice mBluetoothDevice = bt_adapter.getRemoteDevice(macItem);
                                connectDevice(mBluetoothDevice);
                            }
                    );
                }
            }

            aa_bt_paired.notifyDataSetChanged();
        } else {
            tv_appaires.setTextColor(Color.rgb(200, 0, 0));
            tv_appaires.setVisibility(TextView.VISIBLE);
            lv_bt_devices.setVisibility(TextView.INVISIBLE);
            tv_appaires.setText("Aucun périphérique Bluetooth appairé");
        }
    }


    public void deconnexion(View v) {
        bt_manager.closeDevice(sbt_device_interface); // Close by interface instance
        bt_manager.close();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(con_main_activity, "Merci d'avoir utilisé l'application!!", Toast.LENGTH_LONG).show();
        getApplicationContext();
        super.onBackPressed();
    }

    public void decouverteBluetooth() {
        if (bt_adapter.isEnabled()) {
            if (bt_adapter.isDiscovering()) {
                // the button is pressed when it discovers, so cancel the discovery
                bt_adapter.cancelDiscovery();
                btn_bt_recherche.setImageResource(R.drawable.loupe_1);
                tv_btn_recherche.setTextColor(Color.rgb(104, 149, 197));
                tv_btn_recherche.setText("Rechercher");
                tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                tv_bluetooth.setText("Fin de la recherche ...");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        tv_bluetooth.setTextColor(Color.rgb(124, 124, 124));
                        tv_bluetooth.setText("L'équipe XXIITEAM vous souhaite la bienvenue sur l'application BTArdroid");
                    }
                }, 2000);
            } else {
                btn_bt_recherche.setImageResource(R.drawable.loupe_2);
                al_bt_devices_discovered.clear();
                aa_bt_decouverte.clear();
                bt_adapter.startDiscovery();
                tv_btn_recherche.setTextColor(Color.rgb(255, 127, 80));
                tv_bluetooth.setTextColor(Color.rgb(0, 200, 0));
                tv_bluetooth.setText("Recherche en cours ...");
                tv_btn_recherche.setText("Arrêter");
                bo_first_found = true;
            }
        } else {
            tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
            tv_bluetooth.setText("Veuiller activer le Bluetooth en cliquant sur l'icône ...");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    tv_bluetooth.setTextColor(Color.rgb(124, 124, 124));
                    tv_bluetooth.setText("L'équipe XXIITEAM vous souhaite la bienvenue sur l'application BTArdroid");
                }
            }, 3000);
        }
    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {

                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!al_bt_devices_discovered.contains(device.getName() + " - " + device.getAddress()) && !al_bt_devices.contains(device.getName() + " - " + device.getAddress())) {
                    if (bo_first_found == true) {
                        tv_discovered.setVisibility(TextView.VISIBLE);
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
                bo_first_found = false;
                tv_bluetooth.setText("");
                tv_discovered.setVisibility(TextView.VISIBLE);
                btn_bt_recherche.setImageResource(R.drawable.loupe_1);
                tv_btn_recherche.setTextColor(Color.rgb(104, 149, 197));
                tv_btn_recherche.setText("Rechercher");
                tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                tv_bluetooth.setText("Fin de la recherche ...");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        tv_bluetooth.setTextColor(Color.rgb(124, 124, 124));
                        tv_bluetooth.setText("L'équipe XXIITEAM vous souhaite la bienvenue sur l'application BTArdroid");
                    }
                }, 2000);
                if (al_bt_devices_discovered.isEmpty()) {
                    tv_discovered.setVisibility(TextView.INVISIBLE);
                    tv_bluetooth.setTextColor(Color.rgb(200, 0, 0));
                    //tv_bluetooth.setText("Aucun périphérique Bluetooth à proximité ...");
                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            tv_bluetooth.setTextColor(Color.rgb(124, 124, 124));
                            tv_bluetooth.setText("L'équipe XXIITEAM vous souhaite la bienvenue sur l'application BTArdroid");
                        }
                    }, 1500);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bReceiver);
    }
}
