package info.androidhive.gmail.discovery.dial;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import info.androidhive.gmail.R;
import info.androidhive.gmail.activity.*;
import info.androidhive.gmail.activity.MainActivity;
import info.androidhive.gmail.adapter.MessagesAdapter;
import info.androidhive.gmail.helper.DividerItemDecoration;
import info.androidhive.gmail.settings.SettingsActivity;
import info.androidhive.gmail.sqlite.DatabaseHelper;


public class ServerFinder extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, MessagesAdapter.MessageAdapterListener {
	private static final String LOG_TAG = "ServerFinder";

	/**
	 * Request code used by wifi settings activity
	 */
	private static final int CODE_WIFI_SETTINGS = 1;
	private static final String HEADER_APPLICATION_URL = "Application-URL";
	private ProgressDialog progressDialog;
	private AlertDialog confirmationDialog;
	private DialServer previousDialServer;
	private List<DialServer> recentlyConnectedServers;
	private InetAddress broadcastAddress;
	private WifiManager wifiManager;
	private boolean active;

	/**
	 * Handles used to pass data back to calling activities.
	 */
	public static final String EXTRA_DIAL_SERVER = "dial_server";
	public static final String EXTRA_RECENTLY_CONNECTED = "recently_connected";
	private ListView stbList;
	private  DeviceFinderListAdapter dataAdapter;
	private BroadcastHandler broadcastHandler;
	private BroadcastDiscoveryClient broadcastClient;
	private Thread broadcastClientThread;
	private TrackedDialServers trackedServers;
	private Handler handler = new Handler();

	/**
	 * Handler message number for a service update from broadcast client.
	 */
	public static final int BROADCAST_RESPONSE = 100;

	/**
	 * Handler message number for all delayed messages
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
		dataAdapter = new DeviceFinderListAdapter();
		trackedServers = new TrackedDialServers();

	}






	////////////////////////////
	private  List<info.androidhive.gmail.model.Message> messages = new ArrayList<>();
	private ArrayList<Integer> deleteClicked = new ArrayList<Integer>();
	private  RecyclerView recyclerView;
	private MessagesAdapter mAdapter;
	private SwipeRefreshLayout swipeRefreshLayout;
	private ActionMode actionMode;
	private Button buttonDiscovery;





	private TabLayout tabLayout;
	private ActionBar actionBar;
	private DrawerLayout drawerLayout;
	private ViewPager viewPager;
	public  Context context=this;

	public Context getContext() {
		return context;
	}

	///////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dataAdapter = new DeviceFinderListAdapter();
		trackedServers = new TrackedDialServers();

		setContentView(R.layout.activity_discovery);
		previousDialServer = getIntent().getParcelableExtra(EXTRA_DIAL_SERVER);

		recentlyConnectedServers = getIntent().getParcelableArrayListExtra(EXTRA_RECENTLY_CONNECTED);

		broadcastHandler = new BroadcastHandler();
		wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);



		stbList = (ListView) findViewById(R.id.stb_listD);
		stbList.setOnItemClickListener(selectHandler);
		stbList.setAdapter(dataAdapter);


		/*swipeRefreshLayout.post(new Runnable() {
			@Override
			public void run() {
				showOtherDevices();

			}
		});
		*/

				stbList.getAdapter();
				trackedServers.serverClear();
				int i=0;
				Boolean test =false;
				if (!isWifiAvailable()) {
					try {
						buildNoWifiDialog().show();
					} catch (Exception e) {
						Log.e(LOG_TAG, "startBroadcast"+i, e);
					}
				}
				else {
					Log.i(LOG_TAG, "startBroadcast" + i);
					broadcastClient = new BroadcastDiscoveryClient(broadcastAddress, broadcastHandler);
					broadcastClientThread = new Thread(broadcastClient);
					broadcastClientThread.start();
					Message message = DelayedMessage.BROADCAST_TIMEOUT.obtainMessage(broadcastHandler);
					broadcastHandler.sendMessageDelayed(message, getResources().getInteger(R.integer.broadcast_timeout));
					showProgressDialog(buildBroadcastProgressDialog());
					//buildBroadcastProgressDialog1();
					i++;
					Log.i(LOG_TAG, "startBroadcast" + i);
				}


		/////////////////////////////

		buttonDiscovery = (Button) findViewById(R.id.buttonDiscoveryD);

		Log.i("TEST","-1");

		//Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		//setSupportActionBar(toolbar);
		Log.i("TEST","0");



		recyclerView = (RecyclerView) findViewById(R.id.recycler_viewD);
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layoutD);
		swipeRefreshLayout.setOnRefreshListener(this);

		mAdapter = new MessagesAdapter(this, messages, this);
		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
		recyclerView.setLayoutManager(mLayoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
		recyclerView.setAdapter(mAdapter);

		Log.i("TEST","1");

		drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer_layoutD);

		Log.i("TEST","4");
		viewPager = (ViewPager) findViewById(R.id.viewpagerD);
		tabLayout = (TabLayout) findViewById(R.id.tabsD);

		Log.i("TEST","5");
		tabLayout.setupWithViewPager(viewPager);

		Log.i("TEST","6");
		NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_viewD);
		if (navigationView != null) {
			setupNavigationDrawerContent(navigationView);
		}
		setupNavigationDrawerContent(navigationView);


		buttonDiscovery.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v) {
				Intent intent = new Intent(ServerFinder.this, info.androidhive.gmail.discovery.dial.MainActivity.class);
				startActivity(intent);
			}
		});

		// show loader and fetch messages
		swipeRefreshLayout.post(
				new Runnable() {
					@Override
					public void run() {
						getInbox();
					}
				}
		);

		////////////////////////////
	}




	/////////////////////////////////////

	private void setupNavigationDrawerContent(NavigationView navigationView) {
		navigationView.setNavigationItemSelectedListener(
				new NavigationView.OnNavigationItemSelectedListener() {
					@Override
					public boolean onNavigationItemSelected(MenuItem menuItem) {
						switch (menuItem.getItemId()) {
							case R.id.item_navigation_drawer_accueil:
								menuItem.setChecked(true);
								drawerLayout.closeDrawer(GravityCompat.START);
								return true;
							case R.id.item_navigation_drawer_notifications:
								menuItem.setChecked(true);
								drawerLayout.closeDrawer(GravityCompat.START);
								return true;
							case R.id.item_navigation_drawer_send_email:
								menuItem.setChecked(true);
								drawerLayout.closeDrawer(GravityCompat.START);
								Intent i = new Intent(Intent.ACTION_SEND);
								i.setType("text/plain");
								i.putExtra(Intent.EXTRA_EMAIL, new String[]{"recipient@example.com"});
								i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
								i.putExtra(Intent.EXTRA_TEXT, "body of email");
								try {
									startActivity(Intent.createChooser(i, "Send mail..."));
								} catch (android.content.ActivityNotFoundException ex) {
								}
								return true;
							case R.id.item_navigation_drawer_rendez_vous:
								menuItem.setChecked(true);
								Toast.makeText(ServerFinder.this, "Launching " + menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
								drawerLayout.closeDrawer(GravityCompat.START);
								//Intent intent1 = new Intent(ClientActivity.this, AddActivity.class);
								//intent1.putExtra(Config.KEY_USER_NAME, username);

								//startActivity(intent1);
								drawerLayout.closeDrawer(GravityCompat.START);
								return true;
							case R.id.item_navigation_drawer_settings:
								menuItem.setChecked(true);
								Toast.makeText(ServerFinder.this, "Launching " + menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
								drawerLayout.closeDrawer(GravityCompat.START);
								Intent intent = new Intent(ServerFinder.this, SettingsActivity.class);



								// Intent for the activity to open when user selects the notification

								// Use TaskStackBuilder to build the back stack and get the PendingIntent
								PendingIntent pendingIntent =
										TaskStackBuilder.create(ServerFinder.this)
												// add all of DetailsActivity's parents to the stack,
												// followed by DetailsActivity itself
												.addNextIntentWithParentStack(intent)
												.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

								NotificationCompat.Builder builder = new NotificationCompat.Builder(ServerFinder.this);
								builder.setContentIntent(pendingIntent);
								startActivity(intent);

								return true;
							case R.id.item_navigation_drawer_help:
								menuItem.setChecked(true);
								Toast.makeText(ServerFinder.this, menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
								drawerLayout.closeDrawer(GravityCompat.START);
						}
						return true;
					}
				});
	}


	private void saveServerToLocalStorage(int id,String ipAddress , String friendlyName, String model, String isImportant, String name, int test1, int test2, int color) {
		info.androidhive.gmail.model.Message n = new info.androidhive.gmail.model.Message(id,ipAddress, friendlyName, model, isImportant,name,toBool(test1),toBool(test2), color) ;
		messages.add(n);
		refreshList();
	}

	private Boolean toBool(int test){
		if (test ==  1)
			return false;
		else
			return true;
	}

	private void refreshList() {
		mAdapter.notifyDataSetChanged();
	}





	/**
	 * Fetches servers
	 */
	private void getInbox() {
		swipeRefreshLayout.setRefreshing(true);

		stbList.getAdapter();
		trackedServers.serverClear();
		int i=0;
		Boolean test =false;
		if (!isWifiAvailable()) {
			try {
				buildNoWifiDialog().show();
			} catch (Exception e) {
				Log.e(LOG_TAG, "startBroadcast"+i, e);
			}
		}
		else {
			Log.i(LOG_TAG, "startBroadcast" + i);
			broadcastClient = new BroadcastDiscoveryClient(broadcastAddress, broadcastHandler);
			broadcastClientThread = new Thread(broadcastClient);
			broadcastClientThread.start();
			Message message = DelayedMessage.BROADCAST_TIMEOUT.obtainMessage(broadcastHandler);
			broadcastHandler.sendMessageDelayed(message, getResources().getInteger(R.integer.broadcast_timeout));
			showProgressDialog(buildBroadcastProgressDialog());
			//buildBroadcastProgressDialog1();
			i++;
			Log.i(LOG_TAG, "startBroadcast" + i);
		}

				mAdapter.notifyDataSetChanged();
		swipeRefreshLayout.setRefreshing(false);


        /*call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                // clear the inbox
                messages.clear();

                // add all the messages
                // messages.addAll(response.body());

                // TODO - avoid looping
                // the loop was performed to add colors to each message
                for (Message message : response.body()) {
                    // generate a random color
                    message.setColor(getRandomMaterialColor("400"));
                    messages.add(message);
                }

                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Unable to fetch json: " + t.getModel(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });*/
	}

	/**
	 * chooses a random color from array.xml
	 */
	private int getRandomMaterialColor(String typeColor) {
		int returnColor = Color.GRAY;
		int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getPackageName());

		if (arrayId != 0) {
			TypedArray colors = getResources().obtainTypedArray(arrayId);
			int index = (int) (Math.random() * colors.length());
			returnColor = colors.getColor(index, Color.GRAY);
			colors.recycle();
		}
		return returnColor;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		MenuItem search = menu.findItem(R.id.search);
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
		search(searchView);
		return true;
	}


	private void search(SearchView searchView) {

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {

				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {

				mAdapter.getFilter().filter(newText);
				return true;
			}
		});
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {

			//noinspection SimplifiableIfStatement
			case android.R.id.home:
				drawerLayout.openDrawer(GravityCompat.START);
				return true;
			case R.id.search:
				Toast.makeText(getApplicationContext(), "Search...", Toast.LENGTH_SHORT).show();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onRefresh() {
		// swipe refresh is performed, fetch the messages again
		getInbox();
	}

	@Override
	public void onIconClicked(int position) {
		if (actionMode == null) {
		}
		info.androidhive.gmail.model.Message message = messages.get(position);
		deleteClicked.add(message.getId());

		toggleSelection(position);
	}

	@Override
	public void onIconImportantClicked(int position) {
		// Star icon is clicked,
		// mark the message as important
		info.androidhive.gmail.model.Message message = messages.get(position);
		message.setImportant(!message.isImportant());
		messages.set(position, message);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onMessageRowClicked(int position) {
		// verify whether action mode is enabled or not
		// if enabled, change the row state to activated
		if (mAdapter.getSelectedItemCount() > 0) {
			enableActionMode(position);
		} else {
			// read the message which removes bold from the row
			info.androidhive.gmail.model.Message message = messages.get(position);
			message.setRead(true);
			messages.set(position, message);
			mAdapter.notifyDataSetChanged();

			Toast.makeText(getApplicationContext(), "Read: " + message.getModel(), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onRowLongClicked(int position) {
		// long press is performed, enable action mode
		enableActionMode(position);
		info.androidhive.gmail.model.Message message = messages.get(position);
		deleteClicked.add(message.getId());


	}

	private void enableActionMode(int position) {
		if (actionMode == null) {
		}
		toggleSelection(position);
	}

	private void toggleSelection(int position) {
		mAdapter.toggleSelection(position);
		int count = mAdapter.getSelectedItemCount();

		if (count == 0) {
			actionMode.finish();
		} else {
			actionMode.setTitle(String.valueOf(count));
			actionMode.invalidate();
		}
	}


	private class ActionModeCallback implements ActionMode.Callback {
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);

			// disable swipe refresh if action mode is enabled
			swipeRefreshLayout.setEnabled(false);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
				case R.id.action_delete:
					// delete all the selected messages
					deleteMessages();
					mode.finish();
					return true;


				default:
					return false;
			}
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mAdapter.clearSelections();
			swipeRefreshLayout.setEnabled(true);
			actionMode = null;/*
			recyclerView.post(new Runnable() {
				@Override
				public void run() {
					mAdapter.resetAnimationIndex();
					// mAdapter.notifyDataSetChanged();
				}
			});*/
		}
	}

	// deleting the messages from recycler view
	private void deleteMessages() {
		mAdapter.resetAnimationIndex();
		List<Integer> selectedItemPositions =
				mAdapter.getSelectedItems();
		for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
			mAdapter.removeData(selectedItemPositions.get(i));
			//delteing server from local storage
		}

		mAdapter.notifyDataSetChanged();
		deleteClicked.clear();
	}

	////////////////////////////////////






	private void showOtherDevices() {
		broadcastHandler.removeMessages(DELAYED_MESSAGE);
		if (progressDialog.isShowing()) {
			try {
				progressDialog.dismiss();
			} catch (Throwable e) {
				Log.e(LOG_TAG, "showOtherDevices", e);
			}
		}
		if (confirmationDialog != null && confirmationDialog.isShowing()) {
			try {
				confirmationDialog.dismiss();
			} catch (Throwable e) {
				Log.e(LOG_TAG, "showOtherDevices", e);
			}
		}
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
			Log.i("selectHandler","0");

			DialServer DialServer = (DialServer) parent.getItemAtPosition(position);
			Log.i("selectHandler","1");
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

	private class DeviceFinderListAdapter extends BaseAdapter {
		public int getCount() {
			return getTotalSize();
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean areAllItemsEnabled() {
			return false;
		}

		@Override
		public boolean isEnabled(int position) {
			return position != trackedServers.size();
		}

		public Object getItem(int position) {
			return getDialServer(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ListEntryView liv;

			if (position == trackedServers.size()) {
				return getLayoutInflater().inflate(R.layout.device_list_separator_layout, null);
			}

			if (convertView == null || !(convertView instanceof ListEntryView)) {
				liv = (ListEntryView) getLayoutInflater().inflate(R.layout.device_list_item_layout, null);
			} else {
				liv = (ListEntryView) convertView;
			}

			liv.setListEntry(getDialServer(position));
			return liv;
		}

		private int getTotalSize() {
			return trackedServers.size();
		}

		private DialServer getDialServer(int position) {
			if (position < trackedServers.size()) {
				return trackedServers.get(position);
			}
			return null;
		}
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

                        showOtherDevices();
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
												if ("friendlyName".equals(lastTagName)) {
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
								Log.d(LOG_TAG, "friendlyName="+friendlyName);
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
			Log.v(LOG_TAG, "Adding new device: " + dialServer);

			// Notify data adapter and update title.
			dataAdapter.notifyDataSetChanged();

			// Show confirmation dialog only for the first STB and only if
			// progress
			// dialog is visible.
			if ((trackedServers.size() == 1) && progressDialog.isShowing()) {
				broadcastHandler.removeMessages(DELAYED_MESSAGE);
				// delayed automatic adding
				Message message = DelayedMessage.DIAL_SERVER_FOUND.obtainMessage(broadcastHandler);
				broadcastHandler.sendMessageDelayed(message, getResources().getInteger(R.integer.gtv_finder_reconnect_delay));
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
				showOtherDevices();
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
				showOtherDevices();
			}
		});
	}

	private ProgressDialog buildProgressDialog(String message, DialogInterface.OnClickListener cancelListener) {
		ProgressDialog dialog = new ProgressDialog(this);
		dialog.setMessage(message);
		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			public boolean onKey(DialogInterface dialogInterface, int which, KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
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
