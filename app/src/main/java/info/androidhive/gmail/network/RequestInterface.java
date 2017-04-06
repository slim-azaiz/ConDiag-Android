package info.androidhive.gmail.network;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RequestInterface {

    @GET("/diag")
    Call<JSONResponse> getJSON();
}
