package flood.monitor.abstracts;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;

public abstract class Module {

	private List _listeners = new ArrayList();

	public synchronized void addEventListener(ModuleEventListener listener) {
		_listeners.add(listener);
	}

	public synchronized void removeEventListener(ModuleEventListener listener) {
		_listeners.remove(listener);
	}

	private synchronized void updateUI(int eventCode)	{
	    EventObject event = new EventObject(this);
	    Iterator i = _listeners.iterator();
	    while(i.hasNext())	{
	      ((ModuleEventListener) i.next()).handleModuleEvent(event, eventCode);
	    }
	  }
}
