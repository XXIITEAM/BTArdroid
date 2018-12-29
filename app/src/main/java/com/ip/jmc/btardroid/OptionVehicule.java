package com.ip.jmc.btardroid;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;


public class OptionVehicule extends MainActivity {
    public static Context mContextOptionVehicule;
    public static Context getContext() {
        return mContextOptionVehicule;
    }
    TextInputEditText reculParam = findViewById(R.id.reculParam);
    TextInputEditText gaucheParam = findViewById(R.id.gaucheParam);
    TextInputEditText avancerParam = findViewById(R.id.avancerParam);
    TextInputEditText droiteParam = findViewById(R.id.droiteParam);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContextOptionVehicule = getBaseContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_vehicule);
        receptionParamVehicule();

    }
    public void receptionParamVehicule() {

    }
    public void envoyerParamVehicule(View v) {

        String strParam = reculParam.getText().toString() + "/" +
                gaucheParam.getText().toString() + "/" +
                avancerParam.getText().toString() + "/" +
                droiteParam.getText().toString();

        deviceInterface.sendMessage("A");


    }

}
