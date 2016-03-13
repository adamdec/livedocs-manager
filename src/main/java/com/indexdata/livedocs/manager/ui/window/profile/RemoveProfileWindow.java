package com.indexdata.livedocs.manager.ui.window.profile;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
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
import com.indexdata.livedocs.manager.core.utils.DateFormatterUtils;
import com.indexdata.livedocs.manager.core.utils.FileSizeFormatter;
import com.indexdata.livedocs.manager.service.ProfileService;
import com.indexdata.livedocs.manager.service.model.Profile;
import com.indexdata.livedocs.manager.service.security.UserAuthenticationContext;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.components.progress.InfiniteProgressPanel;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;

/**
 * This class will remove profile from database. Please note that this removal is cascade so all discs (attached to
 * given profile) and documents will be also deleted.
 * 
 * @author Adam Dec
 * @since 0.0.3
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class RemoveProfileWindow {

    private static Logger LOGGER = LoggerFactory.getLogger(RemoveProfileWindow.class);

    @Autowired
    private ProfileService profileService;

    @Autowired
    private MessageSourceAdapter messageSourceAdapter;

    @Autowired
    private UserAuthenticationContext userAuthenticationManager;

    private final Shell shell;
    private final Composite documentProfilesComposite;
    private final Profile profile;
    private List<String> deleteProfileLabels = Arrays.asList(
            I18NResources.getProperty("remove.profile.profile.name.label"),
            I18NResources.getProperty("remove.profile.profile.date.created"),
            I18NResources.getProperty("remove.profile.profile.documents"),
            I18NResources.getProperty("remove.profile.profile.size"));

    public RemoveProfileWindow(Composite documentProfilesComposite, Profile profile) {
        this.documentProfilesComposite = documentProfilesComposite;
        this.profile = profile;
        this.shell = new Shell(UIControlsRepository.getInstance().getMainWindowShell(), SWT.TITLE | SWT.SYSTEM_MODAL);
        this.shell.setSize(732, 210);
        this.shell.setText(messageSourceAdapter.getProperty("remove.profile.window.title", profile.getName()));
        UIControlUtils.applyDefaultBackground(shell);
        UIControlUtils.centerWindowPosition(shell);
    }

    private void createContents() {
        final Composite labelComposite = new Composite(shell, SWT.NONE);
        labelComposite.setBounds(10, 10, 706, 62);
        UIControlUtils.applyDefaultBackground(labelComposite);

        final Label warningLabel = new Label(labelComposite, SWT.WRAP | SWT.SHADOW_NONE | SWT.CENTER);
        warningLabel.setBounds(10, 10, 688, 44);
        warningLabel.setText(messageSourceAdapter.getProperty("remove.profile.window.information", profile.getName()));
        UIControlUtils.applyDefaultBackground(warningLabel);
        UIControlUtils.applyDefaultBoldedFont(warningLabel);

        final ScrolledComposite profileScrolledComposite = new ScrolledComposite(shell, SWT.NONE);
        profileScrolledComposite.setBounds(10, 78, 706, 62);
        profileScrolledComposite.setExpandHorizontal(true);
        profileScrolledComposite.setExpandVertical(true);

        final Composite profileComposite = new Composite(profileScrolledComposite, SWT.NONE);
        UIControlUtils.applyDefaultBackground(profileComposite);
        profileComposite.setLayout(new GridLayout(4, true));

        for (final String deleteProfileLabel : deleteProfileLabels) {
            final Label profileLabel = new Label(profileComposite, SWT.NONE);
            profileLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            profileLabel.setText(deleteProfileLabel);
            UIControlUtils.applyDefaultBackground(profileLabel);
            UIControlUtils.applyDefaultFont(profileLabel);
        }

        final Label profileName = new Label(profileComposite, SWT.NONE);
        profileName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        profileName.setText(profile.getName());
        UIControlUtils.applyDefaultBackground(profileName);
        UIControlUtils.applyDefaultFont(profileName);

        final Label profileDateCreated = new Label(profileComposite, SWT.NONE);
        profileDateCreated.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        profileDateCreated.setText(DateFormatterUtils.convertDateTime(profile.getDateCreated()));
        UIControlUtils.applyDefaultBackground(profileDateCreated);
        UIControlUtils.applyDefaultFont(profileDateCreated);

        final Label profileDocumentsNumber = new Label(profileComposite, SWT.NONE);
        profileDocumentsNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        profileDocumentsNumber.setText(String.valueOf(profile.getTotalDocumentsNumber()));
        UIControlUtils.applyDefaultBackground(profileDocumentsNumber);
        UIControlUtils.applyDefaultFont(profileDocumentsNumber);

        final Label profileDocumentsTotalSize = new Label(profileComposite, SWT.NONE);
        profileDocumentsTotalSize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        profileDocumentsTotalSize.setText(FileSizeFormatter.formatSize(profile.getTotalDocumentsSize()));
        UIControlUtils.applyDefaultBackground(profileDocumentsTotalSize);
        UIControlUtils.applyDefaultFont(profileDocumentsTotalSize);
        profileScrolledComposite.setContent(profileComposite);
        profileScrolledComposite.setMinSize(profileComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        final SquareButton removeProfileButton = SquareButtonFactory.getWhiteButton(shell);
        removeProfileButton.setBounds(10, 146, 160, 32);
        removeProfileButton.setText(I18NResources.getProperty("remove.profile.window.button"));
        removeProfileButton.setImage(LiveDocsResourceManager.getImage(Resources.DELETE_BUTTON.getValue()));

        final SquareButton cancelButton = SquareButtonFactory.getWhiteButton(shell);
        cancelButton.setBounds(633, 146, 80, 32);
        cancelButton.setText(I18NResources.getProperty("cancel.button.label"));
        UIControlUtils.addCloseAction(shell, cancelButton);

        final Label passwordLabel = new Label(shell, SWT.NONE);
        passwordLabel.setBounds(190, 151, 190, 28);
        passwordLabel.setText(I18NResources.getProperty("retype.password.label"));
        UIControlUtils.applyDefaultBackground(passwordLabel);
        UIControlUtils.applyDefaultFont(passwordLabel);

        final Text passwordText = new Text(shell, SWT.BORDER | SWT.PASSWORD | SWT.CENTER);
        passwordText.setBounds(385, 146, 220, 28);
        UIControlUtils.applyDefaultBackground(passwordText);
        UIControlUtils.applyDefaultFont(passwordText);
        UIControlUtils.addWindowCloseOnEscOrNotifyButton(shell, removeProfileButton, passwordText);

        removeProfileButton.addSelectionListener(new SelectionAdapter() {

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

                final InfiniteProgressPanel progressPanel = UIControlUtils.createProgressPanel(
                        I18NResources.getProperty("remove.profile.progress"), shell);
                progressPanel.start();

                final Thread performer = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        profileService.removeProfileById(profile.getId());
                        LOGGER.debug("Removed profile with id [{}] and name [{}]", profile.getId(), profile.getName());

                        Display.getDefault().syncExec(
                                () -> {
                                    UIControlUtils
                                            .refreshControl(documentProfilesComposite, AppEvents.REFRESH_PROFILES);
                                    UIControlsRepository.getInstance().sendRefreshProfilesEventToDocumentProfilesCombo();
                                    UIControlsRepository.getInstance().sendSelectionEventToSearchClearButton();
                                    shell.forceFocus();
                                    shell.setFocus();
                                    progressPanel.stop();

                                    final int result = UIControlUtils.createInfoMessageBox(
                                            shell,
                                            messageSourceAdapter.getProperty("remove.profile.progress.ended",
                                                    profile.getName()));
                                    if (result == 32) {
                                        cancelButton.notifyListeners(SWT.Selection, new Event());
                                    }
                                });
                    }
                }, "RemoveProfileThread");
                performer.start();
            }
        });
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
