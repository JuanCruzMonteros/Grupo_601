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
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
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
import data.model.History;
import data.remote.ApiUtils;
import data.remote.SharedPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class MainPage extends AppCompatActivity {

    private TextView txtViewDia;
    private Chronometer chronometer;
    private TextView txtGps;
    private TextView txtViewTestNofShakes;
    private Button buttonHistorial;
    private TextView txtUserName;

    private Boolean flagPrimerShake = true;

    private static final String tag = "MainPageActivity";

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
        buttonHistorial = findViewById(R.id.buttonHistorial);
        buttonHistorial.setEnabled(true);
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
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);



        Bundle extras = getIntent().getExtras();
        txtUserName = findViewById(R.id.textViewUserName);
        if (extras != null) {
            this.token = extras.getString("USER_TOKEN");
            this.userName = extras.getString("USER_NAME");
            txtUserName.setText("User: " + this.userName);
        } else {
            Log.v(tag,"Error al obtener los datos del usuario.");
        }
        postServerEvent("Login", "ACTIVO", "El usuario ingresa en la aplicación.");

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
        buttonHistorial = findViewById(R.id.buttonHistorial);


        getLocation();
        DateFormat df = new SimpleDateFormat("EEEE d 'de' MMMM 'del' yyyy");
        String date = df.format(Calendar.getInstance().getTime()).toUpperCase();
        txtViewDia.setText(date);


        buttonHistorial.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonHistorial.setEnabled(false);
                Intent intent = new Intent(MainPage.this, SharedHistory.class);
                startActivity(intent);
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
                if (flagPrimerShake) {
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    getLocation();
                    postServerEvent("Shake", "ACTIVO", "El usuario comienza a agitar el celular - El usuario registra el inicio de un ataque de migraña");
                    flagPrimerShake = false;
                }

                String text = "Shake Nº : " + String.format("%d", countShakes);
                txtViewTestNofShakes.setText(text);
                Log.v("Shake Nº :", String.valueOf(countShakes));
            }

            @Override
            public void onShakeStops(int count) {
                countShakes = count;
                Long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                AlertDialog.Builder alert = new AlertDialog.Builder(MainPage.this);
                String tiempoTranscurrido = String.format("%02d min, %02d sec",
                        TimeUnit.MILLISECONDS.toMinutes(elapsedMillis - 15000),
                        TimeUnit.MILLISECONDS.toSeconds(elapsedMillis - 15000) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsedMillis - 15000))
                );
                chronometer.stop();

                alert.setTitle("Keep Calm");
                alert.setMessage("Su ataque de migraña ha terminado?");
                alert.setPositiveButton("¡Me encuentro bien!", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getLocation();
                        postServerEvent("Shake", "INACTIVO", "El ataque de migraña duro " + tiempoTranscurrido + " - Se agitó el celular " + countShakes + " veces");
                        countShakes = 0;
                        flagPrimerShake = true;
                        chronometer.setBase(SystemClock.elapsedRealtime());
                    }
                });
                alert.setNegativeButton("¡Necesito ayuda!", new DialogInterface.OnClickListener() {
                    //En esta parte se debería llamar al contacto de emergencia, pero no se llego a implementar.
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getLocation();
                        postServerEvent("Shake", "INACTIVO", "El ataque de migraña duro " + tiempoTranscurrido + " - Se agitó el celular " + countShakes + " veces");
                        countShakes = 0;
                        flagPrimerShake = true;
                        chronometer.setBase(SystemClock.elapsedRealtime());
                    }
                });

                saveLocationSharedPref(countShakes);

                alert.show();
            }
        });
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        registerReceiver(fechaListener, fechaFilter);
    }

    private void postServerEvent(String type_event, String state, String description) {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy - HH:mm:ss - ");
        String date = df.format(Calendar.getInstance().getTime());
        EventPost eventPost = new EventPost(this.token, "TEST", type_event, state, date + description);

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
        Boolean flag = flagPrimerShake;
        Integer count = countShakes;
        fusedLocationProviderClient = getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainPage.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            Toast.makeText(this, "Debe permitir que la aplicación acceda a la ubicación del dispositivo", Toast.LENGTH_SHORT);
            return;
        }

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

                        if (count > 0) {
                            txtGps.setText(address);
                            if (!flag) {
                                //Termina el evento
                                postServerEvent("GPS - Posicion Final del ataque de migraña", "ACTIVO", address);
                            } else {
                                //Inicializa el evento
                                postServerEvent("GPS - Posicion Inicial del ataque de migraña", "ACTIVO", address);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.v("location == null", "asd");
                }
            }

        });
    }

    public void saveLocationSharedPref(int CountShakes){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        // Divido Fecha  entre yyyy-MM-dd y HH:mm:ss
        String[] parts = formattedDate.split(" ");

        History history = new History( parts[0].toString(),
                parts[1].toString(),
                CountShakes,
                this.address,
                Double.parseDouble(String.valueOf(this.latitude)),
                Double.parseDouble(String.valueOf(this.longitude)));

        SharedPref sp = new SharedPref(MainPage.this);
        sp.savePreference(history);

    }
}

