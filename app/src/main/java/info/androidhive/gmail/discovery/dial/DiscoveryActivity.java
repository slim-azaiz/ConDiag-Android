package info.androidhive.gmail.discovery.dial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import info.androidhive.gmail.R;
import info.androidhive.gmail.discovery.android_websockets.WebSocketClient;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;


public class DiscoveryActivity extends Activity {
	private static final String LOG_TAG = "DiscoveryActivity";
	public static final String PREFS_NAME = "preferences";

	protected static final int CODE_SWITCH_SERVER = 1;


	public static DialServer target;

	private static LinkedHashMap<InetAddress, DialServer> recentlyConnected = new LinkedHashMap<InetAddress, DialServer>();

	private WebSocketClient client;
	private String connectionServiceUrl;
	private String state;
	private String protocol;
	private String response;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.discovery);


		startActivityForResult(ServerFinder.createConnectIntent(this, target, getRecentlyConnected()), CODE_SWITCH_SERVER);
	}





	/*
	 * The user has selected a DIAL server to connect to
	 *
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (requestCode == CODE_SWITCH_SERVER) {
			if (resultCode == RESULT_OK && data != null) {
				final DialServer dialServer = data.getParcelableExtra(ServerFinder.EXTRA_DIAL_SERVER);
				if (dialServer != null) {
					Toast.makeText(DiscoveryActivity.this, getString(R.string.finder_connected, dialServer.toString()), Toast.LENGTH_LONG).show();

				}
			}
		}
	}


	/**
	 * @return list of recently connected devices
	 */
	public static ArrayList<DialServer> getRecentlyConnected() {
		ArrayList<DialServer> devices = new ArrayList<DialServer>(recentlyConnected.values());
		Collections.reverse(devices);
		return devices;
	}


}
