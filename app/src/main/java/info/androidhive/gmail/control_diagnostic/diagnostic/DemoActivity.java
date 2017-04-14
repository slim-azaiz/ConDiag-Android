package info.androidhive.gmail.control_diagnostic.diagnostic;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import info.androidhive.gmail.R;


public class DemoActivity extends AppCompatActivity {

  private static final String KEY_DEMO = "demo";

  public static int[] tab10() {
    return new int[] {
            R.string.demo_tab_1,
            R.string.demo_tab_2,
            R.string.demo_tab_3,
            R.string.demo_tab_4,
            R.string.demo_tab_5,
            R.string.demo_tab_6,
            R.string.demo_tab_7,
            R.string.demo_tab_8,
            R.string.demo_tab_9,
            R.string.demo_tab_10
    };
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_demo);

    //retreive parameter
   /* if (savedInstanceState == null) {
      Bundle extras = getIntent().getExtras();
      if(extras == null) {
        ipAddress= null;
      } else {
        ipAddress= extras.getString("IpAddress");
      }
    } else {
      ipAddress= (String) savedInstanceState.getSerializable("IpAddress");
    }

*/
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    ViewGroup tab = (ViewGroup) findViewById(R.id.tab);
//    tab.addView(LayoutInflater.from(this).inflate(demo.layoutResId, tab, false));

    ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
    SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);

    FragmentPagerItems pages = new FragmentPagerItems(this);

    pages.add(FragmentPagerItem.of("STB IDENTIFICATION", Fragment1.class));
    pages.add(FragmentPagerItem.of("memory", Fragment1.class));
    pages.add(FragmentPagerItem.of("STB sysInfo", Fragment1.class));
    pages.add(FragmentPagerItem.of("conditionalAccess", Fragment1.class));
    pages.add(FragmentPagerItem.of("network", Fragment1.class));
    pages.add(FragmentPagerItem.of("software", Fragment1.class));
    pages.add(FragmentPagerItem.of("loader", Fragment1.class));

    for (int titleResId : tabs()) {
      //pages.add(FragmentPagerItem.of(getString(titleResId), DemoFragment.class));
    }

    FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
        getSupportFragmentManager(), pages);

    viewPager.setAdapter(adapter);
    viewPagerTab.setViewPager(viewPager);

  }

  public int[] tabs() {
    return tab10();
  }

}
