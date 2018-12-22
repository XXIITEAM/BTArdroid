package com.ip.jmc.btardroid;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BluetoothCustom extends MainActivity  {


    //Context ctx = getApplicationContext();

    public BluetoothCustom()
    {

        //Test si le Bluetooth est supporté
        if (bluetoothManager == null) {
            Toast.makeText(this, "Le Bluetooth n'est pas supporté", Toast.LENGTH_LONG).show(); // Replace context with your context instance.
            finish();
        }
        //Affichage de l'icône Bluetooth activé ou désactivé

        if (bluetoothAdapter.isEnabled()) {
            ivOn.setVisibility(View.VISIBLE);
            ivOff.setVisibility(View.INVISIBLE);
            listDevicesBT();
        } else {
            ivOn.setVisibility(View.INVISIBLE);
            ivOff.setVisibility(View.VISIBLE);
        }
        //En appuyant sur l'icône : activation ou désactivation du Bluetooth
        ivOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                btOnOff();
            }
        });
        ivOn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                btOnOff();
            }
        });
    }
    public void btOnOff() {
        //Si le Bluetooth n'est pas activé on demande à l'utilisateur de l'activer
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
            //Intent enableBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(enableBlueTooth, REQUEST_CODE_ENABLE_BLUETOOTH);
            ivOn.setVisibility(View.VISIBLE);
            ivOff.setVisibility(View.INVISIBLE);
            listDevicesBT();
        }
        //Sinon on le désactive et on modifie l'icône et on cache la liste des appareils
        else {
            bluetoothAdapter.disable();
            //Toast.makeText(this, "Déconnexion du Bluetooth ...", Toast.LENGTH_LONG).show();
            ivOn.setVisibility(View.INVISIBLE);
            ivOff.setVisibility(View.VISIBLE);
            lvbt.setVisibility(View.INVISIBLE);
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
        Intent intent = new Intent(this, ArduinoDroid.class);
        startActivity(intent);
    }

    public void onMessageSent(String message) {
        //Toast.makeText(this, "Envoi de : " + message, Toast.LENGTH_LONG).show(); // Replace context with your context instance.
        sentMsg = message;
    }

    public void onMessageReceived(String message) {
        receptMsg = message;
        // ArduinoDroid.msgToList(message);
        //Toast.makeText(this, "Message envoyé -> " + sentMsg + "Message recu " + message, Toast.LENGTH_LONG).show(); // Replace context with your context instance.
        //(message);
    }


    public void onError(Throwable error) {
        // Handle the error
        Toast.makeText(this, "Erreur : " + error, Toast.LENGTH_LONG).show();
    }

    public void listDevicesBT() {
    listeArrayAdapter.clear();
        List<BluetoothDevice> pairedDevices = bluetoothManager.getPairedDevicesList();
        while (pairedDevices.isEmpty()) {
            pairedDevices = bluetoothManager.getPairedDevicesList();
        }
        if (!pairedDevices.isEmpty()) {
            lvbt.setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                list.add(device.getName() + " - " + device.getAddress());
            }
            lvbt.setAdapter(listeArrayAdapter);
            lvbt.setOnItemClickListener((popup, lv1, position, id) -> {
                        String selLv = lvbt.getItemAtPosition(position).toString().trim();
                        String segments[] = selLv.split(" - ");
                        String macItem = segments[segments.length - 1];
                        connectDevice(macItem);
                    }
            );
            listeArrayAdapter.notifyDataSetChanged();
        } else {
            lvbt.setVisibility(View.INVISIBLE);
        }
    }

    public void deconnexion(View v) {
        bluetoothManager.closeDevice(deviceInterface); // Close by interface instance
        bluetoothManager.close();
    }



    @Override
    public void onBackPressed() {
        //Toast.makeText(this,"Thanks for using application!!",Toast.LENGTH_LONG).show();
        getApplicationContext();
        super.onBackPressed();
    }


    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check that it is the SecondActivity with an OK result
        if (requestCode == REQUEST_CODE_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) { // Activity.RESULT_OK
                ivOn.setVisibility(View.VISIBLE);
                ivOff.setVisibility(View.INVISIBLE);
                listDevicesBT();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Abandon par l'utilisateur ...", Toast.LENGTH_LONG).show();
            }
        }
    }*/
}
