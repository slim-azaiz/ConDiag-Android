package info.androidhive.gmail.control_diagnostic.diagnostic;

/**
 * Created by slim on 3/27/17.
 */


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
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
import info.androidhive.gmail.adapter.ServerAdapter;
import info.androidhive.gmail.model.Diagnostic;
import info.androidhive.gmail.model.Server;
import info.androidhive.gmail.network.JSONResponse;
import info.androidhive.gmail.network.RequestInterface;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.NOTIFICATION_SERVICE;
import static info.androidhive.gmail.utils.Config.DIAGNOSTIC_LOG;


public class DiagnosticFragment extends Fragment implements DiagnosticAdapter.DiagnosticAdapterListener   {
    private PtrClassicFrameLayout mPtrFrame;
    private RecyclerView recyclerView;
    private ArrayList<Diagnostic> data;
    public static DiagnosticAdapter adapter;
    private  OkHttpClient client;
    public static   Handler handler;
    public static String constVar;
    private String method;
    private DiagnosticType diagnosticType;
    private Runnable runnable;




    public DiagnosticFragment() {

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
                notifyData();
            }

        }else{

            // fragment is no longer visible
        }
    }
    @Override
    public void onDiagnosticRowClicked(int position) {
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated

            // read the modelName which removes bold parameter the row
            Diagnostic diagnostic = data.get(position);
            data.set(position, diagnostic);
            adapter.notifyDataSetChanged();

            // Toast.makeText(getApplicationContext(), "Read: " + modelName.getModel(), Toast.LENGTH_SHORT).show();
    }



    private void loadJSON() {
        // Log.i("DiagnosticFragment",ipAddress);
        Retrofit retrofit = new Retrofit.Builder()
                //.baseUrl("http://"+ipAddress+":8000")
                .baseUrl("http://10.206.208.63" + ":8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<JSONResponse> call;
        diagnosticType = DiagnosticType.valueOf(method);
        switch (diagnosticType) {
            case identification:
                call = request.getIdentification();
                break;
            case memory:
                call = request.getMemory();
                break;
            case sysInfo:
                call = request.getSysInfo();
                break;
            case conditionalAccess:
                call = request.getConditionalAccess();
                break;
            case network:
                call = request.getNetwork();
                break;
            case software:
                call = request.getSoftware();
                break;
            case loader:
                call = request.getLoader();
                break;
            case nvmem:
                call = request.getNvmem();
                break;
            case qamTunerStatus:
                call = request.getQamTunerStatus();
                break;
            case qamVirtualTunerStatus:
                call = request.getQamVirtualTunerStatus();
                break;
            default:
                call = request.getJSON();
                break;
        }

        call.enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                JSONResponse jsonResponse = response.body();
                switch (diagnosticType) {
                    case identification:
                        data = new ArrayList<>(Arrays.asList(jsonResponse.getIdentification()));
                        break;
                    case memory:
                        data = new ArrayList<>(Arrays.asList(jsonResponse.getMemory()));
                        break;
                    case sysInfo:
                        data = new ArrayList<>(Arrays.asList(jsonResponse.getSysInfo()));
                        break;
                    case conditionalAccess:
                        data = new ArrayList<>(Arrays.asList(jsonResponse.getConditionalAccess()));
                        break;
                    case network:
                        data = new ArrayList<>(Arrays.asList(jsonResponse.getNetwork()));
                        break;
                    case software:
                        data = new ArrayList<>(Arrays.asList(jsonResponse.getSoftware()));
                        break;
                    case loader:
                        data = new ArrayList<>(Arrays.asList(jsonResponse.getLoader()));
                        break;
                    case nvmem:
                        data = new ArrayList<>(Arrays.asList(jsonResponse.getNvmem()));
                        break;
                    case qamTunerStatus:
                        data = new ArrayList<>(Arrays.asList(jsonResponse.getQamTunerStatus()));
                        break;
                    case qamVirtualTunerStatus:
                        data = new ArrayList<>(Arrays.asList(jsonResponse.getVirtualQamTunerStatus()));
                        break;
                    default:
                        break;

                }
                try {
                    adapter.clearData();
                } catch (Exception e) {
                    Log.e("ERROR", "showProgressDialog", e);
                }
                adapter = new DiagnosticAdapter(data, getActivity(), getActivity().getFragmentManager());

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
                                handler = null;
                                setUserVisibleHint(true);

                            }
                        })
                        .show();

//                Log.d("Error",t.getMessage());

            }
        });

    }
    public void sendNotification(View view) {


        int NOTIFICATION_ID =1;
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://developer.android.com/reference/android/app/Notification.html"));
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());

       // builder.setSmallIcon(R.drawable.ic_stat_notification);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        //builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        builder.setContentTitle("BasicNotifications Sample");
        builder.setContentText("Time to learn about notifications!");
        builder.setSubText("Tap to view documentation about notifications.");
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }
    private void notifyData(){
        handler =new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 2000);
                Retrofit retrofit = new Retrofit.Builder()
                        //.baseUrl("http://"+ipAddress+":8000")
                        .baseUrl("http://10.206.208.63:8000")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                final RequestInterface request = retrofit.create(RequestInterface.class);
                Call<JSONResponse> call = request.getRealTime();
                call.enqueue(new Callback<JSONResponse>() {
                    @Override
                    public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                        JSONResponse jsonResponse = response.body();
                        data = new ArrayList<>(Arrays.asList(jsonResponse.getRealTime()));
                        int dataPosition =0;

                        for (DynamicParametres c : DynamicParametres.values()) {
                            for (int position = 0; position<adapter.diagnostics.size();position++ ) {
                                if (c.name().equals(adapter.diagnostics.get(position).getParameter())) {
                                    adapter.diagnostics.get(position).setValue(data.get(dataPosition).getValue());
                                }
                            }
                            dataPosition++;

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