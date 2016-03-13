package com.indexdata.livedocs.manager.ui.window.users.profiles;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.indexdata.livedocs.manager.service.ProfileService;
import com.indexdata.livedocs.manager.service.model.Profile;
import com.indexdata.livedocs.manager.service.model.User;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.window.users.helper.ProfilePermissionComboHelper;

/**
 * Populates all profiles.
 * 
 * @author Adam Dec
 * @since 0.0.4
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public final class ProfilePermissionsComposite {

    private static Logger LOGGER = LoggerFactory.getLogger(ProfilePermissionsComposite.class);

    @Autowired
    private ProfileService profileService;

    private final User user;
    private final List<Combo> comboList = new ArrayList<Combo>();

    public ProfilePermissionsComposite(final User user) {
        this.user = user;
    }

    public Composite create(Composite parent) {
        final Composite profilesComposite = new Composite(parent, SWT.NONE);
        profilesComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
        profilesComposite.setLayout(new GridLayout(2, true));

        comboList.clear();
        LOGGER.debug("Requesting profile list...");
        final List<Profile> profileList = profileService.getAllProfilesSortedByName();
        LOGGER.debug("Got profile list sorted by name asc to={}", profileList);

        for (Profile profile : profileList) {
            final Label profileLabel = new Label(profilesComposite, SWT.NONE);
            profileLabel.setText(profile.getName());
            profileLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            UIControlUtils.applyDefaultBackground(profileLabel);
            UIControlUtils.applyDefaultBoldedFont(profileLabel);

            final Combo profilePermissionCombo = new Combo(profilesComposite, SWT.BORDER);
            profilePermissionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
            profilePermissionCombo.setData(profile.getId());
            UIControlUtils.applyDefaultBackground(profilePermissionCombo);
            UIControlUtils.applyDefaultItalicFont(profilePermissionCombo);
            ProfilePermissionComboHelper.supply(profilePermissionCombo, user);
            comboList.add(profilePermissionCombo);
        }

        return profilesComposite;
    }

    public List<Combo> getComboList() {
        return comboList;
    }
}
