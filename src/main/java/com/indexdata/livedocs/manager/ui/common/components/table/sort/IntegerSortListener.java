package com.indexdata.livedocs.manager.ui.common.components.table.sort;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class IntegerSortListener {

    private final Table table;

    public IntegerSortListener(final Table table) {
        this.table = table;
    }

    public Listener createSortListener() {
        // Add sort indicator and sort data when column selected
        return new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (table.getData() == null) {
                    return;
                }

                // Determine new sort column and direction
                final TableColumn sortColumn = table.getSortColumn();
                final TableColumn currentColumn = (TableColumn) e.widget;
                int dir = table.getSortDirection();
                if (sortColumn == currentColumn) {
                    dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
                } else {
                    table.setSortColumn(currentColumn);
                    dir = SWT.UP;
                }

                // Sort the data based on column and direction
                final int index = table.indexOf(currentColumn);
                final int direction = dir;

                Arrays.sort((String[][]) table.getData(), new Comparator<String[]>() {
                    @Override
                    public int compare(String[] a, String[] b) {
                        if (direction == SWT.UP) {
                            return Integer.valueOf(a[index]).compareTo(Integer.valueOf(b[index]));
                        }
                        return Integer.valueOf(b[index]).compareTo(Integer.valueOf(a[index]));
                    }
                });
                // Update data displayed in table
                table.setSortDirection(dir);

                // Fires SWT.SetData event
                table.clearAll();
            }
        };
    }
}
