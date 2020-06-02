package com.example.keepcalm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Get_Gps extends AppCompatActivity {

    Button btn_location;
    TextView textView1, textView2, textView3, textView4, textView5;

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_gps);

        btn_location = findViewById(R.id.btn_location);
        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        textView5 = findViewById(R.id.textView5);

        //inicializo fused
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
                /*
                //checkeo permisos
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION == PackageManager.PERMISSION_GRANTED)
                {
                    //Si el permiso fue garantizado
                    getLocation();
                }else{
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION}, 44);

                }*/
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

        Toast toast = Toast.makeText(this, "Estado de coneccion " +isConnected , Toast.LENGTH_SHORT);
        toast.show();
    }
    private void getLocation() {
        getConectionState();
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
}