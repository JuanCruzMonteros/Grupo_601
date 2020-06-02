package com.example.keepcalm;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import com.example.keepcalm.Events.ShakeDetector;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

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
    private TextView txtViewTestNofShakes;
    private Button buttonOK;
    private Button buttonHistorial;



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
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
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
        unregisterReceiver(fechaListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        fechaFilter = new IntentFilter();
        fechaFilter.addAction(Intent.ACTION_DATE_CHANGED);


        txtViewDia = findViewById(R.id.textViewDia);
        chronometer = findViewById(R.id.chronometer);
        txtViewTestNofShakes = findViewById(R.id.textViewCountShakes);
        buttonOK = findViewById(R.id.buttonOK);
        buttonHistorial = findViewById(R.id.buttonHistorial);


        buttonOK.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                chronometer.stop();
            }
        });

        DateFormat df = new SimpleDateFormat("EEEE d 'de' MMMM 'del' yyyy");
        String date = df.format(Calendar.getInstance().getTime()).toUpperCase();
        txtViewDia.setText(date);

        fechaListener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent i) {
                    DateFormat df = new SimpleDateFormat("EEEE dd-MM-yyyy - HH:mm:ss");
                    String date = df.format(Calendar.getInstance().getTime());
                    txtViewDia.setText(date);
            }
        };




        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.token = extras.getString("USER_TOKEN");
            this.userName = extras.getString("USER_NAME");
            //txtViewTest.setText("Token: "+this.token+"\n"+"User Name: "+this.userName+"\n");

        }


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
                if(count == 1){
                    chronometer.start();
                }
                String text = "Shake Nº : " + String.format("%d",count);
                txtViewTestNofShakes.setText(text);
                Log.v("Shake Nº :",String.valueOf(count));
            }

            @Override
            public void onShakeStops(int count) {
                countShakes = count;
                handleShakeEvent();
            }
        });
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        registerReceiver(fechaListener, fechaFilter);
    }

    private void handleShakeEvent() {
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy - HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());

        EventPost eventPost = new EventPost(this.token, "TEST","Shake Event","ACTIVO",date +" - Se realizaron "+this.countShakes+" Shakes seguidos");

        Call<EventPost> call = ApiUtils.getAPIService().registerEvent(this.token,eventPost);
        call.enqueue(new Callback<EventPost>() {
            @Override
            public void onResponse(Call<EventPost> call, Response<EventPost> response) {

                if(!response.isSuccessful()){

                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Log.v("Error Code",Integer.toString(response.code()));
                        Log.v("Error State",jsonObject.getString("state"));
                        Log.v("Error Msg",jsonObject.getString("msg"));
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                EventPost postResponse = response.body();
                Log.v("Code",Integer.toString(response.code()));
                Log.v("State",postResponse.getState());
                Log.v("User",postResponse.toString());
                Log.v("event",postResponse.getEvent().toString());
            }

            @Override
            public void onFailure(Call<EventPost> call, Throwable t) {
                Log.v("Code",t.getMessage());
            }
        });
    }



}

