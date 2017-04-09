package info.androidhive.gmail.control_diagnostic.diagnostic.sample.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.cleveroad.loopbar.model.MockedItemsFactory;
import com.cleveroad.loopbar.widget.LoopBarView;
import com.cleveroad.loopbar.widget.OnItemClickListener;
import com.cleveroad.loopbar.widget.Orientation;

import info.androidhive.gmail.R;
import info.androidhive.gmail.control_diagnostic.diagnostic.Fragment1;
import info.androidhive.gmail.control_diagnostic.diagnostic.Fragment2;
import info.androidhive.gmail.control_diagnostic.diagnostic.Fragment3;
import info.androidhive.gmail.control_diagnostic.diagnostic.Fragment4;
import info.androidhive.gmail.control_diagnostic.diagnostic.Fragment5;
import info.androidhive.gmail.control_diagnostic.diagnostic.sample.AbstractPageChangedListener;
import info.androidhive.gmail.control_diagnostic.diagnostic.sample.SimpleFragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractLoopBarFragment extends Fragment
        implements
        OnItemClickListener {

    static final String EXTRA_ORIENTATION = "EXTRA_ORIENTATION";

    private LoopBarView loopBarView;
    private ViewPager viewPager;


    //args
    @Orientation
    private int mOrientation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @Orientation
        int orientation = getArguments().getInt(EXTRA_ORIENTATION, Orientation.ORIENTATION_HORIZONTAL);
        this.mOrientation = orientation;

        View rootView = inflater.inflate(R.layout.fragment_loopbar_horizontal,
                container,
                false);

        loopBarView = (LoopBarView) rootView.findViewById(R.id.endlessView);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        loopBarView.addOnItemClickListener(this);

        SimpleFragmentStatePagerAdapter pagerAdapter = new SimpleFragmentStatePagerAdapter(
                getChildFragmentManager(),
                getMockFragments(),
                MockedItemsFactory.getCategoryItems(getContext()));
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new AbstractPageChangedListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                loopBarView.setCurrentItem(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("tag", "on page scrolled");
            }
        });

        return rootView;
    }

    protected List<Fragment> getMockFragments() {
        List<Fragment> fragments = new ArrayList<>(8);
        fragments.add(Fragment1.newInstance());
        fragments.add(Fragment2.newInstance());
        fragments.add(Fragment3.newInstance());
        fragments.add(Fragment4.newInstance());
        fragments.add(Fragment5.newInstance());
        return fragments;
    }

    @Override
    public void onItemClicked(int position) {
        viewPager.setCurrentItem(position);
    }


    protected abstract Fragment getNewInstance(int orientation);

    protected ViewPager getViewPager() {
        return viewPager;
    }

    protected LoopBarView getLoopBarView() {
        return loopBarView;
    }

    private void changeGravity() {
        int nextGravity = loopBarView.getGravity() == LoopBarView.SELECTION_GRAVITY_START ?
                LoopBarView.SELECTION_GRAVITY_END : LoopBarView.SELECTION_GRAVITY_START;
        loopBarView.setGravity(nextGravity);
    }





}
