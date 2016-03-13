package com.indexdata.livedocs.manager.service.converter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.core.convert.converter.Converter;

import com.indexdata.livedocs.manager.repository.domain.ProfilePermissionEntity;
import com.indexdata.livedocs.manager.repository.domain.UserEntity;
import com.indexdata.livedocs.manager.repository.domain.UserPermissionEntity;
import com.indexdata.livedocs.manager.service.model.Permission;
import com.indexdata.livedocs.manager.service.model.User;
import com.indexdata.livedocs.manager.service.model.enums.Section;
import com.indexdata.livedocs.manager.service.security.UserAuthenticationContext;

/**
 * Converts <code>User</code> to <code>UserEntity</code>
 * 
 * @author Adam Dec
 * @since 0.0.4
 */
public class UserConverter implements Converter<User, UserEntity> {

    private final UserAuthenticationContext userAuthenticationContext;

    public UserConverter(UserAuthenticationContext userAuthenticationContext) {
        super();
        this.userAuthenticationContext = userAuthenticationContext;
    }

    @Override
    public UserEntity convert(User user) {
        final UserEntity userEntity = new UserEntity();
        if (user.getId() != null) {
            userEntity.setId(user.getId());
        }
        userEntity.setUserName(user.getUserName());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setEmail(user.getEmail());
        if (user.getOrginalPassword() != null) {
            if (!user.getOrginalPassword().equals(user.getPassword())) {
                userEntity.setPassword(userAuthenticationContext.encodePassword(user.getPassword()));
            } else {
                userEntity.setPassword(user.getPassword());
            }
        } else {
            userEntity.setPassword(userAuthenticationContext.encodePassword(user.getPassword()));
        }

        if (user.getId() == null) {
            userEntity.setDateCreated(new Date());
        }

        final List<UserPermissionEntity> userPermissions = new ArrayList<UserPermissionEntity>(userEntity
                .getUserPermissions().size());
        if (user.getUserPermissions().size() > 0) {
            UserPermissionEntity userPermissionEntity = null;
            for (Permission userPermission : user.getUserPermissions()) {
                userPermissionEntity = new UserPermissionEntity(Section.valueOf(userPermission.getName()),
                        userPermission
                                .getAccessMode(), userEntity);
                if (!userPermissions.contains(userPermissionEntity)) {
                    userPermissions.add(userPermissionEntity);
                }
            }
        }

        final List<ProfilePermissionEntity> profilePermissions = new ArrayList<ProfilePermissionEntity>(userEntity
                .getProfilePermissions().size());
        if (user.getProfilePermissions().size() > 0) {
            ProfilePermissionEntity profilePermissionEntity = null;
            for (Permission profilePermission : user.getProfilePermissions()) {
                profilePermissionEntity = new ProfilePermissionEntity(profilePermission.getName(), profilePermission
                        .getAccessMode(), userEntity);
                if (!profilePermissions.contains(profilePermissionEntity)) {
                    profilePermissions.add(profilePermissionEntity);
                }
            }
        }

        userEntity.setUserPermissions(userPermissions);
        userEntity.setProfilePermissions(profilePermissions);
        return userEntity;
    }
}