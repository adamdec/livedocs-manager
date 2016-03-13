package com.indexdata.livedocs.manager.ui.common.components.table.sort;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;
import com.indexdata.livedocs.manager.ui.window.main.elements.search.FilePageRequest;

public class FileSortListener {

    private final Table table;

    public FileSortListener(final Table table) {
        this.table = table;
    }

    public Listener createSortListener() {
        // Add sort indicator and sort data when column selected
        return new Listener() {

            @Override
            public void handleEvent(final Event event) {
                if (table == null || table.isDisposed()) {
                    return;
                }

                // Determine new sort column and direction
                final TableColumn sortColumn = table.getSortColumn();
                final TableColumn currentColumn = (TableColumn) event.widget;
                int dir = table.getSortDirection();
                if (sortColumn == currentColumn) {
                    dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
                }
                else {
                    table.setSortColumn(currentColumn);
                    dir = SWT.UP;
                }

                // Update data displayed in table
                table.setSortDirection(dir);

                // Fires SWT.SetData event
                table.clearAll();

                // Always start query with sorting from the beginning
                UIControlsRepository.getInstance().sendSelectionEventToAttributesSearchButton(
                        new FilePageRequest(new Order(dir == SWT.UP ? Direction.ASC : Direction.DESC, currentColumn
                                .getText())));
            }
        };
    }
}
