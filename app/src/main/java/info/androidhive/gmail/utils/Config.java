package info.androidhive.gmail.utils;


import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

public class Config extends Activity {
    //urls
    public static final String BASE_URL = "http://10.206.208.109:8000/";

    //logs
    public static final String WAKE_ON_LAN_LOG = "WOL";
    public static final String DISCOVERY_LOG = "DISCOVERY";
    public static final String CONTROL_LOG = "CONTROL";
    public static final String DIAGNOSTIC_LOG = "DIAGNOSTIC";
    public static final String LOGIN_LOG = "LOGIN";
    public static final String HISTORY_LOG = "HISTORY";
    public static final String UTILS_LOG = "UTILS";
    public static final String DEFAULT_PORT = "8000";



    public static WifiManager wifiManager;


    public static boolean isWifiAvailable(Context c) {
        try {
            if (isSimulator()) {
                return true;
            }
            wifiManager = (WifiManager) c.getSystemService(WIFI_SERVICE);

            if (!wifiManager.isWifiEnabled()) {
                return false;
            }
            WifiInfo info = wifiManager.getConnectionInfo();
            return info != null && info.getIpAddress() != 0;
        } catch (Exception e) {
            Log.e(UTILS_LOG, "isWifiAvailable", e);
        }
        return false;
    }
    //when i have a problem of a static function
    /*public static boolean isNetworkConnected(Context c) {
        ConnectivityManager conManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conManager.getActiveNetworkInfo();
        return ( netInfo != null && netInfo.isConnected() );
    }
    */
    public static String getNetworkName(Context c) {
        if (isSimulator()) {
            return "generic";
        }
        if (!isWifiAvailable(c)) {
            return null;
        }
        wifiManager = (WifiManager) c.getSystemService(WIFI_SERVICE);

        WifiInfo info = wifiManager.getConnectionInfo();
        return info != null ? info.getSSID() : null;
    }
    public static boolean isSimulator() {
        return Build.FINGERPRINT.startsWith("generic");
    }


}
