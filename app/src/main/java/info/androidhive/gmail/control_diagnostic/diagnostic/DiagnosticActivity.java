package info.androidhive.gmail.control_diagnostic.diagnostic;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import info.androidhive.gmail.R;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import it.sephiroth.android.library.bottomnavigation.FloatingActionButtonBehavior;
import it.sephiroth.android.library.bottonnavigation.BuildConfig;

import static android.util.Log.INFO;
import static android.util.Log.VERBOSE;
import static it.sephiroth.android.library.bottomnavigation.MiscUtils.log;

@TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
public class DiagnosticActivity extends BaseActivity implements BottomNavigation.OnMenuItemSelectionListener {

    static final String TAG = DiagnosticActivity.class.getSimpleName();

    private Fragment fragment;
    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BottomNavigation.DEBUG = BuildConfig.DEBUG;

        setContentView(getActivityLayoutResId());
        final ViewGroup root = (ViewGroup) findViewById(R.id.CoordinatorLayout01);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        final int statusbarHeight = getStatusBarHeight();
        final boolean translucentStatus = hasTranslucentStatusBar();
        final boolean translucentNavigation = hasTranslucentNavigation();

        log(TAG, VERBOSE, "translucentStatus: %b", translucentStatus);

        if (translucentStatus) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) root.getLayoutParams();
            params.topMargin = -statusbarHeight;

            params = (ViewGroup.MarginLayoutParams) toolbar.getLayoutParams();
            params.topMargin = statusbarHeight;
        }

        if (translucentNavigation) {
            final ViewPager viewPager = getViewPager();
            if (null != viewPager) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewPager.getLayoutParams();
                params.bottomMargin = -getNavigationBarHeight();
            }
        }

        fragmentManager = getSupportFragmentManager();
        fragment = new Fragment1();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.main_container, fragment).commit();
        initializeUI(savedInstanceState);
    }

    protected int getActivityLayoutResId() {return R.layout.activity_diagnostic;}



    protected void initializeUI(final Bundle savedInstanceState) {

        final ViewPager viewPager = getViewPager();
        if (null != viewPager) {

            getBottomNavigation().setOnMenuChangedListener(new BottomNavigation.OnMenuChangedListener() {
                @Override
                public void onMenuChanged(final BottomNavigation parent) {

                    viewPager.setAdapter(new ViewPagerAdapter(DiagnosticActivity.this, parent.getMenuItemCount()));
                    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(
                            final int position, final float positionOffset, final int positionOffsetPixels) { }

                        @Override
                        public void onPageSelected(final int position) {
                            if (getBottomNavigation().getSelectedIndex() != position) {
                                getBottomNavigation().setSelectedIndex(position, false);
                            }
                        }

                        @Override
                        public void onPageScrollStateChanged(final int state) { }
                    });
                }
            });

        }
    }

    @Override
    public void onMenuItemSelect(final int itemId, final int position, final boolean fromUser) {
        log(TAG, INFO, "onMenuItemSelect(" + itemId + ", " + position + ", " + fromUser + ")");
        if (fromUser) {
            if (null != getViewPager()) {
                getViewPager().setCurrentItem(position);
            }
        }

        switch (itemId) {
            case R.id.bbn_item1:
                fragment = new Fragment1();
                break;
            case R.id.bbn_item2:
                fragment = new Fragment2();
                break;
            case R.id.bbn_item3:
                fragment = new Fragment3();
                break;
            case R.id.bbn_item4:
                fragment = new Fragment4();
                break;
            case R.id.bbn_item5:
                fragment = new Fragment5();
                break;
        }
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, fragment).commit();
    }

    @Override
    public void onMenuItemReselect(@IdRes final int itemId, final int position, final boolean fromUser) {
        if (fromUser) {
            final FragmentManager manager = getSupportFragmentManager();
            DiagnosticActivityFragment fragment = (DiagnosticActivityFragment) manager.findFragmentById(R.id.fragment);
        }
    }

    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final int mCount;

        public ViewPagerAdapter(final AppCompatActivity activity, int count) {
            super(activity.getSupportFragmentManager());
            this.mCount = count;
        }

        @Override
        public Fragment getItem(final int position) {
            return new DiagnosticActivityFragment();
        }

        @Override
        public int getCount() {
            return mCount;
        }
    }

}
