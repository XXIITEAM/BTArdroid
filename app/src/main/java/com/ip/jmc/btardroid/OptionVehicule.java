package com.ip.jmc.btardroid;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import static com.ip.jmc.btardroid.MainActivity.deviceInterface;

public class OptionVehicule extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_vehicule);


    }

    public void envoyerParamVehicule(View v) {
        TextInputLayout reculParam = findViewById(R.id.reculParam);
        TextInputLayout gaucheParam = findViewById(R.id.gaucheParam);
        TextInputLayout avancerParam = findViewById(R.id.avancerParam);
        TextInputLayout droiteParam = findViewById(R.id.droiteParam);
        String strParam = reculParam.getEditText().getText().toString() + "/" +
                gaucheParam.getEditText().getText().toString() + "/" +
                avancerParam.getEditText().getText().toString() + "/" +
                droiteParam.getEditText().getText().toString();

        deviceInterface.sendMessage(strParam);


    }

}
