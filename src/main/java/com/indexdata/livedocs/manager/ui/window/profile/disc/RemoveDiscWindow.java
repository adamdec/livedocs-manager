package com.indexdata.livedocs.manager.ui.window.profile.disc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.indexdata.livedocs.manager.core.events.AppEvents;
import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.resources.MessageSourceAdapter;
import com.indexdata.livedocs.manager.core.resources.Resources;
import com.indexdata.livedocs.manager.core.utils.DateFormatterUtils;
import com.indexdata.livedocs.manager.core.utils.DiscFileUtils;
import com.indexdata.livedocs.manager.service.DiscService;
import com.indexdata.livedocs.manager.service.FileService;
import com.indexdata.livedocs.manager.service.model.Disc;
import com.indexdata.livedocs.manager.service.model.File;
import com.indexdata.livedocs.manager.service.model.Profile;
import com.indexdata.livedocs.manager.service.security.UserAuthenticationContext;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.components.progress.InfiniteProgressPanel;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;

/**
 * This class will remove discs (for given profile) from database. Please note that this removal is cascade so all
 * documents (attached to given disc) will be also deleted.
 * 
 * @author Adam Dec
 * @since 0.0.3
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class RemoveDiscWindow {

    @Autowired
    private FileService fileService;

    @Autowired
    private DiscService discService;

    @Autowired
    private MessageSourceAdapter messageSourceAdapter;

    @Autowired
    private UserAuthenticationContext userAuthenticationManager;

    private final Shell shell;
    private final ScrolledComposite discsScrolledComposite;
    private final Disc disc;
    private final Profile profile;

    private List<String> deleteDiscLabels = Arrays.asList(I18NResources.getProperty("remove.disc.profile.disc.title"),
            I18NResources.getProperty("remove.disc.profile.batch.number"),
            I18NResources.getProperty("remove.disc.profile.date.imported"),
            I18NResources.getProperty("remove.disc.profile.files"));
    private final List<Control> tabList = new ArrayList<Control>(3);

    public RemoveDiscWindow(final Shell detailsProfileShell, final ScrolledComposite discsScrolledComposite,
            final Profile profile, final Disc disc) {
        this.shell = new Shell(UIControlsRepository.getInstance().getMainWindowShell(), SWT.TITLE | SWT.SYSTEM_MODAL);
        this.shell.setSize(730, 210);
        this.shell.setText(messageSourceAdapter.getProperty("remove.disc.window.title", profile.getName()));
        UIControlUtils.applyDefaultBackground(shell);
        UIControlUtils.centerWindowPosition(shell);
        this.discsScrolledComposite = discsScrolledComposite;
        this.disc = disc;
        this.profile = profile;
    }

    private void createContents() {
        final Composite labelComposite = new Composite(shell, SWT.NONE);
        labelComposite.setBounds(10, 10, 706, 62);
        UIControlUtils.applyDefaultBackground(labelComposite);

        final Label warningLabel = new Label(labelComposite, SWT.WRAP | SWT.SHADOW_NONE | SWT.CENTER);
        warningLabel.setBounds(10, 10, 688, 44);
        warningLabel.setText(messageSourceAdapter.getProperty("remove.disc.window.information", disc.getTitle()));
        UIControlUtils.applyDefaultBackground(warningLabel);
        UIControlUtils.applyDefaultBoldedFont(warningLabel);

        final ScrolledComposite discScrolledComposite = new ScrolledComposite(shell, SWT.NONE);
        discScrolledComposite.setBounds(10, 78, 706, 62);
        discScrolledComposite.setExpandHorizontal(true);
        discScrolledComposite.setExpandVertical(true);

        final Composite discComposite = new Composite(discScrolledComposite, SWT.NONE);
        UIControlUtils.applyDefaultBackground(discComposite);
        discComposite.setLayout(new GridLayout(4, true));

        for (final String deleteDiscLabel : deleteDiscLabels) {
            final Label profileLabel = new Label(discComposite, SWT.NONE);
            profileLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            profileLabel.setText(deleteDiscLabel);
            UIControlUtils.applyDefaultBackground(profileLabel);
            UIControlUtils.applyDefaultFont(profileLabel);
        }

        final Label discTitle = new Label(discComposite, SWT.NONE);
        discTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        discTitle.setText(disc.getTitle());
        UIControlUtils.applyDefaultBackground(discTitle);
        UIControlUtils.applyDefaultFont(discTitle);

        final Label batchNumber = new Label(discComposite, SWT.NONE);
        batchNumber.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        batchNumber.setText(disc.getBatchNumber());
        UIControlUtils.applyDefaultBackground(batchNumber);
        UIControlUtils.applyDefaultFont(batchNumber);

        final Label dateImported = new Label(discComposite, SWT.NONE);
        dateImported.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        dateImported.setText(DateFormatterUtils.convertDateTime(disc.getImportDate()));
        UIControlUtils.applyDefaultBackground(dateImported);
        UIControlUtils.applyDefaultFont(dateImported);

        final Label files = new Label(discComposite, SWT.NONE);
        files.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        files.setText(String.valueOf(disc.getFilesNumber()));
        UIControlUtils.applyDefaultBackground(files);
        UIControlUtils.applyDefaultFont(files);
        discScrolledComposite.setContent(discComposite);
        discScrolledComposite.setMinSize(discComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        final SquareButton removeDiscButton = SquareButtonFactory.getWhiteButton(shell);
        removeDiscButton.setBounds(10, 146, 145, 32);
        removeDiscButton.setText(I18NResources.getProperty("remove.disc.window.button"));
        removeDiscButton.setImage(LiveDocsResourceManager.getImage(Resources.DELETE_BUTTON.getValue()));

        final SquareButton cancelButton = SquareButtonFactory.getWhiteButton(shell);
        cancelButton.setBounds(633, 146, 80, 32);
        cancelButton.setText(I18NResources.getProperty("cancel.button.label"));
        UIControlUtils.addCloseAction(shell, cancelButton);
        UIControlUtils.addNotifyAction(cancelButton, discsScrolledComposite, AppEvents.REFRESH_DISCS);
        UIControlUtils.applyDefaultFont(cancelButton);

        final Label passwordLabel = new Label(shell, SWT.NONE);
        passwordLabel.setBounds(160, 151, 190, 28);
        passwordLabel.setText(I18NResources.getProperty("retype.password.label"));
        UIControlUtils.applyDefaultBackground(passwordLabel);
        UIControlUtils.applyDefaultFont(passwordLabel);

        final Text passwordText = new Text(shell, SWT.BORDER | SWT.PASSWORD | SWT.CENTER);
        passwordText.setBounds(385, 148, 220, 28);
        UIControlUtils.applyDefaultBackground(passwordText);
        UIControlUtils.applyDefaultFont(passwordText);
        UIControlUtils.addWindowCloseOnEscOrNotifyButton(shell, removeDiscButton, passwordText);

        removeDiscButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                if (passwordText.getText().isEmpty()) {
                    UIControlUtils.createErrorMessageBox(shell, I18NResources.getProperty("empty.password"));
                    return;
                }
                if (!userAuthenticationManager.checkPassword(passwordText.getText())) {
                    UIControlUtils.createErrorMessageBox(shell, I18NResources.getProperty("incorrect.password"));
                    return;
                }

                final InfiniteProgressPanel progressPanel = UIControlUtils.createProgressPanel(
                        I18NResources.getProperty("remove.disc.progress"), shell);
                progressPanel.start();

                final Thread performer = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        final List<File> fileList = fileService.getAllFilesByDiscId(disc.getId());
                        if (discService.removeDiscById(disc.getId(), profile.getId(), disc.getFilesNumber(),
                                disc.getFilesSize())) {
                            DiscFileUtils.deleteDiscFiles(fileList);
                        }

                        Display.getDefault()
                                .syncExec(
                                        () -> {
                                            UIControlsRepository.getInstance().sendRefreshProfilesEventToDetailsProfileWindow();
                                            UIControlsRepository.getInstance()
                                                    .sendRefreshProfilesEventToDocumentProfilesComposite();
                                            UIControlsRepository.getInstance()
                                                    .sendRefreshAttributesEventToProfileAttributesGroupAdapter(
                                                            profile.getId());
                                            UIControlsRepository.getInstance().sendSelectionEventToSearchClearButton();
                                            shell.forceFocus();
                                            shell.setFocus();
                                            progressPanel.stop();

                                            final int result = UIControlUtils.createInfoMessageBox(
                                                    shell,
                                                    messageSourceAdapter.getProperty("remove.disc.progress.ended",
                                                            disc.getTitle()));
                                            if (result == 32) {
                                                cancelButton.notifyListeners(SWT.Selection, new Event());
                                            }
                                        });
                    }
                }, "RemoveDiscThread");
                performer.start();
            }
        });

        this.tabList.add(0, passwordText);
        this.tabList.add(1, removeDiscButton);
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
