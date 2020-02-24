package com.stirling.developments.Views;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.stirling.developments.R;
import com.stirling.developments.Utils.Constants;
import com.stirling.developments.Utils.ElasticSearchAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Credentials;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    //get access to location permission
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private final Context mContext = this;
    private Location mlocation;
    private double latitude;
    private double longitude;
    private String coordsGPS = "";

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

        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
        mlocation = location;
        latitude = mlocation.getLatitude();
        longitude = mlocation.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Status Changed", String.valueOf(status));
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("Provider Enabled", provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Provider Disabled", provider);
            }
        };

        // Now first make a criteria with your requirements
        // this is done to save the battery life of the device
        // there are various other other criteria you can search for..
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        // Creamos un location manager
        final LocationManager locationManager = (LocationManager)getSystemService(
                Context.LOCATION_SERVICE);

        // Esto evita que esté obteniendo ubicación constantemente en segundo plano
        final Looper looper = null;

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(),android.Manifest.permission.ACCESS_COARSE_LOCATION )
                != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(getParent(),
                    new String [] { android.Manifest.permission.ACCESS_COARSE_LOCATION },
                    11
            );
        }
        locationManager.requestSingleUpdate(criteria, locationListener, looper);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        inputFechaNac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragmentDialog datePickerFragmentDialog = DatePickerFragmentDialog
                        .newInstance(new DatePickerFragmentDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerFragmentDialog v, int dayOfMonth,
                                                  int monthOfYear, int year) {
                                if(monthOfYear<9){//Si no hay 0 en meses de 1 cifra la BD rechaza
                                    if(dayOfMonth<=9){//y si también el día es de 1 cifra
                                        inputFechaNac.setText(year + "-0" +(monthOfYear + 1) +
                                                "-0" + dayOfMonth);
                                    }else{
                                        inputFechaNac.setText(year + "-0" + (monthOfYear + 1)
                                                + "-" + dayOfMonth);
                                    }
                                }else{//Si el mes es de 2 cifras
                                    if(dayOfMonth<=9){//y además el día es de 1 cifra
                                        inputFechaNac.setText( year + "-" + (monthOfYear + 1)+
                                                "-0" + dayOfMonth);
                                    }else{
                                        inputFechaNac.setText(year + "-" + (monthOfYear + 1)
                                                + "-" + dayOfMonth);
                                    }
                                }
                            }
                        }, 2000, 01, 11);
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
                if (TextUtils.isEmpty(nombre)) {
                    Toast.makeText(getApplicationContext(), "Introduce un nombre"
                            , Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(fecha)) {
                    Toast.makeText(getApplicationContext(), "Introduce una fecha de" +
                            "nacimiento", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                //Creamos el usuario en el gestor de cuentas de Firebase
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this,
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Toast.makeText(SignupActivity.this,
                                                "createUserWithEmail:onComplete:" +
                                                        task.isSuccessful(),
                                                Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        // If sign in fails, display a message to the user. If sign in succeeds
                                        // the auth state listener will be notified and logic to handle the
                                        // signed in user can be handled in the listener.
                                        if (!task.isSuccessful()) { //En caso de error o correo existente
                                            Toast.makeText(SignupActivity.this,
                                                    "Authentication" + " failed." +
                                                            task.getException(),
                                                    Toast.LENGTH_SHORT).show();
                                            Log.i("Response", "Failed to create user: "
                                                    + task.getException().getMessage());

                                        } else {
                                            //I: Registro correcto --> enviar email verificación??

                                        }
                                    }
                                });
                //Obtenemos la ubicación del smartphone
                coordsGPS = latitude +", " + longitude;
                Log.i("ubic", "ººººººCoords introducidas en BD:ººººººººº "+ coordsGPS);
                //Introducimos informacion en nuestra base de datos, no en firebase
                nuevoUsuario(email, nombre, fecha, coordsGPS);

            }

        });
        comprobarPermisos();
    }



    private void comprobarPermisos() {
        if ( Build.VERSION.SDK_INT >= 23){
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED  ){
                        requestPermissions(new String[]{
                                        android.Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_CODE_ASK_PERMISSIONS);
                return ;
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    obtenerCoords();
                } else {
                    // Permission Denied
                    Toast.makeText( this,"No hay permiso para utilizar el GPS" ,
                            Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

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
    private void nuevoUsuario(String correo, String nombre, String fechaNac, String coords){

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
                            "\"primUbic\":\"" + coords + "\",\n" +
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
                RespuestaU respuestaU = new RespuestaU();
                String jsonResponse;
                try{
                    Log.d(TAG, "onResponse: server response: " + response.toString());
                    //Si la respuesta es satisfactoria
                    if(response.isSuccessful()){
                        Log.d(TAG, "repsonseBody: "+ response.body().toString());
                        System.out.println(respuestaU.toString());
                        System.out.println(respuestaU.getIndex());
                        Log.d(TAG, " -----------onResponse: la response: " + response.body()
                                .toString());
                    }else{
                        jsonResponse = response.errorBody().string(); //error response body
                        System.out.println("Response body: " + jsonResponse);
                    }
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