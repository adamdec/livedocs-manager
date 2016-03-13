package com.indexdata.livedocs.manager.ui.window.users.helper;

import org.eclipse.swt.widgets.Combo;

import com.indexdata.livedocs.manager.service.model.Permission;
import com.indexdata.livedocs.manager.service.model.User;
import com.indexdata.livedocs.manager.service.model.enums.AccessMode;

/**
 * This helper will supply profile permissions.
 * 
 * @author Adam Dec
 * @since 0.0.4
 */
public class ProfilePermissionComboHelper {

    public static void supply(final Combo combo, final User user) {
        combo.setItems(new String[] { AccessMode.Denied.toString(), AccessMode.Read.toString() });
        if (user != null) {
            for (Permission permission : user.getProfilePermissions()) {
                if (((Long) combo.getData()).equals(Long.valueOf(permission.getName()))) {
                    combo.setText(permission.getAccessMode().toString());
                    return;
                }
            }
            combo.setText(combo.getItem(0));
        } else {
            combo.setText(combo.getItem(0));
        }
    }
}