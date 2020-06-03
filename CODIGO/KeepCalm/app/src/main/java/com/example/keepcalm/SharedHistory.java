package com.example.keepcalm;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import data.remote.SharedPref;

public class SharedHistory  extends AppCompatActivity {

    private TextView txtHistorias;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_history);
        txtHistorias = findViewById(R.id.txtHistorias);
        CargarDatos();
    }

    private void  CargarDatos(){
        SharedPref sp = new SharedPref(getBaseContext());
        String historias = sp.getHistoryPreference();

        if(historias == "")
            return;
        String partes[] = historias.split("History");

        if(partes.length > 0){
            txtHistorias.setText(historias);
        }

        Log.i("historias: " , historias);
    }
}
