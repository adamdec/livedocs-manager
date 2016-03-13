package com.indexdata.livedocs.manager.ui.window.main.elements.profiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.indexdata.livedocs.manager.core.events.AppEvents;
import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.service.ProfileService;
import com.indexdata.livedocs.manager.service.model.Profile;
import com.indexdata.livedocs.manager.service.model.enums.AccessMode;
import com.indexdata.livedocs.manager.service.security.UserAuthenticationContext;
import com.indexdata.livedocs.manager.ui.common.events.FileQueryData;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;
import com.indexdata.livedocs.manager.ui.window.main.MainContent;
import com.indexdata.livedocs.manager.ui.window.main.MainWindow;
import com.indexdata.livedocs.manager.ui.window.main.elements.search.TableFileQueryData;

/**
 * Populates all profiles.
 * 
 * @author Adam Dec
 * @since 0.0.3
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public final class DocumentProfilesComboAdapter {

    private static Logger LOGGER = LoggerFactory.getLogger(DocumentProfilesComboAdapter.class);

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserAuthenticationContext userAuthenticationContext;

    private volatile Profile currentProfile;

    private final Group documentProfilesGroup;
    private final CCombo documentProfilesCombo;

    public DocumentProfilesComboAdapter(final Composite mainComposite) {
        this.documentProfilesGroup = new Group(mainComposite, SWT.NONE);
        this.documentProfilesGroup.setBounds(MainWindow.LEFT_SIDE_PADDING, 0, MainContent.FIRST_PANEL_WIDTH, 51);
        this.documentProfilesGroup.setText(I18NResources.getProperty("main.window.document.profiles.label"));
        UIControlUtils.applyDefaultBoldedFont(documentProfilesGroup);
        UIControlUtils.applyDefaultBackground(documentProfilesGroup);

        this.documentProfilesCombo = new CCombo(documentProfilesGroup, SWT.NONE);
        this.documentProfilesCombo.setBounds(MainWindow.RIGHT_SIDE_PADDING, 23, 161, 23);
        this.documentProfilesCombo.setEditable(false);

        UIControlsRepository.getInstance().setDocumentProfilesCombo(documentProfilesCombo);
        UIControlUtils.applyDefaultBackground(documentProfilesCombo);
        UIControlUtils.applyDefaultFont(documentProfilesCombo);

        mainComposite.addListener(AppEvents.FOCUS.getValue(), new Listener() {

            @Override
            public void handleEvent(Event event) {
                documentProfilesCombo.setFocus();
            }
        });
    }

    public void createContent() {
        // This is triggered after we close ManageProfileWindow
        documentProfilesCombo.addListener(AppEvents.REFRESH_PROFILES.getValue(), new Listener() {

            @Override
            public void handleEvent(Event event) {
                LOGGER.debug("Received 'REFRESH_PROFILES' event {}", event);
                populateProfileData();
                documentProfilesGroup.layout(true, true);
            }
        });

        documentProfilesCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (currentProfile != null && !currentProfile.getName().equals(documentProfilesCombo.getText())) {
                    currentProfile = profileService.getProfileByName(documentProfilesCombo.getText());
                    LOGGER.debug("Current profile set to={}", currentProfile.getName());
                    triggerProfileDataUpdate();
                }
            }
        });

        populateProfileData();
    }

    private void populateProfileData() {
        LOGGER.debug("Requesting profile list...");
        final List<Profile> profileList = profileService.getAllProfilesSortedByName();
        LOGGER.debug("Got profile list sorted by name [ASC] with size={}", profileList.size());
        final String[] comboItems = filterProfileBasedOnAccessMode(profileList);
        LOGGER.debug("Profile list [{}] after applying access filter={}", comboItems.length,
                Arrays.toString(comboItems));

        documentProfilesCombo.setItems(comboItems);
        if (comboItems.length == 0) {
            documentProfilesCombo.setText(I18NResources.getProperty("main.window.document.profiles.empty.values"));
            currentProfile = null;
            LOGGER.debug("No current profile to select.");
        }
        else {
            for (Profile profile : profileList) {
                if (comboItems[0].equals(profile.getName())) {
                    currentProfile = profile;
                    documentProfilesCombo.setText(currentProfile.getName());
                    LOGGER.debug("Current profile was set to={}", currentProfile.getName());
                    break;
                }
            }
        }

        Display.getDefault().asyncExec(() -> {
            triggerProfileDataUpdate();
        });
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public Group getDocumentProfilesGroup() {
        return documentProfilesGroup;
    }

    private String[] filterProfileBasedOnAccessMode(final List<Profile> profileList) {
        final List<String> filteredProfileList = new ArrayList<String>();
        for (Profile profile : profileList) {
            final AccessMode accessTo = userAuthenticationContext.getAccessToProfiles(profile.getId());
            if (!AccessMode.Denied.equals(accessTo)) {
                filteredProfileList.add(profile.getName());
            }
            else {
                LOGGER.warn("Profile name={} and id={} was filtered out from combo due to access mode={}",
                        profile.getName(), profile.getId(), accessTo);
            }
        }
        return filteredProfileList.toArray(new String[filteredProfileList.size()]);
    }

    private void triggerProfileDataUpdate() {
        // Refresh profile attributes (the one that we use for searching)
        UIControlsRepository.getInstance().sendRefreshAttributesEventToProfileAttributesGroupAdapter(
                currentProfile != null ? currentProfile.getId() : null);

        // Refresh disc table columns
        UIControlsRepository.getInstance().sendRefreshAttributesEventToScrolledCompositeDiscTable(
                new TableFileQueryData(currentProfile));

        // Reset pagination and documents number
        UIControlsRepository.getInstance().sendPaginationAndDocsNoEventToSearchResultsComposite(new FileQueryData());
    }
}
