package com.indexdata.livedocs.manager.ui.common.components.table;

import java.util.Map;

import org.eclipse.swt.widgets.Table;

import com.indexdata.livedocs.manager.ui.window.main.elements.table.DiscTableData;

/**
 * This class will provide base data adapter for Table components.
 * 
 * @author Adam Dec
 * @since 0.0.3
 */
public class BaseTableDataAdapter {

    protected final Table table;

    public BaseTableDataAdapter(Table table) {
        this.table = table;
    }

    protected DiscTableData[] convertToTableData(Map<Integer, DiscTableData> tableDataMap) {
        if (tableDataMap.size() == 0) {
            return new DiscTableData[0];
        }
        final DiscTableData[] tableData = new DiscTableData[tableDataMap.keySet().size()];
        for (Integer rowNumber : tableDataMap.keySet()) {
            tableData[rowNumber] = tableDataMap.get(rowNumber);
        }
        return tableData;
    }
}