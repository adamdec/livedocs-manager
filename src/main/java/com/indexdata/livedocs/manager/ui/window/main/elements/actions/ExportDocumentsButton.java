package com.indexdata.livedocs.manager.ui.window.main.elements.actions;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;

import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.resources.Resources;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.window.export.ExportDocumentsWindow;
import com.indexdata.livedocs.manager.ui.window.main.elements.profiles.DocumentProfilesComboAdapter;

/**
 * @author Adam Dec
 * @since 0.0.5
 */
public final class ExportDocumentsButton {

    public static SquareButton createButton(final Composite parent,
            final DocumentProfilesComboAdapter documentProfilesComboAdapter) {
        final SquareButton button = SquareButtonFactory.getWhiteButton(parent, 5, 14);
        button.setImage(LiveDocsResourceManager.getImage(Resources.EXPORT_DOCUMENTS_20_20.getValue()));
        button.setBounds(10, 10, 140, 32);
        button.setText(I18NResources.getProperty("export.documents.button"));
        UIControlUtils.applyDefaultBackground(button);
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                new ExportDocumentsWindow(documentProfilesComboAdapter.getCurrentProfile().getId()).open();
            }
        });
        return button;
    }
}