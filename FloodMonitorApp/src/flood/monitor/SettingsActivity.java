package flood.monitor;

import java.io.File;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.provider.ContactsContract.Data;
import android.provider.SearchRecentSuggestions;
import android.widget.Toast;
import flood.monitor.MapViewActivity.AddressSuggestionProvider;
import flood.monitor.modules.Connector;
import flood.monitor.modules.kmlparser.ObjectDataSource;

/**
 * @author Cesar
 * 
 */
public class SettingsActivity extends PreferenceActivity {

	private SettingsActivity activity = this;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// The activity is launched or restarted after been killed.
		// Display the fragment as the main content.
		addPreferencesFromResource(R.xml.preferences);
		Preference clearCache = (Preference) findPreference("pref_ClearCache");
		clearCache
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						File cache = Connector.getCacheDir();
						if (cache.exists()) {
							removeDirectory(cache);
						}
						return true;
					}
				});
		Preference clearAll = (Preference) findPreference("pref_ClearAll");
		clearAll.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				ObjectDataSource data = new ObjectDataSource(activity);
				data.open();
				data.reset();
				data.close();
				setResult(MapViewActivity.RESULT_RESTART);
				Toast.makeText(activity,
						"Application will need to be restarted",
						Toast.LENGTH_LONG).show();
				return true;
			}
		});
		Preference clearHistory = (Preference) findPreference("pref_ClearHistory");
		clearHistory
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						clearSearchHistory();
						return true;
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		// The activity is about to become visible.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		// The activity has become visible (it is now "resumed").
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		// Another activity is taking focus (this activity is about to be
		// "paused").
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.PreferenceActivity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		// The activity is no longer visible (it is now "stopped")
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		// The activity is about to be destroyed.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.preference.PreferenceActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// The activity is been brought back to the front.
	}

	/**
	 * Delete the given file or directory.
	 * 
	 * @param file
	 *            to get remove.
	 * @return true if the operation was sucessful, false otherwise.
	 */
	private boolean removeDirectory(File file) {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				if (!removeDirectory(child)) {
					return false;
				}
			}
			return file.delete();
		} else {
			return file.delete();
		}
	}

	private void clearSearchHistory() {
		SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
				AddressSuggestionProvider.AUTHORITY,
				AddressSuggestionProvider.MODE);
		suggestions.clearHistory();
	}

}
