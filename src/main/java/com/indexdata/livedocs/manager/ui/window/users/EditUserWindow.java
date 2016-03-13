package com.indexdata.livedocs.manager.ui.window.users;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
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
import org.springframework.security.authentication.BadCredentialsException;

import com.indexdata.livedocs.manager.core.events.AppEvents;
import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.resources.MessageSourceAdapter;
import com.indexdata.livedocs.manager.core.resources.Resources;
import com.indexdata.livedocs.manager.service.UserService;
import com.indexdata.livedocs.manager.service.model.Permission;
import com.indexdata.livedocs.manager.service.model.User;
import com.indexdata.livedocs.manager.service.model.enums.AccessMode;
import com.indexdata.livedocs.manager.service.model.enums.Section;
import com.indexdata.livedocs.manager.service.security.UserAuthenticationContext;
import com.indexdata.livedocs.manager.service.security.UserAuthenticationManager;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;
import com.indexdata.livedocs.manager.ui.common.utils.UserPasswordValidator;
import com.indexdata.livedocs.manager.ui.window.users.helper.UserPermissionComboHelper;
import com.indexdata.livedocs.manager.ui.window.users.profiles.ProfilePermissionsComposite;
import com.indexdata.livedocs.manager.ui.window.users.table.UserToRowMapping;

/**
 * This class will provide window to add users to database.
 * 
 * @author Adam Dec
 * @since 0.0.4
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class EditUserWindow {

    private static Logger LOGGER = LoggerFactory.getLogger(EditUserWindow.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserAuthenticationContext userAuthenticationContext;

    @Autowired
    private UserAuthenticationManager userAuthenticationManager;

    @Autowired
    private MessageSourceAdapter messageSourceAdapter;

    private final Shell shell;
    private final Table usersTable;
    private final User user;
    private Text usernameText, firstNameText, lastNameText, emailText, passwordText, repeatPasswordText;
    private Combo usersCombo, profileCombo, uploadCombo;
    private ProfilePermissionsComposite profilePermissionsComposite;

    public EditUserWindow(final Table usersTable, final UserToRowMapping userToRowMapping) {
        this.usersTable = usersTable;
        if (userToRowMapping != null && userToRowMapping.getUserId() != null) {
            user = userService.getUserById(userToRowMapping.getUserId());
        }
        else {
            user = null;
        }
        this.shell = new Shell(UIControlsRepository.getInstance().getMainWindowShell(), SWT.TITLE | SWT.SYSTEM_MODAL);
        this.shell.setSize(700, 415);
        this.shell
                .setText(user == null ? messageSourceAdapter.getProperty("add.user.window.title")
                        : (messageSourceAdapter.getProperty("edit.user.window.title", user.getFirstName(),
                                user.getLastName())));
        UIControlUtils.applyDefaultBackground(shell);
        UIControlUtils.centerWindowPosition(shell);
    }

    private void createContents() {
        final Composite userDetailsComposite = new Composite(shell, SWT.NONE);
        UIControlUtils.applyDefaultBackground(userDetailsComposite);
        userDetailsComposite.setBounds(10, 38, 340, 174);

        final Label usernameLabel = new Label(userDetailsComposite, SWT.NONE);
        UIControlUtils.applyDefaultFont(usernameLabel);
        UIControlUtils.applyDefaultBackground(usernameLabel);
        usernameLabel.setBounds(10, 10, 87, 15);
        usernameLabel.setText(I18NResources.getProperty("add.user.window.details.user.name.label"));

        final Label firstNameLabel = new Label(userDetailsComposite, SWT.NONE);
        UIControlUtils.applyDefaultFont(firstNameLabel);
        UIControlUtils.applyDefaultBackground(firstNameLabel);
        firstNameLabel.setBounds(10, 37, 87, 15);
        firstNameLabel.setText(I18NResources.getProperty("add.user.window.details.first.name.label"));

        final Label lastNameLabel = new Label(userDetailsComposite, SWT.NONE);
        UIControlUtils.applyDefaultFont(lastNameLabel);
        UIControlUtils.applyDefaultBackground(lastNameLabel);
        lastNameLabel.setBounds(10, 64, 87, 15);
        lastNameLabel.setText(I18NResources.getProperty("add.user.window.details.last.name.label"));

        final Label emailLabel = new Label(userDetailsComposite, SWT.NONE);
        UIControlUtils.applyDefaultFont(emailLabel);
        UIControlUtils.applyDefaultBackground(emailLabel);
        emailLabel.setBounds(10, 91, 87, 15);
        emailLabel.setText(I18NResources.getProperty("add.user.window.details.email.label"));

        final Label newPasswordLabel = new Label(userDetailsComposite, SWT.NONE);
        UIControlUtils.applyDefaultFont(newPasswordLabel);
        UIControlUtils.applyDefaultBackground(newPasswordLabel);
        newPasswordLabel.setBounds(10, 118, 97, 21);
        newPasswordLabel.setText(I18NResources.getProperty("add.user.window.details.new.password.label"));

        final Label repeatPasswordLabel = new Label(userDetailsComposite, SWT.NONE);
        UIControlUtils.applyDefaultFont(repeatPasswordLabel);
        UIControlUtils.applyDefaultBackground(repeatPasswordLabel);
        repeatPasswordLabel.setBounds(10, 145, 107, 18);
        repeatPasswordLabel.setText(I18NResources.getProperty("add.user.window.details.repeat.password.label"));

        this.usernameText = new Text(userDetailsComposite, SWT.BORDER | SWT.CENTER);
        UIControlUtils.applyDefaultFont(usernameText);
        UIControlUtils.applyDefaultBackground(usernameText);
        usernameText.setBounds(175, 7, 155, 21);

        this.firstNameText = new Text(userDetailsComposite, SWT.BORDER | SWT.CENTER);
        UIControlUtils.applyDefaultFont(firstNameText);
        UIControlUtils.applyDefaultBackground(firstNameText);
        firstNameText.setBounds(175, 34, 155, 21);

        this.lastNameText = new Text(userDetailsComposite, SWT.BORDER | SWT.CENTER);
        UIControlUtils.applyDefaultFont(lastNameText);
        UIControlUtils.applyDefaultBackground(lastNameText);
        lastNameText.setBounds(175, 61, 155, 21);

        this.emailText = new Text(userDetailsComposite, SWT.BORDER | SWT.CENTER);
        UIControlUtils.applyDefaultFont(emailText);
        UIControlUtils.applyDefaultBackground(emailText);
        emailText.setBounds(175, 88, 155, 21);

        this.passwordText = new Text(userDetailsComposite, SWT.BORDER | SWT.PASSWORD | SWT.CENTER);
        UIControlUtils.applyDefaultFont(passwordText);
        UIControlUtils.applyDefaultBackground(passwordText);
        passwordText.setBounds(175, 115, 155, 21);

        this.repeatPasswordText = new Text(userDetailsComposite, SWT.BORDER | SWT.PASSWORD | SWT.CENTER);
        UIControlUtils.applyDefaultFont(repeatPasswordText);
        UIControlUtils.applyDefaultBackground(repeatPasswordText);
        repeatPasswordText.setBounds(175, 142, 155, 21);

        final Composite userPermissionsComposite = new Composite(shell, SWT.NONE);
        UIControlUtils.applyDefaultBackground(userPermissionsComposite);
        userPermissionsComposite.setBounds(10, 246, 340, 97);

        final Label usersLabel = new Label(userPermissionsComposite, SWT.NONE);
        UIControlUtils.applyDefaultFont(usersLabel);
        UIControlUtils.applyDefaultBackground(usersLabel);
        usersLabel.setBounds(10, 10, 55, 18);
        usersLabel.setText(I18NResources.getProperty("add.user.window.details.user.permissions.users.label"));

        final Label profileLabel = new Label(userPermissionsComposite, SWT.NONE);
        UIControlUtils.applyDefaultFont(profileLabel);
        UIControlUtils.applyDefaultBackground(profileLabel);
        profileLabel.setBounds(10, 39, 55, 18);
        profileLabel.setText(I18NResources.getProperty("add.user.window.details.user.permissions.profiles.label"));

        final Label uploadLabel = new Label(userPermissionsComposite, SWT.NONE);
        UIControlUtils.applyDefaultFont(uploadLabel);
        UIControlUtils.applyDefaultBackground(uploadLabel);
        uploadLabel.setBounds(10, 68, 55, 18);
        uploadLabel.setText(I18NResources.getProperty("add.user.window.details.user.permissions.upload.label"));

        this.usersCombo = new Combo(userPermissionsComposite, SWT.NONE);
        UIControlUtils.applyDefaultFont(usersCombo);
        UIControlUtils.applyDefaultBackground(usersCombo);
        this.usersCombo.setBounds(175, 9, 155, 21);
        this.usersCombo.setData(Section.Users);
        UserPermissionComboHelper.populateUsersDefaultValues(usersCombo);
        UserPermissionComboHelper.supply(usersCombo, user);

        this.profileCombo = new Combo(userPermissionsComposite, SWT.NONE);
        UIControlUtils.applyDefaultFont(profileCombo);
        UIControlUtils.applyDefaultBackground(profileCombo);
        this.profileCombo.setBounds(175, 38, 155, 23);
        this.profileCombo.setData(Section.Profile);
        UserPermissionComboHelper.populateProfilesDefaultValues(profileCombo);
        UserPermissionComboHelper.supply(profileCombo, user);

        this.uploadCombo = new Combo(userPermissionsComposite, SWT.NONE);
        UIControlUtils.applyDefaultFont(uploadCombo);
        UIControlUtils.applyDefaultBackground(uploadCombo);
        this.uploadCombo.setBounds(175, 67, 155, 23);
        this.uploadCombo.setData(Section.Upload);
        UserPermissionComboHelper.populateUploadDefaultValues(uploadCombo);
        UserPermissionComboHelper.supply(uploadCombo, user);

        final Label userDetailsLabel = new Label(shell, SWT.NONE);
        UIControlUtils.applyDefaultBoldedFont(userDetailsLabel);
        UIControlUtils.applyDefaultBackground(userDetailsLabel);
        userDetailsLabel.setBounds(10, 10, 90, 22);
        userDetailsLabel.setText(I18NResources.getProperty("add.user.window.details.label"));

        final Label userPermissionsLabel = new Label(shell, SWT.NONE);
        UIControlUtils.applyDefaultBoldedFont(userPermissionsLabel);
        UIControlUtils.applyDefaultBackground(userPermissionsLabel);
        userPermissionsLabel.setBounds(10, 218, 123, 22);
        userPermissionsLabel.setText(I18NResources.getProperty("add.user.window.details.user.permissions.label"));

        final Label profilePermissionsLabel = new Label(shell, SWT.NONE);
        UIControlUtils.applyDefaultBoldedFont(profilePermissionsLabel);
        UIControlUtils.applyDefaultBackground(profilePermissionsLabel);
        profilePermissionsLabel.setBounds(356, 10, 139, 22);
        profilePermissionsLabel.setText(I18NResources.getProperty("add.user.window.details.profile.permissions.label"));

        final ScrolledComposite profilePermissionsScrolledComposite = new ScrolledComposite(shell, SWT.BORDER
                | SWT.H_SCROLL | SWT.V_SCROLL);
        profilePermissionsScrolledComposite.setBounds(356, 38, 328, 305);
        profilePermissionsScrolledComposite.setExpandHorizontal(true);
        profilePermissionsScrolledComposite.setExpandVertical(true);

        this.profilePermissionsComposite = new ProfilePermissionsComposite(user);
        final Composite profilesComposite = profilePermissionsComposite.create(profilePermissionsScrolledComposite);
        UIControlUtils.applyDefaultBoldedFont(profilesComposite);
        profilePermissionsScrolledComposite.setContent(profilesComposite);
        profilePermissionsScrolledComposite.setMinSize(profilesComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        final SquareButton cancelButton = SquareButtonFactory.getWhiteButton(shell);
        cancelButton.setBounds(604, 349, 80, 32);
        UIControlUtils.addCloseAction(shell, cancelButton, I18NResources.getProperty("cancel.button.label"));

        // If user exists...
        if (user != null) {
            // Populate user data
            usernameText.setText(user.getUserName());
            usernameText.setEnabled(false);
            firstNameText.setText(user.getFirstName());
            lastNameText.setText(user.getLastName());
            emailText.setText(user.getEmail());
            passwordText.setText(userAuthenticationContext.getProvidedPassword());
            repeatPasswordText.setText(userAuthenticationContext.getProvidedPassword());
        }

        // Save action
        final SquareButton saveProfileButton = SquareButtonFactory.getWhiteButton(shell, 6, 18);
        saveProfileButton.setBounds(10, 349, 130, 32);
        saveProfileButton.setText(user != null ? I18NResources.getProperty("save.and.close.button.label")
                : I18NResources.getProperty("add.user.window.button.add"));
        saveProfileButton.setImage(user != null ? LiveDocsResourceManager.getImage(Resources.SAVE_BUTTON.getValue())
                : LiveDocsResourceManager.getImage(Resources.ADD_BUTTON.getValue()));
        saveProfileButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (validateUserForm(user)) {
                    if (user == null) {
                        final long newUserId = userService.saveUser(populateUserData(new User()));
                        LOGGER.debug("Created new user with id={}", newUserId);
                    }
                    else {
                        final long userId = userService.updateUser(populateUserData(user));
                        LOGGER.debug("Edited user {} with id={}", user.getUserName(), userId);

                        if (user.getUserName().equals(userAuthenticationContext.getCurrentUserName())) {
                            try {
                                userAuthenticationManager.setAppAuthenticationdata(user.getUserName(),
                                        userAuthenticationContext.getProvidedPassword());
                                LOGGER.debug("User {} was successfully re-authenticated with new profile permissions.",
                                        user.getUserName());
                            } catch (BadCredentialsException be) {
                                LOGGER.error(
                                        "Could not make re-authenticate (with new profile permissions) for user={} and password={}",
                                        user.getUserName(), userAuthenticationContext.getProvidedPassword(), be);
                                UIControlUtils.createWarningMessageBox(shell,
                                        I18NResources.getProperty("login.window.wrong.password"));
                            }
                        }
                        else {
                            LOGGER.error("{} does not equal to user stored in security context {}", user.getUserName(),
                                    userAuthenticationContext.getCurrentUserName());
                        }
                    }
                    usersTable.notifyListeners(AppEvents.REFRESH_USERS.getValue(), new Event());
                    cancelButton.notifyListeners(SWT.Selection, new Event());
                }
            }
        });
    }

    private User populateUserData(final User user) {
        if (user.getUserName() == null) {
            user.setUserName(usernameText.getText());
        }
        user.setFirstName(firstNameText.getText());
        user.setLastName(lastNameText.getText());
        user.setEmail(emailText.getText());
        user.setPassword(passwordText.getText());
        userAuthenticationContext.setProvidedPassword(passwordText.getText());

        if (user.getUserPermissions().size() > 0) {
            user.getUserPermissions().clear();
        }

        user.getUserPermissions().add(
                new Permission(((Section) usersCombo.getData()).toString(), AccessMode.valueOf(usersCombo.getText())));
        user.getUserPermissions().add(
                new Permission(((Section) profileCombo.getData()).toString(),
                        AccessMode.valueOf(profileCombo.getText())));
        user.getUserPermissions()
                .add(new Permission(((Section) uploadCombo.getData()).toString(), AccessMode.valueOf(uploadCombo
                        .getText())));

        if (user.getProfilePermissions().size() > 0) {
            user.getProfilePermissions().clear();
        }
        for (Combo combo : profilePermissionsComposite.getComboList()) {
            user.getProfilePermissions().add(
                    new Permission(String.valueOf(combo.getData()), AccessMode.valueOf(combo.getText())));
        }
        return user;
    }

    private boolean validateUserForm(final User user) {
        if (user == null && StringUtils.isEmpty(usernameText.getText())) {
            UIControlUtils.createWarningMessageBox(shell, I18NResources.getProperty("add.user.window.empty.username"));
            return false;
        }

        if (StringUtils.isEmpty(firstNameText.getText())) {
            UIControlUtils
                    .createWarningMessageBox(shell, I18NResources.getProperty("add.user.window.empty.first.name"));
            return false;
        }

        if (StringUtils.isEmpty(lastNameText.getText())) {
            UIControlUtils.createWarningMessageBox(shell, I18NResources.getProperty("add.user.window.empty.last.name"));
            return false;
        }

        if (StringUtils.isEmpty(emailText.getText())) {
            UIControlUtils.createWarningMessageBox(shell, I18NResources.getProperty("add.user.window.empty.email"));
            return false;
        }

        if (!EmailValidator.getInstance().isValid(emailText.getText())) {
            UIControlUtils.createWarningMessageBox(shell, I18NResources.getProperty("add.user.window.wrong.email"));
            return false;
        }

        if (StringUtils.isEmpty(passwordText.getText())) {
            UIControlUtils.createWarningMessageBox(shell, I18NResources.getProperty("add.user.window.empty.password"));
            return false;
        }

        if (!passwordText.getText().equals(repeatPasswordText.getText())) {
            UIControlUtils.createWarningMessageBox(shell,
                    I18NResources.getProperty("add.user.window.empty.password.mismatch"));
            return false;
        }

        if (!UserPasswordValidator.validate(shell, passwordText.getText())) {
            return false;
        }
        return true;
    }

    public Table getParent() {
        return usersTable;
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