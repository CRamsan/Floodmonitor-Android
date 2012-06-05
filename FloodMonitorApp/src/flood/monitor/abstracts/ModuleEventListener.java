package flood.monitor.abstracts;

import java.util.EventObject;

import android.app.Activity;
import android.provider.ContactsContract.CommonDataKinds.Event;

public interface ModuleEventListener {

	public void handleModuleEvent(EventObject e, int eventCode);
	
}
