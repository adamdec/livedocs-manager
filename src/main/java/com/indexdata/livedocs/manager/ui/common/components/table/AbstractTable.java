package com.indexdata.livedocs.manager.ui.common.components.table;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.indexdata.livedocs.manager.ui.common.components.AccessAbstractComponent;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.window.profile.disc.ImportDiscWindow;

/**
 * This class will provide adapter for Table component. It will contain all table base functionalities.
 * 
 * @author Adam Dec
 * @since 0.0.4
 */
public abstract class AbstractTable extends AccessAbstractComponent {

    private static final int DEFAULT_COLUMN_WIDTH = 120;

    private final List<TableColumn> columnList = new ArrayList<TableColumn>(ImportDiscWindow.MAPPING_LIMIT + 1);

    protected Table createTable(final ScrolledComposite tableScrolledComposite, final List<String> columnNameList,
            final int tableStyle) {
        final Table table = new Table(tableScrolledComposite, tableStyle);
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        UIControlUtils.applyDefaultFont(table);
        columnList.clear();

        // First column is always empty (MAN-24)
        TableColumn column = new TableColumn(table, SWT.CENTER);
        column.setWidth(0);
        column.setText("");
        columnList.add(column);

        for (String columnName : columnNameList) {
            column = new TableColumn(table, SWT.CENTER);
            column.setWidth(setColumnwWidth(new GC(table), columnNameList));
            column.setText(columnName);
            column.setAlignment(SWT.CENTER);
            columnList.add(column);
        }

        table.addListener(SWT.MeasureItem, new Listener() {
            @Override
            public void handleEvent(Event event) {
                final TableItem item = (TableItem) event.item;
                final String text = item.getText(event.index);
                final Point size = event.gc.textExtent(text);
                event.height = Math.max(event.height, size.y);
            }
        });

        tableScrolledComposite.addControlListener(new TableControlResizeAdapter(tableScrolledComposite, table,
                columnList).create());
        return table;
    }

    private int setColumnwWidth(final GC gc, final List<String> columnNameList) {
        try {
            int maxColumnWidth = DEFAULT_COLUMN_WIDTH;
            for (String column : columnNameList) {
                int columnSize = UIControlUtils.textWidth(gc, column);
                if (columnSize > DEFAULT_COLUMN_WIDTH) {
                    maxColumnWidth = columnSize;
                }
            }
            return maxColumnWidth;
        } finally {
            if (gc != null && !gc.isDisposed()) {
                gc.dispose();
            }
        }
    }

    public List<TableColumn> getColumnList() {
        return columnList;
    }
}