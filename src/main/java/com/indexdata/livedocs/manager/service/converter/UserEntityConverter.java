package com.indexdata.livedocs.manager.service.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.convert.converter.Converter;

import com.indexdata.livedocs.manager.repository.domain.ProfilePermissionEntity;
import com.indexdata.livedocs.manager.repository.domain.UserEntity;
import com.indexdata.livedocs.manager.repository.domain.UserPermissionEntity;
import com.indexdata.livedocs.manager.service.model.Permission;
import com.indexdata.livedocs.manager.service.model.User;

/**
 * Converts <code>UserEntity</code> to <code>User</code>
 * 
 * @author Adam Dec
 * @since 0.0.4
 */
public class UserEntityConverter implements Converter<UserEntity, User> {

    @Override
    public User convert(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }

        Permission permission = null;
        final List<Permission> userPermissions = new ArrayList<Permission>(userEntity.getUserPermissions().size());
        if (userEntity.getUserPermissions().size() > 0) {
            for (UserPermissionEntity userPermission : userEntity.getUserPermissions()) {
                permission = new Permission(userPermission.getSection().toString(), userPermission
                        .getAccessMode());
                if (!userPermissions.contains(permission)) {
                    userPermissions.add(permission);
                }
            }
        }

        final List<Permission> profilePermissions = new ArrayList<Permission>(userEntity.getProfilePermissions().size());
        if (userEntity.getProfilePermissions().size() > 0) {
            for (ProfilePermissionEntity profilePermission : userEntity.getProfilePermissions()) {
                permission = new Permission(profilePermission.getName(), profilePermission.getAccessMode());
                if (!profilePermissions.contains(permission)) {
                    profilePermissions.add(permission);
                }
            }
        }

        return new User(userEntity.getId(), userEntity.getFirstName(), userEntity.getLastName(),
                userEntity.getUserName(), userEntity.getEmail(), userEntity.getPassword(), userPermissions,
                profilePermissions);
    }
}