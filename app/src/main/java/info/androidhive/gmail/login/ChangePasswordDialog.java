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


public class ChangePasswordDialog extends DialogFragment {

    public interface Listener {

        void onPasswordChanged();
    }

    public static final String TAG = ChangePasswordDialog.class.getSimpleName();

    private EditText mEtOldPassword;
    private EditText mEtNewPassword;
    private Button mBtChangePassword;
    private Button mBtCancel;
    private TextView mTvMessage;
    private TextInputLayout mTiOldPassword;
    private TextInputLayout mTiNewPassword;
    private ProgressBar mProgressBar;

    private CompositeSubscription mSubscriptions;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_change_password,container,false);
        mSubscriptions = new CompositeSubscription();
        initViews(view);
        return view;
    }




    private void initViews(View v) {

        mEtOldPassword = (EditText) v.findViewById(R.id.et_old_password);
        mEtNewPassword = (EditText) v.findViewById(R.id.et_new_password);
        mTiOldPassword = (TextInputLayout) v.findViewById(R.id.ti_old_password);
        mTiNewPassword = (TextInputLayout) v.findViewById(R.id.ti_new_password);
        mTvMessage = (TextView) v.findViewById(R.id.tv_message);
        mBtChangePassword = (Button) v.findViewById(R.id.btn_change_password);
        mBtCancel = (Button) v.findViewById(R.id.btn_cancel);
        mProgressBar = (ProgressBar) v.findViewById(R.id.progress);

        mBtChangePassword.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                changePassword();
            }
        });
        mBtCancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {

                dismiss();
            }
        });
    }

    private void changePassword() {

        setError();

        String oldPassword = mEtOldPassword.getText().toString();
        String newPassword = mEtNewPassword.getText().toString();

        int err = 0;

        if (!validateFields(oldPassword)) {

            err++;
            mTiOldPassword.setError("Password should not be empty !");
        }

        if (!validateFields(newPassword)) {

            err++;
            mTiNewPassword.setError("Password should not be empty !");
        }

        if (err == 0) {

            User user = new User();
            user.setOldPassword(oldPassword);
            user.setNewPassword(newPassword);
            changePasswordProgress(user);
            mProgressBar.setVisibility(View.VISIBLE);

        }
    }

    private void setError() {

        mTiOldPassword.setError(null);
        mTiNewPassword.setError(null);
    }

    private void changePasswordProgress(final User user) {

        class ResetPassword extends AsyncTask<String,Void,String> {
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

                   // showMessage("Password changed successfully !");
                    mProgressBar.setVisibility(View.GONE);
                    dismiss();



                    Snackbar.make(getActivity().getCurrentFocus(), "Password changed successfully !", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();


                   // handleError();

                }
                else{
                   // showMessage("Wrong old password !");
                    mProgressBar.setVisibility(View.GONE);
                    dismiss();

                    Snackbar.make(getActivity().getCurrentFocus(), "Wrong old password !", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
            }
            @Override
            protected String doInBackground(String... params) {
                HashMap<String,String> data = new HashMap<>();
                //data.put("","");
                data.put("PASSWORD", user.getNewPassword());
                RequestHandler ruc = new RequestHandler();
                //String result = ruc.sendPostRequest("http://"+parameter+":8000/authentificate"+username+"/"+password,data);
                String result = ruc.sendPostRequest("http://10.206.208.123:8000/resetPassword/"+user.getOldPassword()+"/"+user.getNewPassword(),data);
                return result;
            }
        }
        ResetPassword ulc = new ResetPassword();
        ulc.execute(user.getOldPassword(), user.getNewPassword());


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
