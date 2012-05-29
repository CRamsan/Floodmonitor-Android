package flood.monitor.overlay;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData.Item;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import flood.monitor.R;

public class CustomOverlay extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context context;
		
	public CustomOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	public CustomOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		this.context = context;
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
	  return mOverlays.size();
	}

	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	public void setOverlay(ArrayList<OverlayItem> overlay) {
	    mOverlays = overlay;
	    populate();
	}
	
	@Override
	protected boolean onTap(int index) {
	  OverlayItem item = mOverlays.get(index);
	  /*AlertDialog.Builder dialog = new AlertDialog.Builder(context);
	  dialog.setTitle(item.getTitle());
	  dialog.setMessage(item.getSnippet());
	  dialog.show();*/
	  AlertDialog.Builder builder;
	  AlertDialog alertDialog;

	  Context mContext = context;
	  LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(context.LAYOUT_INFLATER_SERVICE);
	  View layout = inflater.inflate(R.layout.markerdialog ,(ViewGroup) ((Activity)context).findViewById(R.id.markerLayout));

	  TextView text = (TextView) layout.findViewById(R.id.textView1);
	  text.setText(item.getTitle());
	  ImageView image = (ImageView) layout.findViewById(R.id.imageView1);
	  String pathToFile = "/mnt/sdcard/FloodMonitor/cache/cute_cat.jpg";
	  image.setImageBitmap(BitmapFactory.decodeFile(pathToFile));
	  TextView text2 = (TextView) layout.findViewById(R.id.textView2);
	  text2.setText(item.getSnippet());
	  
	  builder = new AlertDialog.Builder(mContext);
	  builder.setView(layout);
	  alertDialog = builder.create();
	  alertDialog.show();
	  return true;
	}
}
