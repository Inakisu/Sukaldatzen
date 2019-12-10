package com.stirling.developments.Views;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.shagi.materialdatepicker.date.DatePickerFragmentDialog;
import com.stirling.developments.Models.POJOs.RespuestaU;
import com.stirling.developments.Models.gson2pojo.Aggregations;
import com.stirling.developments.Models.gson2pojo.Example;
import com.stirling.developments.Models.gson2pojo.Hit;
import com.stirling.developments.Models.gson2pojo.Hits;
import com.stirling.developments.Models.gson2pojo.MyAgg;
import com.stirling.developments.R;
import com.stirling.developments.Utils.Constants;
import com.stirling.developments.Utils.ElasticSearchAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.Credentials;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

public class SignupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputNombre, inputFechaNac;
    private Button btnSignIn, btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String TAG = "Tag verificación Email";
    private String mElasticSearchPassword = Constants.elasticPassword;
    private String queryJson = "";
    private JSONObject jsonObject;
    private ElasticSearchAPI searchAPI;
    private Retrofit retrofit;
    final Calendar calendario = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_signup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        //Inicializamos la API de elasticsearch
        inicializarAPI();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputNombre = (EditText) findViewById(R.id.nombreReg);
        inputFechaNac = (EditText) findViewById(R.id.fechaNacReg);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        inputFechaNac.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /*new DatePickerDialog(SignupActivity.this, date, calendario
                        .get(Calendar.YEAR), calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)).show();*/
                DatePickerFragmentDialog datePickerFragmentDialog = DatePickerFragmentDialog
                        .newInstance(new DatePickerFragmentDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerFragmentDialog v, int dayOfMonth,
                                                  int monthOfYear, int year) {
                                inputFechaNac.setText(dayOfMonth + "-" + (monthOfYear+1)
                                        + "-" + year);
                            }
                        },2000,01,11);
                datePickerFragmentDialog.setMaxDate(System.currentTimeMillis());
                datePickerFragmentDialog.show(getSupportFragmentManager(), null);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString();
                String nombre = inputNombre.getText().toString().trim();
                String fecha = inputFechaNac.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Introduce una dirección de correo",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Introduce una contraseña"
                            , Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Introduzca una contraseña de " +
                            "mínimo 6 caracteres", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(nombre)){
                    Toast.makeText(getApplicationContext(), "Introduce un nombre"
                            , Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(fecha)){
                    Toast.makeText(getApplicationContext(), "Introduce una fecha de" +
                            "nacimiento", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                //Creamos el usuario en el gestor de cuentas de Firebase
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new
                                OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignupActivity.this,
                                        "createUserWithEmail:onComplete:" + task.isSuccessful(),
                                        Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) { //En caso de error o correo existente
                                    Toast.makeText(SignupActivity.this,
                                            "Authentication"+" failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                    Log.i("Response","Failed to create user: "
                                            +task.getException().getMessage());

                                } else {
                                    //I: Registro correcto --> enviar email verificación
                           /*         user = auth.getCurrentUser();
                                    enviarVerif(); //Llamada a método para enviar email verificación*/
                                    /*if(!user.isEmailVerified()) {//I: revisar este if
                                        Toast.makeText(SignupActivity.this,
                                                "Verifique el correo", Toast.LENGTH_SHORT).show();
                                        auth.getInstance().signOut();
                                        startActivity(new Intent(SignupActivity.this,
                                                SignupActivity.class));
                                       // finish();
                                    }else{
                                        startActivity(new Intent(SignupActivity.this,
                                                MainUserActivity.class));
                                        finish();
                                    }*/
                                }
                            }
                        });
                //Introducimos informacion en nuestra base de datos, no en firebase
                nuevoUsuario(email, nombre, fecha);

            }

        });
    }

    /*private void enviarVerif(){ //método para enviar email de verificación
        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Email de verificación enviado a: "
                            + user.getEmail(), Toast.LENGTH_SHORT).show();
                    Log.d("Verificación","Email de verificación enviado a: "+
                            user.getEmail());
                }else{
                    Log.e(TAG, "sendEmailVerification", task.getException());
                    Toast.makeText(getApplicationContext(), "Fallo al enviar email verificación",
                            +Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/
    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
    private void inicializarAPI(){
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_ELASTICSEARCH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        searchAPI = retrofit.create(ElasticSearchAPI.class);
    }
    private void nuevoUsuario(String correo, String nombre, String fechaNac){

        //Generamos un authentication header para identificarnos contra Elasticsearch
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", Credentials.basic("android",
                mElasticSearchPassword));
        String searchString = "";
        try {
            //Este es el JSON en el que especificamos los parámetros de la búsqueda
            queryJson = "{\n"+
                            "\"correousu\":\"" + correo + "\",\n" +
                            "\"nombre\":\"" + nombre + "\",\n" +
                            "\"fechaNac\":\""+ fechaNac + "\",\n" +
                            "\"primUbic\":\"" + "37.761533, -3.798992" + "\",\n" +
                            "\"discapaz\":\"" + 0 + "\"\n" +
                        "}";
            jsonObject = new JSONObject(queryJson);
        }catch (JSONException jerr){
            Log.d("Error: ", jerr.toString());
        }
        //Creamos el body con el JSON
        RequestBody body = RequestBody.create(okhttp3.MediaType
                .parse("application/json; charset=utf-8"),(jsonObject.toString()));
        //Realizamos la llamada mediante la API
        Call<RespuestaU> call = searchAPI.postUserReg(headerMap, body);
        call.enqueue(new Callback<RespuestaU>() {
            @Override
            public void onResponse(Call<RespuestaU> call, Response<RespuestaU> response) {
//                Example example;
//                Aggregations aggregations;
//                MyAgg myAgg;
//                Hits hits = new Hits();
//                Hit hit = new Hit();
                RespuestaU respuestaU = new RespuestaU();
                String jsonResponse;
                try{
                    Log.d(TAG, "onResponse: server response: " + response.toString());
                    //Si la respuesta es satisfactoria
                    if(response.isSuccessful()){
                        Log.d(TAG, "repsonseBody: "+ response.body().toString());
                        System.out.println(respuestaU.toString());
                        System.out.println(respuestaU.getIndex());
//                        example = response.body();
//                        aggregations = example.getAggregations();
//                        myAgg = aggregations.getMyAgg();
//                        hits = myAgg.getHits();
                        Log.d(TAG, " -----------onResponse: la response: " + response.body()
                                .toString());
                    }else{
                        jsonResponse = response.errorBody().string(); //error response body
                        System.out.println("Response body: " + jsonResponse);
                    }

                    /*Log.d(TAG, "onResponse: hits: " + hits.getHits().toString());

                    for(int i = 0; i < hits.getHits().size(); i++){
                        Log.d(TAG, "onResponse: data: " + hits.getHits()
                                .get(i).getSource().toString());
                        //mMedicion.add(hits.getHits().get(i).getSource());
                    }

                    Log.d(TAG, "onResponse: size: ");*/


                }catch (NullPointerException e){
                    Log.e(TAG, "onResponse: NullPointerException: " + e.getMessage() );
                }
                catch (IndexOutOfBoundsException e){
                    Log.e(TAG, "onResponse: IndexOutOfBoundsException: " + e.getMessage() );
                }
                catch (IOException e){
                    Log.e(TAG, "onResponse: IOException: " + e.getMessage() );
                }
            }

            @Override
            public void onFailure(Call<RespuestaU> call, Throwable t) {
                Log.e(TAG, "onFailure del POST usuario registrado ");
                System.out.println("El throwable: " + t);
            }
        });

    }

}