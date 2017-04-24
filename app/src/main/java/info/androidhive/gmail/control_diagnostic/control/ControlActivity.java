package info.androidhive.gmail.control_diagnostic.control;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

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
  private ImageButton bVolUp;
  private ImageButton bVolDown;
  private ImageButton bChannelUp;
  private ImageButton bChannelDown;
  private ImageButton bUp;
  private ImageButton bDown;
  private ImageButton bLeft;
  private ImageButton bRight;
  private ImageButton bExit;
  private ImageButton bMenu;
  private ImageButton bRed;
  private ImageButton bGreen;
  private ImageButton bBlue;
  private ImageButton bYellow;
  private ImageButton bInfo;
  private ImageButton bSelect;
  private ImageButton bBack;
  private ImageButton bMute;

  private Context context;
  private String ipAddress;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.wow);

    bPower = (ImageButton) findViewById(R.id.bPower);
    bPower.setOnClickListener(this);
    bVolUp = (ImageButton) findViewById(R.id.bVolUp);
    bVolUp.setOnClickListener(this);
    bVolDown = (ImageButton) findViewById(R.id.bVolDown);
    bVolDown.setOnClickListener(this);
    bChannelUp = (ImageButton) findViewById(R.id.bChannelUp);
    bChannelUp.setOnClickListener(this);
    bChannelDown = (ImageButton) findViewById(R.id.bChannelDown);
    bChannelDown.setOnClickListener(this);
    bUp = (ImageButton) findViewById(R.id.bUp);
    bUp.setOnClickListener(this);

    bDown = (ImageButton) findViewById(R.id.bDown);
    bDown.setOnClickListener(this);
    bLeft = (ImageButton) findViewById(R.id.bLeft);
    bLeft.setOnClickListener(this);
    bRight = (ImageButton) findViewById(R.id.bRight);
    bRight.setOnClickListener(this);
    bExit = (ImageButton) findViewById(R.id.bExit);
    bExit.setOnClickListener(this);
    bMenu = (ImageButton) findViewById(R.id.bMenu);
    bMenu.setOnClickListener(this);
    bRed = (ImageButton) findViewById(R.id.bRed);
    bRed.setOnClickListener(this);
    bGreen = (ImageButton) findViewById(R.id.bGreen);
    bGreen.setOnClickListener(this);
    bBlue = (ImageButton) findViewById(R.id.bBlue);
    bBlue.setOnClickListener(this);
    bInfo = (ImageButton) findViewById(R.id.bInfo);
    bInfo.setOnClickListener(this);
    bSelect = (ImageButton) findViewById(R.id.bSelect);
    bSelect.setOnClickListener(this);
    bBack = (ImageButton) findViewById(R.id.bBack);
    bBack.setOnClickListener(this);
    bMute = (ImageButton) findViewById(R.id.bMute);
    bMute.setOnClickListener(this);
    bYellow = (ImageButton) findViewById(R.id.bYellow);
    bYellow.setOnClickListener(this);

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

    final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this
            .findViewById(android.R.id.content)).getChildAt(0);
    viewGroup.setOnTouchListener(new OnSwipeTouchListener(this) {
      @Override
      public void onSwipeDown() {
        Log.d("TOUCH","Action was DOWN");
        postCommand(viewGroup,"0xe0163085");

      }

      @Override
      public void onSwipeLeft() {
        Log.d("TOUCH","Action was LEFT");
        postCommand(viewGroup,"0xe0173085");

      }

      @Override
      public void onSwipeUp() {
        Log.d("TOUCH","Action was UP");
        postCommand(viewGroup,"0xe0143085");

      }

      @Override
      public void onSwipeRight() {
        Log.d("TOUCH","Action was RIGHT");
        postCommand(viewGroup,"0xe0153085");

      }
    });
  }

  @Override
  public void onClick(View view) {
    Command command = null;
    switch (view.getId()) {
      case R.id.bPower:
        Log.i("bPower","Success");
        postCommand(view,"0x708b3085");
        break;
      case R.id.bVolUp:
        postCommand(view,"0xe01c3085");
        break;
      case R.id.bVolDown:
        postCommand(view,"0xe01d3085");
        break;
      case R.id.bChannelUp:
        postCommand(view,"0xe01e3085");
        break;
      case R.id.bChannelDown:
        postCommand(view,"0xe01f3085");
        break;
      case R.id.bUp:
        Log.i("bUp","Success");
        postCommand(view,"0xe0143085");
        break;
      case R.id.bDown:
        postCommand(view,"0xe0163085");
        break;
      case R.id.bLeft:
        postCommand(view,"0xe0173085");
        break;
      case R.id.bRight:
        postCommand(view,"0xe0153085");
        break;
      case R.id.bSelect:
        postCommand(view,"0xe0193085");
        break;
      case R.id.bInfo:
        postCommand(view,"0xe0133085");
        break;
      case R.id.bMenu:
        postCommand(view,"0xe0163085");
        break;
      case R.id.bExit:
       // postCommand(view,"0xe0163085");
        break;
      case R.id.bMute:
        postCommand(view,"0xe01b3085");
        break;
      case R.id.bBack:
        postCommand(view,"0xd0263085");
        break;
      case R.id.bRed:
        postCommand(view,"0x90603085");
        break;
      case R.id.bBlue:
        postCommand(view,"0x90633085");
        break;
      case R.id.bYellow:
        postCommand(view,"0x90623085");
        break;
      case R.id.bGreen:
        postCommand(view,"0x90613085");
        break;
      default:
        break;
    }
  }
  private void postCommand(final View view,String key){
    Retrofit retrofit = new Retrofit.Builder()
            //.baseUrl("http://"+ipAddress+":8000")
            .baseUrl("http://10.206.208.70:8000")
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


          Snackbar.make(view, "Unable to fetch json", Snackbar.LENGTH_INDEFINITE)
                  .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                  })
                  .show();

        }
      }
    });

  }

}
