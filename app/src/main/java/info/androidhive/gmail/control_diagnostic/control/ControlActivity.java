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
  private String ipAddress;

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
  }

  @Override
  public void onClick(View view) {
    Command command = null;
    switch (view.getId()) {
      //up 0xe0143085
      //down 0xe0163085
      //left 0xe0173085
      //right 0xe0153085
      //select 0xe0193085
      case R.id.bPower:
        postCommand(view,"0xe0193085");
        //postCommand(view,"0x708b3085");
        break;
      case R.id.bVolUp:
        postCommand(view,"0xe0153085");
        //postCommand(view,"0xe01c3085");
        break;
      case R.id.bVolDown:
        postCommand(view,"0xe0173085");
        //postCommand(view,"0xe01d3085");
        break;
      case R.id.bChannelUp:
        postCommand(view,"0xe0143085");
        //postCommand(view,"0xe01e3085");
        break;
      case R.id.bChannelDown:
        postCommand(view,"0xe0163085");
        // /postCommand(view,"0xe01f3085");
        break;
      default:
        break;
    }
  }
  private void postCommand(final View view,String key){
    Retrofit retrofit = new Retrofit.Builder()
            //.baseUrl("http://"+ipAddress+":8000")
            .baseUrl("http://10.206.208.78:8000")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    RequestInterface request = retrofit.create(RequestInterface.class);
    Call<JSONResponse> call ;
    call = request.encoded(key);
    call.enqueue(new Callback<JSONResponse>() {
      @Override
      public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {

      }
      @Override
      public void onFailure(Call<JSONResponse> call, Throwable t) {
        //  mPtrFrame.refreshComplete();
        Log.i("MESSAGE",t.getMessage());
        if(!t.getMessage().contains("JsonReader")) {


          Snackbar.make(view, "Unable to fetch json: " + t.getMessage(), Snackbar.LENGTH_LONG)
                  .setAction("Action", null).show();

        }
      }
    });

  }
}
