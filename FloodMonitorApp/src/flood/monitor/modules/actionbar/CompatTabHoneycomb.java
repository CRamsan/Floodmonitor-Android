package flood.monitor.modules.actionbar;

public class CompatTabHoneycomb extends CompatTab {
    // The native tab object that this CompatTab acts as a proxy for.
    ActionBar.Tab mTab;

    protected CompatTabHoneycomb(FragmentActivity activity, String tag) {
        // Proxy to new ActionBar.newTab API
        mTab = activity.getActionBar().newTab();
    }

    public CompatTab setText(int resId) {
        // Proxy to new ActionBar.Tab.setText API
        mTab.setText(resId);
        return this;
    }

    // Do the same for other properties (icon, callback, etc.)
}