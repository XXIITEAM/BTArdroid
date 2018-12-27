package com.ip.jmc.btardroid;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BluetoothCustom extends MainActivity  {
    ArduinoDroid ard;
    public static Context mContextBluetoothCustom;
    public static Context getContext() {
        return mContextBluetoothCustom;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    public void BluetoothCustomOnCreate()
    {
        mContextBluetoothCustom = getBaseContext();
        //Test si le Bluetooth est supporté
        if (bluetoothManager == null) {
            Toast.makeText(mContextMainActivity, "Le Bluetooth n'est pas supporté", Toast.LENGTH_LONG).show(); // Replace context with your context instance.
            finish();
        }
        //Affichage de l'icône Bluetooth activé ou désactivé
        if (bluetoothAdapter.isEnabled()) {
            bouttonBluetoothConnect.setImageResource(R.drawable.bt_on_2);
            listDevicesBT();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            mContextMainActivity.registerReceiver(bReceiver, filter);
        } else {
            bouttonBluetoothConnect.setImageResource(R.drawable.bt_off);
        }
    }
    public void btOnOff() {
        //Si le Bluetooth n'est pas activé on l'active
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(mContextMainActivity, "Activation du Bluetooth ...", Toast.LENGTH_LONG).show();
            bluetoothAdapter.enable();
            bouttonBluetoothConnect.setImageResource(R.drawable.bt_on_2);
            listDevicesBT();
        }
        //Sinon on le désactive et on modifie l'icône
        else {
            Toast.makeText(mContextMainActivity, "Déconnexion du Bluetooth ...", Toast.LENGTH_LONG).show();
            bluetoothAdapter.disable();
            bouttonBluetoothConnect.setImageResource(R.drawable.bt_off);
            listeArrayAdapter.clear();
        }
    }

    private void connectDevice(String mac) {
        bluetoothManager.openSerialDevice(mac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onConnected, this::onError);
    }

    public void onConnected(BluetoothSerialDevice connectedDevice) {
        // You are now connected to this device!
        // Here you may want to retain an instance to your device:
        deviceInterface = connectedDevice.toSimpleDeviceInterface();
        // Listen to bluetooth events
        deviceInterface.setListeners(this::onMessageReceived, this::onMessageSent, this::onError);
        Intent myIntent = new Intent(mContextMainActivity, ArduinoDroid.class);
        mContextMainActivity.startActivity(myIntent);
    }

    public void onMessageSent(String message) {
        //Toast.makeText(this, "Envoi de : " + message, Toast.LENGTH_LONG).show(); // Replace context with your context instance.
        strMessageEnvoye = message;
    }

    public void onMessageReceived(String message) {
        strMessageRecu = message;
        new ArduinoDroid().convertParams(strMessageEnvoye, strMessageRecu);
    }


    public void onError(Throwable error) {
        // Handle the error
        Toast.makeText(this, "Erreur : " + error, Toast.LENGTH_LONG).show();
    }

    public void listDevicesBT() {
        try { Thread.sleep(1800); }
        catch (InterruptedException ex) { android.util.Log.d("BTArdroid Erreur", ex.toString()); }
        List<BluetoothDevice> pairedDevices = bluetoothManager.getPairedDevicesList();
        listeArrayAdapter = new ArrayAdapter(mContextMainActivity, android.R.layout.simple_list_item_1, listBluetoothDevices);
        while (pairedDevices.isEmpty()) {
            pairedDevices = bluetoothManager.getPairedDevicesList();
            break;
        }
        if (!pairedDevices.isEmpty()) {
            for (BluetoothDevice device : pairedDevices) {
                listBluetoothDevices.add(device.getName() + " - " + device.getAddress());
            }

            listViewBluetoothDevices.setAdapter(listeArrayAdapter);
            listViewBluetoothDevices.setOnItemClickListener((popup, lv1, position, id) -> {
                        String selLv = listViewBluetoothDevices.getItemAtPosition(position).toString().trim();
                        String segments[] = selLv.split(" - ");
                        String macItem = segments[segments.length - 1];
                        Toast.makeText(mContextMainActivity, "Tentative de connexion avec l'appareil ...", Toast.LENGTH_LONG).show();
                        connectDevice(macItem);
                    }
            );
            //listeArrayAdapter.notifyDataSetChanged();
        }
    }

    public void deconnexion(View v) {
        bluetoothManager.closeDevice(deviceInterface); // Close by interface instance
        bluetoothManager.close();
    }



    @Override
    public void onBackPressed() {
        Toast.makeText(mContextMainActivity,"Merci d'avoir utilisé l'application!!",Toast.LENGTH_LONG).show();
        getApplicationContext();
        super.onBackPressed();
    }

    public void decouverteBluetooth() {
        if (bluetoothAdapter.isDiscovering()) {
            // the button is pressed when it discovers, so cancel the discovery
            bluetoothAdapter.cancelDiscovery();
            bouttonBluetoothRecherche.setImageResource(R.drawable.loupe_1);
            Toast.makeText(mContextMainActivity, "Fin de la recherche ...",
                    Toast.LENGTH_LONG).show();
        } else {
            bouttonBluetoothRecherche.setImageResource(R.drawable.loupe_2);
            listBluetoothDevices.clear();
            listeArrayAdapter.notifyDataSetChanged();

            Toast.makeText(mContextMainActivity, "Recherche de nouveaux périphériques",
                    Toast.LENGTH_LONG).show();
            bluetoothAdapter.startDiscovery();
            textViewBluetooth.setText("Recherche en cours ...");
            //mContextMainActivity.registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
    }
    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            //if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            if(action.equals(BluetoothDevice.ACTION_FOUND)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the arrayAdapter
                if(device != null) {
                    listBluetoothDevices.add(device.getName() + " - " + device.getAddress());
                    listeArrayAdapter.notifyDataSetChanged();
                }
            }
            if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                textViewBluetooth.setText("");
                bouttonBluetoothRecherche.setImageResource(R.drawable.loupe_1);
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bReceiver);
    }
}
