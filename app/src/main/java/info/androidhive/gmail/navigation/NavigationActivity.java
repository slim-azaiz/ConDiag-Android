package info.androidhive.gmail.navigation;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;


import info.androidhive.gmail.R;
import info.androidhive.gmail.settings.SettingsActivity;


public class NavigationActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private static DrawerLayout drawerLayout;
    private String username;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation);
        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        if (navigationView != null) {
            setupNavigationDrawerContent(navigationView);
        }
        setupNavigationDrawerContent(navigationView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
         //   case R.id.menuLogout:
           //     logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout(){
        //Creating an alert dialog to confirm logout
        Log.i("HHH","disconnect");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to log out ?");
        alertDialogBuilder.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        //Getting out sharedpreferences
                        //SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        //Getting editor
                       /* SharedPreferences.Editor editor = preferences.edit();

                        //Puting the value false for loggedin
                        editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, false);

                        //Putting blank value to email
                        editor.putString(Config.KEY_USER_EMAIL, "");

                        //Saving the sharedpreferences
                        editor.commit();*/

                        //Starting login activity
                       // Intent intent = new Intent(NavigationActivity.this, Login.class);
                       // startActivity(intent);
                    }
                });

        alertDialogBuilder.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

        //Showing the alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

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
                                Toast.makeText(NavigationActivity.this, "Launching " + menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                //Intent intent1 = new Intent(NavigationActivity.this, AddActivity.class);
                                //intent1.putExtra(Config.KEY_USER_NAME, username);

                                //startActivity(intent1);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.item_navigation_drawer_settings:
                                menuItem.setChecked(true);
                                Toast.makeText(NavigationActivity.this, "Launching " + menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                                drawerLayout.closeDrawer(GravityCompat.START);
                                Intent intent = new Intent(NavigationActivity.this, SettingsActivity.class);



                                // Intent for the activity to open when user selects the notification

                                // Use TaskStackBuilder to build the back stack and get the PendingIntent
                                PendingIntent pendingIntent =
                                        TaskStackBuilder.create(NavigationActivity.this)
                                                // add all of DetailsActivity's parents to the stack,
                                                // followed by DetailsActivity itself
                                                .addNextIntentWithParentStack(intent)
                                                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(NavigationActivity.this);
                                builder.setContentIntent(pendingIntent);
                                startActivity(intent);

                                return true;
                            case R.id.item_navigation_drawer_help:
                                menuItem.setChecked(true);
                                Toast.makeText(NavigationActivity.this, menuItem.getTitle().toString(), Toast.LENGTH_SHORT).show();
                                drawerLayout.closeDrawer(GravityCompat.START);
                            case R.id.item_navigation_drawer_logOut:
                                menuItem.setChecked(true);
                                logout();
                                drawerLayout.closeDrawer(GravityCompat.START);

                        }
                        return true;
                    }
                });
    }
}
