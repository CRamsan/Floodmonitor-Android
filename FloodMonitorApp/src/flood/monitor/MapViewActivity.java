package flood.monitor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class MapViewActivity extends MapActivity {

	MapView mapView;
    String markers = "";
    /** Called when the activity is first created. */
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
    }
        
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.item1://Setings
                //newGame();
                return true;
            case R.id.item2://Upload
    			Intent intent = new Intent(MapViewActivity.this, UploadFormActivity.class);
    			startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    /*Testing
     *     
     */
    
    public void openAsset(){

    	String str="";
    	StringBuffer buf = new StringBuffer();			
    	InputStream is = this.getResources().openRawResource(R.raw.sample);
    	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    	try{
    		
    	if (is!=null) {							
    		while ((str = reader.readLine()) != null) {	
    			buf.append(str + "\n" );
    		}				
    	}		
    	is.close();	
    	Toast.makeText(getBaseContext(), 
    			buf.toString(), Toast.LENGTH_LONG).show();				   	
    	}catch (Exception e) {
    		
    	}

    }
} 