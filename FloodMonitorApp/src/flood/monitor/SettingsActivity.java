package flood.monitor;

import java.io.File;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import flood.monitor.modules.Connector;

public class SettingsActivity extends PreferenceActivity {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from Activity
	// ===========================================================
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// The activity is launched or restarted after been killed.
		// Display the fragment as the main content.
		addPreferencesFromResource(R.xml.preferences);
		Preference clearCache = (Preference) findPreference("pref_ClearCache");
		clearCache.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		             public boolean onPreferenceClick(Preference preference) {
		            	 File cache = Connector.getCacheDir();
		            	 if(cache.exists())
		            	 {
		            		 removeDirectory(cache);
		            	 }
		                 return true;
		             }
		         });

	}

	@Override
	protected void onStart() {
		super.onStart();
		// The activity is about to become visible.
	}

	@Override
	protected void onResume() {
		super.onResume();
		// The activity has become visible (it is now "resumed").
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Another activity is taking focus (this activity is about to be
		// "paused").
	}

	@Override
	protected void onStop() {
		super.onStop();
		// The activity is no longer visible (it is now "stopped")
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		// The activity is about to be destroyed.
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// The activity is been brought back to the front.
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
	private boolean removeDirectory(File file) {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				if(!removeDirectory(child)){
					return false;
				}
			}
			return file.delete();
		}else{
			return file.delete();
		}
	}
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}
