package com.indexdata.livedocs.manager.ui.window.users;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.indexdata.livedocs.manager.core.events.AppEvents;
import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.resources.MessageSourceAdapter;
import com.indexdata.livedocs.manager.core.resources.Resources;
import com.indexdata.livedocs.manager.service.UserService;
import com.indexdata.livedocs.manager.service.model.User;
import com.indexdata.livedocs.manager.service.security.UserAuthenticationContext;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;
import com.indexdata.livedocs.manager.ui.window.profile.RemoveProfileWindow;
import com.indexdata.livedocs.manager.ui.window.users.table.UserToRowMapping;

/**
 * @author Adam Dec
 * @since 0.0.4
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class RemoveUserWindow {

    private static Logger LOGGER = LoggerFactory.getLogger(RemoveProfileWindow.class);

    @Autowired
    private UserService userService;

    @Autowired
    private MessageSourceAdapter messageSourceAdapter;

    @Autowired
    private UserAuthenticationContext userAuthenticationManager;

    private final Shell shell;
    private final Table table;
    private final User user;
    private final Integer rowIndex;
    private final TableEditor editor;
    private final Composite buttonsComposite;
    private final List<Control> tabList = new ArrayList<Control>(3);

    public RemoveUserWindow(final Table table, final UserToRowMapping userToRowMapping, final TableEditor editor,
            final Composite buttonsComposite) {
        this.shell = new Shell(UIControlsRepository.getInstance().getMainWindowShell(), SWT.TITLE | SWT.SYSTEM_MODAL);
        this.shell.setSize(700, 145);

        this.user = userService.getUserById(userToRowMapping.getUserId());
        this.shell.setText(messageSourceAdapter.getProperty("remove.user.window.title", user.getFirstName(),
                user.getLastName()));
        UIControlUtils.centerWindowPosition(shell);
        UIControlUtils.applyDefaultBackground(shell);
        this.table = table;

        this.rowIndex = userToRowMapping.getRowIndex();
        this.editor = editor;
        this.buttonsComposite = buttonsComposite;
    }

    protected void createContents() {
        final Composite labelComposite = new Composite(shell, SWT.NONE);
        UIControlUtils.applyDefaultBackground(labelComposite);
        labelComposite.setBounds(10, 10, 674, 64);

        final Label warningLabel = new Label(labelComposite, SWT.WRAP | SWT.SHADOW_NONE | SWT.CENTER);
        UIControlUtils.applyDefaultBackground(warningLabel);
        UIControlUtils.applyDefaultFont(warningLabel);
        warningLabel.setBounds(10, 10, 654, 44);
        warningLabel.setText(messageSourceAdapter.getProperty("remove.user.window.information", user.getFirstName(),
                user.getLastName()));

        final SquareButton removeUserButton = SquareButtonFactory.getWhiteButton(shell, 6, 12);
        removeUserButton.setBounds(10, 80, 124, 32);
        removeUserButton.setText(I18NResources.getProperty("remove.user.window.button"));
        removeUserButton.setImage(LiveDocsResourceManager.getImage(Resources.DELETE_BUTTON.getValue()));

        final SquareButton cancelButton = SquareButtonFactory.getWhiteButton(shell);
        cancelButton.setBounds(608, 80, 80, 32);
        cancelButton.setText(I18NResources.getProperty("cancel.button.label"));
        UIControlUtils.addCloseAction(shell, cancelButton);
        UIControlUtils.addNotifyAction(cancelButton, table, AppEvents.REFRESH_USERS);

        final Label passwordLabel = new Label(shell, SWT.NONE);
        passwordLabel.setBounds(160, 85, 190, 28);
        passwordLabel.setText(I18NResources.getProperty("retype.password.label"));
        UIControlUtils.applyDefaultBackground(passwordLabel);
        UIControlUtils.applyDefaultFont(passwordLabel);

        final Text passwordText = new Text(shell, SWT.BORDER | SWT.PASSWORD | SWT.CENTER);
        passwordText.setBounds(355, 80, 220, 28);
        UIControlUtils.applyDefaultBackground(passwordText);
        UIControlUtils.applyDefaultFont(passwordText);
        UIControlUtils.addWindowCloseOnEscOrNotifyButton(shell, removeUserButton, passwordText);

        removeUserButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                if (passwordText.getText().isEmpty()) {
                    UIControlUtils.createErrorMessageBox(shell, I18NResources.getProperty("empty.password"));
                    return;
                }
                if (!userAuthenticationManager.checkPassword(passwordText.getText())) {
                    UIControlUtils.createErrorMessageBox(shell, I18NResources.getProperty("wrong.password"));
                    return;
                }

                LOGGER.debug("Remove user with id={} and userName={}", user.getId(), user.getUserName());
                userService.deleteUserById(user.getId());
                table.remove(rowIndex);
                table.layout(true, true);
                table.setRedraw(true);
                table.update();
                cancelButton.notifyListeners(SWT.Selection, new Event());

                UIControlUtils.disposeChildren(buttonsComposite);
                buttonsComposite.dispose();
                editor.getEditor().dispose();
                editor.dispose();
            }
        });

        this.tabList.add(0, passwordText);
        this.tabList.add(1, removeUserButton);
        this.tabList.add(2, cancelButton);
        this.shell.setTabList(tabList.toArray(new Control[0]));
    }

    public void open() {
        this.open(false);
    }

    public void open(boolean isStandalone) {
        createContents();
        shell.open();
        shell.layout();

        if (isStandalone) {
            Display display = Display.getDefault();
            while (!shell.isDisposed()) {
                if (!display.readAndDispatch()) {
                    display.sleep();
                }
            }
        }
    }
}
