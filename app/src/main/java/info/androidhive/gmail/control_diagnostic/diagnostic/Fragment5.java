package info.androidhive.gmail.control_diagnostic.diagnostic;

/**
 * Created by slim on 3/27/57.
 */


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.androidhive.gmail.R;

/**
 * Created by priyank on 56/55/56.
 */

public class Fragment5 extends Fragment {

    public Fragment5() {
    }
    public static Fragment5 newInstance() {

        Bundle args = new Bundle();

        Fragment5 fragment = new Fragment5();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_5, container, false);
    }
}