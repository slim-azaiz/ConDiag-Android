package info.sagemcom.conDiag.history;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.sagemcom.conDiag.R;
import info.sagemcom.conDiag.adapter.ServerAdapter;
import info.sagemcom.conDiag.discovery.dial.DiscoveryActivity;
import info.sagemcom.conDiag.helper.DividerItemDecoration;
import info.sagemcom.conDiag.model.Server;
import info.sagemcom.conDiag.settings.SettingsActivity;
import info.sagemcom.conDiag.sqlite.DatabaseHelper;
import info.sagemcom.conDiag.wol.ARPInfo;
import info.sagemcom.conDiag.wol.WakeOnLan;

import static info.sagemcom.conDiag.utils.Config.WAKE_ON_LAN_LOG;

public class HistoryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, ServerAdapter.ServerAdapterListener {
    private static   List<Server> servers = new ArrayList<>();
    private ArrayList<Integer> deleteClicked = new ArrayList<Integer>();

    public static RecyclerView recyclerView;
    private static ServerAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    public static DatabaseHelper db;


    private TabLayout tabLayout;
    private ActionBar actionBar;
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    public  Context context=this;

    public Context getContext() {
        return context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        db = new DatabaseHelper(this);

        Log.i("TEST","-1");

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        Log.i("TEST","0");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryActivity.this, DiscoveryActivity.class);
                startActivity(intent);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new ServerAdapter(this, servers, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        Log.i("TEST","1");

        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.i("TEST","2");

        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.home_toolbar);

        Log.i("TEST","3");
        actionBar.setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.i("TEST","4");
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        Log.i("TEST","5");
        tabLayout.setupWithViewPager(viewPager);

        Log.i("TEST","6");
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (navigationView != null) {
            setupNavigationDrawerContent(navigationView);
        }
        setupNavigationDrawerContent(navigationView);
       /* buttonSave.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Log.i(HISTORY_LOG,"MAX_ID = "+String.valueOf(db.maxID()));;
                saveServerToLocalStorage(db.maxID()+1,String.valueOf(db.maxID()+1), "Friendly name", "Model 1",getCurrentTime() , "mipmap://" + R.mipmap.google, 1, 1, 4);;
            }
        });
        */

        actionModeCallback = new ActionModeCallback();

        // show loader and fetch servers
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        getInbox();
                    }
                }
        );
    }



    public static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH:mm a  ");
        return  sdf.format(new Date());

    }

    private void doWakeOnLan(String ipAddress) throws IllegalArgumentException {
       // String ipAddress = editIpAddress.getText().toString();

        if (TextUtils.isEmpty(ipAddress)) {
            Log.d(WAKE_ON_LAN_LOG,"Invalid Ip Address");
            return;
        }

        Log.d(WAKE_ON_LAN_LOG,"IP address: " + ipAddress);

        // Get mac address from IP (using arp cache)
        String macAddress = ARPInfo.getMACFromIPAddress(ipAddress);

        if (macAddress == null) {
            Snackbar.make(getCurrentFocus(), "Could not find MAC address, cannot send WOL packet without it.", Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        Log.d(WAKE_ON_LAN_LOG,"MAC address: " + macAddress);
        Log.d(WAKE_ON_LAN_LOG,"IP address2: " + ARPInfo.getIPAddressFromMAC(macAddress));

        // Send Wake on lan packed to ip/mac
        try {
            WakeOnLan.sendWakeOnLan(ipAddress, macAddress);
            Snackbar.make(getCurrentFocus(), "WOL Packet sent", Snackbar.LENGTH_LONG)
                    .show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setupNavigationDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.item_navigation_drawer_discovery:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.item_navigation_drawer_notifications:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.item_navigation_drawer_history:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                //Intent intent1 = new Intent(NavigationActivity.this, AddActivity.class);
                                //intent1.putExtra(Config.KEY_USER_NAME, username);

                                //startActivity(intent1);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.item_navigation_drawer_settings:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                Intent intent = new Intent(HistoryActivity.this, SettingsActivity.class);



                                // Intent for the activity to open when user selects the notification

                                // Use TaskStackBuilder to build the back stack and get the PendingIntent
                                PendingIntent pendingIntent =
                                        TaskStackBuilder.create(HistoryActivity.this)
                                                // add all of DetailsActivity's parents to the stack,
                                                // followed by DetailsActivity itself
                                                .addNextIntentWithParentStack(intent)
                                                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(HistoryActivity.this);
                                builder.setContentIntent(pendingIntent);
                                startActivity(intent);

                                return true;
                            case R.id.item_navigation_drawer_help:
                                menuItem.setChecked(true);
                                drawerLayout.closeDrawer(GravityCompat.START);
                        }
                        return true;
                    }
                });
    }


    public static void saveServerToLocalStorage(int id,String ipAddress , String friendlyName, String model, String isImportant, String name, int test1, int test2, int color) {
        db.addServer(id,ipAddress, friendlyName, model, isImportant,name,test1,test2, color) ;
        Server n = new Server(id,ipAddress, friendlyName, model, isImportant,name,toBool(test1),toBool(test2), color) ;
        servers.add(n);
        loadServers();
        mAdapter.notifyDataSetChanged();
        refreshList();
    }

    private static Boolean toBool(int test){
        if (test ==  1)
            return false;
        else
            return true;
    }

    private static void refreshList() {
        mAdapter.notifyDataSetChanged();
    }


    private static void loadServers() {
        servers.clear();
        Cursor cursor = db.getServers();
        if (cursor.moveToFirst()) {
            do {
                Boolean flag1 = (cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IS_IMPORTANT)) == 0);
                Boolean flag2 = (cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TEST2)) == 0);
                Server server = new Server(
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IP_ADDRESS)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_FRIENDLY_NAME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_MODEL)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TEST)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)),
                        flag1,
                        flag2,
                        4
                );
                servers.add(server);
            } while (cursor.moveToNext());
        }
    }



    /**
     * Fetches servers
     */
    private void getInbox() {
        swipeRefreshLayout.setRefreshing(true);
        loadServers();
        mAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * chooses a random color parameter array.xml
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
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.search:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the servers again
        getInbox();
    }

    @Override
    public void onIconClicked(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        Server server = servers.get(position);
        deleteClicked.add(server.getId());

        toggleSelection(position);
    }

    @Override
    public void onWakeOnLanClicked(final int position) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doWakeOnLan(servers.get(position).getIpAddress());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onIconImportantClicked(int position) {
        // Star icon is clicked,
        // mark the modelName as important
        Server server = servers.get(position);
        server.setImportant(!server.isImportant());
        servers.set(position, server);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onServerRowClicked(int position) {
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        if (mAdapter.getSelectedItemCount() > 0) {
            enableActionMode(position);
        } else {
            // read the modelName which removes bold parameter the row
            Server server = servers.get(position);
            server.setRead(true);
            servers.set(position, server);
            mAdapter.notifyDataSetChanged();

           // Toast.makeText(getApplicationContext(), "Read: " + modelName.getModel(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRowLongClicked(int position) {
        // long press is performed, enable action mode
        enableActionMode(position);
        Server server = servers.get(position);
        deleteClicked.add(server.getId());


    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
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
                    // delete all the selected servers
                    deleteServers();
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
            actionMode = null;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.resetAnimationIndex();
                    // mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    // deleting the servers parameter recycler view
    private void deleteServers() {
        mAdapter.resetAnimationIndex();
        List<Integer> selectedItemPositions =
                mAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mAdapter.removeData(selectedItemPositions.get(i));
            //delteing server parameter local storage
            db.deleteServer(deleteClicked.get(i));
        }

        mAdapter.notifyDataSetChanged();
        deleteClicked.clear();
    }
}