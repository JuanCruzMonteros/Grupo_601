package com.example.keepcalm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.view.inputmethod.InputMethodManager;

import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import data.model.Post;
import data.remote.ApiUtils;
import data.remote.SharedPref;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Handler mHandler = new Handler();
    boolean isRunning = true;
    private EditText userInput;
    private EditText passwordInput;
    private Switch switchRecordarme;
    private Button btnLogin;
    private Button btnRegistrar;

    @Override
    protected void onResume() {
        super.onResume();
        btnLogin = findViewById(R.id.buttonLogin);
        btnLogin.setEnabled(true);
        btnRegistrar = findViewById(R.id.buttonSinCuentaRegistrarse);
        btnRegistrar.setEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                while (isRunning) {
                    try {
                        Thread.sleep(10000);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                // Write your code here to update the UI.
                                displayData();
                            }
                        });
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
            }
        }).start();


        this.userInput = findViewById(R.id.userInput);
        this.passwordInput = findViewById(R.id.passwordInput);
        this.switchRecordarme = findViewById(R.id.switchRecordarme);

        SharedPref sp = new SharedPref(getBaseContext());
        if (sp.getUser() != "") {
            this.userInput.setText(sp.getUser());
            this.passwordInput.setText(sp.getPass());
            this.switchRecordarme.setChecked(true);
        }

        btnRegistrar = findViewById(R.id.buttonSinCuentaRegistrarse);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                btnRegistrar.setEnabled(false);
                startActivity(new Intent(MainActivity.this, Registrar.class));
            }
        });

        btnLogin = findViewById(R.id.buttonLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                btnLogin.setEnabled(false);
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(btnLogin.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                login();
            }
        });

    }


    private void login() {

        String userInputVal = userInput.getText().toString();
        String passwordInputVal = passwordInput.getText().toString();

        if (datosValidados(userInputVal, passwordInputVal)) {

            Post postLogin = new Post("DEV", "", "", 1, userInputVal, passwordInputVal, 1, 1);

            Call<Post> call = ApiUtils.getAPIService().loginUser(postLogin);
            call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {

                    if (!response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            Log.v("Error Code", Integer.toString(response.code()));
                            Log.v("Error State", jsonObject.getString("state"));
                            Log.v("Error Msg", jsonObject.getString("msg"));
                            if (jsonObject.getString("msg").equals("Error de autenticación")) {
                                Toast.makeText(getApplicationContext(), "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                        return;
                    }

                    //Si los datos son validos, recordar usuario y selecciono la opcion:
                    SharedPref sp = new SharedPref(getApplicationContext());
                    if (switchRecordarme.isChecked()) {
                        sp.saveUser(userInput.getText().toString(),
                                passwordInput.getText().toString());
                    } else {
                        sp.deleteUser();
                    }

                    Post postResponse = response.body();
                    Log.v("Code", Integer.toString(response.code()));
                    Log.v("State", postResponse.getState());
                    Log.v("User", postResponse.toString());
                    Log.v("token", postResponse.getToken());
                    Intent intent = new Intent(MainActivity.this, MainPage.class);
                    intent.putExtra("USER_TOKEN", postResponse.getToken());
                    intent.putExtra("USER_NAME", userInputVal);
                    startActivity(intent);
                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    Log.v("Code", t.getMessage());
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Error en los campos ingresados", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean datosValidados(String userInputVal, String passwordInputVal) {
        if (!TextUtils.isEmpty(userInputVal) && !TextUtils.isEmpty(passwordInputVal)) {
            return true;
        }
        if (TextUtils.isEmpty(userInputVal)) {
            this.userInput.setError("Usuario Invalido");
        }
        if (TextUtils.isEmpty(passwordInputVal)) {
            this.passwordInput.setError("Contraseña Invalida");
        }
        return false;
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

    private void displayData() {
        ConnectivityManager cn=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf=cn.getActiveNetworkInfo();
        if(nf != null && nf.isConnected()==true )
        {
            Toast.makeText(this, "Internet disponible", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Internet No Disponible", Toast.LENGTH_SHORT).show();
        }
    }

}


