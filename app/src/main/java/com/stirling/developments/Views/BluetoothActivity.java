package com.stirling.developments.Views;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.stirling.developments.Models.HitsLists.HitsListC;
import com.stirling.developments.Models.POJOs.RespuestaB;
import com.stirling.developments.Models.POJOs.RespuestaU;
import com.stirling.developments.R;
import com.stirling.developments.Models.BluetoothLE;
import com.stirling.developments.Utils.BluetoothLEHelper;
import com.stirling.developments.Utils.BleCallback;
import com.stirling.developments.Utils.Constants;
import com.stirling.developments.Utils.ElasticSearchAPI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Credentials;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

public class BluetoothActivity extends AppCompatActivity {

    SharedPreferences preferences;

    BluetoothLEHelper ble;

    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothSocket btsocket;

    private final UUID my_UUID = UUID.fromString("ba3f52c2-5caf-4f4d-9b2d-e981698856a7");
    private final static int REQUEST_ENABLE_BT = 1;
    private String obtenidaMACWiFi;
    private String obtenidaMACWiFiString="";

    private ListView dispEncontrados;
    private ListView dispVinculados;
    private ProgressBar progressBar2;
    private ProgressBar progressBar3;
    private ProgressBar progressBar32;
    private Button botonBuscar;
    private Button botonAceptar;
    private Button botonAceptar2;
    private Button botonPrueba;
    private PopupWindow popupWindow;
    private PopupWindow popupWindow2;
    private RelativeLayout relativeLayout;
    private EditText editText;
    private EditText editTextPass;
    private EditText editTextPass2;

    private OutputStream outputStream;
    private InputStream inputStream;

    private ArrayList<String> arListEncont;
    private ArrayList<String> arListEmparej;
    private ArrayList<BluetoothLE> arBLEEncont;

    private String wifiSSIDConectado;

    private String wifiSSIDIntrod;
    private String wifiPassIntrod;
    private String wifiPassIntrod2;

    private ArrayAdapter<String> arrayAdapterDispEncontrados;
    private ArrayAdapter<String> arrayAdapterDispEmparejados;

    BluetoothDevice selDevice;
    private BleCallback bleCallback;

    private String queryJson = "";
    private JSONObject jsonObject;
    private String mElasticSearchPassword = Constants.elasticPassword;
    private Retrofit retrofit;
    private ElasticSearchAPI searchAPI;

    FirebaseAuth auth;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getBaseContext().getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_bluetooth);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        ble = new BluetoothLEHelper(this);
        auth = FirebaseAuth.getInstance();
        email = auth.getCurrentUser().getEmail();

        //Verificamos que el Bluetooth esté encendido, y si no lo está pedimos encenderlo
        if (adapter == null || !adapter.isEnabled()) {
            Intent enableBtIntent =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        //Inicializamos la API
        inicializarAPI();

        arListEncont = new ArrayList<String>();

        //Inicializamos elementos de la interfaz
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar2);
        botonBuscar = (Button) findViewById(R.id.buscarButton);
        botonPrueba = (Button) findViewById(R.id.botnPrueba);
        botonBuscar.setVisibility(View.VISIBLE);
        progressBar2.setVisibility(View.GONE);

        //Pedimos permisos en runtime, aparte de en el AndroidManifest, en caso de ser necesario
        solicitarPermisos();

        //Inicializamos list views para poder ir añadiendo
        dispEncontrados = (ListView) findViewById(R.id.listView1);
        dispVinculados = (ListView) findViewById(R.id.listView2);

        //Adapters para poder pasar a las ListViews desde arrays
        arrayAdapterDispEncontrados = new ArrayAdapter<String>(this,
                R.layout.text1, arListEncont);
        arrayAdapterDispEmparejados = new ArrayAdapter<String>(this,
                R.layout.text1, arListEmparej);

        //Hacemos visible nuestro dispositivo
        hacerVisible();

        bleCallback = new BleCallback(){

            @Override
            public void onBleConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onBleConnectionStateChange(gatt, status, newState);

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    runOnUiThread(() -> Toast.makeText(BluetoothActivity.this,
                            "Connected to GATT server.", Toast.LENGTH_SHORT).show());
                }

                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    runOnUiThread(() -> Toast.makeText(BluetoothActivity.this,
                            "Disconnected from GATT server.", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onBleServiceDiscovered(BluetoothGatt gatt, int status) {
                super.onBleServiceDiscovered(gatt, status);
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    Log.e("Ble ServiceDiscovered","onServicesDiscovered received: "
                            + status);
                }
            }

            @Override
            public void onBleCharacteristicChange(BluetoothGatt gatt,
                                                  BluetoothGattCharacteristic characteristic) {
                super.onBleCharacteristicChange(gatt, characteristic);
                Log.i("BluetoothLEHelper","onCharacteristicChanged Value: "
                        + Arrays.toString(characteristic.getValue()));
            }

            @Override //Modificar para que devuelva un string
            public void onBleRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
                                  int status) {
                super.onBleRead(gatt, characteristic, status);

                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.i("TAG", Arrays.toString(characteristic.getValue()));
                    runOnUiThread(() -> Toast.makeText(BluetoothActivity.this,
                            "onCharacteristicRead : "+Arrays.toString(characteristic
                                    .getValue()), Toast.LENGTH_SHORT).show());

                    //Intentar obtener una dirección MAC escrita en hexadecimal
                    obtenidaMACWiFi = Arrays.toString(characteristic.getValue());
                    //obtenidaMACWiFi = obtenidaMACWiFi.substring(obtenidaMACWiFi.length()-70);
                    obtenidaMACWiFi = obtenidaMACWiFi.substring(1,70);
                    obtenidaMACWiFi = obtenidaMACWiFi.replaceAll(" ","");

                    String[] parts = obtenidaMACWiFi.split(",");
                    for(int i = 0; i < parts.length ; i++){
                        byte[] bytes = {};
                        String tradHex = Integer.toHexString(Integer.parseInt(parts[i]));
                        System.out.println("toHexString ---> "+ tradHex);
                        try {
                            bytes = Hex.decodeHex(tradHex);
                        } catch (DecoderException e) {
                            e.printStackTrace();
                        }

                        String tradASCII = new String(bytes); //hexChar
                        System.out.println("tradASCII -------> "+ tradASCII);
                        obtenidaMACWiFiString = obtenidaMACWiFiString + tradASCII;
                    }
                    System.out.println("============= MAC WiFi en bytes =====> " + obtenidaMACWiFi);
//
                    // obtenidaMACWiFiString = Hex.encodeHexString(obtenidaMACWiFi);
                    System.out.println("===== MAC WiFi en String ====> "+ obtenidaMACWiFiString);
                    //Introducimos en sharedPreferences la dirección MAC WiFi del módulo obtenida
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("dirMACWiFi", obtenidaMACWiFiString);
                    editor.commit();
                    //editor.apply();
                    //agregarANavMenu(obtenidaMACWiFiString);
                    //verificación
                    String loQueHeMetido = preferences.getString("dirMACWiFi","");
                    System.out.println("Obtenido de sharPref: "+ loQueHeMetido);
                }
            }

            @Override
            public void onBleWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
                                   int status) {
                super.onBleWrite(gatt, characteristic, status);
                runOnUiThread(() -> Toast.makeText(BluetoothActivity.this,
                        "onCharacteristicWrite Status : " + status, Toast.LENGTH_SHORT).show());
            }
        };

        botonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtenemos la SSID del WiFi al que se está conectado
                wifiSSIDConectado = getWifiConectado();
                System.out.println("WiFi Conectada a: " + wifiSSIDConectado);
                //Limpiamos la lista de dispositivos encontrados
                arListEncont.clear();
                //Comienza la búsqueda, mostrar diálogo de progreso
                progressBar2.setVisibility(View.VISIBLE);
                botonBuscar.setVisibility(View.GONE);

                //Comenzar a buscar dispositivos
                //adapter.startDiscovery();
                if(ble.isReadyForScan()){
                    Handler mHandler = new Handler();
                    ble.scanLeDevice(true);

                    mHandler.postDelayed(() -> { //postDelayed //AtTime()

                        //--The scan is over, you should recover the found devices.
                        //La búsqueda finaliza, cerramos diálogo de progreso
                        progressBar2.setVisibility(View.GONE);
                        botonBuscar.setVisibility(View.VISIBLE);
                        /*System.out.println("----------------------Refrescar antes del Log found");
                        arrayAdapterDispEncontrados.notifyDataSetChanged();
                        dispEncontrados.refreshDrawableState();*/
//                        dispEncontrados.setAdapter(arrayAdapterDispEncontrados);


                        Log.v("Devices found: ", String.valueOf(ble.getListDevices()));
                        //Mi lista
                        arBLEEncont = ble.getListDevices();
                        //Ahora pasamos de una lista de dispositivos bluetooh
                        //a una lista de Strings para poder mostrarla en el ListView
                        deDeviceAString(arBLEEncont); //arListEncont =
                        System.out.println("+++++++++ Antes ");
//                        arrayAdapterDispEncontrados.notifyDataSetChanged();

                    }, ble.getScanPeriod()); //SystemClock.uptimeMillis()+1000); //ble.getScanPeriod()
                    dispEncontrados.setAdapter(arrayAdapterDispEncontrados);

//                    System.out.println("++++++++++Después");
//                    dispEncontrados.setAdapter(arrayAdapterDispEncontrados);
//                    arrayAdapterDispEncontrados.notifyDataSetChanged();

                }
                // Register for broadcasts when a device is discovered.
                IntentFilter filter = new IntentFilter();
                filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                filter.addAction(BluetoothDevice.ACTION_FOUND);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                //registerReceiver(mReceiver, filter);
                //Añadir aquí a la lista de dispositivos encontrados? No, aquí no

            }
        });

        dispEncontrados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Si pulsamos un item de la lista, cancelamos la búsqueda
                //adapter.cancelDiscovery();
                //Detenemos la animación de la progressbar
                progressBar2.setVisibility(View.GONE);
                final String info = ((TextView) view).getText().toString();

                //Obtener la dirección MAC del dispositivo cuando hacemos click en él
                String dirMAC = info.substring(info.length()-17);

                //Conectar al dispositivo
                //BluetoothDevice device = adapter.getRemoteDevice(dirMAC);
                for (BluetoothLE bte : arBLEEncont){
                    if(bte.getMacAddress().equals(dirMAC)){
                        selDevice = bte.getDevice();
                        break;
                    }
                }
                ble.connect(selDevice,bleCallback);
                Log.i("Conectando con device","esto va después de ble.connect");
                /*try {
                    btsocket = device.createInsecureRfcommSocketToServiceRecord(my_UUID);
                    btsocket.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("----Catch del socket connection: " +e);
                }*/

                //Comprobamos conexión con el dispositivo
                checkIfBleIsConnected(ble);

                //Si está conectado a WiFi sólo pedimos password, si no, pedimos tod o
                if(conectadoWiFi()){
                    popUpSolicitarPass();
                }else{
                    //Solicitamos SSID WiFi y password al usuario mediante un pop-up
                    popUpSolicitar();
                }
            }
        });

        botonPrueba.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                borrarLaCazuela("11:11:11", "a@a.com");
            }
        });
    }

    private void inicializarAPI(){
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_ELASTICSEARCH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        searchAPI = retrofit.create(ElasticSearchAPI.class);
    }

    private String getWifiConectado() {
        if(conectadoWiFi()){
            WifiManager wifiManager = (WifiManager) getSystemService (Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo ();
            String ssid  = info.getSSID();
            if (ssid.startsWith("\"") && ssid.endsWith("\"")){
                ssid = ssid.substring(1, ssid.length()-1);
            }
            return ssid;
        }else{
            return null;
        }
    }

    private boolean conectadoWiFi(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi.isConnected()) {
            return true;
        }else{
            return false;
        }
    }

    //Transformar disp. Bluetooth a información en String
    private ArrayList<String> deDeviceAString(ArrayList<BluetoothLE> arrayEncBTDevice){
        ArrayList<String> arrayEncString = new ArrayList<String>();
        for(BluetoothLE bluetoothLE : arrayEncBTDevice){
            String aString = bluetoothLE.getName() +"\n"+bluetoothLE.getMacAddress();
            //arrayEncString.add(aString);
            arListEncont.add(aString);
        }
        dispEncontrados.setAdapter(arrayAdapterDispEncontrados);
        //arrayAdapterDispEncontrados.notifyDataSetChanged();
        return arrayEncString;
    }

    //Hacemos nuestro dispositivo visible a otros dispositivos Bluetooth
    private void hacerVisible() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 400);
        startActivity(discoverableIntent);
        Log.i("Log", "Discoverable ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Des-registramos el receptor ACTION_FOUND.
        /*if(adapter != null){
            adapter.cancelDiscovery();
        }*/
       // unregisterReceiver(mReceiver);

        //Desconectamos el dispositivo BLE
        ble.disconnect();
    }

    private void checkIfBleIsConnected(BluetoothLEHelper bluetoothLEHelper){
        if(bluetoothLEHelper.isConnected()){
            Log.i("isConnected","---->El dispositivo está conectado<----");
        }else{
            Log.i("isConnected", "--->El dispositivo no está conectado<---");
        }
    }

    private void agregarANavMenu(String dirmac){
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        Menu m = navView.getMenu();
        SubMenu menuGroup = m.addSubMenu("Lista de cazuelas");
        menuGroup.add(dirmac);
    }

    //Se genera y muestra un pop-up en el que introducir la contraseña de la red WiFi
    private void popUpSolicitarPass(){
        LayoutInflater layoutInflater = (LayoutInflater) BluetoothActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.popupcontr, null);

        botonAceptar2 = (Button) customView.findViewById(R.id.aceptarBtn2);
        editTextPass2 = (EditText) customView.findViewById(R.id.editTextPass2);
        progressBar32 = (ProgressBar) customView.findViewById(R.id.progressBar32);
        progressBar32.setVisibility(View.GONE);

        //Instanciar la ventana pop-up
        popupWindow2 = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow2.setAnimationStyle(R.style.DialogAnimation);

        //Obtenemos la dirección MAC del WiFi del módulo
        obtenerMacModulo();
        //Mostrar la ventana pop-up
        popupWindow2.showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);
        //Cerrar la ventana pop-up al pulsar en el botón
        botonAceptar2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                progressBar32.setVisibility(View.VISIBLE);
                botonAceptar2.setVisibility(View.GONE);
                wifiPassIntrod2 = editTextPass2.getText().toString();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                popupWindow2.dismiss();
                progressBar32.setVisibility(View.GONE);
                botonAceptar2.setVisibility(View.VISIBLE);

                mandarWiFiaModulo(getWifiConectado(), wifiPassIntrod2);
            }
        });
    }

    //Se genera y muestra un pop-up en el que introducir información acerca de la red WiFi
    private void popUpSolicitar(){
        //Instanciar el archivo de layout popup.xml
        LayoutInflater layoutInflater = (LayoutInflater) BluetoothActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.popup,null);

        botonAceptar = (Button) customView.findViewById(R.id.aceptarBtn);
        editText = (EditText) customView.findViewById(R.id.editText);
        editTextPass = (EditText) customView.findViewById(R.id.editTextPass);
        progressBar3 = (ProgressBar) customView.findViewById(R.id.progressBar3);
        progressBar3.setVisibility(View.GONE);

        //Instanciar la ventana pop up
        popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(R.style.DialogAnimation);

        //Obtenemos la dirección MAC del WiFi del módulo
        obtenerMacModulo();

        //Mostrar la ventana pop up
        popupWindow.showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);

        //Cerrar la ventana pop up al pulsa en el boton
        botonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar3.setVisibility(View.VISIBLE);
                botonAceptar.setVisibility(View.GONE);
                wifiSSIDIntrod = editText.getText().toString();
                wifiPassIntrod = editTextPass.getText().toString();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                popupWindow.dismiss();
                progressBar3.setVisibility(View.GONE);
                botonAceptar.setVisibility(View.VISIBLE);

                mandarWiFiaModulo(wifiSSIDIntrod, wifiPassIntrod);
            }
        });
    }

    //En este método se obtiene la dirección MAC del WiFi del módulo
    private void obtenerMacModulo(){
        if(ble.isConnected()){
            ble.read(Constants.SERVICE_UUID,Constants.SEND_WIFI_MAC_UUID);
           // bleCallback.onBleRead();
        }else{
            Log.e("obtenerMacModulo","ble no está conectado!!");
            System.out.println("ble no conectado!");
        }
    }

    //En este método se enviará la información de la red WiFi al módulo
    private void mandarWiFiaModulo(String SSID, String pass){
        if(ble.isConnected()){
            ble.write(Constants.SERVICE_UUID, Constants.SSID_CHARACTERISTIC_UUID,SSID);
            Log.i("Enviar info Wifi:", "Enviada SSID");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ble.write(Constants.SERVICE_UUID, Constants.PASSWORD_CHARACTERISTIC_UUID,pass);
            Log.i("Enviar info Wifi:", "Enviada contraseña");
//            busquedaEntrada(obtenidaMACWiFi,email);
            borrarLaCazuela(obtenidaMACWiFi, email);
        }
    }

    //Buscamos en cazuelas_sukaldatzen alguna entrada con MAC y correo para eliminarla.
    //Este método se ejecuta al sincronizar por Bluetooth una olla, para evitar duplicidades
    /*public void busquedaEntrada(String mac, String correo){

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", Credentials.basic("android",
                mElasticSearchPassword));
        String searchString = "";
        try {
            queryJson = "{\n" +
                        "  \"query\":{\n" +
                        "    \"bool\":{\n" +
                        "      \"must\":[\n" +
                        "        {\n" +
                        "          \"match\":{\n" +
                        "            \"idMac\":\""+ mac +"\"\n" +
                        "          }\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"match\":{\n" +
                        "            \"correousu\":\""+ correo +"\"\n" +
                        "          }\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";
            jsonObject = new JSONObject(queryJson);
        }catch (JSONException jerr){
            Log.d("Error: ", jerr.toString());
        }
        //Creamos el body con el JSON
        RequestBody body = RequestBody.create(okhttp3.MediaType
                .parse("application/json; charset=utf-8"),(jsonObject.toString()));
        Call<HitsObjectC> call = searchAPI.searchCazuela(headerMap, body);

        call.enqueue(new Callback<HitsObjectC>() {
            @Override
            public void onResponse(Call<HitsObjectC> call, Response<HitsObjectC> response) {
                HitsListC hitsList = new HitsListC();
                String jsonResponse = "";
                try{
                    Log.d(TAG, "onResponse: server response: " + response.toString());

                    if(response.isSuccessful()){
                        hitsList = response.body().getHits();
                        Log.d(TAG, " -----------onResponse: la response: "+response.body()
                                .toString());
                    }else{
                        jsonResponse = response.errorBody().string(); //error response body
                    }

                    Log.d(TAG, "onResponse: hits: " + hitsList);

                    for(int i = 0; i < hitsList.getCazuelaIndex().size(); i++){
                        Log.d(TAG, "onResponse: data: " + hitsList.getCazuelaIndex().get(i)
                                .getCazuela().toString());
                    }
                    //Si existe hay que llamar a un método que borre y luego otro que introduzca
                    if (hitsList.getCazuelaIndex().size() == 0){
                        //habría que borrar la entrada que se ha encontrado, no necesitamos esta
                        //llamada y otra para borra la entrada
                        //prueba de momento
                        borrarLaCazuela(mac, correo);
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
            public void onFailure(Call<HitsObjectC> call, Throwable t) {

            }
        });
    }*/
    //por comprobar func. de la API
    public void borrarLaCazuela(String mac, String correo){
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", Credentials.basic("android",
                mElasticSearchPassword));
        try{
            queryJson = "{\n" +
                        "  \"query\":{\n" +
                        "    \"bool\":{\n" +
                        "      \"must\":[\n" +
                        "        {\n" +
                        "          \"match\":{\n" +
                        "            \"idMac\":\""+ mac +"\"\n" +
                        "          }\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"match\":{\n" +
                        "            \"correousu\":\""+ correo +"\"\n" +
                        "          }\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";
            jsonObject = new JSONObject(queryJson);
        }catch (JSONException jerr){
            Log.d("Error: ", jerr.toString());
        }
        //Creamos el body con el JSON
        RequestBody body = RequestBody.create(okhttp3.MediaType
                .parse("application/json; charset=utf-8"),(jsonObject.toString()));
        Call<RespuestaB> call = searchAPI.deleteCazuela(headerMap, body);

        call.enqueue(new Callback<RespuestaB>() {
            @Override
            public void onResponse(Call<RespuestaB> call, Response<RespuestaB> response) {
                HitsListC hitsList = new HitsListC();
                String jsonResponse = "";
                try{
                    Log.d(TAG, "onResponse borrar cazuela: server response: "
                            + response.toString());

                    if(response.isSuccessful()){
                        //hitsList = response.body().getHits();
                        Log.d(TAG, " onResponse borrar cazuela: response body: "+response.body()
                                .toString());
                    }else{
                        jsonResponse = response.errorBody().string(); //error response body
                    }

                    Log.d(TAG, "onResponse borrar cazuela: data: " );

                    addCazuelaUsuario(mac, correo);
                }catch (NullPointerException e){
                    Log.e(TAG, "onResponse borrarCaz: " +
                            "NullPointerException: " + e.getMessage() );
                }
                catch (IndexOutOfBoundsException e){
                    Log.e(TAG, "onResponse borrarCaz:" +
                            " IndexOutOfBoundsException: " + e.getMessage() );
                }
                catch (IOException e){
                    Log.e(TAG, "onResponse borrarCaz: IOException: " + e.getMessage() );
                }
            }

            @Override
            public void onFailure(Call<RespuestaB> call, Throwable t) {

            }
        });

    }

    public void addCazuelaUsuario(String mac, String correo){
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", Credentials.basic("android",
                mElasticSearchPassword));
        try {
            queryJson = "{\n" +
                    "  \"idMac\":\""+ mac +"\",\n" +
                    "  \"nombreCazuela\":\""+ mac +"\",\n" +
                    "  \"correousu\":\""+ correo +"\",\n" +
                    "  \"dueno\":true\n" +
                    "}";
            jsonObject = new JSONObject(queryJson);
        }catch (JSONException jerr){
            Log.d("Error: ", jerr.toString());
        }
        //Creamos el body con el JSON
        RequestBody body = RequestBody.create(okhttp3.MediaType
                .parse("application/json; charset=utf-8"),(jsonObject.toString()));
        //Realizamos la llamada mediante la API
        Call<RespuestaU> call = searchAPI.postCazuela(headerMap, body);
        call.enqueue(new Callback<RespuestaU>() {
            @Override
            public void onResponse(Call<RespuestaU> call, Response<RespuestaU> response) {
                String jsonResponse = "";
                try{
                    Log.d(TAG, "onResponse addcazuela: server response: " + response.toString());
                    //Si la respuesta es satisfactoria
                    if(response.isSuccessful()){
                        Log.d(TAG, "repsonseBody addcazuela: "+ response.body().toString());
                        Log.d(TAG, " --onResponse addcazuela: la response: "+response.body()
                                .toString());
                    }else{
                        jsonResponse = response.errorBody().string(); //error response body
                    }
                    Log.d(TAG, "onResponse add cazuela: ok ");

                }catch (NullPointerException e){
                    Log.e(TAG, "onResponse addcazuela: NullPointerException: " + e.getMessage() );
                }
                catch (IndexOutOfBoundsException e){
                    Log.e(TAG, "onResponse addcazuela: IndexOutOfBoundsException: " + e.getMessage() );
                }
                catch (IOException e){
                    Log.e(TAG, "onResponse addcazuela: IOException: " + e.getMessage() );
                }
            }

            @Override
            public void onFailure(Call<RespuestaU> call, Throwable t) {

            }
        });
    }

    /*private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //Limpiamos la lista de dispositivos encontrados
                arListEncont.clear();
                //Comienza la búsqueda, mostrar diálogo de progreso
                progressBar2.setVisibility(View.VISIBLE);
                botonBuscar.setVisibility(View.GONE);
                Log.i("BT", ">----->BT: Comienza búsqueda <-----<");

            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //La búsqueda finaliza, cerramos diálogo de progreso
                progressBar2.setVisibility(View.GONE);
                botonBuscar.setVisibility(View.VISIBLE);
                //En caso de no encontrar dispositivos que se muestre el siguiente mensaje
                if (arrayAdapterDispEncontrados.getCount() == 0) {
                    String noDevices = "Ningún dispositivo encontrado";
                    arListEncont.add(noDevices);
                }
                Log.i("BT", ">----->BT: Finaliza la búsqueda <-----<");

            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Boolean repetido = false;
                Boolean esNull = false;
                //Dispositivo bluetooth encontrado
                BluetoothDevice device = (BluetoothDevice) intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //Obtenemos el nombre del dispositivo
                String deviceName = device.getName();
                //Obtenemos dirección MAC
                String deviceHWAddress = device.getAddress();
                Log.i("BT", ">----->----->BT: " + deviceName
                        + "\n" + deviceHWAddress);

                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (device.getName() == null) {
                        //Si es null nada
                    } else {//Si no, comprobar que no esté repetido
                        for (String x : arListEncont) {
                            if (x.equals(device.getName() + "\n" + device.getAddress())) {
                                repetido = true;
                                break;//Si está repetido paramos el for
                            }
                        }
                        if (!repetido && !esNull) { //Si no está repetido, añadimos a la lista para mostrar
                            arListEncont.add(deviceName + "\n" + deviceHWAddress);
                            arrayAdapterDispEncontrados.notifyDataSetChanged();
                        }
                    }
                }


            }
        }
    };*/

    //En este método se solicitan los permisos necesarios para utilizar
    // funcionalidades Bluetooth
    private void solicitarPermisos() {
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // Only ask for these permissions on runtime when running Android 6.0 or higher
            switch (ContextCompat.checkSelfPermission(getBaseContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                case PackageManager.PERMISSION_DENIED:
                    ((TextView) new AlertDialog.Builder(this)
                            .setTitle("Runtime Permissions up ahead")
                            .setMessage(Html.fromHtml("<p>Para ver dispositivos " +
                                    "bluetooth cercanos pulse \"Permitir\" en el popup de " +
                                    "permisos.</p><p>Para más información " +
                                    " <a href=\"http://developer.android.com/about/versions/" +
                                    "marshmallow/android-6.0-changes.html#behavior-hardware-id\">" +
                                    "pulse aquí.</a>.</p>"))
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (ContextCompat.checkSelfPermission(getBaseContext(),
                                            Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                            PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(BluetoothActivity.this,
                                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                                    }
                                }
                            })
                            .show()
                            .findViewById(android.R.id.message))
                            .setMovementMethod(LinkMovementMethod.getInstance());       // Make the link clickable. Needs to be called after show(), in order to generate hyperlinks
                    break;
                case PackageManager.PERMISSION_GRANTED:
                    break;
            }
        }
    }
}


