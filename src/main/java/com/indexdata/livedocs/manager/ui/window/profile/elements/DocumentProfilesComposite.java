package com.indexdata.livedocs.manager.ui.window.profile.elements;

import java.util.List;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.indexdata.livedocs.manager.core.events.AppEvents;
import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.resources.Resources;
import com.indexdata.livedocs.manager.service.ProfileService;
import com.indexdata.livedocs.manager.service.model.Profile;
import com.indexdata.livedocs.manager.service.model.enums.AccessMode;
import com.indexdata.livedocs.manager.ui.common.components.AccessAbstractComponent;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;
import com.indexdata.livedocs.manager.ui.window.main.elements.profiles.DocumentProfilesComboAdapter;
import com.indexdata.livedocs.manager.ui.window.profile.DetailsProfileWindow;
import com.indexdata.livedocs.manager.ui.window.profile.EditProfileWindow;
import com.indexdata.livedocs.manager.ui.window.profile.RemoveProfileWindow;

/**
 * Populates all profiles with actions.
 * 
 * @author Adam Dec
 * @since 0.0.3
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class DocumentProfilesComposite extends AccessAbstractComponent {

    private static Logger LOGGER = LoggerFactory.getLogger(DocumentProfilesComboAdapter.class);

    @Autowired
    private ProfileService profileService;

    private final ScrolledComposite documentProfilesScrolledComposite;
    private final Composite documentProfilesComposite;

    public DocumentProfilesComposite(final ScrolledComposite documentProfilesScrolledComposite) {
        this.documentProfilesScrolledComposite = documentProfilesScrolledComposite;
        this.documentProfilesComposite = new Composite(documentProfilesScrolledComposite, SWT.NONE);
        UIControlUtils.applyDefaultBackground(documentProfilesComposite);
        UIControlsRepository.getInstance().setDocumentProfilesComposite(documentProfilesComposite);
    }

    public Composite create() {
        documentProfilesComposite.addListener(AppEvents.REFRESH_PROFILES.getValue(), new Listener() {

            @Override
            public void handleEvent(Event event) {
                LOGGER.debug("Received 'REFRESH_PROFILES' event {}", event);
                UIControlUtils.disposeChildren(documentProfilesComposite);
                populateProfileData(documentProfilesComposite);
                documentProfilesScrolledComposite.setMinSize(documentProfilesComposite.computeSize(SWT.DEFAULT,
                        SWT.DEFAULT));
                documentProfilesScrolledComposite.layout(true, true);
            }
        });
        populateProfileData(documentProfilesComposite);
        return documentProfilesComposite;
    }

    private void populateProfileData(Composite documentProfilesComposite) {
        LOGGER.debug("Requesting profile list...");
        final List<Profile> profileList = profileService.getAllProfilesSortedByName();
        LOGGER.debug("Got profile list sorted by name [ASC] with size={}", profileList.size());

        if (profileList.size() == 0) {
            documentProfilesComposite.setLayout(new GridLayout(1, true));
            final Label noProfilesLabel = new Label(documentProfilesComposite, SWT.NONE);
            noProfilesLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            noProfilesLabel.setText(I18NResources.getProperty("manage.profile.window.no.profiles"));
            UIControlUtils.applyDefaultBackground(noProfilesLabel);
            UIControlUtils.applyDefaultFont(noProfilesLabel);
        }
        else {
            documentProfilesComposite.setLayout(new GridLayout(getProfileAccess().equals(AccessMode.Manage) ? 4 : 1,
                    false));
            for (final Profile profile : profileList) {
                final Label discProfile = new Label(documentProfilesComposite, SWT.NONE);
                discProfile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
                discProfile.setText(profile.getName());
                UIControlUtils.applyDefaultBackground(discProfile);
                UIControlUtils.applyDefaultFont(discProfile);

                if (getProfileAccess().equals(AccessMode.Manage)) {
                    final SquareButton infoButton = SquareButtonFactory.getWhiteImageButton(documentProfilesComposite,
                            0, 0);
                    infoButton.setImage(LiveDocsResourceManager.getImage(Resources.INFO_BUTTON.getValue()));
                    infoButton.setData(profile);
                    infoButton.addSelectionListener(new SelectionAdapter() {

                        @Override
                        public void widgetSelected(SelectionEvent event) {
                            new DetailsProfileWindow(profile).open();
                        }
                    });
                    infoButton.setLayoutData(new GridData(16, 16));

                    final SquareButton editButton = SquareButtonFactory.getWhiteImageButton(documentProfilesComposite,
                            0, 0);
                    editButton.setImage(LiveDocsResourceManager.getImage(Resources.EDIT_BUTTON.getValue()));
                    editButton.setData(profile);
                    editButton.addSelectionListener(new SelectionAdapter() {

                        @Override
                        public void widgetSelected(SelectionEvent event) {
                            new EditProfileWindow(documentProfilesComposite, profile).open();
                        }
                    });
                    editButton.setLayoutData(new GridData(16, 16));

                    final SquareButton deleteButton = SquareButtonFactory.getWhiteImageButton(
                            documentProfilesComposite, 0, 0);
                    deleteButton.setImage(LiveDocsResourceManager.getImage(Resources.DELETE_BUTTON.getValue()));
                    deleteButton.setData(profile);
                    deleteButton.addSelectionListener(new SelectionAdapter() {

                        @Override
                        public void widgetSelected(SelectionEvent event) {
                            new RemoveProfileWindow(documentProfilesComposite, profile).open();
                        }
                    });
                    deleteButton.setLayoutData(new GridData(16, 16));
                }
            }
        }
    }
}
