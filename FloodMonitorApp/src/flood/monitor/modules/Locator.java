/*package flood.monitor.modules;

public class Locator {

}
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class Locator extends Activity {
	private EditText firstField;
	private EditText secondField;

	@Override 
	public void onCreate(Bundle savedInstanceState){super.onCreate(savedInstanceState);setContentView(R.layout.main);AssetManager assetManager = getAssets();String[] files = null;try{files = assetManager.list("image")} catch (IOException e){Log.e("tag",e.getMessage())}firstField = (EditText) findViewById(R.id.firstId);firstField.setText(Integer.toString(files.length) + " file. File name is "+ files[0]);InputStream inputStream = null;try{inputStream = assetManager.open("readme.txt")} catch (IOException e){Log.e("tag",e.getMessage())}String s = readTextFile(inputStream);secondField = (EditText) findViewById(R.id.secondId);secondField.setText(s)}	private String readTextFile(InputStream inputStream){ByteArrayOutputStream outputStream = new ByteArrayOutputStream();byte buf[] = new byte[1024];int len;try{while ((len = inputStream.read(buf)) != -1){outputStream.write(buf,0,len)}outputStream.close();inputStream.close()} catch (IOException e){}return outputStream.toString()}}