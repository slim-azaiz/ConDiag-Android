package info.androidhive.gmail.control_diagnostic.diagnostic;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;

import info.androidhive.gmail.R;
import info.androidhive.gmail.login.RequestHandler;
import info.androidhive.gmail.login.User;
import rx.subscriptions.CompositeSubscription;

import static info.androidhive.gmail.adapter.DiagnosticAdapter.diagnostics;
import static info.androidhive.gmail.login.Validation.validateFields;


public class ChangeParameterDialog extends DialogFragment {

    public interface Listener {

        void onParameterChanged();
    }
    private String param;
    private String value;

    public ChangeParameterDialog(String param, String value){
        this.param = param;
        this.value = value;

    }

    public static final String TAG = ChangeParameterDialog.class.getSimpleName();

    public  TextView mTvOldParameter;
    private EditText mEtNewParameter;
    private Button mBtChangeParameter;
    private Button mBtCancel;
    private TextView mTvMessage;
    private TextInputLayout mTiOldParameter;
    private TextInputLayout mTiNewParameter;
    private ProgressBar mProgressBar;

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
            }
        });
    }

    private void changeParameter() {

        setError();

        String oldParameter = mTvOldParameter.getText().toString();
        String newParameter = mEtNewParameter.getText().toString();

        int err = 0;

        if (!validateFields(oldParameter)) {

            err++;
            mTiOldParameter.setError("'New Value' field should not be empty !");
        }

        if (!validateFields(newParameter)) {

            err++;
            mTiNewParameter.setError("'New Value' field should not be empty !");
        }

        if (err == 0) {

           /* Diagnostic diagnostic = new Diagnostic();
            diagnostic.setOldParameter(oldParameter);
            user.setNewParameter(newParameter);
            changeParameterProgress(user);
            */mProgressBar.setVisibility(View.VISIBLE);

        }
    }

    private void setError() {

        mTiOldParameter.setError(null);
        mTiNewParameter.setError(null);
    }

    private void changeParameterProgress(final User user) {

        class ResetParameter extends AsyncTask<String,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(Login.this, "Please Wait", null, true, true);
                loading = new ProgressDialog(getActivity());
                loading.setTitle("Attendez s'il vous plaît ..");
                loading.setMessage("La liste est en train de charger");
                loading.setIndeterminate(true);
                loading.setCancelable(false);
                loading.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                if(s.equalsIgnoreCase("success")){

                   // showMessage("Parameter changed successfully !");
                    mProgressBar.setVisibility(View.GONE);
                    dismiss();



                    Snackbar.make(getActivity().getCurrentFocus(), "Parameter changed successfully !", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


                   // handleError();

                }
                else{
                   // showMessage("Wrong old parameter !");
                    mProgressBar.setVisibility(View.GONE);
                    dismiss();

                    Snackbar.make(getActivity().getCurrentFocus(), "Wrong old parameter !", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
            }
            @Override
            protected String doInBackground(String... params) {
                HashMap<String,String> data = new HashMap<>();
                //data.put("","");
             //   data.put("PASSWORD", user.getNewParameter());
                RequestHandler ruc = new RequestHandler();
                //String result = ruc.sendPostRequest("http://"+parameter+":8000/authentificate"+username+"/"+parameter,data);
             //   String result = ruc.sendPostRequest("http://10.206.208.123:8000/resetParameter/"+user.getOldParameter()+"/"+user.getNewParameter(),data);
              //  return result;
                return "result";
            }
        }
        ResetParameter ulc = new ResetParameter();
        //ulc.execute(user.getOldParameter(), user.getNewParameter());


       // handleError();
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
