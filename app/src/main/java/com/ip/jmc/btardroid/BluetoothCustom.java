package com.ip.jmc.btardroid;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BluetoothCustom extends MainActivity  {
    ArduinoDroid ard;
    public static Context mContextBluetoothCustom;
    public static Context getContext() {
        return mContextBluetoothCustom;
    }
    public static boolean firstFound;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public void BluetoothCustomOnCreate()
    {
        textViewBtnRafraichir.setTextColor(Color.rgb(104,149,197));
        mContextBluetoothCustom = getBaseContext();
        textViewBtnRecherche.setTextColor(Color.rgb(104,149,197));
        listeArrayAdapterBTDecouverte = new ArrayAdapter(mContextMainActivity, android.R.layout.simple_list_item_1, listBluetoothDevicesDiscovered);
        listeArrayAdapter = new ArrayAdapter(mContextMainActivity, android.R.layout.simple_list_item_1, listBluetoothDevices);
        //Test si le Bluetooth est supporté
        if (bluetoothManager == null) {
            Toast.makeText(mContextMainActivity, "Le Bluetooth n'est pas supporté", Toast.LENGTH_LONG).show(); // Replace context with your context instance.
            finish();
        }
        //Affichage de l'icône Bluetooth activé ou désactivé
        if (bluetoothAdapter.isEnabled()) {
            bouttonBluetoothConnect.setImageResource(R.drawable.bt_on_2);
            textViewBtnBT.setTextColor(Color.rgb(104,149,197));
            textViewBtnBT.setText("Désactiver");
            listDevicesBT();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            mContextMainActivity.registerReceiver(bReceiver, filter);
        } else {
            bouttonBluetoothConnect.setImageResource(R.drawable.bt_off);
            textViewBtnBT.setTextColor(Color.rgb(200,0,0));
            textViewBtnBT.setText("Activer");
        }
    }
    public void btOnOff() {
        //Si le Bluetooth n'est pas activé on l'active
        if (!bluetoothAdapter.isEnabled()) {
            textViewBluetooth.setTextColor(Color.rgb(0,200,0));
            textViewBluetooth.setText("Activation du Bluetooth ...");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    textViewBluetooth.setText("");
                }
            }, 3000);
            bluetoothAdapter.enable();
            bouttonBluetoothConnect.setImageResource(R.drawable.bt_on_2);
            textViewBtnBT.setTextColor(Color.rgb(104,149,197));
            textViewBtnBT.setText("Désactiver");
            listDevicesBT();
        }
        //Sinon on le désactive et on modifie l'icône
        else {
            textViewDiscovered.setVisibility(TextView.INVISIBLE);
            textViewAppaires.setVisibility(TextView.INVISIBLE);
            textViewBluetooth.setTextColor(Color.rgb(200,0,0));
            textViewBluetooth.setText("Déconnexion du Bluetooth ...");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    textViewBluetooth.setText("");
                }
            }, 2000);
            bluetoothAdapter.disable();
            bouttonBluetoothConnect.setImageResource(R.drawable.bt_off);
            textViewBtnBT.setTextColor(Color.rgb(200,0,0));
            textViewBtnBT.setText("Activer");
            listeArrayAdapter.clear();
            listeArrayAdapterBTDecouverte.clear();
            listBluetoothDevices.clear();
            listBluetoothDevicesDiscovered.clear();
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
        strMessageEnvoye = message;
    }

    public void onMessageReceived(String message) {
        strMessageRecu = message;
        new ArduinoDroid().traitementReponse(strMessageEnvoye, strMessageRecu);
    }

    public void onError(Throwable error) {
        // Handle the error
        Toast.makeText(this, "Erreur : " + error, Toast.LENGTH_LONG).show();
    }

    public void listDevicesBT() {
        try { Thread.sleep(1800); }
        catch (InterruptedException ex) { android.util.Log.d("BTArdroid Erreur", ex.toString()); }
        List<BluetoothDevice> pairedDevices = bluetoothManager.getPairedDevicesList();
        listeArrayAdapter.clear();
        listBluetoothDevices.clear();
        while (pairedDevices.isEmpty()) {
            pairedDevices = bluetoothManager.getPairedDevicesList();
            break;
        }

        if (!pairedDevices.isEmpty()) {
            textViewAppaires.setTextColor(Color.rgb(104,149,197));
            textViewAppaires.setText("Liste des périphériques appairés :");
            textViewAppaires.setVisibility(TextView.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                if(!listBluetoothDevices.contains(device.getName() + " - " + device.getAddress())) {
                    listBluetoothDevices.add(device.getName() + " - " + device.getAddress());
                }
            }
            listViewBluetoothDevices.setAdapter(listeArrayAdapter);
            listViewBluetoothDevices.setVisibility(TextView.VISIBLE);
            listViewBluetoothDevices.setOnItemClickListener((popup, lv1, position, id) -> {
                        String selLv = listViewBluetoothDevices.getItemAtPosition(position).toString().trim();
                        String segments[] = selLv.split(" - ");
                        String macItem = segments[segments.length - 1];
                        Toast.makeText(mContextMainActivity, "Tentative de connexion avec l'appareil ...", Toast.LENGTH_LONG).show();
                        connectDevice(macItem);
                    }
            );
            listeArrayAdapter.notifyDataSetChanged();
        }
        else
        {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    textViewAppaires.setTextColor(Color.rgb(200,0,0));
                    //textViewBluetooth.setText("Aucun périphérique Bluetooth appairé ...");
                    textViewBluetooth.setText("");
                    textViewAppaires.setVisibility(TextView.VISIBLE);
                    listViewBluetoothDevices.setVisibility(TextView.INVISIBLE);
                    textViewAppaires.setText("Aucun périphérique Bluetooth appairé");
                }
            }, 4000);

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
        if(bluetoothAdapter.isEnabled()) {
            if (bluetoothAdapter.isDiscovering()) {
                // the button is pressed when it discovers, so cancel the discovery
                bluetoothAdapter.cancelDiscovery();
                bouttonBluetoothRecherche.setImageResource(R.drawable.loupe_1);
                textViewBtnRecherche.setTextColor(Color.rgb(104,149,197));
                textViewBtnRecherche.setText("Rechercher");
                textViewBluetooth.setTextColor(Color.rgb(0,200,0));
                textViewBluetooth.setText("Fin de la recherche ...");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        textViewBluetooth.setText("");
                    }
                }, 1500);
            } else {
                bouttonBluetoothRecherche.setImageResource(R.drawable.loupe_2);
                listBluetoothDevicesDiscovered.clear();
                listeArrayAdapterBTDecouverte.clear();
                bluetoothAdapter.startDiscovery();
                textViewBtnRecherche.setTextColor(Color.rgb(255,127,80));
                textViewBluetooth.setTextColor(Color.rgb(104,149,197));
                textViewBluetooth.setText("Recherche en cours ...");
                textViewBtnRecherche.setText("Arrêter");
                firstFound = true;
            }
        }
        else
        {
            textViewBluetooth.setTextColor(Color.rgb(200,0,0));
            textViewBluetooth.setText("Veuiller activer le Bluetooth en cliquant sur l'icône ...");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    textViewBluetooth.setText("");
                }
            }, 3000);
        }
    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if(action.equals(BluetoothDevice.ACTION_FOUND)) {
                if(firstFound == true)
                {
                    textViewDiscovered.setVisibility(TextView.VISIBLE);
                    firstFound = false;

                }
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the arrayAdapter
                if(device != null) {
                    if(!listBluetoothDevicesDiscovered.contains(device.getName() + " - " + device.getAddress()))
                    {
                        listBluetoothDevicesDiscovered.add(device.getName() + " - " + device.getAddress());
                        listViewbtdiscover.setAdapter(listeArrayAdapterBTDecouverte);
                        listViewbtdiscover.setOnItemClickListener((popup, lv1, position, id) -> {
                                    String selLv = listViewbtdiscover.getItemAtPosition(position).toString().trim();
                                    String segments[] = selLv.split(" - ");
                                    String macItem = segments[segments.length - 1];
                                    Toast.makeText(mContextMainActivity, "Tentative de connexion avec l'appareil ...", Toast.LENGTH_LONG).show();
                                    connectDevice(macItem);
                                }
                        );
                        listeArrayAdapterBTDecouverte.notifyDataSetChanged();
                    }

                }
            }
            if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                firstFound = false;
                textViewBluetooth.setText("");
                bouttonBluetoothRecherche.setImageResource(R.drawable.loupe_1);
                textViewBtnRecherche.setTextColor(Color.rgb(104,149,197));
                textViewBtnRecherche.setText("Rechercher");
                textViewBluetooth.setTextColor(Color.rgb(0,200,0));
                textViewBluetooth.setText("Fin de la recherche ...");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        textViewBluetooth.setText("");
                    }
                }, 1500);
                if(listBluetoothDevicesDiscovered.isEmpty())
                {
                    textViewDiscovered.setVisibility(TextView.INVISIBLE);
                    textViewBluetooth.setTextColor(Color.rgb(200,0,0));
                    textViewBluetooth.setText("Aucun périphérique Bluetooth à proximité ...");
                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            textViewBluetooth.setText("");
                        }
                    }, 1500);
                }
            }
        }
    };
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bReceiver);
    }
}
