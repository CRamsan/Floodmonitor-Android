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
import android.webkit.WebView;
import android.widget.Button;

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