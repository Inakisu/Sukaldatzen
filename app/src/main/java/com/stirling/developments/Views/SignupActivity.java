package com.stirling.developments.Views;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import java.util.Calendar;
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
    final Calendar calendario = Calendar.getInstance();

    private final Context mContext = this;
    private Location location;
    private double latitude;
    private double longitude;
    private Boolean act;
    private Boolean gpsStatus;
    private Boolean networkStatus;
    private String coordsGPS = "";
    private LocationManager locationManager;
    private android.location.LocationListener myLocationListener;

    private static final long tiempoMinimo = 1000 * 60 * 3; //3 minutos
    private static final long distanciaMinima = 500;
//    private LocationListener locationListener;

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

        obtenerCoords();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        inputFechaNac.setOnClickListener(new View.OnClickListener() {
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
                                if(monthOfYear<9){//Si no hay 0 en meses de 1 cifra la BD rechaza
                                    inputFechaNac.setText(dayOfMonth + "-0" + (monthOfYear + 1)
                                            + "-" + year);
                                }else{
                                    inputFechaNac.setText(dayOfMonth + "-" + (monthOfYear + 1)
                                            + "-" + year);
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
                                                    "Authentication" + " failed." + task.getException(),
                                                    Toast.LENGTH_SHORT).show();
                                            Log.i("Response", "Failed to create user: "
                                                    + task.getException().getMessage());

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
                nuevoUsuario(email, nombre, fecha, coordsGPS);

            }

        });
    }

    //Método obtención de coordenadas
    private void obtenerCoords() {
        String serviceString = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(serviceString);


        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            //No hay permisos, hay que pedirlos
            Log.e("Location", "No hay permisos de localización.1");
            System.out.println(" %%%% Return 1 - No permisos localización %%%%");
//            return;
            showSettingsAlert();
        }


        myLocationListener = new android.location.LocationListener() {
            public void onLocationChanged(Location locationListener) {

                if (gpsHabilitado(SignupActivity.this)) {
                    if (locationListener != null) {
                        if (ActivityCompat.checkSelfPermission(SignupActivity.this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(SignupActivity.this,
                                        android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                        PackageManager.PERMISSION_GRANTED) {
                            Log.e("Location", "No hay permisos de localización.2");
                            System.out.println(" %%%% Return 2 - No permisos localización %%%%");
                            return;
                        }

                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                } else if (internetConectado(SignupActivity.this)) {
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            //introducimos los valores en el string que se enviará junto al
                            //al resto de datos del usuario en el registro.
                            coordsGPS = latitude + "," + longitude;
                        }
                    }
                }
            }
            public void onProviderDisabled(String provider) {
            }
            public void onProviderEnabled(String provider) {
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };
    }

    //Comprobamos que el gps esté habilitado para poder obtener ubicación
    public boolean gpsHabilitado(Context mContext) {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    //Comprobamos que haya internet para localización mediante network
    public static boolean internetConectado(Context ctx) {
        ConnectivityManager connectivityMgr = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        //Comprobamos si el WiFi o los datos móviles están disponibles o no. Si alguno de ellos
        //está disponible o conectado devolverá true, si no, false.
        if (wifi != null) {
            if (wifi.isConnected()) {
                return true;
            }
        }
        if (mobile != null) {
            if (mobile.isConnected()) {
                return true;
            }
        }
        return false;
    }

    //Diálogo solicitando permisos para la utilización de la ubicación.
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        // Título del diálogo
        alertDialog.setTitle("Opciones GPS");
        // Mensaje del diálogo
        alertDialog.setMessage("Es necesario obtener ubicación GPS." +
                " ¿Desea ir al menú de configuración?");
        // Si se pulsa ir al menú de ajustes
        alertDialog.setPositiveButton("Ajustes ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        // Si se pulsa el botón cancelar
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        // Mostrar mensaje de alerta
        alertDialog.show();
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