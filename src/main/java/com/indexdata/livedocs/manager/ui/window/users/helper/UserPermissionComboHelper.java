package com.indexdata.livedocs.manager.ui.window.users.helper;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.eclipse.swt.widgets.Combo;

import com.indexdata.livedocs.manager.service.model.Permission;
import com.indexdata.livedocs.manager.service.model.User;
import com.indexdata.livedocs.manager.service.model.enums.AccessMode;
import com.indexdata.livedocs.manager.service.model.enums.Section;

/**
 * This helper will supply user permissions.
 * 
 * @author Adam Dec
 * @since 0.0.4
 */
public class UserPermissionComboHelper {

    public static void supply(final Combo combo, final User user) {
        if (user != null) {
            for (Permission permission : user.getUserPermissions()) {
                if (((Section) combo.getData()).toString().equals(permission.getName())) {
                    combo.setText(permission.getAccessMode().toString());
                    break;
                }
            }
        } else {
            combo.setText(combo.getItem(0));
        }
    }

    public static void populateUsersDefaultValues(final Combo combo) {
        final List<String> comboItems = new ArrayList<String>();
        for (AccessMode accessMode : EnumSet.allOf(AccessMode.class)) {
            comboItems.add(accessMode.toString());
        }
        combo.setItems(comboItems.toArray(new String[0]));
    }

    public static void populateProfilesDefaultValues(final Combo combo) {
        final List<String> comboItems = new ArrayList<String>();
        for (AccessMode accessMode : EnumSet.allOf(AccessMode.class)) {
            if (accessMode.equals(AccessMode.Write)) {
                continue;
            }
            comboItems.add(accessMode.toString());
        }
        combo.setItems(comboItems.toArray(new String[0]));
    }

    public static void populateUploadDefaultValues(final Combo combo) {
        final List<String> comboItems = new ArrayList<String>();
        for (AccessMode accessMode : EnumSet.allOf(AccessMode.class)) {
            if (accessMode.equals(AccessMode.Write) || accessMode.equals(AccessMode.Read)) {
                continue;
            }
            comboItems.add(accessMode.toString());
        }
        combo.setItems(comboItems.toArray(new String[0]));
    }
}