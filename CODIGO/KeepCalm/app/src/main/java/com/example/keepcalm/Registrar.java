package com.example.keepcalm;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.os.Bundle;
import android.widget.Toast;
import android.content.Intent;
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


        eTxtGrupo.setMovementMethod(null);
        eTxtDNI.setMovementMethod(null);
/*
        eTxtApellido.setText("Chervin");
        eTxtNombre.setText("Facundo test2");
        eTxtDNI.setText("12345678");
        eTxtGrupo.setText("602");
        eTxtEmail.setText("facundo.chervintest@gmail.com");
        eTxtPW.setText("12345678");*/


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
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(btnRegistrar.getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                register();
            }
        });



    }

    private void register() {
        this.ambiente = "TEST";
        this.apellido = eTxtApellido.getText().toString();
        this.nombre = eTxtNombre.getText().toString();
        this.dni = Integer.parseInt(eTxtDNI.getText().toString());
        this.grupo = Integer.parseInt(eTxtGrupo.getText().toString());
        this.email = eTxtEmail.getText().toString();
        this.password = eTxtPW.getText().toString();
        this.comision = Integer.parseInt(spinner.getSelectedItem().toString());


        if(datosValidados()){
            Post post = new Post(this.ambiente, this.nombre, this.apellido, this.dni, this.email, this.password,this.comision, this.grupo);

            Call<Post> call = ApiUtils.getAPIService().createPost(post);
            Log.v("call",call.toString());
            call.enqueue(new Callback<Post>() {
                @Override
                public void onResponse(Call<Post> call, Response<Post> response) {

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

                    Post postResponse = response.body();
                    Log.v("Code",Integer.toString(response.code()));
                    Log.v("State",postResponse.getState());
                    Log.v("User",postResponse.toString());

                    Intent intent = new Intent(Registrar.this, MainPage.class);
                    intent.putExtra("USER_TOKEN", postResponse.getToken());
                    intent.putExtra("USER_NAME", post.getEmail());
                    startActivity(intent);

                }

                @Override
                public void onFailure(Call<Post> call, Throwable t) {
                    Log.v("Code",t.getMessage());
                }
            });
        }
        else {
            Toast.makeText(getApplicationContext(), "Error en los campos ingresados", Toast.LENGTH_SHORT).show();
        }


    }

    private boolean datosValidados(){
        if(!TextUtils.isEmpty(this.apellido) && !TextUtils.isEmpty(this.nombre) && !TextUtils.isEmpty(this.email)
                && !TextUtils.isEmpty(this.password) && !TextUtils.isEmpty(this.dni.toString().trim())
                && !TextUtils.isEmpty(this.grupo.toString().trim()) && this.password.length() >= 8){
            return true;
        }
        if(this.password.length() < 8 || TextUtils.isEmpty(this.password)){
            this.eTxtPW.setError("La contraseña no puede estar vacìa y debe tener al menos 8 caracteres.");
        }
        if(TextUtils.isEmpty(this.apellido)) {
            this.eTxtApellido.setError("Este campo no puede estar vacio.");
        }
        if(TextUtils.isEmpty(this.nombre)){
            this.eTxtNombre.setError("Este campo no puede estar vacio.");
        }
        if(TextUtils.isEmpty(this.email)) {
            this.eTxtEmail.setError("Este campo no puede estar vacio.");
        }
        if(TextUtils.isEmpty(this.dni.toString().trim())){
            this.eTxtDNI.setError("Este campo no puede estar vacio.");
        }
        if(TextUtils.isEmpty(this.grupo.toString().trim())) {
            this.eTxtGrupo.setError("Este campo no puede estar vacio.");
        }
        return false;
    }
}
