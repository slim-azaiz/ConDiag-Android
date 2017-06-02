package info.sagemcom.conDiag.network;

import java.util.List;

import info.sagemcom.conDiag.model.Diagnostic;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Ravi Tamada on 21/02/17.
 * www.androidhive.info
 */

public interface ApiInterface {
    @GET("diag.json")
    Call<List<Diagnostic>> getInbox();
}
