package info.androidhive.gmail.control_diagnostic.diagnostic;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.gmail.R;


public class DemoActivity extends AppCompatActivity {


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_demo);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    ViewGroup tab = (ViewGroup) findViewById(R.id.tab);
//    tab.addView(LayoutInflater.from(this).inflate(demo.layoutResId, tab, false));

    //final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpagerDemo);

    final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpagerDemo);
    SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);


    TabsAdapter adapter = new TabsAdapter(getSupportFragmentManager());
    adapter.addFrag(new Fragment1(), "memory");
    adapter.addFrag(new Fragment1(), "identification");
    adapter.addFrag(new Fragment1(), "software");
    adapter.addFrag(new Fragment1(), "network");
    adapter.addFrag(new Fragment1(), "loader");
    adapter.addFrag(new Fragment1(), "sysInfo");
    adapter.addFrag(new Fragment1(), "conditionalAccess");


    viewPager.setAdapter(adapter);
    viewPagerTab.setViewPager(viewPager);

  }

  public class TabsAdapter extends FragmentPagerAdapter {
    private final List<android.support.v4.app.Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public TabsAdapter(FragmentManager fm) {
      super(fm);
    }

    public void addFrag(android.support.v4.app.Fragment fragment, String title){
      Bundle bundle = new Bundle();
      bundle.putString("method", title);
      fragment.setArguments(bundle);

      mFragmentList.add(fragment);
      mFragmentTitleList.add(title);
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
      return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
      return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return mFragmentTitleList.get(position);
    }
  }

}
