package com.stirling.developments.Utils;

import com.stirling.developments.Models.HitsObjects.HitsObject;
import com.stirling.developments.Models.HitsObjects.HitsObjectC;
import com.stirling.developments.Models.HitsObjects.HitsObjectM;
import com.stirling.developments.Models.POJOs.RespuestaB;
import com.stirling.developments.Models.POJOs.RespuestaU;
import com.stirling.developments.Models.gson2pojo.Aggregations;
//import com.stirling.developments.Models.gson2pojo.Example;
import com.stirling.developments.Models.gson2pojo.Example;
import com.stirling.developments.Models.gson2pojo.MyAgg;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface ElasticSearchAPI {

    //Llamada para buscar usuario. Headermap para autenticacion y body para query json
    @POST("usuarios_sukaldatzen/_search")
    Call<HitsObject> searchUsuario(@HeaderMap Map<String, String> headers,
                                   @Body RequestBody params);

    //Llamada para introducir un usuario nuevo en la base de datos
    @POST("/usuarios_sukaldatzen/_doc")
    Call<RespuestaU> postUserReg(@HeaderMap Map<String, String> headers,
                                 @Body RequestBody params);

    //Prueba: LLamada para eliminar la entrada de un usuario
    //usado a la hora de actualizar la ubicación de un usuario
    @POST("/usuarios_sukaldatzen/_delete_by_query")
    Call<RequestBody> deleteUserByQuery(@HeaderMap Map<String, String> headers,
                                        @Body RequestBody params);

    //Llamada para obtener información sobre una cazuela
    @POST("/cazuelas_sukaldatzen/_search")
    Call<HitsObjectC> searchCazuela(@HeaderMap Map<String, String> headers,
                                    @Body RequestBody params);

    //Llamada para introducir una cazuela nueva en la base de datos
    @POST("/cazuelas_sukaldatzen/_doc")
    Call<RespuestaU> postCazuela(@HeaderMap Map<String, String> headers,
                                  @Body RequestBody params);

    //Lamada para borrar una entrada del índice de una cazuela
    @POST("/cazuelas_sukaldatzen/_delete_by_query")
    Call<RespuestaB> deleteCazuela(@HeaderMap Map<String, String> headers,
                                   @Body RequestBody params);

    //Llamada para obtener información acerca de una medición. En hits
    @POST("/mediciones_sukaldatzen/_search")
    Call<HitsObjectM> searchMedicion(@HeaderMap Map<String, String> headers,
                                     @Body RequestBody params);

    //Llamada para obtener información acerca de una medición. Con aggregations
    @POST("mediciones_sukaldatzen/_search?filter_path=aggregations.myAgg.hits.hits._source*")
    Call<Example> searchHitsAgg(@HeaderMap Map<String,String> headers,
                                @Body RequestBody params);

    //Llamada para introducir una medición nueva en la base de datos
    @POST("/mediciones_sukaldatzen/_doc")
    Call<RequestBody> postMedicion();

    //
}
