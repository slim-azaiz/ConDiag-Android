package info.androidhive.gmail.login;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import info.androidhive.gmail.control_diagnostic.ControlDiagnostic;
import info.androidhive.gmail.network.RequestInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.subscriptions.CompositeSubscription;

import static android.content.Context.MODE_WORLD_READABLE;
import static info.androidhive.gmail.control_diagnostic.diagnostic.DiagnosticActivity.url;
import static info.androidhive.gmail.login.Validation.validateFields;
import static info.androidhive.gmail.utils.Config.BASE_URL;


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
    private String url;

    public ChangeUsernameDialog(String url){
        this.url = url;
    }


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
            resetUsername(user);
            mProgressBar.setVisibility(View.VISIBLE);

        }
    }

    private void setError() {

        mTiOldUsername.setError(null);
        mTiNewUsername.setError(null);
    }


    private void resetUsername(final User user){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<String> call ;
        call = request.resetUsername(user.getOldUsername(),user.getNewUsername());
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
