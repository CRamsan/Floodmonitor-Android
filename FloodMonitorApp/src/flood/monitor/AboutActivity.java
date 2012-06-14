package flood.monitor;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class AboutActivity extends Activity {

	private WebView mWebView;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

	    mWebView = (WebView) findViewById(R.id.aboutWebView);
	    mWebView.getSettings().setJavaScriptEnabled(true);
	    mWebView.loadUrl(this.getString(R.string.text_AboutURL));
	}
}