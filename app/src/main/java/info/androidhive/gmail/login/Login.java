package info.androidhive.gmail.login;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import info.androidhive.gmail.R;
import info.androidhive.gmail.activity.MainActivity;
import info.androidhive.gmail.control_diagnostic.ControlDiagnostic;
import info.androidhive.gmail.control_diagnostic.diagnostic.DiagnosticActivity;
import info.androidhive.gmail.utils.Config;


import java.util.HashMap;

import static info.androidhive.gmail.login.Validation.validateFields;
import static info.androidhive.gmail.utils.Config.CONTROL_LOG;
import static info.androidhive.gmail.utils.Config.LOGIN_LOG;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextUserName;
    private EditText editTextPassword;

    private Button buttonLogin;
    private String ipAddress;

    private TextInputLayout mTiEmail;
    private TextInputLayout mTiPassword;
    private TextView mTvForgotPassword;
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
        editor.putString("ipAddress", ipAddress);
        editor.commit();

        Toast.makeText(Login.this, "ipAddress " + ipAddress, Toast.LENGTH_SHORT).show();


        editTextUserName = (EditText) findViewById(R.id.username);
        editTextPassword = (EditText) findViewById(R.id.password);
        mTiEmail = (TextInputLayout) findViewById(R.id.ti_email);
        mTiPassword = (TextInputLayout) findViewById(R.id.ti_password);
        mTvForgotPassword = (TextView) findViewById(R.id.tv_forgot_password);


        buttonLogin = (Button) findViewById(R.id.buttonUserLogin);

        buttonLogin.setOnClickListener(this);
        mTvForgotPassword.setOnClickListener(this);

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






       // if ((editTextUserName.getText().toString().isEmpty()) || (editTextPassword.getText().toString().isEmpty())) {
         //   Toast.makeText(Login.this, "Please fill in the blanks", Toast.LENGTH_LONG).show();
       // } else {
             postInformation(username, password);
        }
    }


    private void postInformation(final String username, final String password){
        class Authentificate extends AsyncTask<String,Void,String>{
                ProgressDialog loading;
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    //loading = ProgressDialog.show(Login.this, "Please Wait", null, true, true);
                    loading = new ProgressDialog(Login.this);
                    loading.setTitle("Please wait ..");
                    loading.setIndeterminate(true);
                    loading.setCancelable(false);
                    loading.show();
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    loading.dismiss();
                    if(s.equalsIgnoreCase("success")){
                        Intent intent = new Intent(Login.this,ControlDiagnostic.class);
                        intent.putExtra(Config.KEY_USER_NAME,username);
                        startActivity(intent);
                    }
                    else{
                        if (s.isEmpty()){
                            Snackbar.make(getCurrentFocus(),"ERROR", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }else {
                        Snackbar.make(getCurrentFocus(), s, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        }
                    }
                }
                @Override
                protected String doInBackground(String... params) {
                    HashMap<String,String> data = new HashMap<>();
                    //data.put("","");
                    //data.put("PASSWORD", password);
                    RequestHandler ruc = new RequestHandler();
                   String result = ruc.sendPostRequest("http://"+ipAddress+":8000/authentificate/"+username+"/"+password,data);
                   // String result = ruc.sendPostRequest("http://10.206.208.162:8000/authentificate/"+username+"/"+password,data);
                    return result;
                }
            }
            Authentificate ulc = new Authentificate();
            ulc.execute(username, password);
        }
    private void showDialog(){

        ChangePasswordDialog fragment = new ChangePasswordDialog();

        fragment.show(getFragmentManager(), ChangePasswordDialog.TAG);
    }

        @Override
    public void onClick(View v) {
        if(v == buttonLogin){
            login();
        }
            if (v==mTvForgotPassword){
                showDialog();
            }
    }
}
