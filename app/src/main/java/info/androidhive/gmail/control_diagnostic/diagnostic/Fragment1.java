package info.androidhive.gmail.control_diagnostic.diagnostic;

/**
 * Created by slim on 3/27/17.
 */


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import info.androidhive.gmail.R;
import info.androidhive.gmail.adapter.DiagnosticAdapter;
import info.androidhive.gmail.adapter.ServerAdapter;
import info.androidhive.gmail.helper.DividerItemDecoration;
import info.androidhive.gmail.model.Diagnostic;
import info.androidhive.gmail.network.ApiClient;
import info.androidhive.gmail.network.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment1 extends Fragment implements SwipeRefreshLayout.OnRefreshListener   {
    //public static PtrClassicFrameLayout mPtrFrame;
    private List<Diagnostic> diagnostics = new ArrayList<>();
    private RecyclerView recyclerView;
    private DiagnosticAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;


    public Fragment1() {
        getDiagnosticInformation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_1, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_viewDiag);
      //  mPtrFrame = (PtrClassicFrameLayout) view.findViewById(R.id.rotate_header_list_view_frameDiag);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layoutDiag);
        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new DiagnosticAdapter(getContext(), diagnostics);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        getDiagnosticInformation();
                    }
                }
        );

        return  view;
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the servers again
        //getDiagnosticInformation();
    }


    public void getDiagnosticInformation(){


       // swipeRefreshLayout.setRefreshing(true);
        Log.i("DIAG","1");

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Diagnostic>> call = apiService.getInbox();
        Log.i("DIAG","2");

        call.enqueue(new Callback<List<Diagnostic>>() {
            @Override
            public void onResponse(Call<List<Diagnostic>> call, Response<List<Diagnostic>> response) {
                // clear the inbox
                Log.i("DIAG","3");

                diagnostics.clear();

                // add all the diagnostics
                // diagnostics.addAll(response.body());
                Log.i("DIAG","4");

                // TODO - avoid looping
                // the loop was performed to add colors to each diagnostic
                for (Diagnostic diagnostic : response.body()) {
                    // generate a random color
                    Log.i("DIAG","5");

                    // diagnostic.setColor(getRandomMaterialColor("400"));
                    diagnostics.add(diagnostic);
                }
                Log.i("DIAG","6");

                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                Log.i("DIAG","7");

            }

            @Override
            public void onFailure(Call<List<Diagnostic>> call, Throwable t) {
                Toast.makeText(getActivity(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
              //  mPtrFrame.setRefreshing(false);
            }
        });




    }
}