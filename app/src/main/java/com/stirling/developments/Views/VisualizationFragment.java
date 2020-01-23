package com.stirling.developments.Views;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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
import com.stirling.developments.Utils.Notifications;
import com.stirling.developments.Utils.SwipeDetector;
//I: Search API de Elasticsearch
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
    private boolean alTAct = false;
    private String elCorreo = "";
    private String queryJson = "";
    private JSONObject jsonObject;

    private float tempOlla;
    private int lastX;

    private final static int INTERVAL = 3500;
    private LineGraphSeries<DataPoint> serie1;
    private LineGraphSeries<DataPoint> serie2;

    private boolean seguir = false;
    private boolean rCorriendo = false;
    private int minutosTemp = 0;
    private long millisCounter = 0;
    private float mil = 0;
    Animation animBlink;
    private Drawable abierto;

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
    @BindView(R.id.imgAlerta) ImageView imgAlerta;
    @BindView(R.id.imgCandado) ImageView imgCandado;

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

        //setteamos animación de parpadeo al indicador de temperatura
        animBlink = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                R.anim.blink);

        final Handler handler = new Handler();
        /* your code here */
        new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 2 * 1000); // every 2 seconds
                //lo que queremos que haga cada dos segundos
                comprobarAlarmaT(alTAct);
                actualizarTemperatura();
                actualizarColor();
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
                        tempOlla = 0;
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
                        tempOlla = 0;

                    }else{
                        Log.i("Swipe DaI: ", "No hay más pantallas hacia ese lado");
                    }
                }
            }

        });
        //Boton poner alamra temperatura
        bSetTemperatureAlarm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(bSetTemperatureAlarm.getText().equals("Activar")){ //Activar alarma
                    temperatureThreshold.setText(Html.fromHtml("<b>Temperatura límite: </b>" +
                            seekBarTemp.getProgress() + "ºC"));
                    alTAct=true;
                    bSetTemperatureAlarm.setText("Desactivar");
                }else{ //Desactivar alarma
                    temperatureThreshold.setText(Html.fromHtml("<b> </b>"));
                    bSetTemperatureAlarm.setText("Activar");
                }
            }
        });
        //Boton poner temporizador
        bSetTimeAlarm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(bSetTimeAlarm.getText().toString().equals("Activar")){ //Activar temporizador
                    timeAlarm.setText(Html.fromHtml("<b>Tiempo restante:</b> " +
                            seekBarTime.getProgress() + "min."));
                    if(!rCorriendo){
                        seguir = true;
                    }
                    minutosTemp = seekBarTime.getProgress();
                    bSetTimeAlarm.setText("Desactivar");
                    seguir = true;
                    arrancarTimer();
                }else{ //Desactivar temporizador
                    bSetTimeAlarm.setText("Activar");
                    seguir = false;
                    timeAlarm.setText(Html.fromHtml(" "));
                }
                timeAlarm.setText(Html.fromHtml("<b>Tiempo restante:</b> " +
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
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onResume(){
        super.onResume();
        //obtenerCazuelasUsuario();
    }
    //Encender contador que funciona si 'true' durante X minutos establecidos en var. minutosTemp.
    public void arrancarTimer(){
        millisCounter = minutosTemp*60*1000; //Trabajamos con millisegundos
        mil = 0;
        new unCountDown(millisCounter, 1000).start();
    }
    public void comprobarAlarmaT(boolean activada){
        if(activada){
            if(tempOlla>=seekBarTemp.getProgress()){
                Notifications.show(getActivity(), VisualizationFragment.class,
                        "Temperatura tupper", "Temperatura consigna alcanzada");
                bSetTemperatureAlarm.setText("Activar");
                alTAct =false;
            }
        }
    }

    public class unCountDown extends CountDownTimer {

        public unCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            seekBarTime.setMax((int) millisInFuture);
        }

        @Override
        public void onFinish() {
            timeAlarm.setText(Html.fromHtml("<b>Tiempo restante:</b> " +
                    "Finalizado"));
            seekBarTime.setMax(120);
            Notifications.show(getActivity(), VisualizationFragment.class,
                    "Temporizador olla", "El temporizador ha finalizado.");
            bSetTimeAlarm.setText("Activar");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if(!seguir){
                this.cancel();
                seekBarTime.setMax(120);
            }
            millisCounter = millisCounter - 1000;
            mil = millisCounter /60 / 1000;
            timeAlarm.setText(Html.fromHtml("<b>Tiempo restante:</b> " +
                    Math.round(mil) + "min."));
            seekBarTime.setProgress(Math.round(millisUntilFinished));
            long timeRemaining = millisUntilFinished;
            seekBarTime.setProgress((int) (timeRemaining));
            //Log.i(TAG, "Time tick: " + millisUntilFinished);
        }
    }

    //Cambio de color del círculo de temperatura
    public void actualizarColor(){

        int temp1 = 40;
        int temp2 = 100;
        int temp3 = 120;
        int temp4 = 140;
        if(tempOlla <= temp1){ //0-40
            imgAlerta.setImageResource(R.drawable.ic_warning_gris);
            imgCandado.setImageResource(R.drawable.ic_lock_open);
            tvTemperature.setBackgroundColor(getContext().getColor(R.color.tempAzul));
            tvTemperature.clearAnimation();
        }else if(temp1 < tempOlla && tempOlla < temp2){//40-100
            imgAlerta.setImageResource(R.drawable.ic_warning_gris);
            imgCandado.setImageResource(R.drawable.ic_lock_open);
            tvTemperature.setBackgroundColor(getContext().getColor(R.color.tempVerde));
            tvTemperature.clearAnimation();
        }else if(temp2 <= tempOlla && tempOlla < temp3){//100-120
            imgAlerta.setImageResource(R.drawable.ic_warning_gris);
            imgCandado.setImageResource(R.drawable.ic_lock_close);
            tvTemperature.setBackgroundColor(getContext().getColor(R.color.tempAmarillo));
            tvTemperature.clearAnimation();
        }else if(tempOlla >= temp3 && tempOlla < temp4) {//120-140
            imgAlerta.setImageResource(R.drawable.ic_warning);
            imgCandado.setImageResource(R.drawable.ic_lock_close);
            tvTemperature.setBackgroundColor(getContext().getColor(R.color.tempRojo));
            //hacer que parpadee en rojo
            tvTemperature.startAnimation(animBlink);
            imgAlerta.setVisibility(View.VISIBLE);
        }else if(tempOlla >= temp4){//>140

        }else{
            imgAlerta.setImageResource(R.drawable.ic_warning_gris);
            imgCandado.setImageResource(R.drawable.ic_lock_open);
            tvTemperature.setBackgroundColor(getContext().getColor(R.color.material_grey300));
            tvTemperature.clearAnimation();
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
                    //String ta = hits.getHits().get(0).getSource().getTempsInt().toString();
                    Float ta = hits.getHits().get(0).getSource().getTempsInt();
                    int taI = Math.round(ta);
                    tvTemperature.setText(taI + "ºC");
                    Log.i("Tª: ", "Temperatura actualizada: "+ ta + " ºC");
                    Log.i("Tª: ", "Temperatura tapa: " + hits.getHits().get(0).getSource()
                            .getTempsTapa().toString()+ " ºC");

                    //Actualizamos la MAC de la cazuela mostrada
                    String mac = hits.getHits().get(0).getSource().getIdMac(); //.toString()
                    tvMAC.setText(" " + mac + " ");

                    //Introducimos esta última temperatura en la segunda serie
                    String fjroi = hits.getHits().get(0).getSource().getTimestamp();
                    Float taInt = hits.getHits().get(0).getSource().getTempsInt();
                    tempOlla = taInt;
                    lastX++;//sustituir por hora?
                    serie2.appendData(new DataPoint(lastX ,taInt),true,1000);

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

    private void inicializarAPI(){
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_ELASTICSEARCH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        searchAPI = retrofit.create(ElasticSearchAPI.class);
    }

    private void obtenerCazuelasUsuario(){
        Log.i("obtenerCazuelasUsuario: ", "ha entrado <=======");
        mCazuela = new ArrayList<Cazuela>();

        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("Authorization", Credentials.basic("android",
                mElasticSearchPassword));
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

    public void listarCazuelasUI(){
        for(Cazuela caz : mCazuela){
            //Añadimos una pantalla
        }
    }



}
