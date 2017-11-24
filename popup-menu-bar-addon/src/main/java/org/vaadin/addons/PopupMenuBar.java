package org.vaadin.addons;

import com.vaadin.server.Resource;
import com.vaadin.ui.MenuBar;
import org.vaadin.addons.client.PopupMenuBarState;

import java.util.HashMap;

// This is the server-side UI component that provides public API 
// for PopupMenuBar
public class PopupMenuBar extends MenuBar {

    public PopupMenuBar() {
    }

    // We must override getState() to cast the state to PopupMenuBarState
    @Override
    protected PopupMenuBarState getState() {
        return (PopupMenuBarState) super.getState();
    }

    /**
     * Create a browser window opener item instead of a normal menu item
     * @param menuItem item to enhance
     * @param url URL to open
     * @param target target (for example "_blank", which is the default)
     */
    public void setMenuItemUrl(MenuItem menuItem, String url, String target) {
        if (menuItem == null) {
            return;
        }
        if (getState().menuItemUrls == null) {
            getState().menuItemUrls = new HashMap<>();
        }
        if (getState().menuItemTargets == null) {
            getState().menuItemTargets = new HashMap<>();
        }
        getState().menuItemUrls.put(menuItem.getId(), url);
        getState().menuItemTargets.put(menuItem.getId(), target);
    }

}
