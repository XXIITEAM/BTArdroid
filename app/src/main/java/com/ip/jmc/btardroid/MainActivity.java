package com.ip.jmc.btardroid;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;


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
    public static ListView listViewbtdiscover;
    public static ListView listViewParams;
    public static ArrayList listBluetoothDevices = new ArrayList();
    public static ArrayList listBluetoothDevicesDiscovered = new ArrayList();
    static ArrayAdapter listeArrayAdapter;
    static ArrayAdapter listeArrayAdapterBTDecouverte;
    static ArrayAdapter adapterParams;
    static ImageButton bouttonBluetoothConnect;
    static ImageButton bouttonBluetoothRecherche;
    static TextView textViewBluetooth;
    static TextView textViewBtnRecherche;
    static TextView textViewBtnBT;
    static TextView textViewDiscovered;
    static TextView textViewAppaires;
    static TextView textViewBtnRafraichir;
    static TextView textViewBtnQuitter;
    static TextView textViewBtnVoiture;
    public static boolean serialTest = false;
    public static void btnBTOn(View v) {
        new BluetoothCustom().btOnOff();
    }
    public void BtnRecherche(View v) {
        new BluetoothCustom().decouverteBluetooth();
    }
    public void BtnRafraichir(View v) {
        if(bluetoothAdapter.isEnabled())
        {
            textViewBluetooth.setTextColor(Color.rgb(0,200,0));
            textViewBluetooth.setText("Mise à jour de la liste des périphériques appairés ...");
            textViewAppaires.setText("");
            new BluetoothCustom().listDevicesBT();

        }
        else
        {
            textViewBluetooth.setTextColor(Color.rgb(200,0,0));
            textViewBluetooth.setText("Veuiller activer le Bluetooth en cliquant sur l'icône ...");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    textViewBluetooth.setTextColor(Color.rgb(124,124,124));
                    textViewBluetooth.setText("L'équipe XXIITEAM vous souhaite la bienvenue sur l'application BTArdroid");
                }
            }, 3000);
        }

    }
    public void BtnVoiture(View v) {
        if(bluetoothAdapter.isEnabled())
        {
            if (serialTest == true) {
                Intent myIntent = new Intent(mContextMainActivity, ArduinoDroid.class);
                startActivity(myIntent);
            } else {
                textViewBluetooth.setTextColor(Color.rgb(200, 0, 0));
                textViewBluetooth.setText("Vous n'êtes pas connecté à une voiture arduino ...");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        textViewBluetooth.setTextColor(Color.rgb(124, 124, 124));
                        textViewBluetooth.setText("L'équipe XXIITEAM vous souhaite la bienvenue sur l'application BTArdroid");
                    }
                }, 3000);
            }
        }
        else
        {
            textViewBluetooth.setTextColor(Color.rgb(200, 0, 0));
            textViewBluetooth.setText("Le Bluetooth doit être activé pour utiliser cette application ...");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    textViewBluetooth.setTextColor(Color.rgb(124, 124, 124));
                    textViewBluetooth.setText("L'équipe XXIITEAM vous souhaite la bienvenue sur l'application BTArdroid");
                }
            }, 3000);
        }
    }
    public void BtnQuitter(View v) {
        System.exit(0);
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
        initInterface();
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
       AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                    while(true) {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(bluetoothAdapter.isEnabled()) {
                                            new BluetoothCustom().listDevicesBTThread();
                                        }
                                        else
                                        {
                                            new BluetoothCustom().testBluetooth();
                                        }
                                    }
                                });
                    }
            }
       });
    }

    private void initInterface()
    {
        listViewBluetoothDevices = findViewById(R.id.listviewbt);
        listViewbtdiscover = findViewById(R.id.listviewbtdiscover);
        bouttonBluetoothConnect = findViewById(R.id.BtnBT);
        bouttonBluetoothRecherche = findViewById(R.id.BtnRecherche);
        textViewBluetooth = findViewById(R.id.textViewBT);
        textViewBtnRecherche = findViewById(R.id.textViewBtnRecherche);
        textViewBtnBT = findViewById(R.id.textViewBtnBt);
        textViewAppaires = findViewById(R.id.textViewAppaires);
        textViewDiscovered = findViewById(R.id.textViewDiscovered);
        textViewBtnRafraichir = findViewById(R.id.textViewBtnRafraichir);
        textViewBtnVoiture = findViewById(R.id.textViewBtnVoiture);
        textViewBtnQuitter = findViewById(R.id.textViewBtnQuitter);
        textViewBtnQuitter.setTextColor(Color.rgb(104,149,197));
        textViewBtnVoiture.setTextColor(Color.rgb(104,149,197));
    }

}