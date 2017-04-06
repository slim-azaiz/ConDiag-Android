package info.androidhive.gmail.control_diagnostic.diagnostic;

/**
 * Created by slim on 3/27/17.
 */


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import info.androidhive.gmail.R;
import info.androidhive.gmail.adapter.DiagnosticAdapter;
import info.androidhive.gmail.adapter.ServerAdapter;
import info.androidhive.gmail.helper.DividerItemDecoration;
import info.androidhive.gmail.model.Diagnostic;
import info.androidhive.gmail.network.ApiClient;
import info.androidhive.gmail.network.ApiInterface;
import info.androidhive.gmail.network.JSONResponse;
import info.androidhive.gmail.network.RequestInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Fragment1 extends Fragment   {
    //public static PtrClassicFrameLayout mPtrFrame;
    private RecyclerView recyclerView;
    private ArrayList<Diagnostic> data;
    private DiagnosticAdapter adapter;


    public Fragment1() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_1, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_viewDiag);
      //  mPtrFrame = (PtrClassicFrameLayout) view.findViewById(R.id.rotate_header_list_view_frameDiag);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        loadJSON();
        return  view;
    }




    private void loadJSON(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.206.208.98:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<JSONResponse> call = request.getJSON();
        call.enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {

                JSONResponse jsonResponse = response.body();
                data = new ArrayList<>(Arrays.asList(jsonResponse.getDiagnostics()));
                adapter = new DiagnosticAdapter(data);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });

    }

}