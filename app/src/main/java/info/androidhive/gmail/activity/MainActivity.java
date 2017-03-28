package info.androidhive.gmail.activity;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.gmail.R;
import info.androidhive.gmail.adapter.MessagesAdapter;
import info.androidhive.gmail.helper.DividerItemDecoration;
import info.androidhive.gmail.model.Message;
import info.androidhive.gmail.navigation.ClientActivity;
import info.androidhive.gmail.settings.SettingsActivity;
import info.androidhive.gmail.sqlite.DatabaseHelper;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, MessagesAdapter.MessageAdapterListener {
    private  List<Message> messages = new ArrayList<>();
    private ArrayList<Integer> deleteClicked = new ArrayList<Integer>();

    public static RecyclerView recyclerView;
    private MessagesAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    public static DatabaseHelper db;
    private Button buttonSave;
    private Button buttonTest;




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
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(this);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonTest = (Button) findViewById(R.id.buttonTest);

        Log.i("TEST","-1");

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        Log.i("TEST","0");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Discover Service", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new MessagesAdapter(this, messages, this);
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
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

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


        buttonSave.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Log.i("MAX_ID",String.valueOf(db.maxID()));
                saveServerToLocalStorage(db.maxID()+1,String.valueOf(db.maxID()+1), "Friendly name", "Model 1", "15:30pm", "mipmap://" + R.mipmap.google, 1, 1, 4);;
            }
        });

        buttonTest.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, info.androidhive.gmail.control_diagnostic.diagnostic.MainActivity.class);
                startActivity(intent);
            }
        });
        actionModeCallback = new ActionModeCallback();

        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        getInbox();
                    }
                }
        );
    }

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
                                    Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                }
                                return true;
                            case R.id.item_navigation_drawer_rendez_vous:
                                menuItem.setChecked(true);
                                Toast.makeText(MainActivity.this, "Launching " + menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                //Intent intent1 = new Intent(ClientActivity.this, AddActivity.class);
                                //intent1.putExtra(Config.KEY_USER_NAME, username);

                                //startActivity(intent1);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.item_navigation_drawer_settings:
                                menuItem.setChecked(true);
                                Toast.makeText(MainActivity.this, "Launching " + menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);



                                // Intent for the activity to open when user selects the notification

                                // Use TaskStackBuilder to build the back stack and get the PendingIntent
                                PendingIntent pendingIntent =
                                        TaskStackBuilder.create(MainActivity.this)
                                                // add all of DetailsActivity's parents to the stack,
                                                // followed by DetailsActivity itself
                                                .addNextIntentWithParentStack(intent)
                                                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);
                                builder.setContentIntent(pendingIntent);
                                startActivity(intent);

                                return true;
                            case R.id.item_navigation_drawer_help:
                                menuItem.setChecked(true);
                                Toast.makeText(MainActivity.this, menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                                drawerLayout.closeDrawer(GravityCompat.START);
                        }
                        return true;
                    }
                });
    }


    private void saveServerToLocalStorage(int id,String ipAddress , String friendlyName, String model, String isImportant, String name, int test1, int test2, int color) {
        db.addServer(id,ipAddress, friendlyName, model, isImportant,name,test1,test2, color) ;
        Message n = new  Message(id,ipAddress, friendlyName, model, isImportant,name,toBool(test1),toBool(test2), color) ;
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


    private void loadServers() {
        messages.clear();
        Cursor cursor = db.getServers();
        if (cursor.moveToFirst()) {
            do {
                Boolean flag1 = (cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IS_IMPORTANT)) == 0);
                Boolean flag2 = (cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_TEST2)) == 0);
                Message message = new Message(
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
                messages.add(message);
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
            actionMode = startSupportActionMode(actionModeCallback);
        }
        Message message = messages.get(position);
        deleteClicked.add(message.getId());

        toggleSelection(position);
    }

    @Override
    public void onIconImportantClicked(int position) {
        // Star icon is clicked,
        // mark the message as important
        Message message = messages.get(position);
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
            Message message = messages.get(position);
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
        Message message = messages.get(position);
        deleteClicked.add(message.getId());


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

    // deleting the messages from recycler view
    private void deleteMessages() {
        mAdapter.resetAnimationIndex();
        List<Integer> selectedItemPositions =
                mAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mAdapter.removeData(selectedItemPositions.get(i));
            //delteing server from local storage
            db.deleteServer(deleteClicked.get(i));
        }

        mAdapter.notifyDataSetChanged();
        deleteClicked.clear();
    }
}