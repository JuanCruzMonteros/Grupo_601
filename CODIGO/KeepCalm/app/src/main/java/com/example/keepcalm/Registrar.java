package com.example.keepcalm;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;

import retrofit2.Call;

import data.remote.APIService;
import data.remote.ApiUtils;
import data.User;
import data.model.Post;
import retrofit2.Callback;
import retrofit2.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class Registrar extends AppCompatActivity {

    private Button btnRegistrar;
    private EditText eTxtApellido;
    private EditText eTxtNombre;
    private EditText eTxtDNI;
    private Spinner spinner;
    private EditText eTxtGrupo;
    private EditText eTxtEmail;
    private EditText eTxtPW;

    private String ambiente;
    private String apellido;
    private String nombre;
    private Integer dni;
    private String email;
    private String password;
    private Integer comision;
    private Integer grupo;

    private APIService mAPIService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);


        spinner = findViewById(R.id.spinnerComision);
        btnRegistrar = findViewById(R.id.buttonRegistrarse);
        eTxtApellido = findViewById(R.id.editTextApellido);
        eTxtNombre = findViewById(R.id.editTextNombre);
        eTxtDNI = findViewById(R.id.editTextDNI);
        eTxtGrupo = findViewById(R.id.editTextGrupo);
        eTxtEmail = findViewById(R.id.editTextEmail);
        eTxtPW = findViewById(R.id.editTextPassword);

        eTxtApellido.setText("Chervin");
        eTxtNombre.setText("Facundo test2");
        eTxtDNI.setText("12345678");
        eTxtGrupo.setText("602");
        eTxtEmail.setText("facundo.chervintest@gmail.com");
        eTxtPW.setText("12345678");


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.comisionesArray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        mAPIService = ApiUtils.getAPIService();

        btnRegistrar.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
//                ambiente = "TEST";
//                apellido = eTxtApellido.getText().toString();
//                nombre = eTxtNombre.getText().toString();
//                dni = Integer.parseInt(eTxtDNI.getText().toString());
//                grupo = Integer.parseInt(eTxtGrupo.getText().toString());
//                email = eTxtEmail.getText().toString();
//                password = eTxtPW.getText().toString();
//                comision = Integer.parseInt(spinner.getSelectedItem().toString());


                createPost();

            }
        });



    }

    private void createPost() {
        Post post = new Post("DEV", "Facundo", "Chervin", 39762219, "facundo.chervin@gmail.com", "contraseña123",3900, 601);
        Post postLogin = new Post("DEV", "", "", 1, "facundo.chervin@gmail.com", "contraseña123",1, 1);
        String json = new Gson().toJson(post);
        Log.v("JSOOON",json);
//        Call<Post> call = ApiUtils.getAPIService().createPost(post);
//        Log.v("call",call.toString());
//        call.enqueue(new Callback<Post>() {
//            @Override
//            public void onResponse(Call<Post> call, Response<Post> response) {
//
//                if(!response.isSuccessful()){
//
//                    try {
//                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
//                        Log.v("Error Code",Integer.toString(response.code()));
//                        Log.v("Error State",jsonObject.getString("state"));
//                        Log.v("Error Env",jsonObject.getString("env"));
//                        Log.v("Error Msg",jsonObject.getString("msg"));
//                    } catch (IOException | JSONException e) {
//                        e.printStackTrace();
//                    }
//                    return;
//                }
//
//                Post postResponse = response.body();
//                Log.v("Code",Integer.toString(response.code()));
//                Log.v("Env",postResponse.getEnv());
//                Log.v("State",postResponse.getState());
//                Log.v("User",postResponse.toString());
//
//            }
//
//            @Override
//            public void onFailure(Call<Post> call, Throwable t) {
//                Log.v("Code",t.getMessage());
//                Log.v("Code",t.getMessage());
//            }
//        });

        Call<Post> call = ApiUtils.getAPIService().loginUser(postLogin);
        Log.v("call",call.toString());
        call.enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if(!response.isSuccessful()){

                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Log.v("Error Code",Integer.toString(response.code()));
                        Log.v("Error State",jsonObject.getString("state"));
                        Log.v("Error Env",jsonObject.getString("env"));
                        Log.v("Error Msg",jsonObject.getString("msg"));
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                Post postResponse = response.body();
                Log.v("Code",Integer.toString(response.code()));
                //Log.v("Env",postResponse.getEnv());
                Log.v("State",postResponse.getState());
                Log.v("User",postResponse.toString());
                Log.v("token",postResponse.getToken());


            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Log.v("Code",t.getMessage());
            }
        });

    }
}
