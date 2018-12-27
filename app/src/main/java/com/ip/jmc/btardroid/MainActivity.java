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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public static Context mContextMainActivity;
    public final static String EXTRA_MESSAGE = "com.ip.jmc.MESSAGE";
    public final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    public static SimpleBluetoothDeviceInterface deviceInterface;
    public static String strMessageEnvoye = "";
    public static String strMessageRecu = "";
    public static BluetoothManager bluetoothManager = BluetoothManager.getInstance();
    public static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public static Button bt1, bt2, bt3, bt4, bt5, bt6;
    public static ListView listViewBluetoothDevices;
    public static ListView listViewParams;
    public static ArrayList listBluetoothDevices = new ArrayList();
    static ArrayAdapter listeArrayAdapter;
    static ArrayAdapter adapterParams;
    static ImageButton bouttonBluetoothConnect;
    static ImageButton bouttonBluetoothRecherche;
    static TextView textViewBluetooth;

    public static void btnBTOn(View v) {
        new BluetoothCustom().btOnOff();
    }
    public void BtnRecherche(View v) {
        new BluetoothCustom().decouverteBluetooth();
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
        //listViewParams = findViewById(R.id.listViewParams);
        bouttonBluetoothConnect = findViewById(R.id.BtnBT);
        bouttonBluetoothRecherche = findViewById(R.id.BtnRecherche);
        textViewBluetooth = findViewById(R.id.textViewBT);
        new BluetoothCustom().BluetoothCustomOnCreate();
        int MY_PERMISSIONS_REQUEST = 200;
        int permissions=ContextCompat.checkSelfPermission (this,Manifest.permission.ACCESS_FINE_LOCATION);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST);
        permissions=ContextCompat.checkSelfPermission (this,Manifest.permission.BLUETOOTH);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.BLUETOOTH},
                MY_PERMISSIONS_REQUEST);
        permissions=ContextCompat.checkSelfPermission (this,Manifest.permission.BLUETOOTH_ADMIN);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.BLUETOOTH_ADMIN},
                MY_PERMISSIONS_REQUEST);
    }

}