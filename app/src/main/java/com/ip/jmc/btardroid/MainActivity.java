package com.ip.jmc.btardroid;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.ip.jmc.MESSAGE";
    public final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    public static SimpleBluetoothDeviceInterface deviceInterface;
    public static String sentMsg = "";
    public static String receptMsg = "";
    BluetoothManager bluetoothManager = BluetoothManager.getInstance();
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Button bt1, bt2, bt3, bt4, bt5, bt6;
    public static ListView lvbt;
    public static ImageView ivOn;
    public static ImageView ivOff;
    public static ArrayList list = new ArrayList();
    static ArrayAdapter listeArrayAdapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvbt = (ListView) findViewById(R.id.listviewbt);
        ivOn = findViewById(R.id.imageViewBtOn);
        ivOff = findViewById(R.id.imageViewBtOff);
        listeArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        BluetoothCustom btCustom = new BluetoothCustom();
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