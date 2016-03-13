package com.indexdata.livedocs.manager.ui.window.profile;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.DataIntegrityViolationException;

import com.indexdata.livedocs.manager.core.events.AppEvents;
import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.resources.Resources;
import com.indexdata.livedocs.manager.core.utils.ValidationUtils;
import com.indexdata.livedocs.manager.service.ProfileService;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;

/**
 * This class will add new profile to database.
 * 
 * @author Adam Dec
 * @since 0.0.3
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class AddProfileWindow {

    @Autowired
    private ProfileService profileService;

    private final Shell shell;
    private final Composite documentProfilesComposite;

    public AddProfileWindow(final Composite documentProfilesComposite) {
        this.documentProfilesComposite = documentProfilesComposite;
        this.shell = new Shell(UIControlsRepository.getInstance().getMainWindowShell(), SWT.TITLE | SWT.SYSTEM_MODAL);
        this.shell.setSize(500, 130);
        this.shell.setText(I18NResources.getProperty("add.profile.window.title"));
        UIControlUtils.applyDefaultBackground(shell);
        UIControlUtils.centerWindowPosition(shell);
    }

    protected void createContents() {
        final Label profileNameLabel = new Label(shell, SWT.NONE);
        profileNameLabel.setBounds(10, 10, 100, 21);
        profileNameLabel.setText(I18NResources.getProperty("add.profile.window.profile.name.label"));
        UIControlUtils.applyDefaultBackground(profileNameLabel);
        UIControlUtils.applyDefaultFont(profileNameLabel);

        final Text profileNameText = new Text(shell, SWT.BORDER | SWT.CENTER);
        profileNameText.setText(I18NResources.getProperty("add.profile.window.new.profile.name.text"));
        profileNameText.setBounds(10, 37, 474, 21);
        UIControlUtils.applyDefaultBackground(profileNameText);
        UIControlUtils.applyDefaultFont(profileNameText);
        UIControlUtils.cleanTextOnFocus(profileNameText);
        UIControlUtils.addDefaultValueKeyListener(profileNameText);

        final SquareButton closeButton = SquareButtonFactory.getWhiteButton(shell);
        closeButton.setBounds(404, 64, 80, 32);
        closeButton.setText(I18NResources.getProperty("close.button.label"));
        UIControlUtils.addCloseAction(shell, closeButton);

        final SquareButton createProfileButton = SquareButtonFactory.getWhiteButton(shell);
        createProfileButton.setBounds(10, 64, 152, 32);
        createProfileButton.setText(I18NResources.getProperty("add.profile.window.create.profile.button"));
        createProfileButton.setImage(LiveDocsResourceManager.getImage(Resources.ADD_BUTTON.getValue()));
        createProfileButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (!ValidationUtils.validateProfileTextBox(shell, profileNameText)) {
                    return;
                }

                try {
                    profileService.createNewProfile(profileNameText.getText());
                    UIControlUtils.refreshControl(documentProfilesComposite, AppEvents.REFRESH_PROFILES);
                    closeButton.notifyListeners(SWT.Selection, new Event());
                } catch (DataIntegrityViolationException de) {
                    UIControlUtils.createWarningMessageBox(shell,
                            I18NResources.getProperty("add.profile.window.profile.exists"));
                }
            }
        });

        UIControlUtils.addWindowCloseOnEscOrNotifyButton(shell, createProfileButton, profileNameText);
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
