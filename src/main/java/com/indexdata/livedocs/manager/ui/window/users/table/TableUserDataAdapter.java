package com.indexdata.livedocs.manager.ui.window.users.table;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.widgets.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indexdata.livedocs.manager.service.model.User;
import com.indexdata.livedocs.manager.ui.common.components.table.BaseTableDataAdapter;
import com.indexdata.livedocs.manager.ui.window.main.elements.table.DiscTableData;

/**
 * This class will provide data adapter for Table component. It will provide table user data.
 * 
 * @author Adam Dec
 * @since 0.0.4
 */
public class TableUserDataAdapter extends BaseTableDataAdapter {

    private static Logger LOGGER = LoggerFactory.getLogger(TableUserDataAdapter.class);

    private DiscTableData[] discTableDataArray;

    public TableUserDataAdapter(final Table table) {
        super(table);
    }

    public void populateData(final List<User> users) {
        int rowNumber = 0;
        this.discTableDataArray = new DiscTableData[users.size()];
        for (User user : users) {
            discTableDataArray[rowNumber++] = new DiscTableData(Arrays.asList("", String.valueOf(user.getId()), user.getFirstName(),
                    user.getLastName(), user.getUserName(), user.getEmail()).toArray(new String[0]));
        }
        table.setData(discTableDataArray);
        table.setItemCount(discTableDataArray.length);
        LOGGER.debug("Set {} items in the users  table.", discTableDataArray.length);

        // Fires SWT.SetData event
        table.clearAll();
    }

    public DiscTableData[] getDiscTableDataArray() {
        return discTableDataArray;
    }
}