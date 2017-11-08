package org.vaadin.addons.demo;

import org.vaadin.addons.PopupMenuBar;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("demo")
@Title("PopupMenuBar Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI
{

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {

        // Initialize our new UI component
        PopupMenuBar menuBar = new PopupMenuBar();
        //MenuBar menuBar = new MenuBar();

        MenuBar.MenuItem helpMainItem = menuBar.addItem("Help", (MenuBar.Command) null);

        MenuBar.MenuItem generalHelp = helpMainItem.addItem("General help", (MenuBar.Command) null);

        MenuBar.MenuItem about = helpMainItem.addItem("About", (MenuBar.Command) null);

        menuBar.setMenuItemUrl(generalHelp, "https://www.vaadin.com");
        menuBar.setMenuItemUrl(about,"https://www.google.com");

        // Show it in the middle of the screen
        final VerticalLayout layout = new VerticalLayout();
        layout.setStyleName("demoContentLayout");
        layout.setSizeFull();
        layout.addComponent(menuBar);
        layout.setComponentAlignment(menuBar, Alignment.MIDDLE_CENTER);
        setContent(layout);
    }
}
