package com.indexdata.livedocs.manager.ui.common.components.table.sort;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import com.indexdata.livedocs.manager.ui.window.main.elements.table.DiscTableData;

public class StringSortListener {

    private final Table table;

    public StringSortListener(final Table table) {
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

                // determine new sort column and direction
                final TableColumn sortColumn = table.getSortColumn();
                final TableColumn currentColumn = (TableColumn) e.widget;
                int dir = table.getSortDirection();
                if (sortColumn == currentColumn) {
                    dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
                }
                else {
                    table.setSortColumn(currentColumn);
                    dir = SWT.UP;
                }

                // Sort the data based on column and direction
                final int index = table.indexOf(currentColumn);
                final int direction = dir;
                final DiscTableData[] discTableData = (DiscTableData[]) table.getData();

                Arrays.sort(discTableData, new Comparator<DiscTableData>() {

                    @Override
                    public int compare(DiscTableData a, DiscTableData b) {
                        if (direction == SWT.UP) {
                            return a.getFieldValueList()[index].compareTo(b.getFieldValueList()[index]);
                        }
                        return b.getFieldValueList()[index].compareTo(a.getFieldValueList()[index]);
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
