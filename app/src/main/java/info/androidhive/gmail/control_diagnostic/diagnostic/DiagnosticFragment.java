package info.androidhive.gmail.control_diagnostic.diagnostic;

/**
 * Created by slim on 3/27/17.
 */


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Arrays;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import info.androidhive.gmail.R;
import info.androidhive.gmail.adapter.DiagnosticAdapter;
import info.androidhive.gmail.model.Diagnostic;
import info.androidhive.gmail.network.JSONResponse;
import info.androidhive.gmail.network.RequestInterface;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static android.content.Context.NOTIFICATION_SERVICE;
import static info.androidhive.gmail.utils.Config.BASE_URL;
import static info.androidhive.gmail.utils.Config.DIAGNOSTIC_LOG;
import static info.androidhive.gmail.utils.Config.isWifiAvailable;


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
    public static Runnable runnable;




    public DiagnosticFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_1, container, false);
    }

    @Override
    public void onViewCreated(final View view,  Bundle savedInstanceState) {




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
            loadJSON();

            diagnosticType = DiagnosticType.valueOf(method);
            switch (diagnosticType) {
                case memory:
                    if (handler!= null)
                        handler.removeCallbacks(runnable);
                    notifyData();
                    break;
                case sysInfo:
                    handler.removeCallbacks(runnable);
                    notifyData();
                    break;
                case network:
                    handler.removeCallbacks(runnable);
                    notifyData();
                    break;
                case software:
                    handler.removeCallbacks(runnable);
                    notifyData();
                    break;
                default:
                    handler.removeCallbacks(runnable);
                    Log.i(DIAGNOSTIC_LOG,"STOP HADLER");
                    break;
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
                //.baseUrl(url)
                .baseUrl(BASE_URL)
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
                if (!isWifiAvailable(getContext())) {

                    Snackbar.make(getView(), "Wifi is not available", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    handler.removeCallbacks(runnable);
                                    setUserVisibleHint(true);

                                }
                            })
                            .show();
                }
                else {
                    Snackbar.make(getView(), "STB and Smartphone are not available on the same network", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    handler.removeCallbacks(runnable);
                                    setUserVisibleHint(true);

                                }
                            })
                            .show();
                }
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
        handler.postDelayed(runnable= new Runnable() {
            @Override
            public void run() {
                if (handler != null) {

                    handler.postDelayed(this, 2000);
                    Retrofit retrofit = new Retrofit.Builder()
                            //.baseUrl(url)
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    final RequestInterface request = retrofit.create(RequestInterface.class);
                    Call<JSONResponse> call = request.getRealTime(method);
                    call.enqueue(new Callback<JSONResponse>() {
                        @Override
                        public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                            JSONResponse jsonResponse = response.body();
                            data = new ArrayList<>(Arrays.asList(jsonResponse.getRealTime()));
                            int dataPosition = 0;

                            for (DynamicParametres c : DynamicParametres.values()) {
                                for (int position = 0; position < adapter.diagnostics.size(); position++) {
                                   if (c.name().equals(adapter.diagnostics.get(position).getParameter())) {
                                       // adapter.diagnostics.get(position).setValue("dddd");                                         adapter.diagnostics.get(position).setValue(data.get(dataPosition).getValue());
                                       adapter.diagnostics.get(position).setValue(data.get(dataPosition).getValue());
                                       Log.i(DIAGNOSTIC_LOG,String.valueOf(dataPosition));
                                       dataPosition++;
                                    }
                                }
                            }
                            try {
                                adapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                Log.e("ERROR", "Adapter is not Unitialized", e);
                            }
                        }
                        @Override
                        public void onFailure(Call<JSONResponse> call, Throwable t) {
                            handler.removeCallbacksAndMessages(runnable);
                            //  mPtrFrame.refreshComplete();
                            //Log.d("Error", t.getMessage());
                        }
                    });
                }
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
    }
}