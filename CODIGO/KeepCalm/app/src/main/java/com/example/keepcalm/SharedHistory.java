package com.example.keepcalm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import data.remote.SharedPref;

public class SharedHistory  extends AppCompatActivity {

    private TextView txtHistorias;
    private Button btn_EliminarLista;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_history);
        txtHistorias = findViewById(R.id.txtHistorias);
        btn_EliminarLista = findViewById(R.id.btn_EliminarLista);


        btn_EliminarLista.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                SharedPref sp = new SharedPref(SharedHistory.this);
                sp.ClearPreference();
                txtHistorias.setText("");
            }
        });

        CargarDatos();
    }

    private void  CargarDatos(){
        SharedPref sp = new SharedPref(getBaseContext());
        String historias = sp.getHistoryPreference();

        if(historias == "")
            return;
        txtHistorias.setText(historias);
    }
}
