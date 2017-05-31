package info.androidhive.gmail.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import info.androidhive.gmail.R;
import info.androidhive.gmail.control_diagnostic.ControlDiagnostic;
import info.androidhive.gmail.control_diagnostic.diagnostic.DiagnosticType;
import info.androidhive.gmail.model.Diagnostic;
import info.androidhive.gmail.network.JSONResponse;
import info.androidhive.gmail.network.RequestInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


import java.util.ArrayList;
import java.util.Arrays;

import static info.androidhive.gmail.login.Validation.validateFields;
import static info.androidhive.gmail.utils.Config.BASE_URL;
import static info.androidhive.gmail.utils.Config.DEFAULT_PORT;
import static info.androidhive.gmail.utils.Config.isWifiAvailable;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextUserName;
    private EditText editTextPassword;

    private  Button buttonLogin;
    private String ipAddress;
    private String url;
    private ProgressBar mProgressBar;


    private TextInputLayout mTiEmail;
    private TextInputLayout mTiPassword;
    private TextView mTvForgotPassword;
    private TextView mTvForgotUsename;
    private DiagnosticType diagnosticType;
    public static ArrayList<Diagnostic> dataIdentification;
    public static ArrayList<Diagnostic> dataSoftware;
    public static ArrayList<Diagnostic> dataSysInfo;
    public static ArrayList<Diagnostic> dataMemory;
    public static ArrayList<Diagnostic> dataTuner;
    public static ArrayList<Diagnostic> dataVirtualTuner;
    public static ArrayList<Diagnostic> dataNetwork;
    public static  ArrayList<Diagnostic> dataNvmem;
    public static ArrayList<Diagnostic> dataLoader;
    public static ArrayList<Diagnostic> dataConditionalAccess;

    public  static SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //retreive parameter
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                ipAddress= null;
            } else {
                ipAddress= extras.getString("IpAddress");
            }
        } else {
            ipAddress= (String) savedInstanceState.getSerializable("IpAddress");
        }

/*
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();



        editor.putString("ipAddress", ipAddress);  // Saving string//        Snackbar.make(getCurrentFocus(), ipAddress, Snackbar.LENGTH_LONG)
  //              .setAction("Action", null).show();

        String ipAddress2 = pref.getString("ipAddress", "");
        Toast.makeText(Login.this, "ipAddress " + ipAddress2, Toast.LENGTH_SHORT).show();
*/
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = myPrefs.edit();
        url = "http://"+ipAddress+":"+DEFAULT_PORT;
        editor.putString("ipAddress", url);
        editor.commit();

        mProgressBar = (ProgressBar) findViewById(R.id.progress);

        editTextUserName = (EditText) findViewById(R.id.username);
        editTextPassword = (EditText) findViewById(R.id.password);
        mTiEmail = (TextInputLayout) findViewById(R.id.ti_email);
        mTiPassword = (TextInputLayout) findViewById(R.id.ti_password);
        mTvForgotPassword = (TextView) findViewById(R.id.tv_forgot_password);
        mTvForgotUsename = (TextView) findViewById(R.id.tv_forgot_username);


        buttonLogin = (Button) findViewById(R.id.buttonUserLogin);

        buttonLogin.setOnClickListener(this);
        mTvForgotPassword.setOnClickListener(this);
        mTvForgotUsename.setOnClickListener(this);


    }

    private void authenticiate(final String username, final String password){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                //.baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<String> call ;
        call = request.authenticiate(username,password);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.body().toString().equalsIgnoreCase("success")){
                    Intent intent = new Intent(Login.this, ControlDiagnostic.class);
                    loadJSON();
                    startActivity(intent);
                }
                else {
                    mProgressBar.setVisibility(View.GONE);
                    Snackbar.make(getCurrentFocus(), response.body().toString(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //  mPtrFrame.refreshComplete();
                Log.i("MESSAGE",t.getMessage());
            }


        });
    }


    private void loadJSON() {
        for (DiagnosticType c : DiagnosticType.values()) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    //.baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            RequestInterface request = retrofit.create(RequestInterface.class);
            Call<JSONResponse> call;

            if(c.name() == DiagnosticType.identification.name()) {
                        call = request.getIdentification();
                        call.enqueue(new Callback<JSONResponse>() {
                            @Override
                            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                                JSONResponse jsonResponse = response.body();
                                dataIdentification = new ArrayList<>(Arrays.asList(jsonResponse.getIdentification()));
                            }
                            @Override
                            public void onFailure(Call<JSONResponse> call, Throwable t) {
                            }
                        });
                    }
                    else if (c.name() == DiagnosticType.conditionalAccess.name()) {
                        call = request.getConditionalAccess();
                        call.enqueue(new Callback<JSONResponse>() {
                            @Override
                            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                                JSONResponse jsonResponse = response.body();
                                dataConditionalAccess = new ArrayList<>(Arrays.asList(jsonResponse.getConditionalAccess()));
                            }
                            @Override
                            public void onFailure(Call<JSONResponse> call, Throwable t) {
                            }
                        });
                    }
                    else if (c.name() == DiagnosticType.loader.name()) {
                        call = request.getLoader();
                        call.enqueue(new Callback<JSONResponse>() {
                            @Override
                            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                                JSONResponse jsonResponse = response.body();
                                dataLoader = new ArrayList<>(Arrays.asList(jsonResponse.getLoader()));
                            }
                            @Override
                            public void onFailure(Call<JSONResponse> call, Throwable t) {
                            }
                        });
                    }
                    else if (c.name() == DiagnosticType.memory.name()) {
                        call = request.getMemory();
                        call.enqueue(new Callback<JSONResponse>() {
                            @Override
                            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                                JSONResponse jsonResponse = response.body();
                                dataMemory = new ArrayList<>(Arrays.asList(jsonResponse.getMemory()));
                            }
                            @Override
                            public void onFailure(Call<JSONResponse> call, Throwable t) {
                            }
                        });
                    }
                    else if (c.name() == DiagnosticType.network.name()) {
                        call = request.getNetwork();
                        call.enqueue(new Callback<JSONResponse>() {
                            @Override
                            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                                JSONResponse jsonResponse = response.body();
                                dataNetwork = new ArrayList<>(Arrays.asList(jsonResponse.getNetwork()));
                            }
                            @Override
                            public void onFailure(Call<JSONResponse> call, Throwable t) {
                            }
                        });
                    }

                    else if (c.name() == DiagnosticType.nvmem.name()) {
                        call = request.getNvmem();
                        call.enqueue(new Callback<JSONResponse>() {
                            @Override
                            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                                JSONResponse jsonResponse = response.body();
                                dataNvmem = new ArrayList<>(Arrays.asList(jsonResponse.getNvmem()));
                            }
                            @Override
                            public void onFailure(Call<JSONResponse> call, Throwable t) {
                            }
                        });
                    }
                    else if (c.name() == DiagnosticType.qamTunerStatus.name()) {
                        call = request.getQamTunerStatus();
                        call.enqueue(new Callback<JSONResponse>() {
                            @Override
                            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                                JSONResponse jsonResponse = response.body();
                                dataTuner = new ArrayList<>(Arrays.asList(jsonResponse.getQamTunerStatus()));
                            }
                            @Override
                            public void onFailure(Call<JSONResponse> call, Throwable t) {
                            }
                        });
                    }
                    else if (c.name() == DiagnosticType.qamVirtualTunerStatus.name()) {
                        call = request.getQamVirtualTunerStatus();
                        call.enqueue(new Callback<JSONResponse>() {
                            @Override
                            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                                JSONResponse jsonResponse = response.body();
                                dataVirtualTuner = new ArrayList<>(Arrays.asList(jsonResponse.getVirtualQamTunerStatus()));
                            }
                            @Override
                            public void onFailure(Call<JSONResponse> call, Throwable t) {
                            }
                        });
                    }
                    else if (c.name() == DiagnosticType.software.name()) {
                        call = request.getSoftware();
                        call.enqueue(new Callback<JSONResponse>() {
                            @Override
                            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                                JSONResponse jsonResponse = response.body();
                                dataSoftware = new ArrayList<>(Arrays.asList(jsonResponse.getSoftware()));
                            }
                            @Override
                            public void onFailure(Call<JSONResponse> call, Throwable t) {
                            }
                        });
                    }
                    else if (c.name() == DiagnosticType.sysInfo.name()) {
                        call = request.getSysInfo();
                        call.enqueue(new Callback<JSONResponse>() {
                            @Override
                            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                                JSONResponse jsonResponse = response.body();
                                dataSysInfo = new ArrayList<>(Arrays.asList(jsonResponse.getSysInfo()));
                            }
                            @Override
                            public void onFailure(Call<JSONResponse> call, Throwable t) {
                            }
                        });
                    }
        }
    }



    private void setError() {

        mTiEmail.setError(null);
        mTiPassword.setError(null);
    }

    private void login() {
        setError();

        String username = editTextUserName.getText().toString().trim();

        String password = editTextPassword.getText().toString().trim();





        int err = 0;

        if (!validateFields(username)) {

            err++;
            mTiEmail.setError("username should not be empty !");
        }

        if (!validateFields(password)) {

            err++;
            mTiPassword.setError("Password should not be empty !");
        }

        if (err == 0) {
            if (!isWifiAvailable(getBaseContext())) {
                Snackbar.make(getCurrentFocus(), "Wifi is not available", Snackbar.LENGTH_LONG)
                        .show();
            }else{
                authenticiate(username, password);
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }
    }


    private void showPasswordDialog(){

        ChangePasswordDialog fragment = new ChangePasswordDialog(url);

        fragment.show(getFragmentManager(), ChangePasswordDialog.TAG);
    }
    private void showUsernameDialog(){

        ChangeUsernameDialog fragment = new ChangeUsernameDialog(url);

        fragment.show(getFragmentManager(), ChangeUsernameDialog.TAG);
    }

        @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.buttonUserLogin:
                    login();
                    break;
                case R.id.tv_forgot_password:
                    showPasswordDialog();
                    break;
                case R.id.tv_forgot_username:
                    showUsernameDialog();
                    break;
                default:
                    break;

            }
    }
}
