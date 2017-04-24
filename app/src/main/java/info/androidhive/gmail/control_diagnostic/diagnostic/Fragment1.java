package info.androidhive.gmail.control_diagnostic.diagnostic;

/**
 * Created by slim on 3/27/17.
 */


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import android.widget.Toast;

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

import static android.R.attr.data;


public class Fragment1 extends Fragment   {
    private PtrClassicFrameLayout mPtrFrame;
    private RecyclerView recyclerView;
    private ArrayList<Diagnostic> data;
    public static DiagnosticAdapter adapter;
    private  OkHttpClient client;
    public static   Handler handler;
    public static String constVar;
    private String method;
    private Runnable runnable;
    public Fragment1() {

    }
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


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            method = getArguments().getString("method");
            Log.i("METHOD",method);
            loadJSON();
            if(handler!=null) {
              //  handler.removeCallbacks(null);
                Log.i("HANDLER", "STOPPED");
            }else {
                Log.i("HANDLER", "NULL");
                notifyData(method);
            }

        }else{

            // fragment is no longer visible
        }
    }


    private void loadJSON(){
       // Log.i("Fragment1",ipAddress);
        Retrofit retrofit = new Retrofit.Builder()
                //.baseUrl("http://"+ipAddress+":8000")
               .baseUrl("http://10.206.208.70"+":8000")
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

                Snackbar.make(getView(), "Unable to fetch json", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                handler=null;
                                setUserVisibleHint(true);

                            }
                        })
                        .show();

//                Log.d("Error",t.getMessage());

            }
        });



    }
    private void notifyData(final String meth){
        handler =new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://10.206.208.70:8000")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                final RequestInterface request = retrofit.create(RequestInterface.class);
                Call<JSONResponse> call = request.getRealTime();
                call.enqueue(new Callback<JSONResponse>() {
                    @Override
                    public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                        JSONResponse jsonResponse = response.body();
                        data = new ArrayList<>(Arrays.asList(jsonResponse.getRealTime()));
                        Log.i("METHOD_THREAD",meth);
                        switch (adapter.diagnostics.get(1).getParameter()){
                            case "Used memory":
                                //get value of used_memory in real time
                                adapter.diagnostics.get(1).setValue(data.get(0).getValue());
                                break;
                            case "Internal temperature":
                                //get value of Internal_Temperature in real time
                                adapter.diagnostics.get(1).setValue(data.get(1).getValue());
                                break;
                        };
                        if (adapter.diagnostics.size()>=3) {

                            switch (adapter.diagnostics.get(2).getParameter()) {
                                case "STB IP Address":
                                    //get value of stb_ip_address in real time
                                    adapter.diagnostics.get(2).setValue(data.get(5).getValue());
                                    break;
                                case "Total software updates":
                                    //get value of total_software_updates in real time
                                    adapter.diagnostics.get(2).setValue(data.get(6).getValue());
                                    break;
                                case "CPU utilisation":
                                    //get value of CPU_Utilisation in real time
                                    adapter.diagnostics.get(2).setValue(data.get(2).getValue());
                                    break;
                            }
                            ;
                        }
                        if (adapter.diagnostics.size()>=4) {
                            switch (adapter.diagnostics.get(3).getParameter()) {
                                case "HDMI port status":
                                    //get value of HDMI_Port_Status in real time
                                    adapter.diagnostics.get(3).setValue(data.get(3).getValue());
                                    break;
                            }
                            ;
                        }
                        if (adapter.diagnostics.size()>=5) {

                            switch (adapter.diagnostics.get(4).getParameter()) {
                                case "STB ethernet port status":
                                    //get value of stb_ethernet_port_status in real time
                                    adapter.diagnostics.get(4).setValue(data.get(4).getValue());
                                    break;
                            }
                            ;
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<JSONResponse> call, Throwable t) {
                        handler.removeCallbacksAndMessages(null);
                        //  mPtrFrame.refreshComplete();
                        Log.d("Error", t.getMessage());
                    }
                });

            }
        }, 1000);

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
    }

   /* public class RealTime extends Service {

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onCreate() {
            Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.206.208.98:8000")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            final RequestInterface request = retrofit.create(RequestInterface.class);
            Call<JSONResponse> call = request.getRealTime();
            call.enqueue(new Callback<JSONResponse>() {
                @Override
                public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                    JSONResponse jsonResponse = response.body();
                    data = new ArrayList<>(Arrays.asList(jsonResponse.getRealTime()));
                    String meth="";
                    switch (meth){
                        case "memory":
                            //get value of used_memory in real time
                            adapter.diagnostics.get(1).setValue(data.get(0).getValue());
                            break;
                        case "sysInfo":
                            //get value of Internal_Temperature in real time
                            adapter.diagnostics.get(1).setValue(data.get(1).getValue());
                            //get value of CPU_Utilisation in real time
                            adapter.diagnostics.get(2).setValue(data.get(2).getValue());
                            //get value of HDMI_Port_Status in real time
                            adapter.diagnostics.get(3).setValue(data.get(3).getValue());
                            break;
                        case "network":
                            //get value of stb_ip_address in real time
                            adapter.diagnostics.get(2).setValue(data.get(5).getValue());
                            //get value of stb_ethernet_port_status in real time
                            adapter.diagnostics.get(4).setValue(data.get(4).getValue());
                            break;
                        case "software":
                            //get value of total_software_updates in real time
                            adapter.diagnostics.get(2).setValue(data.get(6).getValue());
                            break;
                        default:
                            break;
                    };
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<JSONResponse> call, Throwable t) {
                    //  mPtrFrame.refreshComplete();
                    Log.d("Error", t.getMessage());
                }
            });
        }

        @Override
        public void onDestroy() {
            Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
        }
        @Override
        public void onStart(Intent intent, int startid) {
            Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();
        }
    }*/
}