package info.androidhive.gmail.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import info.androidhive.gmail.control_diagnostic.ControlDiagnostic;
import info.androidhive.gmail.control_diagnostic.diagnostic.DiagnosticActivity;
import info.androidhive.gmail.utils.Config;


import java.util.HashMap;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private EditText editTextUserName;
    private EditText editTextPassword;

    private Button buttonLogin;
    private TextView mTextView;
    private String userid="";
    private String ipAddress;


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
        Toast.makeText(Login.this, ipAddress, Toast.LENGTH_LONG).show();

        editTextUserName = (EditText) findViewById(R.id.username);
        editTextPassword = (EditText) findViewById(R.id.password);
        mTextView = (TextView) findViewById(R.id.textView);


        buttonLogin = (Button) findViewById(R.id.buttonUserLogin);

        buttonLogin.setOnClickListener(this);
    }


    private void login() {
        String username = editTextUserName.getText().toString().trim();

        String password = editTextPassword.getText().toString().trim();
        if ((editTextUserName.getText().toString().isEmpty()) || (editTextPassword.getText().toString().isEmpty())) {
            Toast.makeText(Login.this, "Please fill in the blanks", Toast.LENGTH_LONG).show();
        } else {
            // postInformation(username, password);
             mTextView.setText("response "+ getInformation());
        }
    }




    private String getInformation(){
        final String url = Config.URL_GET_ID;
        class UpdateUser extends AsyncTask<Void,Void,String>{
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Login.this,"Loading...","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
            }

            @Override
            protected String doInBackground(Void... params) {

                RequestHandler rh = new RequestHandler();
                mTextView.setText("response0 ");

                userid = rh.sendGetRequest(url);
                mTextView.setText(userid);

                // Toast.makeText(Login.this,url, Toast.LENGTH_LONG).show();
                Log.i("RESPONSE",userid);
                try {
                    JSONObject jsonObject = new JSONObject(userid);

                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    JSONObject jo = jsonArray.getJSONObject(1);
                    userid = jo.getString("username");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //s = jo.getString("id");
                return userid;
            }
        }

        UpdateUser ue = new UpdateUser();
        ue.execute();
        return userid;
    }





    private void postInformation(final String username, final String password){
        class Authentificate extends AsyncTask<String,Void,String>{
                ProgressDialog loading;
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    //loading = ProgressDialog.show(Login.this, "Please Wait", null, true, true);
                    loading = new ProgressDialog(Login.this);
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
                        Intent intent = new Intent(Login.this,DiagnosticActivity.class);
                        intent.putExtra(Config.KEY_USER_NAME,username);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(Login.this,s,Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                protected String doInBackground(String... params) {
                    HashMap<String,String> data = new HashMap<>();
                    //data.put("","");
                    data.put("PASSWORD", password);
                    RequestHandler ruc = new RequestHandler();
                    //String result = ruc.sendPostRequest("http://"+parameter+":8000/authentificate"+username+"/"+password,data);
                    String result = ruc.sendPostRequest("http://10.206.208.109:8000/authentificate/"+username+"/"+password,data);
                    return result;
                }
            }
            Authentificate ulc = new Authentificate();
            ulc.execute(username, password);
        }

        @Override
    public void onClick(View v) {
        if(v == buttonLogin){
            login();
        }
    }
}
