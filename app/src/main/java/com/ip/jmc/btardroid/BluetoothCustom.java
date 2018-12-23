package com.ip.jmc.btardroid;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BluetoothCustom extends MainActivity  {


    public void BluetoothCustomOnCreate()
    {
        //Test si le Bluetooth est supporté
        if (bluetoothManager == null) {
            Toast.makeText(mContextMainActivity, "Le Bluetooth n'est pas supporté", Toast.LENGTH_LONG).show(); // Replace context with your context instance.
            finish();
        }
        //Affichage de l'icône Bluetooth activé ou désactivé
        if (bluetoothAdapter.isEnabled()) {
            bouttonBluetoothConnect.setImageResource(R.drawable.bt_on);
            listDevicesBT();
        } else {
            bouttonBluetoothConnect.setImageResource(R.drawable.bt_off);
        }
    }
    public void btOnOff() {
        //Si le Bluetooth n'est pas activé on l'active
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(mContextMainActivity, "Activation du Bluetooth ...", Toast.LENGTH_LONG).show();
            bluetoothAdapter.enable();
            bouttonBluetoothConnect.setImageResource(R.drawable.bt_on);
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
        // ArduinoDroid.convertParams(message);
        //Toast.makeText(this, "Message envoyé -> " + strMessageEnvoye + "Message recu " + message, Toast.LENGTH_LONG).show(); // Replace context with your context instance.
        //(message);
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

    public void decouverteBluetooth()
    {
        Toast.makeText(mContextMainActivity,"Découverte des appareils Bluetooth",Toast.LENGTH_LONG).show();
        bluetoothAdapter.startDiscovery();
    }
}
