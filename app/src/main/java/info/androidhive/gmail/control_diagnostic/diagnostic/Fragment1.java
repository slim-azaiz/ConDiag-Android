package info.androidhive.gmail.control_diagnostic.diagnostic;

/**
 * Created by slim on 3/27/17.
 */


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import info.androidhive.gmail.R;
import info.androidhive.gmail.adapter.DiagnosticAdapter;
import info.androidhive.gmail.model.Diagnostic;
import info.androidhive.gmail.network.ApiClient;
import info.androidhive.gmail.network.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment1 extends Fragment   {
    public static PtrClassicFrameLayout mPtrFrame;
    private List<Diagnostic> diagnostics = new ArrayList<>();
    private RecyclerView recyclerView;
    private DiagnosticAdapter mAdapter;

    public Fragment1() {
        getDiagnosticInformation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_1, container, false);
    }


    public void getDiagnosticInformation(){


        //mPtrFrame.setRefreshing(true);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<List<Diagnostic>> call = apiService.getInbox();

        call.enqueue(new Callback<List<Diagnostic>>() {
            @Override
            public void onResponse(Call<List<Diagnostic>> call, Response<List<Diagnostic>> response) {
                // clear the inbox
                diagnostics.clear();

                // add all the diagnostics
                // diagnostics.addAll(response.body());

                // TODO - avoid looping
                // the loop was performed to add colors to each diagnostic
                for (Diagnostic diagnostic : response.body()) {
                    // generate a random color
                   // diagnostic.setColor(getRandomMaterialColor("400"));
                    diagnostics.add(diagnostic);
                }

                mAdapter.notifyDataSetChanged();
                //swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Diagnostic>> call, Throwable t) {
                Toast.makeText(getActivity(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
              //  mPtrFrame.setRefreshing(false);
            }
        });




    }
}