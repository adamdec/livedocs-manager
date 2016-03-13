package com.indexdata.livedocs.manager.ui.window.users.table;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;

public class UserActionButtonsComposite {

    public static Composite create(final Table table) {
        final Composite buttonsComposite = new Composite(table, SWT.NONE);
        UIControlUtils.applyDefaultBackground(buttonsComposite);
        buttonsComposite.setLayout(new GridLayout(2, true));
        return buttonsComposite;
    }
}