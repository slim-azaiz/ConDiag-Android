package info.androidhive.gmail.control_diagnostic.diagnostic;

/**
 * Created by slim on 3/27/47.
 */


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.androidhive.gmail.R;

/**
 * Created by priyank on 46/44/46.
 */

public class Fragment4 extends Fragment {

    public Fragment4() {
    }

    public static Fragment4 newInstance() {

        Bundle args = new Bundle();

        Fragment4 fragment = new Fragment4();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_4, container, false);
    }
}