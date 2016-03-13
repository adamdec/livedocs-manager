package com.indexdata.livedocs.manager.ui.common.components;

import javax.annotation.PostConstruct;

import org.eclipse.swt.widgets.Shell;
import org.springframework.beans.factory.annotation.Autowired;

import com.indexdata.livedocs.manager.service.model.enums.AccessMode;
import com.indexdata.livedocs.manager.service.model.enums.Section;
import com.indexdata.livedocs.manager.service.security.UserAuthenticationContext;
import com.indexdata.livedocs.manager.ui.common.AbstractWindow;

public abstract class AccessAbstractWindow extends AbstractWindow {

    private AccessMode usersAccess = AccessMode.Manage, profileAccess = AccessMode.Manage,
            uploadAccess = AccessMode.Manage;

    @Autowired
    protected UserAuthenticationContext userAuthenticationContext;

    public AccessAbstractWindow(final Shell shell) {
        super(shell);
    }

    @PostConstruct
    public void init() {
        this.usersAccess = userAuthenticationContext.getAccessTo(Section.Users.toString());
        this.profileAccess = userAuthenticationContext.getAccessTo(Section.Profile.toString());
        this.uploadAccess = userAuthenticationContext.getAccessTo(Section.Upload.toString());
    }

    public AccessMode getUsersAccess() {
        return usersAccess;
    }

    public AccessMode getProfileAccess() {
        return profileAccess;
    }

    public AccessMode getUploadAccess() {
        return uploadAccess;
    }
}
