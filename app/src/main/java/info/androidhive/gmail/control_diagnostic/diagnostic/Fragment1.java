package info.androidhive.gmail.control_diagnostic.diagnostic;

/**
 * Created by slim on 3/27/17.
 */


import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import info.androidhive.gmail.R;
import info.androidhive.gmail.adapter.DiagnosticAdapter;
import info.androidhive.gmail.adapter.ServerAdapter;
import info.androidhive.gmail.discovery.dial.ServerFinder;
import info.androidhive.gmail.helper.DividerItemDecoration;
import info.androidhive.gmail.login.RequestHandler;
import info.androidhive.gmail.model.Diagnostic;
import info.androidhive.gmail.network.ApiClient;
import info.androidhive.gmail.network.ApiInterface;
import info.androidhive.gmail.network.JSONResponse;
import info.androidhive.gmail.network.LoggingInterceptor;
import info.androidhive.gmail.network.RequestInterface;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Fragment1 extends Fragment   {
    private  PtrClassicFrameLayout mPtrFrame;
    private RecyclerView recyclerView;
    private ArrayList<Diagnostic> data;
    public static DiagnosticAdapter adapter;
    private  OkHttpClient client;
    private  Handler handler;
    private String method;
    public Fragment1() {

    }

    private String mCategoryId;
    private String mCategorySlug;

    public static Fragment1 newInstance(Bundle b) {
        Fragment1 fragment = new Fragment1();
        fragment.setArguments(b);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments() != null) {
            Log.i("MENU GET", String.valueOf(getArguments().toString()));
        } else {
            Log.i("MENU", "getArgument is null");
        }

        int numFragment =FragmentPagerItem.getPosition(getArguments());
        Log.i("numFragment", String.valueOf(numFragment));

        switch (numFragment){
            case 0:
                method ="identification";
                break;
            case 1:
                method ="memory";
                break;
            case 2:
                method ="sysInfo";
                break;
            case 3:
                method ="conditionalAccess";
                break;
            case 4:
                method ="network";
                break;
            case 5:
                method ="software";
                break;
            case 6:
                method ="loader";
                break;
            default:
                method ="loader";
                break;
        }
        View view= inflater.inflate(R.layout.fragment_1, container, false);
        mPtrFrame = (PtrClassicFrameLayout) view.findViewById(R.id.rotate_header_list_view_frameDiag);


        Log.i("MENU",String.valueOf(getArguments().getInt("someInt",0)));

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_viewDiag);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);


        //loadJSON();
        mPtrFrame = (PtrClassicFrameLayout) view.findViewById(R.id.rotate_header_list_view_frameDiag);
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                loadJSON();
                    //notifyData();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
        // the following are default settings
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        // default is false
        mPtrFrame.setPullToRefresh(false);
        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrame.autoRefresh();
            }
        }, 100);

        return  view;
    }




    private void loadJSON(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.206.208.82:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.i("MENU",String.valueOf(getArguments().getInt("someInt",0)));

        Log.i("MENU",method);

        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<JSONResponse> call ;
        switch (method){
            case "identification":
                call = request.getIdentification();
                break;
            case "memory":
                call = request.getMemory();
                break;
            case "sysInfo":
                call = request.getSysInfo();
                break;
            case "conditionalAccess":
                call = request.getConditionalAccess();
                break;
            case "network":
                call = request.getNetwork();
                break;
            case "software":
                call = request.getSoftware();
                break;
            case "loader":
                call = request.getLoader();
                break;
            default:
                call = request.getJSON();
                break;
        }

        call.enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                JSONResponse jsonResponse = response.body();
                switch (method){
                    case "identification":
                        data = new ArrayList<>(Arrays.asList(jsonResponse.getIdentification()));
                        break;
                    case "memory":
                        data = new ArrayList<>(Arrays.asList(jsonResponse.getMemory()));
                        break;
                    case "sysInfo":
                        data = new ArrayList<>(Arrays.asList(jsonResponse.getSysInfo()));
                        break;
                    case "conditionalAccess":
                        data = new ArrayList<>(Arrays.asList(jsonResponse.getConditionalAccess()));
                        break;
                    case "network":
                        data = new ArrayList<>(Arrays.asList(jsonResponse.getNetwork()));
                        break;
                    case "software":
                        data = new ArrayList<>(Arrays.asList(jsonResponse.getSoftware()));
                        break;
                    case "loader":
                        data = new ArrayList<>(Arrays.asList(jsonResponse.getLoader()));
                        break;
                    default:
                        data = new ArrayList<>(Arrays.asList(jsonResponse.getDiagnostics()));
                        break;

                }
                //data = new ArrayList<>(Arrays.asList(jsonResponse.getDiagnostics()));
                //Toast.makeText(getContext(), data.size(), Toast.LENGTH_LONG).show();
                Log.i("DATA_SIZE",jsonResponse.toString());
                adapter = new DiagnosticAdapter(data);
                recyclerView.setAdapter(adapter);
                mPtrFrame.refreshComplete();

            }
            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                mPtrFrame.refreshComplete();
                try {
                    adapter.diagnostics.clear();
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e("ERROR", "showProgressDialog", e);
            }
                Snackbar.make(getView(), "Unable to fetch json: " + t.getMessage(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
//                Log.d("Error",t.getMessage());

            }
        });



    }
    private void notifyData(){

        handler = new Handler();
         final Runnable myRunnable ;

        handler.postDelayed(new Runnable(){
            public void run(){
                handler.postDelayed(this, 2000);

                Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.6:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
                final RequestInterface request = retrofit.create(RequestInterface.class);
                Call<JSONResponse> call = request.getJSON();
                call.enqueue(new Callback<JSONResponse>() {
                    @Override
                    public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                        JSONResponse jsonResponse = response.body();
                        data = new ArrayList<>(Arrays.asList(jsonResponse.getDiagnostics()));
                        //get value of temperature in real time
                        adapter.diagnostics.get(28).setValue(data.get(28).getValue());
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onFailure(Call<JSONResponse> call, Throwable t) {
                        handler.removeCallbacksAndMessages(null);
                        mPtrFrame.refreshComplete();
                        Log.d("Error",t.getMessage());
                    }
                });
            }
        }, 2000);
    }


}