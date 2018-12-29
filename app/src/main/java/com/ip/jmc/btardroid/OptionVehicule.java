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
    TextInputEditText reculParam;
    TextInputEditText gaucheParam;
    TextInputEditText avancerParam;
    TextInputEditText droiteParam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_vehicule);
        reculParam = findViewById(R.id.reculParam);
        gaucheParam = findViewById(R.id.gaucheParam);
        avancerParam = findViewById(R.id.avancerParam);
        droiteParam = findViewById(R.id.droiteParam);
        mContextOptionVehicule = getBaseContext();

        receptionParamVehicule();

    }
    public void receptionParamVehicule() {

    }
    public void envoyerParamVehicule(View v) {

        String strParam = "W" + "/" + reculParam.getText().toString() + "/" +
                gaucheParam.getText().toString() + "/" +
                avancerParam.getText().toString() + "/" +
                droiteParam.getText().toString() + "/" + "X" ;

        deviceInterface.sendMessage(strParam);


    }

}
