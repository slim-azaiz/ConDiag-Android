package info.androidhive.gmail.network;

import info.androidhive.gmail.model.Diagnostic;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface RequestInterface {

    @GET("/diagnostic")
    Call<JSONResponse> getDiagnostics();


    @GET("/memory")
    Call<JSONResponse> getMemory();

    @GET("/conditionalAccess")
    Call<JSONResponse> getConditionalAccess();

    @GET("/network")
    Call<JSONResponse> getNetwork();

    @GET("/software")
    Call<JSONResponse> getSoftware();

    @GET("/loader")
    Call<JSONResponse> getLoader();

    @GET("/identification")
    Call<JSONResponse> getIdentification();

    @GET("/sysInfo")
    Call<JSONResponse> getSysInfo();

    @GET("/qamTunerStatus")
    Call<JSONResponse> getQamTunerStatus();

    @GET("/qamVirtualTunerStatus")
    Call<JSONResponse> getQamVirtualTunerStatus();

    @GET("/nvmem")
    Call<JSONResponse> getNvmem();

    @POST("set/{parameter}/{value}")
    Call<JSONResponse> setData(@Path("parameter") String parameter, @Path("value") String value);

    @POST("control/{code}")
    Call<JSONResponse> control(@Path("code") String code);

    @POST("authentificate/{username}/{password}")
    Call<String> authenticiate(@Path("username") String username, @Path("password") String password);


    @POST("resetPassword/{oldPassword}/{newPassword}")
    Call<String> resetPassword(@Path("oldPassword") String oldPassword, @Path("newPassword") String newPassword);

    @POST("resetPassword/{oldUsername}/{newUsername}")
    Call<String> resetUsername(@Path("oldUsername") String oldUsername, @Path("newUsername") String newUsername);
    @GET("realTime/{method}")
    Call<JSONResponse> getRealTime(@Path("method") String method);
}