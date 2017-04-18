package info.androidhive.gmail.control_diagnostic.control;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import info.androidhive.gmail.R;
import info.androidhive.gmail.control_diagnostic.diagnostic.Fragment1;
import info.androidhive.gmail.network.JSONResponse;
import info.androidhive.gmail.network.RequestInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ControlActivity extends AppCompatActivity implements View.OnClickListener {

  private Map<CommandType, Command> commandMap;
  private ImageButton bPower;
  private ImageButton bVolumeUp;
  private ImageButton bVolumeDown;
  private ImageButton bChannelUp;
  private ImageButton bChannelDown;
  private ImageButton bDigits;
  private Context context;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_control);

    bPower = (ImageButton) findViewById(R.id.bPower);
    bPower.setOnClickListener(this);
    bVolumeUp = (ImageButton) findViewById(R.id.bVolUp);
    bVolumeUp.setOnClickListener(this);
    bVolumeDown = (ImageButton) findViewById(R.id.bVolDown);
    bVolumeDown.setOnClickListener(this);
    bChannelUp = (ImageButton) findViewById(R.id.bChannelUp);
    bChannelUp.setOnClickListener(this);
    bChannelDown = (ImageButton) findViewById(R.id.bChannelDown);
    bChannelDown.setOnClickListener(this);
    //deviceSpinner.setOnItemSelectedListener(this);
  }

  @Override
  public void onClick(View v) {
    Command command = null;
    switch (v.getId()) {
      case R.id.bPower:
        postCommand();
        break;
      case R.id.bVolUp:
        break;
      case R.id.bVolDown:
        break;
      case R.id.bChannelUp:
        break;
      case R.id.bChannelDown:
        break;
      default:
        break;
    }
  }
  private void postCommand(){
    Retrofit retrofit = new Retrofit.Builder()
            //.baseUrl("http://"+ipAddress+":8000")
            .baseUrl("http://10.206.208.170:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    RequestInterface request = retrofit.create(RequestInterface.class);
    Call<JSONResponse> call ;
    call = request.encoded("hi");
    call.enqueue(new Callback<JSONResponse>() {
      @Override
      public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
        Log.i("POWER","1");

      }
      @Override
      public void onFailure(Call<JSONResponse> call, Throwable t) {
        //  mPtrFrame.refreshComplete();
        //Snackbar.make(getCurrentFocus(), "Unable to fetch json: " + t.getMessage(), Snackbar.LENGTH_LONG)
        //        .setAction("Action", null).show();
        Log.i("ERROR","1");

      }
    });

  }
}
