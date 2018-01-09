package mb.com.mp3player.interfaces;

import com.google.gson.JsonObject;

import java.util.List;

import mb.com.mp3player.models.RegistrationData;
import mb.com.mp3player.models.SongData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;


/**
 * Created by Anshul on 23-11-17.
 */

public interface ApiInterface
{
    String BASE_URL = "http://192.168.16.250:3000/";

    @Headers("Content-Type: application/json")
    @POST("register")
    Call<JsonObject> senddetails(@Body RegistrationData registrationdata);

    @POST("login")
    @Headers("Content-Type: application/json")
    Call<JsonObject> getdetails(@Body RegistrationData registrationdata);

    @Headers("Content-Type: application/json")
    @POST("addsong")
    Call<JsonObject> addtoplaylist(@Body SongData songdata);

    @Headers("Content-Type: application/json")
    @POST("removesong")
    Call<JsonObject> removefromplaylist(@Body SongData songdata);

    @GET("playlist")
    Call<List<SongData>> getplaylist();


}
