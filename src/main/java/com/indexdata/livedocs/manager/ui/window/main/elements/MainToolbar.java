package com.indexdata.livedocs.manager.ui.window.main.elements;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.indexdata.livedocs.manager.core.DisplayInitializer;
import com.indexdata.livedocs.manager.core.SpringInitializer;
import com.indexdata.livedocs.manager.core.events.AppEvents;
import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.service.model.enums.AccessMode;
import com.indexdata.livedocs.manager.service.security.UserAuthenticationContext;
import com.indexdata.livedocs.manager.ui.common.components.AccessAbstractComponent;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;
import com.indexdata.livedocs.manager.ui.window.login.LoginWindowEX;
import com.indexdata.livedocs.manager.ui.window.main.MainContent;
import com.indexdata.livedocs.manager.ui.window.main.MainWindow;
import com.indexdata.livedocs.manager.ui.window.profile.ManageProfileWindow;
import com.indexdata.livedocs.manager.ui.window.users.UsersContent;

/**
 * @author Adam Dec
 * @since 0.0.4
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public final class MainToolbar extends AccessAbstractComponent {

    private static Logger LOGGER = LoggerFactory.getLogger(MainToolbar.class);

    private final Composite mainComposite, usersComposite, footerComposite;
    private final Shell mainWindow;
    private final DisplayInitializer displayInitializer;
    private final SpringInitializer springInitializer;

    private MainContent mainContent;
    private UsersContent usersContent;

    @Autowired
    private UserAuthenticationContext userAuthenticationContext;

    @Autowired
    private ClassPathXmlApplicationContext applicationContext;

    public MainToolbar(final Shell mainWindow, final Composite mainComposite, final Composite usersComposite,
            final Composite footerComposite, DisplayInitializer displayInitializer, SpringInitializer springInitializer) {
        this.mainWindow = mainWindow;
        this.mainComposite = mainComposite;
        this.usersComposite = usersComposite;
        this.footerComposite = footerComposite;
        this.displayInitializer = displayInitializer;
        this.springInitializer = springInitializer;
    }

    public ToolBar createToolBar() {
        this.mainContent = new MainContent(mainWindow, mainComposite, footerComposite);
        this.mainContent.createContent();

        if (usersAccessGranted()) {
            this.usersContent = new UsersContent(mainWindow, usersComposite, footerComposite);
            this.usersContent.createContent();
        }

        final ToolBar mainToolBar = new ToolBar(mainWindow, SWT.FLAT | SWT.RIGHT);
        mainToolBar
                .setBounds(0, MainWindow.TOOLBAR_Y_LOCATION, MainWindow.MAIN_WINDOW_WIDTH, MainWindow.TOOLBAR_HEIGHT);
        UIControlUtils.applyToolbarStyle(mainToolBar);

        final ToolItem documentsAndProfilesItem = new ToolItem(mainToolBar, SWT.NONE);
        documentsAndProfilesItem.setText(I18NResources.getProperty("main.window.document.profile.tool.item"));
        documentsAndProfilesItem.setToolTipText(documentsAndProfilesItem.getText());
        documentsAndProfilesItem.setWidth(160);
        documentsAndProfilesItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                mainComposite.setVisible(true);
                mainWindow.setTabList(new Control[] {
                        mainComposite, mainToolBar
                });
                mainComposite.notifyListeners(AppEvents.FOCUS.getValue(), new Event());
                mainWindow.addListener(SWT.Resize, mainContent.getMainContentListener());
                if (usersAccessGranted()) {
                    usersComposite.setVisible(false);
                    mainWindow.removeListener(SWT.Resize, usersContent.getUsersContentListener());
                }
                mainWindow.notifyListeners(SWT.Resize, new Event());
                UIControlsRepository.getInstance().sendRefreshProfilesEventToDocumentProfilesCombo();
            }
        });

        if (usersAccessGranted()) {
            final ToolItem separator = new ToolItem(mainToolBar, SWT.NONE);
            separator.setText("|");
            separator.setEnabled(false);

            final ToolItem usersItem = new ToolItem(mainToolBar, SWT.NONE);
            usersItem.setText(I18NResources.getProperty("main.window.document.users.tool.item"));
            usersItem.setToolTipText(usersItem.getText());
            usersItem.setWidth(160);
            usersItem.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    mainComposite.setVisible(false);
                    mainWindow.removeListener(SWT.Resize, mainContent.getMainContentListener());
                    if (usersAccessGranted()) {
                        usersComposite.setVisible(true);
                        mainWindow.setTabList(new Control[] {
                                usersComposite, mainToolBar
                        });
                        usersComposite.notifyListeners(AppEvents.FOCUS.getValue(), new Event());
                        mainWindow.addListener(SWT.Resize, usersContent.getUsersContentListener());
                    }
                    usersContent.getTable().notifyListeners(AppEvents.REFRESH_USERS.getValue(), new Event());
                    mainWindow.notifyListeners(SWT.Resize, new Event());
                }
            });
            usersComposite.setVisible(false);
        }

        if (!getProfileAccess().equals(AccessMode.Denied)) {
            final ToolItem separator = new ToolItem(mainToolBar, SWT.NONE);
            separator.setText("|");
            separator.setEnabled(false);

            final ToolItem manageProfilesItem = new ToolItem(mainToolBar, SWT.NONE);
            manageProfilesItem.setText(I18NResources.getProperty("main.window.manage.profiles.button"));
            manageProfilesItem.setToolTipText(manageProfilesItem.getText());
            manageProfilesItem.setWidth(160);
            manageProfilesItem.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    new ManageProfileWindow().open();
                }
            });
        }

        final ToolItem separator = new ToolItem(mainToolBar, SWT.NONE);
        separator.setText("|");
        separator.setEnabled(false);

        final ToolItem logoutItem = new ToolItem(mainToolBar, SWT.NONE);
        logoutItem.setText(I18NResources.getProperty("main.window.document.logout.tool.item") + " ["
                + userAuthenticationContext.getCurrentUserName() + "]");
        logoutItem.setToolTipText(logoutItem.getText());
        logoutItem.setWidth(160);
        logoutItem.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                SecurityContextHolder.clearContext();
                Event disposeEvent = new Event();
                disposeEvent.data = "LOGOUT";
                mainWindow.notifyListeners(SWT.Dispose, disposeEvent);
                mainWindow.setVisible(false);
                springInitializer.destroy();
                applicationContext.close();

                try {
                    new LoginWindowEX(new SpringInitializer(), displayInitializer).open();
                } catch (final RuntimeException e) {
                    LOGGER.error("Could not launch main application window", e);
                    UIControlUtils.createErrorMessageBoxWithTitle(new Shell(),
                            "Could not launch main application window=" + e.getMessage());
                    System.exit(-1);
                }
            }
        });

        mainComposite.setVisible(true);
        return mainToolBar;
    }

    private boolean usersAccessGranted() {
        return !getUsersAccess().equals(AccessMode.Denied);
    }
}
