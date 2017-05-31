package info.androidhive.gmail.login;

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

import java.util.HashMap;

import info.androidhive.gmail.R;
import info.androidhive.gmail.network.RequestInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.subscriptions.CompositeSubscription;

import static info.androidhive.gmail.control_diagnostic.diagnostic.DiagnosticActivity.url;
import static info.androidhive.gmail.login.Validation.validateFields;
import static info.androidhive.gmail.utils.Config.BASE_URL;


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
    private String url;

    public ChangePasswordDialog(String url){
        this.url = url;
    }
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
            resetPassword(user);
            mProgressBar.setVisibility(View.VISIBLE);

        }
    }

    private void setError() {

        mTiOldPassword.setError(null);
        mTiNewPassword.setError(null);
    }


    private void resetPassword(final User user){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<String> call ;
        call = request.resetPassword(user.getOldPassword(),user.getNewPassword());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                mProgressBar.setVisibility(View.GONE);
                dismiss();
                Snackbar.make(getActivity().getCurrentFocus(), response.body().toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //  mPtrFrame.refreshComplete();
                mProgressBar.setVisibility(View.GONE);
                dismiss();

                Snackbar.make(getActivity().getCurrentFocus(), t.getMessage(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Log.i("MESSAGE",t.getMessage());
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
