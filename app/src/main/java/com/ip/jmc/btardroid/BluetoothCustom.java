package com.ip.jmc.btardroid;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.ip.jmc.btardroid.R.id.imageViewBtOff;
import static com.ip.jmc.btardroid.R.id.imageViewBtOn;

public class BluetoothCustom extends MainActivity  {
    public final static String EXTRA_MESSAGE = "com.ip.jmc.MESSAGE";
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    public static SimpleBluetoothDeviceInterface deviceInterface;
    public static String sentMsg = "";
    public static String receptMsg = "";


    public void main()
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
            Intent enableBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBlueTooth, REQUEST_CODE_ENABLE_BLUETOOTH);
        }
        //Sinon on le désactive et on modifie l'icône et on cache la liste des appareils
        else {
            bluetoothAdapter.disable();
            Toast.makeText(this, "Déconnexion du Bluetooth ...", Toast.LENGTH_LONG).show();
            ivOn = findViewById(imageViewBtOn);
            ivOff = findViewById(imageViewBtOff);
            ivOn.setVisibility(View.INVISIBLE);
            ivOff.setVisibility(View.VISIBLE);
            lv = findViewById(R.id.lv1);
            lv.setVisibility(View.INVISIBLE);
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
        //setContentView(R.layout.activity_main);
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
        ArrayList list = new ArrayList();
        List<BluetoothDevice> pairedDevices = bluetoothManager.getPairedDevicesList();
        while (pairedDevices.isEmpty()) {
            pairedDevices = bluetoothManager.getPairedDevicesList();
        }
        if (!pairedDevices.isEmpty()) {
            lv = findViewById(R.id.lv1);
            lv.setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                list.add(device.getName() + " - " + device.getAddress());
            }
            final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener((popup, lv1, position, id) -> {
                        String selLv = lv.getItemAtPosition(position).toString().trim();
                        String segments[] = selLv.split(" - ");
                        String macItem = segments[segments.length - 1];
                        connectDevice(macItem);
                    }
            );
        } else {
            lv = findViewById(R.id.lv1);
            lv.setVisibility(View.INVISIBLE);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check that it is the SecondActivity with an OK result
        if (requestCode == REQUEST_CODE_ENABLE_BLUETOOTH) {
            if (resultCode == RESULT_OK) { // Activity.RESULT_OK
                ivOn = findViewById(imageViewBtOn);
                ivOff = findViewById(imageViewBtOff);
                ivOn.setVisibility(View.VISIBLE);
                ivOff.setVisibility(View.INVISIBLE);
                listDevicesBT();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Abandon par l'utilisateur ...", Toast.LENGTH_LONG).show();
            }
        }
    }
}
