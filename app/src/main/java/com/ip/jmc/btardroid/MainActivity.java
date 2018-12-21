package com.ip.jmc.btardroid;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    Button bt1,bt2,bt3,bt4,bt5,bt6;
    ListView lv;
    ListView lv1;
    BluetoothManager bluetoothManager = BluetoothManager.getInstance();
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    String msgSent = "";
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt1=findViewById(R.id.bt1);
        bt2=findViewById(R.id.bt2);
        bt3=findViewById(R.id.bt3);
        bt4=findViewById(R.id.bt4);
        bt5=findViewById(R.id.bt5);
        bt6=findViewById(R.id.bt6);
        btOnOff();
        // Setup our BluetoothManager

        if (bluetoothManager == null) {
            // Bluetooth unavailable on this device :( tell the user
            Toast.makeText(this, "Bluetooth not available.", Toast.LENGTH_LONG).show(); // Replace context with your context instance.
            finish();
        }
        if(bluetoothAdapter.isEnabled()) {
            ImageView ivOn = (ImageView) findViewById(R.id.imageViewBtOn);
            ivOn.setVisibility(View.VISIBLE);
            ImageView ivOff = (ImageView) findViewById(R.id.imageViewBtOff);
            ivOff.setVisibility(View.INVISIBLE);
        }
        else
        {
            ImageView ivOn = (ImageView) findViewById(R.id.imageViewBtOn);
            ivOn.setVisibility(View.INVISIBLE);
            ImageView ivOff = (ImageView) findViewById(R.id.imageViewBtOff);
            ivOff.setVisibility(View.VISIBLE);
        }
    }
    public void btOnOff() {

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBlueTooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBlueTooth, REQUEST_CODE_ENABLE_BLUETOOTH);
            //Toast.makeText(this, "Activation du Bluetooth", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_main);
        }
        //else {
            //Toast.makeText(this, R.string.Connexion, Toast.LENGTH_LONG).show();
        //}

    }
    private SimpleBluetoothDeviceInterface deviceInterface;

    private void connectDevice(String mac) {
        bluetoothManager.openSerialDevice(mac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onConnected, this::onError);
    }

    private void onConnected(BluetoothSerialDevice connectedDevice) {
        // You are now connected to this device!
        // Here you may want to retain an instance to your device:
        deviceInterface = connectedDevice.toSimpleDeviceInterface();
        // Listen to bluetooth events
        deviceInterface.setListeners(this::onMessageReceived, this::onMessageSent, this::onError);

        setContentView(R.layout.activity_main);
    }

    private void onMessageSent(String message) {
        
    }

    private void onMessageReceived(String message) {
        // We received a message! Handle it here.
        if(message == msgSent){
            Toast.makeText(this, "Commande reçue " + message, Toast.LENGTH_LONG).show(); // Replace context with your context instance.
        }
        else {
            Toast.makeText(this, "Une erreur est survenue " + message, Toast.LENGTH_LONG).show(); // Replace context with your context instance.

        }
    }

    private void onError(Throwable error) {
        // Handle the error
        Toast.makeText(this, "Received a message! Message was: " + error, Toast.LENGTH_LONG).show();
    }
    public void listDevicesBT(View v){

    if(bluetoothAdapter.isEnabled()) {
        ArrayList list = new ArrayList();

        List<BluetoothDevice> pairedDevices = bluetoothManager.getPairedDevicesList();
        while (pairedDevices.isEmpty()) {
            pairedDevices = bluetoothManager.getPairedDevicesList();
        }
        if (!pairedDevices.isEmpty()) {
            setContentView(R.layout.activity_list_devices);
            for (BluetoothDevice device : pairedDevices) {
                list.add(device.getName() + " - " + device.getAddress());
            }
            final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
            lv = findViewById(R.id.lv1);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener((popup, lv1, position, id) -> {
                        String selLv = lv.getItemAtPosition(position).toString().trim();
                        String segments[] = selLv.split(" - ");
                        String macItem = segments[segments.length - 1];
                        connectDevice(macItem);
                    }

            );
            //onBackPressed();
        }
    }
    }

    public void deconnexion(View v) {
        bluetoothManager.closeDevice(deviceInterface); // Close by interface instance
        bluetoothManager.close();
    }
    public void bt2Click(View v) {
        deviceInterface.sendMessage("2");
        msgSent = "2";
    }
    public void bt3Click(View v) {
        deviceInterface.sendMessage("3");
        msgSent = "3";
    }
    public void bt4Click(View v) {
        deviceInterface.sendMessage("4");
        msgSent = "4";
    }
    public void bt5Click(View v) {
        deviceInterface.sendMessage("5");
        msgSent = "5";
    }
    public void bt6Click(View v) {
        deviceInterface.sendMessage("6");
        msgSent = "6";
    }

    @Override
    public void onBackPressed() {
        //Toast.makeText(this,"Thanks for using application!!",Toast.LENGTH_LONG).show();
        getApplicationContext();
        super.onBackPressed();
    }
}
