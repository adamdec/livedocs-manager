package com.indexdata.livedocs.manager.ui.window.users;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.indexdata.livedocs.manager.core.events.AppEvents;
import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.resources.Resources;
import com.indexdata.livedocs.manager.service.UserService;
import com.indexdata.livedocs.manager.service.model.enums.AccessMode;
import com.indexdata.livedocs.manager.ui.common.components.AccessAbstractComponent;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.window.main.MainWindow;
import com.indexdata.livedocs.manager.ui.window.users.table.TableUserDataAdapter;
import com.indexdata.livedocs.manager.ui.window.users.table.UserTable;

/**
 * This class will provide users window.
 * 
 * @author Adam Dec
 * @since 0.0.4
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class UsersContent extends AccessAbstractComponent {

    @Autowired
    private UserService userService;

    private final Shell mainWindow;
    private final Composite usersComposite;
    private final Composite footerComposite;

    private final List<String> columns = Arrays.asList(
            I18NResources.getProperty("manage.users.window.table.id.column"),
            I18NResources.getProperty("manage.users.window.table.first.name.column"),
            I18NResources.getProperty("manage.users.window.table.last.name.column"),
            I18NResources.getProperty("manage.users.window.table.user.name.column"),
            I18NResources.getProperty("manage.users.window.table.email.column"), "");

    private final List<String> columnsWithReadAccess = Arrays.asList(
            I18NResources.getProperty("manage.users.window.table.id.column"),
            I18NResources.getProperty("manage.users.window.table.first.name.column"),
            I18NResources.getProperty("manage.users.window.table.last.name.column"),
            I18NResources.getProperty("manage.users.window.table.user.name.column"),
            I18NResources.getProperty("manage.users.window.table.email.column"));

    private Listener usersContentListener;
    private Composite addUserComposite;
    private ScrolledComposite usersTableScrolledComposite;
    private SquareButton addUserButton;
    private Table table;

    public UsersContent(final Shell mainWindow, final Composite usersComposite, final Composite footerComposite) {
        super();
        this.mainWindow = mainWindow;
        this.usersComposite = usersComposite;
        this.footerComposite = footerComposite;
    }

    public void createContent() {
        this.addUserComposite = new Composite(usersComposite, SWT.NONE);
        this.addUserComposite.setBounds(MainWindow.RIGHT_SIDE_PADDING, MainWindow.BOTTOM_SIDE_PADDING,
                usersComposite.getSize().x, 35);
        UIControlUtils.applyDefaultBackground(addUserComposite);

        final Label documentProfilesLabel = new Label(addUserComposite, SWT.NONE);
        documentProfilesLabel.setBounds(10, 10, 85, 21);
        documentProfilesLabel.setText(I18NResources.getProperty("manage.users.window.account.label"));
        UIControlUtils.applyDefaultBackground(documentProfilesLabel);
        UIControlUtils.applyDefaultFont(documentProfilesLabel);

        if (isAllowed()) {
            this.addUserButton = SquareButtonFactory.getWhiteImageButton(addUserComposite, 2, 18);
            this.addUserButton.setBackgroundImage(LiveDocsResourceManager.getImage(Resources.ADD_BUTTON.getValue()));
            this.addUserButton.setBounds(addUserComposite.getSize().x, 12, 20, 20);
            this.addUserButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    new EditUserWindow((Table) usersTableScrolledComposite.getContent(), null).open();
                }
            });

            this.usersComposite.addListener(AppEvents.FOCUS.getValue(), new Listener() {

                @Override
                public void handleEvent(Event event) {
                    addUserButton.setFocus();
                }
            });
        }

        // Users table
        this.usersTableScrolledComposite = new ScrolledComposite(usersComposite, SWT.NONE);
        this.usersTableScrolledComposite.setAlwaysShowScrollBars(true);
        this.usersTableScrolledComposite.setExpandVertical(true);
        this.usersTableScrolledComposite.setExpandHorizontal(true);
        this.usersTableScrolledComposite.setLocation(MainWindow.RIGHT_SIDE_PADDING, addUserComposite.getSize().y
                + addUserComposite.getLocation().y + MainWindow.VERTICAL_MARGIN);
        UIControlUtils.applyDefaultBackground(usersTableScrolledComposite);

        this.table = new UserTable().createUserTable(usersTableScrolledComposite,
                getUsersAccess().equals(AccessMode.Manage) || getUsersAccess().equals(AccessMode.Write) ? columns
                        : columnsWithReadAccess);
        this.usersTableScrolledComposite.setContent(table);

        // Populate table data
        this.table.addListener(AppEvents.REFRESH_USERS.getValue(), new Listener() {

            @Override
            public void handleEvent(Event event) {
                new TableUserDataAdapter(table).populateData(userService.getAllUsers());
                table.layout(true, true);
                table.update();
                table.redraw();
            }
        });

        registerUsersContentListeners();
    }

    private void registerUsersContentListeners() {
        this.usersContentListener = new Listener() {

            @Override
            public void handleEvent(Event e) {
                final Rectangle mainWindowRectangle = mainWindow.getClientArea();
                usersComposite.setSize(
                        mainWindowRectangle.width - MainWindow.RIGHT_SIDE_PADDING - usersComposite.getLocation().x,
                        usersComposite.getSize().y);

                // Horizontal scaling
                addUserComposite.setSize(
                        mainWindowRectangle.width - MainWindow.RIGHT_SIDE_PADDING - addUserComposite.getLocation().x,
                        addUserComposite.getSize().y);

                if (isAllowed()) {
                    addUserButton.setLocation(addUserComposite.getSize().x - MainWindow.RIGHT_SIDE_PADDING
                            - addUserButton.getSize().x, addUserButton.getLocation().y);
                }

                final int newCompositeWidth = mainWindowRectangle.width - MainWindow.RIGHT_SIDE_PADDING
                        - usersTableScrolledComposite.getLocation().x;
                usersTableScrolledComposite.setSize(newCompositeWidth, usersTableScrolledComposite.getSize().y);

                // Vertical scaling
                usersTableScrolledComposite.setSize(
                        usersTableScrolledComposite.getSize().x,
                        mainWindowRectangle.height - footerComposite.getSize().y
                                - usersTableScrolledComposite.getLocation().y - usersComposite.getLocation().y);

            }
        };
        mainWindow.addListener(SWT.Resize, getUsersContentListener());
    }

    public Table getTable() {
        return table;
    }

    private boolean isAllowed() {
        return getUsersAccess().equals(AccessMode.Manage);
    }

    public Listener getUsersContentListener() {
        return usersContentListener;
    }
}
