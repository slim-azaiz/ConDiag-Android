package info.androidhive.gmail.control_diagnostic.diagnostic;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.picasso.Picasso;

import info.androidhive.gmail.R;
import it.sephiroth.android.library.bottomnavigation.BottomBehavior;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import it.sephiroth.android.library.bottomnavigation.MiscUtils;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager.SystemBarConfig;
import com.squareup.picasso.Picasso;

import it.sephiroth.android.library.bottomnavigation.BottomBehavior;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import it.sephiroth.android.library.bottomnavigation.MiscUtils;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private static final String TAG = MainActivityFragment.class.getSimpleName();
    RecyclerView mRecyclerView;
    CoordinatorLayout mCoordinatorLayout;
    ViewGroup mRoot;
    private SystemBarTintManager.SystemBarConfig config;
    private ToolbarScrollHelper scrollHelper;

    public MainActivityFragment() { }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.RecyclerView01);
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final BaseActivity activity = (BaseActivity) getActivity();
        config = activity.getSystemBarTint().getConfig();
        mRoot = (ViewGroup) activity.findViewById(R.id.CoordinatorLayout01);
        if (mRoot instanceof CoordinatorLayout) {
            mCoordinatorLayout = (CoordinatorLayout) mRoot;
        }

        final int navigationHeight;
        final int actionbarHeight;

        if (activity.hasTranslucentNavigation()) {
            navigationHeight = config.getNavigationBarHeight();
        } else {
            navigationHeight = 0;
        }

        if (activity.hasTranslucentStatusBar()) {
            actionbarHeight = config.getActionBarHeight();
        } else {
            actionbarHeight = 0;
        }

        MiscUtils.log(TAG, Log.VERBOSE, "navigationHeight: " + navigationHeight);
        MiscUtils.log(TAG, Log.VERBOSE, "actionbarHeight: " + actionbarHeight);

        final BottomNavigation navigation = activity.getBottomNavigation();
        if (null != navigation) {
            navigation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    navigation.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    final ViewGroup.LayoutParams params = navigation.getLayoutParams();
                    final CoordinatorLayout.Behavior behavior;

                    if (params instanceof CoordinatorLayout.LayoutParams) {
                        final CoordinatorLayout.LayoutParams coordinatorLayoutParams = (CoordinatorLayout.LayoutParams) params;
                        behavior = coordinatorLayoutParams.getBehavior();
                    } else {
                        behavior = null;
                    }

                    if (behavior instanceof BottomBehavior) {
                        final boolean scrollable = ((BottomBehavior) behavior).isScrollable();
                        int systemBottomNavigation = activity.hasTranslucentNavigation() ? activity.getNavigationBarHeight() : 0;

                        MiscUtils.log(TAG, Log.VERBOSE, "scrollable: " + scrollable);

                        int totalHeight;

                        if (scrollable) {
                            if (systemBottomNavigation > 0) {
                                totalHeight = systemBottomNavigation;
                            } else {
                                totalHeight = navigationHeight;
                            }
                        } else {
                            totalHeight = navigation.getNavigationHeight();
                        }

                        createAdater(totalHeight, activity.hasManagedToolbarScroll());
                    } else {
                        createAdater(navigationHeight, activity.hasAppBarLayout());
                    }
                }
            });
        } else {
            createAdater(navigationHeight, activity.hasAppBarLayout());
        }

        if (!activity.hasManagedToolbarScroll()) {
            scrollHelper = new ToolbarScrollHelper(activity, activity.getToolbar());
            scrollHelper.initialize(mRecyclerView);
        }
    }

    private void createAdater(int height, final boolean hasAppBarLayout) {
        MiscUtils.log(getClass().getSimpleName(), Log.INFO, "createAdapter(" + height + ")");
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    public void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    static class TwoLinesViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView description;
        final ImageView imageView;
        final Button button1;
        final Button button2;
        final int marginBottom;

        public TwoLinesViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(android.R.id.title);
            description = (TextView) itemView.findViewById(android.R.id.text1);
            imageView = (ImageView) itemView.findViewById(android.R.id.icon);
            marginBottom = ((ViewGroup.MarginLayoutParams) itemView.getLayoutParams()).bottomMargin;
            button1 = (Button) itemView.findViewById(android.R.id.button1);
            button2 = (Button) itemView.findViewById(android.R.id.button2);
        }
    }

}
