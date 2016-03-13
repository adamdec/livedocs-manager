package com.indexdata.livedocs.manager.ui.window.profile;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;

import com.indexdata.livedocs.manager.core.events.AppEvents;
import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.resources.Resources;
import com.indexdata.livedocs.manager.core.utils.DateFormatterUtils;
import com.indexdata.livedocs.manager.core.utils.FileSizeFormatter;
import com.indexdata.livedocs.manager.service.model.Attribute;
import com.indexdata.livedocs.manager.service.model.Profile;
import com.indexdata.livedocs.manager.service.model.enums.AccessMode;
import com.indexdata.livedocs.manager.ui.common.components.AccessAbstractWindow;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;
import com.indexdata.livedocs.manager.ui.window.profile.disc.ImportDiscWindow;
import com.indexdata.livedocs.manager.ui.window.profile.disc.RemoveDiscWindow;
import com.indexdata.livedocs.manager.ui.window.profile.elements.ProfileDetailsDiscsComposite;

/**
 * @author Adam Dec
 * @since 0.0.5
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class DetailsProfileWindow extends AccessAbstractWindow {

    private static Logger LOGGER = LoggerFactory.getLogger(DetailsProfileWindow.class);

    private final Profile profile;
    private Text profileNameText, numberOfDocumentsText, creationDataText, totalSizeOfText;
    private Label attributesNumberText, importedDiscsText;

    public DetailsProfileWindow(final Profile profile) {
        super(new Shell(UIControlsRepository.getInstance().getMainWindowShell(), SWT.TITLE | SWT.SYSTEM_MODAL));
        this.profile = profile;
        this.shell.setSize(810, 380);
        this.shell.setText(messageSourceAdapter.getProperty("details.profile.window.title", profile.getName()));
        this.shell.addListener(AppEvents.REFRESH_PROFILES.getValue(), new Listener() {

            @Override
            public void handleEvent(Event event) {
                LOGGER.debug("Received 'REFRESH_PROFILES' event {}", event);
                populateData();
            }
        });
        UIControlsRepository.getInstance().setDetailsProfileWindow(shell);
        UIControlUtils.applyDefaultBackground(shell);
        UIControlUtils.centerWindowPosition(shell);
    }

    private void populateData() {
        final Profile updatedProfile = profileService.getProfileById(profile.getId());
        LOGGER.debug("Refreshing profile data:{}", updatedProfile);
        profileNameText.setText(updatedProfile.getName());
        numberOfDocumentsText.setText(String.valueOf(updatedProfile.getTotalDocumentsNumber()));
        creationDataText.setText(DateFormatterUtils.convertDateTime(updatedProfile.getDateCreated()));
        totalSizeOfText.setText(FileSizeFormatter.formatSize(updatedProfile.getTotalDocumentsSize()));
        attributesNumberText.setText(String.valueOf(updatedProfile.getAttributes().size()));
        importedDiscsText.setText(String.valueOf(discService.countByProfileId(profile.getId())));
    }

    protected void createContents() {
        // Profile details
        final ScrolledComposite detailsScrolledComposite = new ScrolledComposite(shell, SWT.NONE);
        detailsScrolledComposite.setBounds(10, 10, 317, 106);
        detailsScrolledComposite.setExpandHorizontal(true);
        detailsScrolledComposite.setExpandVertical(true);

        final Composite detailsComposite = new Composite(detailsScrolledComposite, SWT.NONE);
        UIControlUtils.applyDefaultBackground(detailsComposite);
        detailsComposite.setLayout(new GridLayout(2, false));

        final Label profileNameLabel = new Label(detailsComposite, SWT.NONE);
        profileNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        profileNameLabel.setText(I18NResources.getProperty("details.profile.window.name.label"));
        UIControlUtils.applyDefaultBackground(profileNameLabel);
        UIControlUtils.applyDefaultBoldedFont(profileNameLabel);

        final Label numberOfDocumentsLabel = new Label(detailsComposite, SWT.NONE);
        numberOfDocumentsLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        numberOfDocumentsLabel.setText(I18NResources.getProperty("details.profile.window.no.documents.label"));
        UIControlUtils.applyDefaultBackground(numberOfDocumentsLabel);
        UIControlUtils.applyDefaultBoldedFont(numberOfDocumentsLabel);

        this.profileNameText = new Text(detailsComposite, SWT.CENTER);
        this.profileNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        this.profileNameText.setText(profile.getName());
        this.profileNameText.setEditable(false);
        UIControlUtils.applyDefaultBackground(profileNameText);
        UIControlUtils.applyDefaultFont(profileNameText);

        this.numberOfDocumentsText = new Text(detailsComposite, SWT.CENTER);
        this.numberOfDocumentsText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        this.numberOfDocumentsText.setText(String.valueOf(profile.getTotalDocumentsNumber()));
        this.numberOfDocumentsText.setEditable(false);
        UIControlUtils.applyDefaultBackground(numberOfDocumentsText);
        UIControlUtils.applyDefaultFont(numberOfDocumentsText);

        final Label dateCreatedLabel = new Label(detailsComposite, SWT.NONE);
        dateCreatedLabel.setAlignment(SWT.CENTER);
        dateCreatedLabel.setText(I18NResources.getProperty("details.profile.window.date.created.label"));
        UIControlUtils.applyDefaultBackground(dateCreatedLabel);
        UIControlUtils.applyDefaultBoldedFont(dateCreatedLabel);

        final Label totalSizeOfLabel = new Label(detailsComposite, SWT.NONE);
        totalSizeOfLabel.setText(I18NResources.getProperty("details.profile.window.total.size.label"));
        UIControlUtils.applyDefaultBackground(totalSizeOfLabel);
        UIControlUtils.applyDefaultBoldedFont(totalSizeOfLabel);

        this.creationDataText = new Text(detailsComposite, SWT.CENTER);
        this.creationDataText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        this.creationDataText.setText(DateFormatterUtils.convertDateTime(profile.getDateCreated()));
        this.creationDataText.setEditable(false);
        UIControlUtils.applyDefaultBackground(creationDataText);
        UIControlUtils.applyDefaultFont(creationDataText);

        this.totalSizeOfText = new Text(detailsComposite, SWT.CENTER);
        this.totalSizeOfText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        this.totalSizeOfText.setText(FileSizeFormatter.formatSize(profile.getTotalDocumentsSize()));
        this.totalSizeOfText.setEditable(false);
        UIControlUtils.applyDefaultBackground(totalSizeOfText);
        UIControlUtils.applyDefaultFont(totalSizeOfText);
        detailsScrolledComposite.setContent(detailsComposite);
        detailsScrolledComposite.setMinSize(detailsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        // Attributes number
        final ScrolledComposite attributesNumberScrolledComposite = new ScrolledComposite(shell, SWT.NONE);
        attributesNumberScrolledComposite.setBounds(10, 122, 317, 26);
        attributesNumberScrolledComposite.setExpandHorizontal(true);
        attributesNumberScrolledComposite.setExpandVertical(true);

        final Composite attributesNumberComposite = new Composite(attributesNumberScrolledComposite, SWT.NONE);
        attributesNumberComposite.setBounds(10, 122, 317, 26);
        UIControlUtils.applyDefaultBackground(attributesNumberComposite);
        attributesNumberComposite.setLayout(new GridLayout(2, true));

        final Label attributesNumberLabel = new Label(attributesNumberComposite, SWT.NONE);
        attributesNumberLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        attributesNumberLabel.setText(I18NResources.getProperty("details.profile.window.attributes.label"));
        UIControlUtils.applyDefaultBackground(attributesNumberLabel);
        UIControlUtils.applyDefaultBoldedFont(attributesNumberLabel);

        this.attributesNumberText = new Label(attributesNumberComposite, SWT.RIGHT);
        this.attributesNumberText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        this.attributesNumberText.setText(String.valueOf(profile.getAttributes().size()));
        UIControlUtils.applyDefaultBackground(attributesNumberText);
        UIControlUtils.applyDefaultFont(attributesNumberText);
        attributesNumberScrolledComposite.setContent(attributesNumberComposite);
        attributesNumberScrolledComposite.setMinSize(attributesNumberComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        // Attributes
        final ScrolledComposite attributesScrolledComposite = new ScrolledComposite(shell, SWT.V_SCROLL);
        attributesScrolledComposite.setBounds(10, 154, 317, 187);
        attributesScrolledComposite.setExpandHorizontal(true);
        attributesScrolledComposite.setExpandVertical(true);

        final Composite attributesComposite = new Composite(attributesScrolledComposite, SWT.NONE);
        attributesComposite.setLayout(new GridLayout(2, true));
        UIControlUtils.applyDefaultBackground(attributesComposite);

        int count = 1;
        for (Attribute attribute : profile.getAttributes()) {
            final Label attributeName = new Label(attributesComposite, SWT.NONE);
            attributeName.setText("Attribute " + (count++) + ":");
            attributeName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            UIControlUtils.applyDefaultBackground(attributeName);
            UIControlUtils.applyDefaultFont(attributeName);

            final Label attributeValue = new Label(attributesComposite, SWT.NONE);
            attributeValue.setText(attribute.getName());
            attributeValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            UIControlUtils.applyDefaultBackground(attributeValue);
            UIControlUtils.applyDefaultFont(attributeValue);
        }
        attributesScrolledComposite.setContent(attributesComposite);
        attributesScrolledComposite.setMinSize(attributesComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        // Discs number
        final ScrolledComposite discsNumberScrolledComposite = new ScrolledComposite(shell, SWT.NONE);
        discsNumberScrolledComposite.setBounds(333, 10, 466, 25);
        discsNumberScrolledComposite.setExpandHorizontal(true);
        discsNumberScrolledComposite.setExpandVertical(true);

        final Composite discsNumberComposite = new Composite(discsNumberScrolledComposite, SWT.NONE);
        UIControlUtils.applyDefaultBackground(discsNumberComposite);
        discsNumberComposite.setLayout(new GridLayout(2, true));

        final Label importedDiscsLabel = new Label(discsNumberComposite, SWT.NONE);
        UIControlUtils.applyDefaultBackground(importedDiscsLabel);
        UIControlUtils.applyDefaultBoldedFont(importedDiscsLabel);
        importedDiscsLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        importedDiscsLabel.setText(I18NResources.getProperty("details.profile.window.imported.discs.label"));

        this.importedDiscsText = new Label(discsNumberComposite, SWT.RIGHT);
        this.importedDiscsText.setLayoutData(new GridData(SWT.FILL, SWT.RIGHT, true, false, 1, 1));
        this.importedDiscsText.setText(String.valueOf(discService.countByProfileId(profile.getId())));
        UIControlUtils.applyDefaultBackground(importedDiscsText);
        UIControlUtils.applyDefaultFont(importedDiscsText);

        discsNumberScrolledComposite.setContent(discsNumberComposite);
        discsNumberScrolledComposite.setMinSize(discsNumberComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        SquareButton importDiscButton = null, removeDiscButton = null;
        if (!getUploadAccess().equals(AccessMode.Denied)) {
            importDiscButton = SquareButtonFactory.getWhiteButton(shell, 6, 8);
            importDiscButton.setImage(LiveDocsResourceManager.getImage(Resources.ADD_BUTTON.getValue()));
            importDiscButton.setBounds(333, 301, 105, 32);
            importDiscButton.setText(I18NResources.getProperty("details.profile.window.import.disc.button"));

            removeDiscButton = SquareButtonFactory.getWhiteButton(shell, 6, 8);
            removeDiscButton.setImage(LiveDocsResourceManager.getImage(Resources.DELETE_BUTTON.getValue()));
            removeDiscButton.setBounds(452, 301, 115, 32);
            removeDiscButton.setText(I18NResources.getProperty("details.profile.window.remove.disc.button"));
            removeDiscButton.setEnabled(false);
        }

        // Discs table
        final ScrolledComposite discsScrolledComposite = new ScrolledComposite(shell, SWT.V_SCROLL);
        discsScrolledComposite.setBounds(333, 42, 466, 253);
        discsScrolledComposite.setExpandHorizontal(true);
        discsScrolledComposite.setExpandVertical(true);
        UIControlUtils.applyDefaultBackground(discsScrolledComposite);
        final ProfileDetailsDiscsComposite profileDetailsDiscsComposite = new ProfileDetailsDiscsComposite(
                discsScrolledComposite, profile, removeDiscButton);
        final Table discTable = profileDetailsDiscsComposite.create();
        discsScrolledComposite.setContent(discTable);
        discsScrolledComposite.setMinSize(discTable.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        if (!getUploadAccess().equals(AccessMode.Denied)) {
            importDiscButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    new ImportDiscWindow(shell, discsScrolledComposite, profile).open();
                }
            });

            removeDiscButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent event) {
                    LOGGER.debug("Currently selected disc [{}]", profileDetailsDiscsComposite.getSelectedDisc());
                    new RemoveDiscWindow(shell, discsScrolledComposite, profile, profileDetailsDiscsComposite
                            .getSelectedDisc()).open();
                }
            });
        }

        final SquareButton closeButton = SquareButtonFactory.getWhiteButton(shell);
        closeButton.setBounds(720, 301, 80, 32);
        closeButton.setText(I18NResources.getProperty("close.button.label"));
        UIControlUtils.addCloseAction(shell, closeButton);
    }
}
