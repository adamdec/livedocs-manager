package com.indexdata.livedocs.manager.ui.window.main.elements;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.indexdata.livedocs.manager.core.resources.Resources;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;

/**
 * @author Adam Dec
 * @since 0.0.1
 */
public final class ClientImageLabel {

    public static Label createLabel(Composite parent) {
        final Label label = new Label(parent, SWT.NONE);
        label.setBounds(634, 31, 200, 46);
        label.setImage(LiveDocsResourceManager.getImage(Resources.CLIENT_LABEL.getValue()));
        UIControlUtils.applyDefaultBackground(label);
        return label;
    }
}