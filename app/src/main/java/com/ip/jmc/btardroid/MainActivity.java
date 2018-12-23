package com.ip.jmc.btardroid;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    public static Context mContextMainActivity;
    public final static String EXTRA_MESSAGE = "com.ip.jmc.MESSAGE";
    public final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    public static SimpleBluetoothDeviceInterface deviceInterface;
    public static String strMessageEnvoye = "";
    public static String strMessageRecu = "";
    BluetoothManager bluetoothManager = BluetoothManager.getInstance();
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Button bt1, bt2, bt3, bt4, bt5, bt6;
    public static ListView listViewBluetoothDevices;
    public static ArrayList listBluetoothDevices = new ArrayList();
    static ArrayAdapter listeArrayAdapter;
    static ImageButton bouttonBluetoothConnect;

    public static void btnBTOn(View v) {
        new BluetoothCustom().btOnOff();
    }

    public static Context getContext() {
        return mContextMainActivity;
    }

    public void btnSuivant(View v) {
        Intent intent = new Intent(mContextMainActivity, ArduinoDroid.class);
        startActivity(intent);

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContextMainActivity = getBaseContext();
        listViewBluetoothDevices = findViewById(R.id.listviewbt);
        bouttonBluetoothConnect = findViewById(R.id.BtnBT);
        BluetoothCustom btCustom = new BluetoothCustom();
    }

}