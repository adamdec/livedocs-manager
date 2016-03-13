package com.indexdata.livedocs.manager.core.utils;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;

public class ValidationUtils {

    public static boolean validateAttributeTextBox(final Shell shell, final Text text) {
        if (text.getText().isEmpty()) {
            UIControlUtils.createWarningMessageBox(shell,
                    I18NResources.getProperty("add.attribute.window.empty.attribute.name"));
            return false;
        }

        if (text.getText().startsWith("[")) {
            UIControlUtils.createWarningMessageBox(shell,
                    I18NResources.getProperty("add.attribute.window.incorrect.attribute.name"));
            return false;
        }
        return true;
    }

    public static boolean validateProfileTextBox(final Shell shell, final Text text) {
        if (text.getText().isEmpty()) {
            UIControlUtils.createWarningMessageBox(shell,
                    I18NResources.getProperty("add.profile.window.empty.profile.name"));
            return false;
        }

        if (text.getText().startsWith("[")) {
            UIControlUtils.createWarningMessageBox(shell,
                    I18NResources.getProperty("add.profile.window.incorrect.profile.name"));
            return false;
        }
        return true;
    }
}