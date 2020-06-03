package com.example.keepcalm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import data.model.History;

public class Get_Gps extends AppCompatActivity {

    Button btn_location,btn_GetObject,btn_historial;
    TextView textView1, textView2, textView3, textView4, textView5;

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_gps);
        btn_historial = findViewById(R.id.btn_historial);
        btn_GetObject = findViewById(R.id.btn_GetObject);
        btn_location = findViewById(R.id.btn_location);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);

        //inicializo fused
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Get_Gps.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            Log.v("Entro","al if");
        }
        btn_historial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearPreference(getApplicationContext());
            }
        });
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        btn_GetObject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataSharedPreferences(5);
            }
        });
    }

    private void getConectionState(){
        //test de validacion de conexion a internet
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        Toast toast = Toast.makeText(this, "Estado de coneccion " + isConnected , Toast.LENGTH_SHORT);
        toast.show();
    }
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Get_Gps.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            Toast toast = Toast.makeText(this, "Debe permitir que la aplicación acceda a la ubicación del dispositivo" , Toast.LENGTH_SHORT);
            return;
        }
        //getConectionState();

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //
                Location location = task.getResult();
                if (location != null) {

                    try {
                        //si obtuvo algo, inicializo Geocoder
                        Geocoder geocoder = new Geocoder(Get_Gps.this, Locale.getDefault());
                        //Inicializo lista de direcciones

                        List<Address> direcciones;
                        direcciones = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );
                        //set Latitud
                        textView1.setText(Html.fromHtml("<font color='#6200EE'<b>Latitude</b><br></font>" +
                                direcciones.get(0).getLatitude()
                        ));
                        //set Longitud
                        textView2.setText(Html.fromHtml("<font color='#6200EE'<b>Longitude</b><br></font>" +
                                direcciones.get(0).getLongitude()
                        ));
                        //set Pais
                        textView3.setText(Html.fromHtml("<font color='#6200EE'<b>Pais</b><br></font>" +
                                direcciones.get(0).getCountryName()
                        ));
                        //set Localidad
                        textView4.setText(Html.fromHtml("<font color='#6200EE'<b>Localidad</b><br></font>" +
                                direcciones.get(0).getLocality()
                        ));
                        //set Addres Line
                        textView5.setText(Html.fromHtml("<font color='#6200EE'<b>Addres Line</b><br></font>" +
                                direcciones.get(0).getAddressLine(0)
                        ));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        }


    // Para usar este metodo debe estar en Oncreate
    // fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    // y como atributo:
    // FusedLocationProviderClient fusedLocationProviderClient;
    private void saveDataSharedPreferences (int CountShakes) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Get_Gps.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            return;
        }

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //
                Location location = task.getResult();
                if (location != null) {

                    try {
                        //si obtuvo algo, inicializo Geocoder
                        Geocoder geocoder = new Geocoder(Get_Gps.this, Locale.getDefault());
                        //Inicializo lista de direcciones

                        List<Address> direcciones;
                        direcciones = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String formattedDate = df.format(c.getTime());
                        // Divido Fecha  entre yyyy-MM-dd y HH:mm:ss
                        String[] parts = formattedDate.split(" ");

                        History history = new History(  parts[0].toString(),
                                                        parts[1].toString(),
                                                        CountShakes,
                                                        direcciones.get(0).getAddressLine(0),
                                                        Double.parseDouble(String.valueOf(direcciones.get(0).getLatitude())),
                                                        Double.parseDouble(String.valueOf(direcciones.get(0).getLongitude())));

                        Log.i("History actual:",history.toString());
                        savePreference(getApplicationContext(),history);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }



    public static final String PREFS_KEY = "sharedPref";
    public void savePreference(Context context, History history) {
        int indice;
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        indice = preferences.getInt("indice", 0);

        if(indice > 3 ){
            String algo = getPreference(context);
            Log.i("mayor que 3", algo);
        }


        String historyList = preferences.getString("historyList", "");

        SharedPreferences.Editor editor;
        editor = preferences.edit();

        editor.putString("historyList", historyList + (indice+1) + "-" + history);
        editor.putInt("indice", (indice+1));
        editor.commit();
    }
/*
    public void savePreference(Context context, History history) {
        int indice;
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        indice = preferences.getInt("indice", 0);
        String historyList = preferences.getString("historyList", "");

        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();

        editor.putString("historyList", historyList + (indice+1) + "-" + history);
        editor.putInt("indice", (indice+1));
        editor.commit();
    }*/

    public String getPreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        int indice = preferences.getInt("indice", 0);
        String historyList = preferences.getString("historyList", "");
        return historyList;
    }

    public void ClearPreference(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = settings.edit();

        editor.putString("historyList", "");
        editor.putInt("indice", 0);
        editor.commit();
    }
}