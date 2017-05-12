package info.androidhive.gmail.login;

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
import rx.subscriptions.CompositeSubscription;

import static info.androidhive.gmail.login.Validation.validateFields;


public class ChangeUsernameDialog extends DialogFragment {

    public interface Listener {

        void onUsernameChanged();
    }

    public static final String TAG = ChangeUsernameDialog.class.getSimpleName();

    private EditText mEtOldUsername;
    private EditText mEtNewUsername;
    private Button mBtChangeUsername;
    private Button mBtCancel;
    private TextView mTvMessage;
    private TextInputLayout mTiOldUsername;
    private TextInputLayout mTiNewUsername;
    private ProgressBar mProgressBar;

    private CompositeSubscription mSubscriptions;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_change_username,container,false);
        mSubscriptions = new CompositeSubscription();
        initViews(view);
        return view;
    }




    private void initViews(View v) {

        mEtOldUsername = (EditText) v.findViewById(R.id.et_old_username);
        mEtNewUsername = (EditText) v.findViewById(R.id.et_new_username);
        mTiOldUsername = (TextInputLayout) v.findViewById(R.id.ti_old_username);
        mTiNewUsername = (TextInputLayout) v.findViewById(R.id.ti_new_username);
        mTvMessage = (TextView) v.findViewById(R.id.tv_message);
        mBtChangeUsername = (Button) v.findViewById(R.id.btn_change_username);
        mBtCancel = (Button) v.findViewById(R.id.btn_cancel);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);

        mBtChangeUsername.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                changeUsername();
            }
        });
        mBtCancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                dismiss();
            }
        });
    }

    private void changeUsername() {

        setError();

        String oldUsername = mEtOldUsername.getText().toString();
        String newUsername = mEtNewUsername.getText().toString();

        int err = 0;

        if (!validateFields(oldUsername)) {

            err++;
            mTiOldUsername.setError("Username should not be empty !");
        }

        if (!validateFields(newUsername)) {

            err++;
            mTiNewUsername.setError("Username should not be empty !");
        }

        if (err == 0) {

            User user = new User();
            user.setOldUsername(oldUsername);
            user.setNewUsername(newUsername);
            changeUsernameProgress(user);
            mProgressBar.setVisibility(View.VISIBLE);

        }
    }

    private void setError() {

        mTiOldUsername.setError(null);
        mTiNewUsername.setError(null);
    }

    private void changeUsernameProgress(final User user) {

        class ResetUsername extends AsyncTask<String,Void,String> {
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

                   // showMessage("Username changed successfully !");
                    mProgressBar.setVisibility(View.GONE);
                    dismiss();



                    Snackbar.make(getActivity().getCurrentFocus(), "Username changed successfully !", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


                   // handleError();

                }
                else{
                   // showMessage("Wrong old username !");
                    mProgressBar.setVisibility(View.GONE);
                    dismiss();

                    Snackbar.make(getActivity().getCurrentFocus(), "Wrong old username !", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
            }
            @Override
            protected String doInBackground(String... params) {
                HashMap<String,String> data = new HashMap<>();
                //data.put("","");
                data.put("PASSWORD", user.getNewUsername());
                RequestHandler ruc = new RequestHandler();
                //String result = ruc.sendPostRequest("http://"+parameter+":8000/authentificate"+username+"/"+username,data);
                String result = ruc.sendPostRequest("http://10.206.208.123:8000/resetUsername/"+user.getOldUsername()+"/"+user.getNewUsername(),data);
                return result;
            }
        }
        ResetUsername ulc = new ResetUsername();
        ulc.execute(user.getOldUsername(), user.getNewUsername());


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
