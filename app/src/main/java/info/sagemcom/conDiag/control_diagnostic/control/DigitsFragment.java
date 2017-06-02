package info.sagemcom.conDiag.control_diagnostic.control;

/**
 * Created by slim on 3/27/17.
 */


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import info.sagemcom.conDiag.R;


public class DigitsFragment extends Fragment   {
    public DigitsFragment() {

    }
    private String page;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        page = getArguments().getString("page");
        switch (page){
            case "digits":
                return inflater.inflate(R.layout.digits_layout, container, false);
            case "playback":
                return inflater.inflate(R.layout.play_back_layout, container, false);
            default:
                return inflater.inflate(R.layout.colors_layout, container, false);
        }
    }

    @Override
    public void onViewCreated(final View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

        }else{
            // fragment is no longer visible
        }
    }
}