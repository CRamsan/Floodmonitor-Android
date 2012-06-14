package flood.monitor.abstracts;

import java.util.EventObject;

public interface ModuleEventListener {

	public void handleModuleEvent(EventObject e, int eventCode);
	
}
