package com.indexdata.livedocs.manager.ui.window.users.table;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

public class UserActionButtonsTableEditor {

    public static void setContent(final TableEditor editor, final TableItem tableItem, final Composite composite) {
        tableItem.setData(editor);
        editor.minimumHeight = composite.getSize().y;
        editor.minimumWidth = composite.getSize().x;
        editor.horizontalAlignment = SWT.CENTER;
        editor.verticalAlignment = SWT.CENTER;
        editor.setEditor(composite, tableItem, 6);
    }

    public static void disposeEditor(final TableItem tableItem) {
        if (tableItem.getData() != null) {
            ((TableEditor) tableItem.getData()).getEditor().dispose();
            ((TableEditor) tableItem.getData()).dispose();
            tableItem.setData(null);
        }
    }
}