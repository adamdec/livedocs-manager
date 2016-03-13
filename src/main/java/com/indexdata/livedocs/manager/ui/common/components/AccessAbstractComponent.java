package com.indexdata.livedocs.manager.ui.common.components;

import org.springframework.beans.factory.annotation.Autowired;

import com.indexdata.livedocs.manager.service.model.enums.AccessMode;
import com.indexdata.livedocs.manager.service.model.enums.Section;
import com.indexdata.livedocs.manager.service.security.UserAuthenticationContext;

public class AccessAbstractComponent {

    @Autowired
    protected UserAuthenticationContext userAuthenticationContext;

    public AccessMode getUsersAccess() {
        return userAuthenticationContext.getAccessTo(Section.Users.toString());
    }

    public AccessMode getProfileAccess() {
        return userAuthenticationContext.getAccessTo(Section.Profile.toString());
    }

    public AccessMode getUploadAccess() {
        return userAuthenticationContext.getAccessTo(Section.Upload.toString());
    }
}
