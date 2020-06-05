package com.example.keepcalm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
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
        txtHistorias.setMovementMethod(new ScrollingMovementMethod());
        btn_EliminarLista = findViewById(R.id.btn_EliminarLista);


        btn_EliminarLista.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SharedHistory.this);
                alert.setTitle("Keep Calm");
                alert.setMessage("¿Está seguro que desea eliminar el historial?");
                alert.setPositiveButton("Si, eliminar!", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPref sp = new SharedPref(SharedHistory.this);
                        sp.ClearPreference();
                        txtHistorias.setText("");
                    }
                });

                alert.setNegativeButton("Cancelar", null);

                AlertDialog dialog = alert.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);

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
