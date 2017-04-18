package info.androidhive.gmail.control_diagnostic.diagnostic;

/**
 * Created by slim on 3/27/17.
 */


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.Arrays;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import info.androidhive.gmail.R;
import info.androidhive.gmail.adapter.DiagnosticAdapter;
import info.androidhive.gmail.model.Diagnostic;
import info.androidhive.gmail.network.JSONResponse;
import info.androidhive.gmail.network.RequestInterface;
import it.sephiroth.android.library.bottomnavigation.BottomBehavior;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import it.sephiroth.android.library.bottomnavigation.MiscUtils;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Fragment1 extends Fragment   {
    private PtrClassicFrameLayout mPtrFrame;
    private RecyclerView recyclerView;
    private ArrayList<Diagnostic> data;
    public static DiagnosticAdapter adapter;
    private  OkHttpClient client;
    private  Handler handler;
    public static String constVar;
    private String method;
    public Fragment1() {

    }
    private Boolean mIsRefreshing = false;
    private String ipAddress;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_1, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_viewDiag);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        Log.i("mIsRefreshing",mIsRefreshing.toString());
        /*recyclerView.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (mIsRefreshing) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
        );
*/
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            method = getArguments().getString("method");
            Log.i("METHOD",method);

            loadJSON();

        }else{
            // fragment is no longer visible
        }
    }


    private void loadJSON(){
       // Log.i("Fragment1",ipAddress);
        Retrofit retrofit = new Retrofit.Builder()
                //.baseUrl("http://"+ipAddress+":8000")
               .baseUrl("http://10.206.208.112"+":8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


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
                try {
                    adapter.clearData();
                } catch (Exception e) {
                    Log.e("ERROR", "showProgressDialog", e);
                }
                adapter = new DiagnosticAdapter(data);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                mIsRefreshing = true;
                Log.i("mIsRefreshing",mIsRefreshing.toString());


                //  mPtrFrame.refreshComplete();

            }
            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
              //  mPtrFrame.refreshComplete();
                try {
                    adapter.clearData();
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
                      //  mPtrFrame.refreshComplete();
                        Log.d("Error",t.getMessage());
                    }
                });
            }
        }, 2000);
    }

    public class WrapContentLinearLayoutManager extends LinearLayoutManager {

        public WrapContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout)    {
            super(context, orientation, reverseLayout);
        }
        @Override
        public boolean canScrollHorizontally() {
            return false;
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("probe", "meet a IOOBE in RecyclerView");
            }
        }
        @Override
        public boolean canScrollVertically() {
            if (mIsRefreshing) {
                return true;
            } else {
                return false;
            }        }
    }
}