package com.example.keepcalm;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.example.keepcalm.Events.ShakeDetector;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import data.model.EventPost;
import data.remote.ApiUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPage extends AppCompatActivity {

    private TextView txtViewDia;
    private TextView txtViewHora;
    private Chronometer chronometer;
    private TextView txtGps;
    private TextView txtViewTestNofShakes;
    private Button buttonOK;
    private Button buttonHistorial;



    private double latitude;
    private double longitude;
    private String address;

    FusedLocationProviderClient fusedLocationProviderClient;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private String token;
    private String userName;
    private Integer countShakes = 0;

    private static IntentFilter fechaFilter;
    private BroadcastReceiver fechaListener;


    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        registerReceiver(fechaListener, fechaFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mShakeDetector);
        unregisterReceiver(fechaListener);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(fechaListener);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            this.token = extras.getString("USER_TOKEN");
            this.userName = extras.getString("USER_NAME");
        }
        else {
            //mostrar error!!
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainPage.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        fechaFilter = new IntentFilter();
        fechaFilter.addAction(Intent.ACTION_DATE_CHANGED);
        txtGps = findViewById(R.id.textViewGPS);
        txtViewDia = findViewById(R.id.textViewDia);
        chronometer = findViewById(R.id.chronometer);
        txtViewTestNofShakes = findViewById(R.id.textViewCountShakes);
        buttonOK = findViewById(R.id.buttonOK);
        buttonHistorial = findViewById(R.id.buttonHistorial);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        DateFormat df = new SimpleDateFormat("EEEE d 'de' MMMM 'del' yyyy");
        String date = df.format(Calendar.getInstance().getTime()).toUpperCase();
        txtViewDia.setText(date);





        buttonHistorial.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, SharedHistory.class);
                startActivity(intent);
            }
        });

        buttonOK.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getLocation();
                txtGps.setText(address);
                //chronometer.stop();
            }
        });



        fechaListener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                DateFormat df = new SimpleDateFormat("EEEE dd-MM-yyyy - HH:mm:ss");
                String date = df.format(Calendar.getInstance().getTime());
                txtViewDia.setText(date);
            }
        };





        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                countShakes++;
                if (countShakes == 1) {
                    chronometer.start();
                    getLocation();
                    txtGps.setText(address);
                    //Logear en el servidor el evento de inicio de shake con la ubicación del dispositivo
                }
                String text = "Shake Nº : " + String.format("%d", count);
                txtViewTestNofShakes.setText(text);
                Log.v("Shake Nº :", String.valueOf(count));
            }

            @Override
            public void onShakeStops(int count) {
                countShakes = count;
                Long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                chronometer.stop();
                AlertDialog.Builder alert = new AlertDialog.Builder(MainPage.this);
                alert.setTitle("Doctor");
                alert.setMessage("Su ataque de ... ha terminado?");
                alert.setPositiveButton("¡Me encuentro bien!",new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getLocation();
                        txtGps.setText(address);
                        Log.v("Tiempo que paso", String.valueOf(elapsedMillis-5000));

                        //handleShakeEvent();
                    }
                });
                alert.setNegativeButton("¡Necesito ayuda!", null);
                /*
                alert.setNegativeButton("¡Necesito ayuda!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //En este caso llamar al contacto de emergencia
                        //No se va a implementar en la entrega por el tema del tiempo

                    }
                });*/
                alert.show();
            }
        });
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        registerReceiver(fechaListener, fechaFilter);
    }

    private void handleShakeEvent() {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy - HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());

        EventPost eventPost = new EventPost(this.token, "TEST", "Shake Event", "ACTIVO", date + " - Se realizaron " + this.countShakes + " Shakes seguidos");

        Call<EventPost> call = ApiUtils.getAPIService().registerEvent(this.token, eventPost);
        call.enqueue(new Callback<EventPost>() {
            @Override
            public void onResponse(Call<EventPost> call, Response<EventPost> response) {

                if (!response.isSuccessful()) {

                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Log.v("Error Code", Integer.toString(response.code()));
                        Log.v("Error State", jsonObject.getString("state"));
                        Log.v("Error Msg", jsonObject.getString("msg"));
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                EventPost postResponse = response.body();
                Log.v("Code", Integer.toString(response.code()));
                Log.v("State", postResponse.getState());
                Log.v("User", postResponse.toString());
                Log.v("event", postResponse.getEvent().toString());
            }

            @Override
            public void onFailure(Call<EventPost> call, Throwable t) {
                Log.v("Code", t.getMessage());
            }
        });
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainPage.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            Toast.makeText(this, "Debe permitir que la aplicación acceda a la ubicación del dispositivo", Toast.LENGTH_SHORT);
            Log.v("A ver si entro lrpm","si entro");
            return;
        }
        //getConectionState();

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(MainPage.this, Locale.getDefault());
                        List<Address> direcciones;
                        direcciones = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );
                        latitude = direcciones.get(0).getLatitude();
                        longitude = direcciones.get(0).getLongitude();
                        address = direcciones.get(0).getAddressLine(0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}

