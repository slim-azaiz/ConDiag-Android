package info.androidhive.gmail.control_diagnostic.diagnostic.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.cleveroad.loopbar.widget.Orientation;

import info.androidhive.gmail.R;
import info.androidhive.gmail.control_diagnostic.diagnostic.sample.fragments.CategoriesAdapterLoopBarFragment;

public class MainActivity extends AppCompatActivity implements IFragmentReplacer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostic_test);

        if (savedInstanceState == null) {
            replaceFragment(CategoriesAdapterLoopBarFragment.newInstance(Orientation.ORIENTATION_HORIZONTAL));
        }
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }
}
