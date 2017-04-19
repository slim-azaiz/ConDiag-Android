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
    Call<JSONResponse> getJSON();

    @GET("/realTime")
    Call<JSONResponse> getRealTime();
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

    @GET("/command")
    Call<JSONResponse> getCommand();

    @POST("control/{code}")
    Call<JSONResponse> encoded(@Path("code") String code);


}
