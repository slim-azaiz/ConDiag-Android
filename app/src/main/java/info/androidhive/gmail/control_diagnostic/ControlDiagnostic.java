package info.androidhive.gmail.control_diagnostic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import info.androidhive.gmail.R;
import info.androidhive.gmail.control_diagnostic.control.ControlActivity;
import info.androidhive.gmail.control_diagnostic.diagnostic.DiagnosticActivity;

/**
 * Created by slim on 3/18/17.
 */

public class ControlDiagnostic extends AppCompatActivity {
    private Button buttonDiagnostic;
    private Button buttonControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_diagnostic);
        buttonDiagnostic = (Button) findViewById(R.id.buttonControl);
        buttonControl = (Button) findViewById(R.id.buttonDiagnostic);
        buttonDiagnostic.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(ControlDiagnostic.this, DiagnosticActivity.class);
                startActivity(intent);
            }
        });
        buttonControl.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ControlDiagnostic.this, ControlActivity.class);
                startActivity(intent);
            }
        });
    }

}


