package org.vaadin.addons.client;

import com.google.gwt.user.client.Command;
import com.vaadin.client.ui.VMenuBar;

import java.util.ArrayList;
import java.util.List;

public class PopupMenuBarWidget extends VMenuBar {

    public PopupMenuBarWidget() {
        super();
    }

    public PopupMenuBarWidget(boolean subMenu, VMenuBar parentMenu) {
        super(subMenu, parentMenu);
    }

    public CustomMenuItemWithId addItem(String html, Command cmd, Integer id) {
        CustomMenuItemWithId item = new CustomMenuItemWithId();
        item.setHTML(html);
        item.setCommand(cmd);
        item.setId(id);
        this.addItem(item);
        return item;
    }

    public static class CustomMenuItemWithId extends VMenuBar.CustomMenuItem {
        public Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }


    public List<CustomMenuItemWithId> getItemsWithId() {
        List<CustomMenuItem> originalItems = getItems();
        if (originalItems == null) {
            return new ArrayList<>();
        }
        List<CustomMenuItemWithId> itemsWithIds = new ArrayList<>();
        for (int i = 0; i < originalItems.size(); i++) {
            CustomMenuItem item = originalItems.get(i);
            if (item instanceof  CustomMenuItem) {
                itemsWithIds.add((CustomMenuItemWithId) item);
            }
        }
        return itemsWithIds;
    }

}