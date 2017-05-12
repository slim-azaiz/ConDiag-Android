package info.androidhive.gmail.control_diagnostic.control;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Map;

import info.androidhive.gmail.R;
import info.androidhive.gmail.control_diagnostic.control.basicmultitouch.TouchActivity;
import info.androidhive.gmail.network.JSONResponse;
import info.androidhive.gmail.network.RequestInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static info.androidhive.gmail.utils.Config.CONTROL_LOG;


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

    SharedPreferences myPrefs2 = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
    String ipAddress2 = myPrefs2.getString("ipAddress","");
    Toast.makeText(ControlActivity.this, "ipAddress " + ipAddress2, Toast.LENGTH_SHORT).show();


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



    SharedPreferences prefs = getSharedPreferences("MyPref",Context.MODE_PRIVATE);
    ipAddress = prefs.getString("ipAddress", null);
    Log.d(CONTROL_LOG,"ipAddress"+ipAddress);
  }

  @Override
  public void onClick(View view) {
    Command command = null;
    switch (view.getId()) {
      case R.id.bPower:
        Log.i("bPower","Success");
        postCommand(view,"0x708b3085");
        //0x30002603
        break;
      case R.id.bVolUp:
        postCommand(view,"0xe01c3085");
        //0x30002610
        break;
      case R.id.bVolDown:
        postCommand(view,"0xe01d3085");
        //0x30002611
        break;
      case R.id.bChannelUp:
        postCommand(view,"0xe01e3085");
        //0x30002620
        break;
      case R.id.bChannelDown:
        postCommand(view,"0xe01f3085");
        //0x30002621
        break;
      case R.id.bUp:
        Log.i("bUp","Success");
        postCommand(view,"0xe0143085");
        //0x30002658
        break;
      case R.id.bDown:
        postCommand(view,"0xe0163085");
        //0x30002659
        break;
      case R.id.bLeft:
        postCommand(view,"0xe0173085");
        //0x3000265a
        break;
      case R.id.bRight:
        postCommand(view,"0xe0153085");
        //0x3000265b
        break;
      case R.id.bSelect:
        postCommand(view,"0xe0193085");
        //0x3000265c
        break;
      case R.id.bInfo:
        postCommand(view,"0xe0133085");
        //0x3000263c
        break;
      case R.id.bMenu:
        postCommand(view,"0xf00c3085");
        //0x30002654
        break;
      case R.id.bExit:
        //postCommand(view,"0xe0163085");
        //
        break;
      case R.id.bMute:
        postCommand(view,"0xe01b3085");
        //0x3000260d
        break;
      case R.id.bBack:
        postCommand(view,"0xd0263085");
        //
        break;
      case R.id.bRed:
        postCommand(view,"0x90603085");
        //0x3000266d
        break;
      case R.id.bBlue:
        postCommand(view,"0x90633085");
        //0x30002670
        break;
      case R.id.bYellow:
        postCommand(view,"0x90623085");
        //0x3000266f
        break;
      case R.id.bGreen:
        postCommand(view,"0x90613085");
        //0x3000266e
        break;
      default:
        break;
    }
    //1  0xd0283085 0x30002601
    //2  0xd0293085 0x30002602
    //3  0xd02a3085 0x30002603
    //4  0xd02b3085 0x30002604
    //5  0xd02c3085 0x30002605
    //6  0xd02d3085 0x30002606
    //7  0xd02e3085 0x30002607
    //8  0xd02f3085 0x30002608
    //9  0xc0303085 0x30002609
    //0  0xc0313085 0x30002600
    //0 left    0x3000264d
    //0 right   0x3000264c

    //pause  0xd0213085  0x30002633
    //left   0xd0223085  0x3000262b
    //right  0xd0243085  0x3000262a
    //rec    0xd0203085  0x30002637

  }
  public static void postCommand(final View view,String key){
    Retrofit retrofit = new Retrofit.Builder()
            //.baseUrl("http://"+ipAddress+":8000")
            .baseUrl("http://10.206.208.98:8000")
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
        Log.i("MESSAGE",t.
                getMessage());
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

  public static void postCommand(String key){
    Retrofit retrofit = new Retrofit.Builder()
            //.baseUrl("http://"+ipAddress+":8000")
            .baseUrl("http://10.206.208.73:8000")
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
        Log.i("MESSAGE",t.
                getMessage());

      }
    });

  }



  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_control, menu);
    MenuItem direction = menu.findItem(R.id.direction);
    return true;
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    switch (item.getItemId()) {
      case R.id.direction:
        Intent intent = new Intent(ControlActivity.this, TouchActivity.class);
        startActivity(intent);
        return true;
    }

    return super.onOptionsItemSelected(item);
  }


}
