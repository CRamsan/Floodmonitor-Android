package flood.monitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HelpActivity extends Activity {
    static final int PROGRESS_DIALOG = 0;
    static final int ALERT_DIALOG = 1;
    ProgressDialog progressDialog;
    AlertDialog alert;
	
    /** Called when the activity is first created. */
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
              
        Button ViewMap = (Button) findViewById(R.id.viewMap);  
        ViewMap.setOnClickListener(new View.OnClickListener() {  
            public void onClick(View v) {  
            			Intent intent = new Intent(HelpActivity.this, MapViewActivity.class);
            			startActivity(intent);
                    }   
        });

        Button PostPicture = (Button) findViewById(R.id.uploadForm);  
        PostPicture.setOnClickListener(new View.OnClickListener() {  
        	public void onClick(View v) {  
            			Intent intent = new Intent(HelpActivity.this, UploadFormActivity.class);
            			startActivity(intent);
                    }  
        });  
    }
     
    @Override 
    public void onResume(){
    	super.onResume();
        if(!this.isOnline()){
			showDialog(ALERT_DIALOG);
		}
	}
    
    public boolean isOnline() {
   	 ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
   	 if(cm.getNetworkInfo(0).isConnectedOrConnecting() || cm.getNetworkInfo(1).isConnectedOrConnecting()){
   		 return true;
   	 }else{
   		 return false;
   	 }
   }
    
    protected Dialog onCreateDialog(int id) {
        switch(id) {
        case PROGRESS_DIALOG:
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Connecting to Database");
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(true);
            return progressDialog;
        case ALERT_DIALOG:
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("This app requires internet connection.")
        	       .setCancelable(false)
        	       .setNeutralButton("Exit", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                HelpActivity.this.finish();
        	           }
        	       });
        	alert = builder.create();
        	return alert;
        default:
            return null;
        }
    }
} 