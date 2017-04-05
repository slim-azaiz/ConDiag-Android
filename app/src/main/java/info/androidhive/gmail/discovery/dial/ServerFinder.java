package info.androidhive.gmail.discovery.dial;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import info.androidhive.gmail.R;



public final class ServerFinder extends Activity {
	private static final String LOG_TAG = "ServerFinder";

	/**
	 * Request code used by wifi settings activity
	 */
	private static final int CODE_WIFI_SETTINGS = 1;

	private static Context context;

	private static final String HEADER_APPLICATION_URL = "Application-URL";

	private ProgressDialog progressDialog;
	private AlertDialog confirmationDialog;
	private DialServer previousDialServer;
	private List<DialServer> recentlyConnectedServers;

	private InetAddress broadcastAddress;
	private WifiManager wifiManager;
	private boolean active;
	public static List<String> tabIpFilter;
	/**
	 * Handles used to pass data back to calling activities.
	 */
	public static final String EXTRA_DIAL_SERVER = "dial_server";
	public static final String EXTRA_RECENTLY_CONNECTED = "recently_connected";

	private BroadcastHandler broadcastHandler;
	private BroadcastDiscoveryClient broadcastClient;
	private Thread broadcastClientThread;
	private TrackedDialServers trackedServers;
	private Handler handler = new Handler();

	/**
	 * Handler modelName number for a service update parameter broadcast client.
	 */
	public static final int BROADCAST_RESPONSE = 100;

	/**
	 * Handler modelName number for all delayed messages
	 */
	private static final int DELAYED_MESSAGE = 101;

	private enum DelayedMessage {
		BROADCAST_TIMEOUT, DIAL_SERVER_FOUND;

		Message obtainMessage(Handler handler) {
			Message message = handler.obtainMessage(DELAYED_MESSAGE);
			message.obj = this;
			return message;
		}
	}

	public ServerFinder() {
		tabIpFilter = new ArrayList<String>();
		try {
			broadcastAddress = InetAddress.getByName("239.255.255.250");
		} catch (UnknownHostException e) {
			Log.e(LOG_TAG, "broadcastAddress", e);
		}
		trackedServers = new TrackedDialServers();
//		previousDialServer = getIntent().getParcelableExtra(EXTRA_DIAL_SERVER);
//		recentlyConnectedServers = getIntent().getParcelableArrayListExtra(EXTRA_RECENTLY_CONNECTED);
		broadcastHandler = new BroadcastHandler();


		try {
			broadcastClient = new BroadcastDiscoveryClient(broadcastAddress, broadcastHandler);
			broadcastClientThread = new Thread(broadcastClient);
			broadcastClientThread.start();
			Message message = DelayedMessage.BROADCAST_TIMEOUT.obtainMessage(broadcastHandler);
			//broadcastHandler.sendMessageDelayed(modelName, getResources().getInteger(R.integer.broadcast_timeout));
			broadcastHandler.removeMessages(DELAYED_MESSAGE);
		} catch (RuntimeException e) {
			Log.e(LOG_TAG, "startBroadcast", e);
		}
	}
	private SwipeRefreshLayout swipeRefreshLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		//setContentView(device_finder_layout);

		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
		swipeRefreshLayout.setRefreshing(false);

		previousDialServer = getIntent().getParcelableExtra(EXTRA_DIAL_SERVER);
		recentlyConnectedServers = getIntent().getParcelableArrayListExtra(EXTRA_RECENTLY_CONNECTED);
		broadcastHandler = new BroadcastHandler();

		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
		/*swipeRefreshLayout.post(new Runnable() {
			@Override
			public void run() {
				showOtherDevices();

			}
		});
		*/
	}


	@Override
	protected void onStart() {
		super.onStart();


		try {
			broadcastAddress = InetAddress.getByName("239.255.255.250");
		} catch (UnknownHostException e) {
			Log.e(LOG_TAG, "broadcastAddress", e);
		}

		startBroadcast();
	}

	@Override
	protected void onPause() {
		active = false;
		broadcastHandler.removeMessages(DELAYED_MESSAGE);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		active = true;
	}

	@Override
	protected void onStop() {
		if (null != broadcastClient) {
			broadcastClient.stop();
			broadcastClient = null;
		}

		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(LOG_TAG, "ActivityResult: " + requestCode + ", " + resultCode);
		if (requestCode == CODE_WIFI_SETTINGS) {
			if (!isWifiAvailable()) {
				buildNoWifiDialog().show();
			} else {
				startBroadcast();
			}
		}
	}

	private void startBroadcast() {
		if (!isWifiAvailable()) {
			try {
				buildNoWifiDialog().show();
			} catch (Exception e) {
				Log.e(LOG_TAG, "startBroadcast", e);
			}
			return;
		}
		try {
			broadcastClient = new BroadcastDiscoveryClient(broadcastAddress, broadcastHandler);
			broadcastClientThread = new Thread(broadcastClient);
			broadcastClientThread.start();
			Message message = DelayedMessage.BROADCAST_TIMEOUT.obtainMessage(broadcastHandler);
			broadcastHandler.sendMessageDelayed(message, getResources().getInteger(R.integer.broadcast_timeout));
			showProgressDialog(buildBroadcastProgressDialog());
		} catch (RuntimeException e) {
			Log.e(LOG_TAG, "startBroadcast", e);
		}
	}

	private OnItemClickListener selectHandler = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			DialServer DialServer = (DialServer) parent.getItemAtPosition(position);
			if (DialServer != null) {
				connectToEntry(DialServer);
			}
		}
	};

	/**
	 * Connects to the chosen entry in the list. Finishes the activity and
	 * returns the informations on the chosen box.
	 * 
	 * @param DialServer
	 *            the listEntry representing the box you want to connect to
	 */
	private void connectToEntry(DialServer DialServer) {
		Intent resultIntent = new Intent();
		resultIntent.putExtra(EXTRA_DIAL_SERVER, DialServer);
		setResult(RESULT_OK, resultIntent);
		finish();
	}



	private void enableWifi() {
		if (null != wifiManager) {
			wifiManager.setWifiEnabled(true);
		}
	}

	/**
	 * Returns an intent that starts this activity.
	 */
	public static Intent createConnectIntent(Context ctx, DialServer recentlyConnected, ArrayList<DialServer> recentlyConnectedList) {
		Intent intent = new Intent(ctx, ServerFinder.class);
		intent.putExtra(EXTRA_DIAL_SERVER, recentlyConnected);
		intent.putParcelableArrayListExtra(EXTRA_RECENTLY_CONNECTED, recentlyConnectedList);
		return intent;
	}


	/**
	 * Represents an entry in the box list.
	 */
	public static class ListEntryView extends LinearLayout {

		public ListEntryView(Context context, AttributeSet attrs) {
			super(context, attrs);
			myContext = context;
		}

		public ListEntryView(Context context) {
			super(context);
			myContext = context;
		}

		@Override
		protected void onFinishInflate() {
			super.onFinishInflate();
			tvName = (TextView) findViewById(R.id.device_list_item_name);
			tvTargetAddr = (TextView) findViewById(R.id.device_list_target_addr);
		}

		private void updateContents() {
			if (null != tvName) {
				String txt = myContext.getString(R.string.unkown_tgt_name);
				if (null != listEntry) {
					txt = formatName(listEntry);
				}
				tvName.setText(txt);
			}

			if (null != tvTargetAddr) {
				String txt = myContext.getString(R.string.unkown_tgt_addr);
				if ((null != listEntry) && (null != listEntry.getIpAddress())) {
					txt = listEntry.getIpAddress().getHostAddress();
				}
				tvTargetAddr.setText(txt);
			}
		}

		public DialServer getListEntry() {
			return listEntry;
		}

		public void setListEntry(DialServer listEntry) {
			this.listEntry = listEntry;
			updateContents();
		}

		private Context myContext = null;
		private DialServer listEntry = null;
		private TextView tvName = null;
		private TextView tvTargetAddr = null;
	}

	private final class BroadcastHandler extends Handler {
		/** {inheritDoc} */
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == DELAYED_MESSAGE) {
				if (!active) {
					return;
				}
				switch ((DelayedMessage) msg.obj) {
				case BROADCAST_TIMEOUT:
					try {
						broadcastClient.stop();
						try {
							if (progressDialog.isShowing()) {
								progressDialog.dismiss();
							}
						} catch (Throwable e) {
							Log.e(LOG_TAG, "handleMessage", e);
						}
						buildBroadcastTimeoutDialog().show();
					} catch (Exception e1) {
						Log.e(LOG_TAG, "handleMessage", e1);
					}
					break;

				case DIAL_SERVER_FOUND:
					try {
						// Check if there is previously connected remote and
						// suggest it
						// for connection:
						DialServer toConnect = null;
						if (previousDialServer != null) {
							Log.d(LOG_TAG, "Previous Remote Device: " + previousDialServer);
							toConnect = trackedServers.findDialServer(previousDialServer);
						}
						if (toConnect == null) {
							Log.d(LOG_TAG, "No previous device found.");
							// No default found - suggest any device
							toConnect = trackedServers.get(0);
						}

						try {
							progressDialog.dismiss();
						} catch (Throwable e) {
							Log.e(LOG_TAG, "handleMessage", e);
						}
					} catch (Exception e) {
						Log.e(LOG_TAG, "handleMessage", e);
					}
					break;
				}
			}

			switch (msg.what) {
			case BROADCAST_RESPONSE:
				final BroadcastAdvertisement advert = (BroadcastAdvertisement) msg.obj;
				if (advert.getLocation() != null) {
					new Thread(new Runnable() {
						public void run() {
							HttpResponse response = new HttpRequestHelper().sendHttpGet(advert.getLocation());
							if (response != null) {
								String appsUrl = null;
								Header header = response.getLastHeader(HEADER_APPLICATION_URL);
								if (header != null) {
									appsUrl = header.getValue();
									Log.d(LOG_TAG, "appsUrl="+appsUrl);
								}
								String friendlyName = null;
								String manufacturer = null;
								String modelName = null;
								String uuid = null;
								try {
									InputStream inputStream = response.getEntity().getContent();
									BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
									XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
									factory.setNamespaceAware(true);
									XmlPullParser parser = factory.newPullParser();
									parser.setInput(reader);
									int eventType = parser.getEventType();
									String lastTagName = null;
									while (eventType != XmlPullParser.END_DOCUMENT) {
										switch (eventType) {
										case XmlPullParser.START_DOCUMENT:
											break;
										case XmlPullParser.START_TAG:
											String tagName = parser.getName();
											lastTagName = tagName;
											break;
										case XmlPullParser.TEXT:
											if (lastTagName != null) {
												if ("value".equals(lastTagName)) {
													friendlyName = parser.getText();
												} else if ("UDN".equals(lastTagName)) {
													uuid = parser.getText();
												} else if ("manufacturer".equals(lastTagName)) {
													manufacturer = parser.getText();
												} else if ("modelName".equals(lastTagName)) {
													modelName = parser.getText();
												}
											}
											break;
										case XmlPullParser.END_TAG:
											tagName = parser.getName();
											lastTagName = null;
											break;
										}
										eventType = parser.next();
									}
									inputStream.close();
								} catch (Exception e) {
									Log.e(LOG_TAG, "parse device description", e);
								}
								Log.d(LOG_TAG, "value="+friendlyName);
								final DialServer dialServer = new DialServer(advert.getLocation(), advert.getIpAddress(), advert.getPort(), appsUrl, friendlyName, uuid, manufacturer, modelName);
								handler.post(new Runnable() {
									public void run() {
										handleDialServerAdd(dialServer);
									}
								});
							}
						}
					}).start();
				}
				break;
			}
		}
	}

	private void handleDialServerAdd(final DialServer dialServer) {
		if (trackedServers.add(dialServer)) {
			//Log.v(LOG_TAG, "Adding new device: " + dialServer);

			String ipAddress =dialServer.getIpAddress().toString().substring(1 );

			//Log.i("IP_ADDRESS",parameter);
			if((!tabIpFilter.contains(ipAddress))&&(MainActivity.mPtrFrame.isRefreshing()==true)) {
				MainActivity.saveServerToLocalStorage(1, ipAddress, dialServer.getFriendlyName(), dialServer.getModelName(), "15:30pm", "mipmap://" + R.mipmap.google, 1, 1, 4);
				tabIpFilter.add(ipAddress);
			}
			// Notify data adapter and update title.
			// Show confirmation dialog only for the first STB and only if
			// progress
			// dialog is visible.
			if ((trackedServers.size() == 1) ) {
				broadcastHandler.removeMessages(DELAYED_MESSAGE);
				// delayed automatic adding
				//Server modelName = DelayedMessage.DIAL_SERVER_FOUND.obtainMessage(broadcastHandler);
				//broadcastHandler.sendMessageDelayed(modelName, getResources().getInteger(R.integer.gtv_finder_reconnect_delay));
			}
		}
	}

	private ProgressDialog buildBroadcastProgressDialog() {
		String message;
		String networkName = getNetworkName();
		if (!TextUtils.isEmpty(networkName)) {
			message = getString(R.string.finder_searching_with_ssid, networkName);
		} else {
			message = getString(R.string.finder_searching);
		}

		return buildProgressDialog(message, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int which) {
				broadcastHandler.removeMessages(DELAYED_MESSAGE);
			}
		});
	}

	private void buildBroadcastProgressDialog1() {
		String message;
		String networkName = getNetworkName();
		if (!TextUtils.isEmpty(networkName)) {
			message = getString(R.string.finder_searching_with_ssid, networkName);
		} else {
			message = getString(R.string.finder_searching);
		}

		 buildProgressDialog(message, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int which) {
				broadcastHandler.removeMessages(DELAYED_MESSAGE);
			}
		});
	}

	private ProgressDialog buildProgressDialog(String message, DialogInterface.OnClickListener cancelListener) {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage(message);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			public boolean onKey(DialogInterface dialogInterface, int which, KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
					finish();
					return true;
				}
				return false;
			}
		});
		dialog.setButton(getString(R.string.finder_cancel), cancelListener);
		return dialog;
	}


	private static final String formatName(DialServer dialServer) {
		StringBuffer buffer = new StringBuffer();
		if (dialServer.getFriendlyName() != null) {
			buffer.append(dialServer.getFriendlyName());
			if (dialServer.getModelName() != null) {
				buffer.append("/").append(dialServer.getModelName());
			}
			if (dialServer.getManufacturer() != null) {
				buffer.append("/").append(dialServer.getManufacturer());
			}
		} else {
			if (dialServer.getModelName() != null) {
				buffer.append("/").append(dialServer.getModelName());
			}
			if (dialServer.getManufacturer() != null) {
				buffer.append("/").append(dialServer.getManufacturer());
			}
		}
		return buffer.toString();
	}


	private AlertDialog buildBroadcastTimeoutDialog() {
		String message;
		String networkName = getNetworkName();
		if (!TextUtils.isEmpty(networkName)) {
			message = getString(R.string.finder_no_devices_with_ssid, networkName);
		} else {
			message = getString(R.string.finder_no_devices);
		}

		return buildTimeoutDialog(message, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int id) {
				startBroadcast();
			}
		});
	}

	private AlertDialog buildTimeoutDialog(CharSequence message, DialogInterface.OnClickListener retryListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		return builder.setMessage(message).setCancelable(false).setNegativeButton(R.string.finder_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int id) {
				setResult(RESULT_CANCELED, null);
				finish();
			}
		}).create();
	}

	private AlertDialog buildNoWifiDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(R.string.finder_wifi_not_available);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.finder_configure, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int id) {
				Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
				startActivityForResult(intent, CODE_WIFI_SETTINGS);
			}
		});
		builder.setNegativeButton(R.string.finder_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int id) {
				setResult(RESULT_CANCELED, null);
				finish();
			}
		});
		return builder.create();
	}

	private void showProgressDialog(ProgressDialog newDialog) {
		try {
			try {
				if ((progressDialog != null) && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			} catch (Throwable e) {
				Log.e(LOG_TAG, "showProgressDialog", e);
			}
			progressDialog = newDialog;
			newDialog.show();
		} catch (Exception e) {
			Log.e(LOG_TAG, "showProgressDialog", e);
		}
	}


	private boolean isSimulator() {
		return Build.FINGERPRINT.startsWith("generic");
	}

	private boolean isWifiAvailable() {
		try {
			if (isSimulator()) {
				return true;
			}
			if (!wifiManager.isWifiEnabled()) {
				return false;
			}
			WifiInfo info = wifiManager.getConnectionInfo();
			return info != null && info.getIpAddress() != 0;
		} catch (Exception e) {
			Log.e(LOG_TAG, "isWifiAvailable", e);
		}
		return false;
	}

	private String getNetworkName() {
		if (isSimulator()) {
			return "generic";
		}
		if (!isWifiAvailable()) {
			return null;
		}
		WifiInfo info = wifiManager.getConnectionInfo();
		return info != null ? info.getSSID() : null;
	}

    private DialServer DialServerFromString(String text) {
        String[] ipPort = text.split(":");
        int port;
        if (ipPort.length == 1) {
            port = getResources().getInteger(R.integer.manual_default_port);
        } else if (ipPort.length == 2) {
            try {
                port = Integer.parseInt(ipPort[1]);
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            return null;
        }

        try {
            InetAddress address = InetAddress.getByName(ipPort[0]);
            // TODO
            return new DialServer(null, address, port, null, getString(R.string.manual_ip_default_box_name), null, null, null);
        } catch (UnknownHostException e) {
        }
        return null;
    }

}
