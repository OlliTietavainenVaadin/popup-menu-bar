package org.vaadin.addons.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.UIDL;
import com.vaadin.client.VConsole;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.ui.Icon;
import com.vaadin.client.ui.VMenuBar;
import com.vaadin.client.ui.menubar.MenuBarConnector;
import com.vaadin.shared.ui.ComponentStateUtil;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.menubar.MenuBarConstants;
import org.vaadin.addons.PopupMenuBar;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

// Connector binds client-side widget class to server-side component class
// Connector lives in the client and the @Connect annotation specifies the
// corresponding server-side component
@Connect(PopupMenuBar.class)
public class PopupMenuBarConnector extends MenuBarConnector {

    private Map<Integer, String> menuItemUrls;

    public PopupMenuBarConnector() {
    }

    @Override
    public PopupMenuBarWidget getWidget() {
        return (PopupMenuBarWidget) super.getWidget();
    }

    // We must implement getState() to cast to correct type
    @Override
    public PopupMenuBarState getState() {
        return (PopupMenuBarState) super.getState();
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().htmlContentAllowed = uidl.hasAttribute(MenuBarConstants.HTML_CONTENT_ALLOWED);

        getWidget().openRootOnHover = uidl.getBooleanAttribute(MenuBarConstants.OPEN_ROOT_MENU_ON_HOWER);

        getWidget().enabled = isEnabled();

        // For future connections
        getWidget().client = client;
        getWidget().uidlId = uidl.getId();

        // Empty the menu every time it receives new information
        if (!getWidget().getItems().isEmpty()) {
            getWidget().clearItems();
        }

        UIDL options = uidl.getChildUIDL(0);

        if (null != getState() && !ComponentStateUtil.isUndefinedWidth(getState())) {
            UIDL moreItemUIDL = options.getChildUIDL(0);
            StringBuffer itemHTML = new StringBuffer();

            if (moreItemUIDL.hasAttribute("icon")) {
                Icon icon = client.getIcon(moreItemUIDL.getStringAttribute("icon"));
                if (icon != null) {
                    itemHTML.append(icon.getElement().getString());
                }
            }

            String moreItemText = moreItemUIDL.getStringAttribute("text");
            if ("".equals(moreItemText)) {
                moreItemText = "&#x25BA;";
            }
            itemHTML.append(moreItemText);

            getWidget().moreItem = GWT.create(VMenuBar.CustomMenuItem.class);
            getWidget().moreItem.setHTML(itemHTML.toString());
            getWidget().moreItem.setCommand(VMenuBar.emptyCommand);

            getWidget().collapsedRootItems = new VMenuBar(true, getWidget());
            getWidget().moreItem.setSubMenu(getWidget().collapsedRootItems);
            getWidget().moreItem.addStyleName(getWidget().getStylePrimaryName() + "-more-menuitem");
        }

        UIDL uidlItems = uidl.getChildUIDL(1);
        Iterator<Object> itr = uidlItems.getChildIterator();
        Stack<Iterator<Object>> iteratorStack = new Stack<Iterator<Object>>();
        //--- Stack<PopupMenuBarWidget> menuStack = new Stack<PopupMenuBarWidget>();
        Stack<VMenuBar> menuStack = new Stack<VMenuBar>();
        PopupMenuBarWidget currentMenu = getWidget();
        // -*- VMenuBar currentMenu = getWidget();

        while (itr.hasNext()) {
            UIDL item = (UIDL) itr.next();
            PopupMenuBarWidget.CustomMenuItemWithId currentItem = null;
            // -*- VMenuBar.CustomMenuItem currentItem = null;

            final int itemId = item.getIntAttribute("id");

            boolean itemHasCommand = item.hasAttribute("command");
            boolean itemIsCheckable = item.hasAttribute(MenuBarConstants.ATTRIBUTE_CHECKED);

            String itemHTML = getWidget().buildItemHTML(item);

            Command cmd = null;
            if (!item.hasAttribute("separator")) {
                if (itemHasCommand || itemIsCheckable) {
                    // Construct a command that fires onMenuClick(int) with the
                    // item's id-number
                    cmd = new Command() {
                        @Override
                        public void execute() {
                            getWidget().hostReference.onMenuClick(itemId);
                        }
                    };
                }
            }

            currentItem = currentMenu.addItem(itemHTML.toString(), cmd, itemId);
            currentItem.updateFromUIDL(item, client);

            if (item.getChildCount() > 0) {
                menuStack.push(currentMenu);
                iteratorStack.push(itr);
                itr = item.getChildIterator();
                currentMenu = new PopupMenuBarWidget(true, currentMenu);
                client.getVTooltip().connectHandlersToWidget(currentMenu);
                // this is the top-level style that also propagates to items -
                // any item specific styles are set above in
                // currentItem.updateFromUIDL(item, client)
                if (ComponentStateUtil.hasStyles(getState())) {
                    for (String style : getState().styles) {
                        currentMenu.addStyleDependentName(style);
                    }
                }
                currentItem.setSubMenu(currentMenu);
            }

            while (!itr.hasNext() && !iteratorStack.empty()) {
                boolean hasCheckableItem = false;
                for (VMenuBar.CustomMenuItem menuItem : currentMenu.getItems()) {
                    hasCheckableItem = hasCheckableItem || menuItem.isCheckable();
                }
                if (hasCheckableItem) {
                    currentMenu.addStyleDependentName("check-column");
                } else {
                    currentMenu.removeStyleDependentName("check-column");
                }

                itr = iteratorStack.pop();
                currentMenu = (PopupMenuBarWidget) menuStack.pop();
            }
        } // while

        getLayoutManager().setNeedsHorizontalLayout(this);




        /* --- */
        updateUrlsToMenuItems(getWidget());
    }

    // Whenever the state changes in the server-side, this method is called
    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        this.menuItemUrls = getState().menuItemUrls;
        super.onStateChanged(stateChangeEvent);

        updateUrlsToMenuItems(getWidget());
    }

    public void updateUrlsToMenuItems(final PopupMenuBarWidget menuBar) {

        if (menuItemUrls == null) {
            return;
        }
        if (menuItemUrls.size() == 0) {
            return;
        }

        List<PopupMenuBarWidget.CustomMenuItemWithId> items = menuBar.getItemsWithId();

        if (items == null || items.size() == 0) {
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            PopupMenuBarWidget.CustomMenuItemWithId item = items.get(i);

            VMenuBar subMenu = item.getSubMenu();
            if (item.getSubMenu() != null && (subMenu instanceof PopupMenuBarWidget)) {
                updateUrlsToMenuItems((PopupMenuBarWidget) item.getSubMenu());
            }
            final Integer itemId = item.getId();
            if (menuItemUrls.containsKey(itemId)) {

                item.setCommand(new Command() {
                    @Override
                    public void execute() {
                        if (menuBar.isAttached()) {
                            Window.open(menuItemUrls.get(itemId), "_blank", "");
                            menuBar.hideChildren();

                        }
                    }
                });
            }

        }

    }

}
