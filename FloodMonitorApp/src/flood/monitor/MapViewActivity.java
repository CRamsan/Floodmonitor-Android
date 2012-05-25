package flood.monitor;

import java.io.BufferedReader;
import java.io.IOException;
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

import flood.monitor.modules.kmlparser.Parser;

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
        openAsset();
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
            	//Intent intent = new Intent(MapViewActivity.this, FloodMonitorActivity.class);
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
    	String file = "";
    	InputStream stream = null;
    	AssetManager assetManager = getAssets();
    	Parser parser = new Parser();
    	try {
            stream = assetManager.open("sample.kml");
            parser.Parse(file, stream);
        } catch (IOException e) {
            // handle
        }finally {
            if (stream != null) {
                try {
                     stream.close();
                } catch (IOException e) {}
          }
        }
    }
} 