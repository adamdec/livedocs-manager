package com.indexdata.livedocs.manager.ui.common.components.table;

import java.util.List;

import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * This class will provide control adapter for Table component. It will resize table columns evenly.
 * 
 * @author Adam Dec
 * @since 0.0.4
 */
public class TableControlResizeAdapter {

    private final ScrolledComposite tableScrolledComposite;
    private final Table table;
    private final List<TableColumn> columnList;

    public TableControlResizeAdapter(ScrolledComposite tableScrolledComposite, Table table, List<TableColumn> columnList) {
        super();
        this.tableScrolledComposite = tableScrolledComposite;
        this.table = table;
        this.columnList = columnList;
    }

    public ControlAdapter create() {
        return new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                final Rectangle area = ((Composite) tableScrolledComposite).getClientArea();
                if (table != null && !table.isDisposed()) {
                    // First column is always empty (MAN-24)
                    final int size = columnList.size() - 1;
                    final int width = area.width - table.getVerticalBar().getSize().x;
                    final int height = area.height - table.getHorizontalBar().getSize().y;
                    final Point oldSize = table.getSize();
                    final int columnsSize = size > 0 ? size : 1;
                    final int newHeaderWidth = width / columnsSize;
                    if (oldSize.x > area.width) {
                        setColumnSize(newHeaderWidth);
                        table.setSize(area.width, height);
                    } else {
                        table.setSize(area.width, height);
                        setColumnSize(newHeaderWidth);
                    }
                }
            }

            private void setColumnSize(final int newHeaderWidth) {
                for (TableColumn coulmn : columnList) {
                    // First column is always empty (MAN-24)
                    if (!"".equals(coulmn.getText())) {
                        coulmn.setWidth(newHeaderWidth);
                    }
                }
            }
        };
    }
}