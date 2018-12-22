package com.ip.jmc.btardroid;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    public final static String EXTRA_MESSAGE = "com.ip.jmc.MESSAGE";
    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    public static SimpleBluetoothDeviceInterface deviceInterface;
    public static String sentMsg = "";
    public static String receptMsg = "";
    BluetoothManager bluetoothManager = BluetoothManager.getInstance();
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Button bt1, bt2, bt3, bt4, bt5, bt6;
    ListView lv;
    ListView lv1;
    ImageView ivOn ;
    ImageView ivOff;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bluetooth bluetooth = new Bluetooth();
        bluetooth.init();
    }


    public void btTest(View v) {
        //deviceInterface.sendMessage("5");
        //msgSent = "5";
        Intent intent = new Intent(this, ArduinoDroid.class);
        //TextView editText = findViewById(R.id.editText);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

}