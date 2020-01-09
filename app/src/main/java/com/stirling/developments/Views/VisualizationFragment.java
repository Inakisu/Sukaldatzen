package com.stirling.developments.Views;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.libRG.CustomTextView;
import com.stirling.developments.Models.POJOs.Cazuela;
import com.stirling.developments.Models.HitsLists.HitsList;
import com.stirling.developments.Models.HitsLists.HitsListC;
import com.stirling.developments.Models.HitsObjects.HitsObject;
import com.stirling.developments.Models.HitsObjects.HitsObjectC;
import com.stirling.developments.Models.POJOs.Usuario;
import com.stirling.developments.Models.gson2pojo.Aggregations;
import com.stirling.developments.Models.gson2pojo.Example;
import com.stirling.developments.Models.gson2pojo.Hit;
import com.stirling.developments.Models.gson2pojo.Hits;
import com.stirling.developments.Models.gson2pojo.MyAgg;
import com.stirling.developments.Models.gson2pojo.Source;
import com.stirling.developments.R;
import com.stirling.developments.Utils.Constants;
import com.stirling.developments.Utils.ElasticSearchAPI;
import com.stirling.developments.Utils.SwipeDetector;
//I: Search API de Elasticsearch
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.ContentValues.TAG;

public class VisualizationFragment extends Fragment
{
    private static final String BASE_URL = "http://10.128.0.104:9200/";

    static SharedPreferences sharedPreferences;
//    private static SharedPreferences.Editor editor = sharedPreferences.edit();


    private DatabaseReference usersReference, cazuelasReference;
    private ValueEventListener valueEventListener;
    private int currentPage;
    private ArrayList<Cazuela> cazuelas;
    private Cazuela currentCazuela;
    private String macCurrentCazuela;
    private String mIndice = "";
    private String mAccion = "";
    private String mElasticSearchPassword = Constants.elasticPassword;
    private ArrayList<Usuario> mUsuario; //Lista donde se guardan los términos que buscaremos //O las resp.
    private ArrayList<Cazuela> mCazuela; // Lista donde se almacenarán las respuestas de la query de las cazuelas
    private ArrayList<Source> mMedicion; // Medicion//Lista donde se almacenarán las respuestas de la query de las mediciones

    private FirebaseAuth mAuth;
    private Retrofit retrofit;
    private ElasticSearchAPI searchAPI;

    private String elCorreo = "";
    private String queryJson = "";
    private JSONObject jsonObject;

    private float tempOlla;
    private int lastX;

    private final static int INTERVAL = 3500;
    private LineGraphSeries<DataPoint> serie1;
    private LineGraphSeries<DataPoint> serie2;

    private PopupWindow popupWindow;
    private Button botonAceptar;
    private Button botonCancelar;


    @BindView(R.id.cazuelaMAC) TextView tvMAC;
    @BindView(R.id.cazuelaStatus) TextView tvStatus;
    @BindView(R.id.pageIndicador) TextView tvPageIndicator;
    @BindView(R.id.timeAlarm) TextView timeAlarm;
    @BindView(R.id.temperatureThreshold) TextView temperatureThreshold;
    @BindView(R.id.temperatureIndicator) CustomTextView tvTemperature;
    @BindView(R.id.alarmTemperatureSeekbar) org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
            seekBarTemp;
    @BindView(R.id.alarmTimeSeekbar) org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
            seekBarTime;
    @BindView(R.id.bSetAlarm) TextView bSetTemperatureAlarm;
    @BindView(R.id.bSetTime) TextView bSetTimeAlarm;
    @BindView(R.id.alarmLayout) LinearLayout llAlarm;
//    @BindView(R.id.textNombreCazuela) TextView nombreCazuela;
    @BindView(R.id.graph) GraphView graphView;
//    @BindView(R.id.botonGrafico) Button botonGraf;
    @BindView(R.id.switchGrafico) Switch switchGraph;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        currentPage = 0;
        currentCazuela = new Cazuela();
        return inflater.inflate(R.layout.visualization_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        sharedPreferences = this.getActivity().getSharedPreferences("navprefs",
//                Context.MODE_PRIVATE);
        ButterKnife.bind(this, view);
        lastX = 0;
        serie1 = new LineGraphSeries<>(); //añadirle las temperaturas anteriores a la actual
        serie2 = new LineGraphSeries<>();
        mMedicion = new ArrayList<Source>(); //Mediciones

        //Inicializamos la API de elasticsearch
        inicializarAPI();

        //Obtenemos el correo electrónico del usuario
        mAuth = FirebaseAuth.getInstance();
        elCorreo = mAuth.getCurrentUser().getEmail();

        obtenerCazuelasUsuario();
        listarCazuelasUI(); //esto va a sobrar, ya verás, te lo digo yo
        //enPrueba();
        actualizarTemperatura();

        //Inicializamos el gráfico
        iniciarGrafico(graphView);
        //..y lo ocultamos
        graphView.setVisibility(View.GONE);

        //Inicializamos contador de cazuelas si las hay
        if(mCazuela.size() > 0){
            tvPageIndicator.setText("Cazuela " + (currentPage + 1) + " de " +
                    mCazuela.size());
        }

        final Handler handler = new Handler();
        /* your code here */
        new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 2 * 1000); // every 2 seconds
                //lo que queremos que haga cada dos segundos
                actualizarTemperatura();
//                actualizarGrafico();
            }
        }.run();

        //Creamos el handler para detectar swipes
        new SwipeDetector(view).setOnSwipeListener(new SwipeDetector.onSwipeEvent() {
            @Override
            public void SwipeEventDetected(View v, SwipeDetector.SwipeTypeEnum swipeType) {
                if(swipeType==SwipeDetector.SwipeTypeEnum.LEFT_TO_RIGHT){
                    if(currentPage > 0){
                        currentPage--;
                        currentCazuela = mCazuela.get(currentPage); //cazuelas
                        tvTemperature.setText(" "); //Para que no aparezca la última tª de la caz anterior
                        tvPageIndicator.setText("Cazuela " + (currentPage + 1) + " de " +
                                mCazuela.size());
                        goToAppropriateCazuela();
                        actualizarTemperatura(); //para que se actualice al instante de cambiar
                    }else{
                        Log.i("Swipe IaD: ", "No hay más pantallas hacia ese lado");
                    }
                }
                if(swipeType==SwipeDetector.SwipeTypeEnum.RIGHT_TO_LEFT){
                    if(currentPage < mCazuela.size() - 1){
                        currentPage++;
                        currentCazuela = mCazuela.get(currentPage); //cazuela
                        tvTemperature.setText(" "); //Para que no aparezca la última tª de la caz anterior
                        tvPageIndicator.setText("Cazuela " + (currentPage + 1) + " de " +
                                mCazuela.size());
                        goToAppropriateCazuela();
                        actualizarTemperatura(); // para que se actualice al instante de cambiar
                    }else{
                        Log.i("Swipe DaI: ", "No hay más pantallas hacia ese lado");
                    }
                }
            }

        });
        //Set temperature alarm click listener
        bSetTemperatureAlarm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//              currentCazuela.setTemperatureAlarm(seekBarTemp.getProgress());
                temperatureThreshold.setText(Html.fromHtml("<b>Temperature limit: </b>" +
                        seekBarTemp.getProgress() + "ºC"));
            }
        });
        //Set temperature alarm click listener
        bSetTimeAlarm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                currentCazuela.setTimeAlarm(seekBarTime.getProgress());
                timeAlarm.setText(Html.fromHtml("<b>Time remaining:</b> " +
                        seekBarTime.getProgress() + "min."));
            }
        });
        //Controlamos si mostrar u ocultar el gráfico dependiendo del estado del switch
        switchGraph.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean oncendido) {
                if(oncendido)
                    graphView.setVisibility(View.VISIBLE);
                else
                    graphView.setVisibility(View.GONE);
            }
        });

    }

    //Cambio de color del círculo de temperatura
    public void actualizarColor(){

        int temp1 = getResources().getInteger(R.integer.tempVerdeMenorQue);
        int temp2 = getResources().getInteger(R.integer.tempAmarillaMayVerMenQue);
        int temp3 = getResources().getInteger(R.integer.tempRojaMayorQue);
        if(tempOlla < temp1){
            tvTemperature.setBackgroundColor(getContext().getColor(R.color.tempVerde));
        }else if(temp1 < tempOlla && tempOlla < temp2){
            tvTemperature.setBackgroundColor(getContext().getColor(R.color.tempAmarillo));
        }else if(tempOlla < temp3){
            tvTemperature.setBackgroundColor(getContext().getColor(R.color.tempRojo));
        }else{
            tvTemperature.setBackgroundColor(getContext().getColor(R.color.material_grey300));
        }
    }

    public void saveArrayList(ArrayList<Cazuela> list, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }


    private void actualizarTemperatura() {
        String macC = macCurrentCazuela;
        //Generamos un authentication header para identificarnos contra Elasticsearch
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", Credentials.basic("android",
                mElasticSearchPassword));
        String searchString = "";
        try {
            //Este es el JSON en el que especificamos los parámetros de la búsqueda
            queryJson = "{\n" +
                        "  \"query\":{ \n" +
                        "    \"bool\":{\n" +
                        "      \"must\": [\n" +
                        "        {\"match\": {\n" +
                        "          \"idMac\": \"" + macC + "\"\n" +
                        "          }\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  },\n" +
                        "  \"aggs\": {\n" +
                        "    \"myAgg\": {\n" +
                        "      \"top_hits\": {\n" +
                        "        \"size\": 2,\n" +
                        "        \"sort\": [\n" +
                        "          {\n" +
                        "            \"timestamp\":{\n" +
                        "              \"order\": \"desc\"\n" +
                        "            }\n" +
                        "          }]\n" +
                        "      }\n" +
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
        //Realizamos la llamada mediante la API
        Call<Example> call = searchAPI.searchHitsAgg(headerMap, body);
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                Example example;
                Aggregations aggregations;
                MyAgg  myAgg;
                Hits hits = new Hits();
                Hit hit = new Hit();
                String jsonResponse = "";
                try{
                    Log.d(TAG, "onResponse: server response: " + response.toString());
                    //Si la respuesta es satisfactoria
                    if(response.isSuccessful()){
                        Log.d(TAG, "repsonseBody: "+ response.body().toString());
                        example = response.body();
                        aggregations = example.getAggregations();
                        myAgg = aggregations.getMyAgg();
                        hits = myAgg.getHits();
                        Log.d(TAG, " -----------onResponse: la response: "+response.body()
                                .toString());
                    }else{
                        jsonResponse = response.errorBody().string(); //error response body
                    }

                    Log.d(TAG, "onResponse: hits: " + hits.getHits().toString());

                    for(int i = 0; i < hits.getHits().size(); i++){
                        Log.d(TAG, "onResponse: data: " + hits.getHits()
                                .get(i).getSource().toString());
                        mMedicion.add(hits.getHits().get(i).getSource());
                    }

                    Log.d(TAG, "onResponse: size: " + mMedicion.size());
                    //setup the list of posts

                    //Actualizamos temperatura con la última obtenida
                    String ta = hits.getHits().get(0).getSource().getTempsInt().toString();
                    tvTemperature.setText(ta + "ºC");
                    Log.i("Tª: ", "Temperatura actualizada: "+ ta + " ºC");
                    Log.i("Tª: ", "Temperatura tapa: " + hits.getHits().get(0).getSource()
                            .getTempsTapa().toString()+ " ºC");

                    //Actualizamos la MAC de la cazuela mostrada
                    String mac = hits.getHits().get(0).getSource().getIdMac(); //.toString()
                    tvMAC.setText(" " + mac + " ");

                    //Introducimos esta última temperatura en la segunda serie
                    String fjroi = hits.getHits().get(0).getSource().getTimestamp();
                    int taInt = hits.getHits().get(0).getSource().getTempsInt();
                    tempOlla = taInt;
                    lastX++;//sustituir por hora?
                    serie2.appendData(new DataPoint(lastX ,taInt),true,1000);
                    actualizarColor();

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
            public void onFailure(Call<Example> call, Throwable t) {

            }
        });
    }

    private void primerasTemps(){
        String macC = macCurrentCazuela;
        //Generamos un authentication header para identificarnos contra Elasticsearch
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", Credentials.basic("android",
                mElasticSearchPassword));
        String searchString = "";
        try {
            //Este es el JSON en el que especificamos los parámetros de la búsqueda
            queryJson = "{\n" +
                    "  \"query\":{ \n" +
                    "    \"bool\":{\n" +
                    "      \"must\": [\n" +
                    "        {\"match\": {\n" +
                    "          \"idMac\": \"" + macC + "\"\n" +
                    "          }\n" +
                    "        }\n" +
                    "      ]\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"aggs\": {\n" +
                    "    \"myAgg\": {\n" +
                    "      \"top_hits\": {\n" +
                    "        \"size\": 500,\n" +
                    "        \"sort\": [\n" +
                    "          {\n" +
                    "            \"timestamp\":{\n" +
                    "              \"order\": \"desc\"\n" +
                    "            }\n" +
                    "          }]\n" +
                    "      }\n" +
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
        //Realizamos la llamada mediante la API
        Call<Example> call = searchAPI.searchHitsAgg(headerMap, body);
        call.enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {
                Example example;
                Aggregations aggregations;
                MyAgg  myAgg;
                Hits hits = new Hits();
                Hit hit = new Hit();
                String jsonResponse = "";
                try{
                    Log.d(TAG, "onResponse: server response: " + response.toString());
                    //Si la respuesta es satisfactoria
                    if(response.isSuccessful()){
                        Log.d(TAG, "repsonseBody: "+ response.body().toString());
                        example = response.body();
                        aggregations = example.getAggregations();
                        myAgg = aggregations.getMyAgg();
                        hits = myAgg.getHits();
                        Log.d(TAG, " -----------onResponse: la response: "+response.body()
                                .toString());
                    }else{
                        jsonResponse = response.errorBody().string(); //error response body
                    }

                    Log.d(TAG, "onResponse: hits: " + hits.getHits().toString());

                    for(int i = 0; i < hits.getHits().size(); i++){
                        Log.d(TAG, "onResponse: data: " + hits.getHits()
                                .get(i).getSource().toString());
                        mMedicion.add(hits.getHits().get(i).getSource());
                    }

                    Log.d(TAG, "onResponse: size: " + mMedicion.size());
                    //setup the list of posts

                    //Actualizamos temperatura con la última obtenida
                    String ta = hits.getHits().get(0).getSource().getTempsInt().toString();
                    tvTemperature.setText(ta + "ºC");
                    Log.i("Tª: ", "Temperatura actualizada: "+ ta + " ºC");
                    Log.i("Tª: ", "Temperatura tapa: " + hits.getHits().get(0).getSource()
                            .getTempsTapa().toString()+ " ºC");

                    //Actualizamos la MAC de la cazuela mostrada
                    String mac = hits.getHits().get(0).getSource().getIdMac(); //.toString()
                    tvMAC.setText(" " + mac + " ");

                    //Introducimos las temperaturas anteriores en la primera serie
                    for (int i = 0; i <= mMedicion.size() ; i++){
                        lastX++;
                        //Añadir el array con las últimas X mediciones
                        serie1.appendData(new DataPoint(lastX, tempOlla),
                                true, 500);
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
            public void onFailure(Call<Example> call, Throwable t) {

            }
        });
    }

    private void iniciarGrafico(GraphView graph){
        //Configuramos el viewport
        Viewport viewport = graphView.getViewport();
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);//ver qué hacen estas historias
        viewport.setMinX(4);
        viewport.setMaxX(80);//ver qué hace esta historia
        viewport.setScalable(true);

        serie1.setTitle("Temperaturas anteriores");
        serie1.setAnimated(true);
        graphView.addSeries(serie1);

        serie2.setTitle("Temperaturas en directo");
        serie2.setAnimated(true);
        graphView.addSeries(serie2);

        lastX=0;
    }

    private void goToAppropriateCazuela()
    {
        macCurrentCazuela = currentCazuela.getIdMac();
        tvMAC.setText(macCurrentCazuela);
        tvStatus.setText(currentCazuela.getNombreCazuela());
    }

    /*
       Esta función detecta cuando se produce un cambio de página
   */
//    private ValueEventListener getValueEventListener()
//    {
//        valueEventListener =  new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
//            {
//                if(dataSnapshot != null)
//                {
//                    //Get most recent object
//                    Cazuela cazuela = dataSnapshot.getValue(Cazuela.class);
////                    currentCazuela.setTemperatura(cazuela.getTemperatura());
////                    currentCazuela.setEstado(cazuela.getEstado());
////                    currentCazuela.setMAC(cazuela.getMAC());
////                    currentCazuela.setNombre(cazuela.getNombre());
//
//                    //Aquí ojo con los null pointer exception
////                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Cazuela: "
////                            + currentCazuela.getNombre());
////                    tvTemperature.setText(currentCazuela.getTemperatura() + "ºC");
////                    tvPageIndicator.setText("Cazuela " + (currentPage + 1) + " de " + cazuelas.size());
////                    tvMAC.setText(Html.fromHtml("<b>MAC:</b> " + currentCazuela.getMAC()));
////                    tvStatus.setText(Html.fromHtml("<b>Status:</b> " +
////                            (currentCazuela.getEstado() == true ? "Running" : "Stopped")));
////                    temperatureThreshold.setText(Html.fromHtml("<b>Temp. Alarm:</b> " +
////                            (currentCazuela.getTemperatureAlarm() == 0 ? "Disabled" :
////                                    (currentCazuela.getTemperatureAlarm() + "ºC"))));
////                    timeAlarm.setText(Html.fromHtml("<b>Time remaining:</b> " +
////                            (currentCazuela.getTimeAlarm() == 0 ? "Disabled" :
////                                    (currentCazuela.getTimeAlarm() + "min"))));
//
////                    if(currentCazuela.getEstado() == true)
////                        tvTemperature.setBackgroundColor(currentCazuela.getTemperatura() < 100 ?
////                                getContext().getColor(R.color.colorPrimary)
////                            : currentCazuela.getTemperatura() > 150 ? getContext()
////                                .getColor(R.color.material_red500)
////                            : getContext().getColor(R.color.colorAccent));
////                    else
////                        tvTemperature.setBackgroundColor(getContext().getColor(R.color.material_grey500));
//
////                   if(currentCazuela.getTemperatura() > currentCazuela.getTemperatureAlarm()
////                           && currentCazuela.getTemperatureAlarm() > 0 &&
////                           currentCazuela.getTemperatureAlarmFired() == false)
////                   {
////                       Notifications.show(requireContext(), VisualizationFragment.class,
////                               "Temperature alarm", "Current temperature is: " +
////                                       currentCazuela.getTemperatura());
////                       currentCazuela.setTemperatureAlarmFired(true);
//                       tvTemperature.setBackgroundColor(getContext()
//                               .getColor(R.color.material_black));
//                   }
//
////                   if(currentCazuela.getEstado() == false) {
////                       llAlarm.setVisibility(View.GONE);
////                   }else {
////                       llAlarm.setVisibility(View.VISIBLE);
////                   }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                System.out.println("onCancelled del valueEventListener que detecta cuando hay un" +
//                        "cambio de página");
//            }
//        };
//
//        return valueEventListener;
//    }

    private void inicializarAPI(){
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_ELASTICSEARCH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        searchAPI = retrofit.create(ElasticSearchAPI.class);
    }

    private void obtenerCazuelasUsuario(){
        mCazuela = new ArrayList<Cazuela>();

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", Credentials.basic("android",
                mElasticSearchPassword));
        //elCorreo = "a@a.com"; //DE PRUEBA, HAY QUE BORRARLO AL ACABAR LAS PRUEBAS
        String searchString = "";
        try {
            queryJson = "{\n" +
                        "  \"query\":{\n" +
                        "    \"bool\":{\n" +
                        "      \"must\": [\n" +
                        "        {\"match\": {\n" +
                        "          \"correousu\": \"" + elCorreo + "\"\n" +
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
                        mCazuela.add(hitsList.getCazuelaIndex().get(i).getCazuela());
                    }
                    saveArrayList(mCazuela, "navprefs");


                    Log.d(TAG, "onResponse: size: " + mCazuela.size());
                    //setup the list of posts
                    currentPage = 0;
                    currentCazuela = mCazuela.get(currentPage);
                    macCurrentCazuela = currentCazuela.getIdMac();
                    Log.i("Obtener MAC cazuela", " ---> "+ macCurrentCazuela);
                    tvMAC.setText(macCurrentCazuela);
//                    addListaToShared("key",mCazuela); //metemos la lista de cazuelas en sharedPreferences
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
    }

  /*  public void addListaToShared(String key, List<Cazuela> list){
        Gson gson = new Gson();
        String json = gson.toJson(list);

        set(key, json);
    }*/
  /*  public static void set(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }*/

    public void enPrueba(){ //throws IOException
        //Prueba 3
        mUsuario = new ArrayList<Usuario>();
        //Constructor de Retrofit para conexión
//        Retrofit retrofit = new Retrofit.Builder() //BaseURL (lo que va antes del _search/ )
//                .baseUrl(Constants.URL_ELASTICSEARCH) //_search/ está en ESAPI
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        //Construir API
//        ElasticSearchAPI searchAPI = retrofit.create(ElasticSearchAPI.class);

        //Hasmap utilizado para el header, en el que irá la autenticación
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", Credentials.basic("android",
                mElasticSearchPassword));

        String searchString = "";

        /*if(!mIndice.equals("")){ //Texto a buscar en general
            searchString = searchString + mIndice+ "*"; //.getText().toString()
        }
        if(!mAccion.equals("")){ //Aquí asginaremos la acción que se desea realizar. _search o _doc
            searchString = searchString + "/" + mAccion;
        }*/

        //Escribimos sentencia JSON entexto y luego la pasamos a objetoJSON
        try {
            //elCorreo = "a@a.com";
            queryJson = "{\n" +
                        "  \"query\":{\n" +
                        "    \"bool\":{\n" +
                        "      \"must\": [\n" +
                        "        {\"match\": {\n" +
                        "          \"correousu\": \""+ elCorreo +"\"\n" +
                        "          }\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";
            jsonObject = new JSONObject(queryJson);
        }catch (JSONException err){
            Log.d("Error", err.toString());
        }
        //Creamos el body con el objeto JSON
        RequestBody body = RequestBody.create(okhttp3.MediaType
                .parse("application/json; charset=utf-8"),(jsonObject.toString()));

        //El método search que se ejecuta es el de la clase ElasticSearchAPI
        Call<HitsObject> call = searchAPI.searchUsuario(headerMap, body);

        call.enqueue(new Callback<HitsObject>() {
            @Override
            public void onResponse(Call<HitsObject> call, Response<HitsObject> response) {

                HitsList hitsList = new HitsList();
                String jsonResponse = "";
                try{
                    Log.d(TAG, "onResponse: server response: " + response.toString());

                    if(response.isSuccessful()){
                        hitsList = response.body().getHits();
                        Log.d(TAG, " -----------onResponse: la response: "+response.body()
                                .toString());
                    }else{
                        jsonResponse = response.errorBody().string();
                    }

                    Log.d(TAG, "onResponse: hits: " + hitsList);

                    for(int i = 0; i < hitsList.getUsuarioIndex().size(); i++){
                        Log.d(TAG, "onResponse: data: " + hitsList.getUsuarioIndex().get(i)
                                .getUsuario().toString());
                        mUsuario.add(hitsList.getUsuarioIndex().get(i).getUsuario());
                    }

                    Log.d(TAG, "onResponse: size: " + mUsuario.size());
                    //setup the list of posts

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
            public void onFailure(Call<HitsObject> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage() );
                Toast.makeText(getActivity(), "search failed", Toast.LENGTH_SHORT).show();
            }
        });

        /*RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost("10.128.0.104", 9200, "http")));


        SearchRequest searchRequest = new SearchRequest("usuario_sukaldatzen");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit: searchResponse.getHits().getHits()){
           System.out.println("-------------------- Esto: "+ hit.getSourceAsString());
        }

        client.close();*/

        /*try {
            /////////////////////////////////////
            //I: Establecemos credenciales con las que se hará las querys contra la BD
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
                    "android", "Becario2017"));
            //I: Inicializar low-ora level client builder para que func. el High level
            RestClientBuilder builder = RestClient.builder(new HttpHost("10.128.0.104", 9200))
                    .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                            .setDefaultCredentialsProvider(credentialsProvider));

            RestHighLevelClient highClient = new RestHighLevelClient(builder);
            //I: añadimos una query de búsqueda a la petición
            SearchRequest searchRequest = new SearchRequest();
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); //Aqui casca

            QueryBuilder qb = QueryBuilders.matchQuery(null,"a@a.com");

            //qb.must(QueryBuilders.matchQuery("correousu", "a@a.com"));
            searchRequest.indices("usuario_sukaldatzen");
            searchRequest.source(searchSourceBuilder);
            searchRequest.source().query(qb);

            ///// Prueba 7
            SearchSourceBuilder searchSB = new SearchSourceBuilder();

            SearchRequest request = new SearchRequest();
            request.source(searchSB);

            SearchResponse response = highClient.search(request,RequestOptions.DEFAULT);
            System.out.println("------------------Resultado: "+response.toString());
            //////

           *//* SearchResponse searchResponse = highClient.search(searchRequest,
            RequestOptions.DEFAULT);

            if (searchResponse.getHits().getTotalHits().value > 0){
                System.out.println(searchResponse.getHits().getTotalHits());
            }else{
                System.out.println("No hay resultados para los criterios determinados.");
            }*//*

            //I: Ahora hacemos algo para probar esa response. También podría verse en el debugger
            *//*SearchHit[] results = searchResponse.getHits().getHits();
            for(SearchHit hit : results){

                String sourceAsString = hit.getSourceAsString();
                System.out.println("Resu: "+sourceAsString);
            if (sourceAsString != null) {
                Gson gson = new GsonBuilder().create();
                System.out.println( gson.fromJson(sourceAsString, Firewall.class));
            }
            }*//*
        }catch (IOException ex){
            System.out.println("Excepción en el try de la query: "+ex);
        }catch (ElasticsearchException esx){
            System.out.println("Excepción ES en la query: "+ esx);
        }

*/
        /////////////////////////////////////
    }

    public void listarCazuelasUI(){
        for(Cazuela caz : mCazuela){
            //Añadimos una pantalla
        }
    }

    /*private void popupsalir(){
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.popupsalir, null);

        botonAceptar = (Button) customView.findViewById(R.id.aceptarBtn);
        botonCancelar = (Button) customView.findViewById(R.id.cancelarBtn);

        //Instanciamos la ventana popup salir
        popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(R.style.DialogAnimation);

        botonAceptar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View w){

            }
        });
        botonCancelar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View w){
                popupWindow.dismiss();
            }
        });
    }

    public void onBackPressed() {
        popupsalir();
        if(popupWindow.isShowing())
            popupWindow.dismiss();
        else
            getActivity().finish();
    }*/

}
