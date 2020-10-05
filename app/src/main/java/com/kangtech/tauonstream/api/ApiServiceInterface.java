package com.kangtech.tauonstream.api;

import com.kangtech.tauonstream.model.data.musicModel;
import com.kangtech.tauonstream.model.data.updateModel;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiServiceInterface {

    //
    //    @Headers("Content-Type:application/json", "AccessKey:A4BC8D32-E0DD-48CD-8A1B-7E0BE88EDA99")
    //    @POST("/api/Patient/Login")
    //    fun login(@Body loginRequest: LoginRequest): Observable<LoginResponse>
    //
    //    @Headers("Content-Type:application/json", "AccessKey:A4BC8D32-E0DD-48CD-8A1B-7E0BE88EDA99")
    //    @POST("/api/Patient/RegisterNewPatient")
    //    fun RegisterNewPatient(@Body registerRequest: RegisterRequest): Observable<String>
    //
    //    @Headers("AccessKey:A4BC8D32-E0DD-48CD-8A1B-7E0BE88EDA99")
    //    @GET("/api/Recipe/GetRepeatableRecipeSpecificPatient/{Id}")
    //    fun GetRepeatableRecipeSpecificPatient(@Path("Id") Id : String) : Observable<List<GetRepeatableRecipeSpecificPatientResponse>>
    //
    //    @Headers("AccessKey:A4BC8D32-E0DD-48CD-8A1B-7E0BE88EDA99")
    //    @GET("/api/Recipe/GetSpecificRecipeDetail/{Id}")
    //    fun GetSpecificRecipeDetail(@Path("Id") Id : String) : Observable<List<GetSpecificRecipeDetailResponse>>
    //
    //    @Headers("AccessKey:A4BC8D32-E0DD-48CD-8A1B-7E0BE88EDA99")
    //    @GET("/api/Patient/GetSpecificPatientCompletedAppointment/{Id}")
    //    fun GetSpecificPatientCompletedAppointment(@Path("Id") Id : String) : Observable<List<GetSpecificPatientCompletedAppointmentResponse>>

/*    @FormUrlEncoded
    @POST("/users/login")
    Call<ResponseBody> loginRequest(@Field("username") String username,
                                    @Field("password") String password);

    @FormUrlEncoded
    @POST("/users/follow")
    Observable<ResponseBody> followUser(@Field("user_otherId") int id_user_other,
                                        @Field("userId") int id_user,
                                        @Header("kangtech_31") String token);

    @FormUrlEncoded
    @HTTP(method = "DELETE", path = "/users/unfollow/{id_user_other}", hasBody = true)
    Observable<ResponseBody> unfollowUser(@Path("id_user_other") int id_user_other,
                                          @Field("userId") int id_user,
                                          @Header("kangtech_31") String token);*/

    @GET("/radio/getpic")
    Observable<musicModel> getData();

    @GET("/radio/update_radio")
    Observable<updateModel> getUpdateData();

}
