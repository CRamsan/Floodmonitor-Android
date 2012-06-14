package flood.monitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

public class HelpActivity extends Activity {
    static final int PROGRESS_DIALOG = 0;
    static final int ALERT_DIALOG = 1;
    ProgressDialog progressDialog;
    AlertDialog alert;
	
    /** Called when the activity is first created. */
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
    }
} 