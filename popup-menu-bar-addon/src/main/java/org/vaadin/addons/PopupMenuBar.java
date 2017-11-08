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

    public void setMenuItemUrl(MenuItem menuItem, String url) {
        if (menuItem == null) {
            return;
        }
        if (getState().menuItemUrls == null) {
            getState().menuItemUrls = new HashMap<>();
        }
        getState().menuItemUrls.put(menuItem.getId(), url);
    }

}
