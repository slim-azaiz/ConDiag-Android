package info.androidhive.gmail.control_diagnostic.diagnostic;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import info.androidhive.gmail.R;
import info.androidhive.gmail.login.RequestHandler;
import info.androidhive.gmail.login.User;
import info.androidhive.gmail.model.Diagnostic;
import info.androidhive.gmail.network.JSONResponse;
import info.androidhive.gmail.network.RequestInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.subscriptions.CompositeSubscription;

import static info.androidhive.gmail.control_diagnostic.diagnostic.DiagnosticFragment.adapter;
import static info.androidhive.gmail.control_diagnostic.diagnostic.DiagnosticFragment.handler;
import static info.androidhive.gmail.control_diagnostic.diagnostic.DiagnosticFragment.runnable;
import static info.androidhive.gmail.login.Validation.validateFields;


public class ChangeParameterDialog extends DialogFragment {

    public interface Listener {

        void onParameterChanged();
    }
    private String param;
    private String value;
    private int position;

    private ArrayList<Diagnostic> dataToSet;


    public ChangeParameterDialog(int position, String param, String value){
        this.param = param;
        this.value = value;
        this.position = position;

    }

    public static final String TAG = ChangeParameterDialog.class.getSimpleName();

    public  TextView mTvOldParameter;

    private EditText mEtNewParameter;
    private Button mBtChangeParameter;
    private Button mBtCancel;
    private TextView mTvMessage;
    private TextInputLayout mTiOldParameter;
    private TextInputLayout mTiNewParameter;
    private static ProgressBar mProgressBar;

    private CompositeSubscription mSubscriptions;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.dialog_change_parameter,container,false);
        getDialog().setTitle(param);
        mSubscriptions = new CompositeSubscription();
        initViews(view);
        return view;
    }




    private void initViews(View v) {
        mTvOldParameter = (TextView) v.findViewById(R.id.et_old_parameter);
        mTvOldParameter.setText(value);
        mEtNewParameter = (EditText) v.findViewById(R.id.et_new_parameter);
        mTiOldParameter = (TextInputLayout) v.findViewById(R.id.ti_old_parameter);
        mTiNewParameter = (TextInputLayout) v.findViewById(R.id.ti_new_parameter);
        mTvMessage = (TextView) v.findViewById(R.id.tv_message);
        mBtChangeParameter = (Button) v.findViewById(R.id.btn_change_parameter);
        mBtCancel = (Button) v.findViewById(R.id.btn_cancel);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);





        mBtChangeParameter.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                changeParameter();
            }
        });
        mBtCancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                dismiss();
                runnable.run();
            }
        });
    }

    private void changeParameter() {

        setError();

        String parameterToSet = mTvOldParameter.getText().toString();
        String valueToSet = mEtNewParameter.getText().toString();

        int err = 0;

        if (!validateFields(parameterToSet)) {

            err++;
            mTiOldParameter.setError("'New Value' field should not be empty !");
        }

        if (!validateFields(valueToSet)) {

            err++;
            mTiNewParameter.setError("'New Value' field should not be empty !");
        }

        if (err == 0) {

           /* Diagnostic diagnostic = new Diagnostic();
            diagnostic.setOldParameter(parameterToSet);
            user.setNewParameter(valueToSet);*/
           // changeParameterProgress(parameterToSet, valueToSet);
            postCommand( param, valueToSet);
            mProgressBar.setVisibility(View.VISIBLE);

        }
    }

    private void setError() {

        mTiOldParameter.setError(null);
        mTiNewParameter.setError(null);
    }

    public  void postCommand(String parameter, String value){
        Retrofit retrofit = new Retrofit.Builder()
                //.baseUrl("http://"+ipAddress+":8000")
                .baseUrl("http://10.206.208.73:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<JSONResponse> call ;
        call = request.setData(parameter,value);

        call.enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                mProgressBar.setVisibility(View.GONE);
                dismiss();
                try {
                    JSONResponse jsonResponse = response.body();
                    dataToSet = new ArrayList<>(Arrays.asList(jsonResponse.getDataToSet()));
                    adapter.diagnostics.get(position).setValue(dataToSet.get(0).getValue());
                    adapter.notifyDataSetChanged();
                    runnable.run();
                    Snackbar.make(getActivity().getCurrentFocus(), "Parameter changed successfully", Snackbar.LENGTH_LONG)
                            .show();

                } catch (Exception e) {
                    Snackbar.make(getActivity().getCurrentFocus(), "Unable to fetch json", Snackbar.LENGTH_LONG)
                            .show();
                    Log.e("ERROR", "showProgressDialog", e);
                }
            }
            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                //  mPtrFrame.refreshComplete();
                mProgressBar.setVisibility(View.GONE);
                dismiss();
                runnable.run();

                Log.i("MESSAGE",t.
                        getMessage());
                if(!t.getMessage().contains("JsonReader")) {
                    Snackbar.make(getActivity().getCurrentFocus(), "Unable to fetch json", Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private void handleError() {
            showMessage("Network Error !");
    }



    private void showMessage(String message) {

        mTvMessage.setVisibility(View.VISIBLE);
        mTvMessage.setText(message);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}
