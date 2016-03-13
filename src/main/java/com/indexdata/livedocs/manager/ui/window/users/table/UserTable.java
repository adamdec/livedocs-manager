package com.indexdata.livedocs.manager.ui.window.users.table;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Configurable;

import com.indexdata.livedocs.manager.core.resources.Resources;
import com.indexdata.livedocs.manager.service.model.enums.AccessMode;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButton;
import com.indexdata.livedocs.manager.ui.common.components.button.SquareButtonFactory;
import com.indexdata.livedocs.manager.ui.common.components.table.AbstractTable;
import com.indexdata.livedocs.manager.ui.common.components.table.sort.IntegerSortListener;
import com.indexdata.livedocs.manager.ui.common.components.table.sort.StringSortListener;
import com.indexdata.livedocs.manager.ui.common.utils.LiveDocsResourceManager;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.window.main.elements.table.DiscTableData;
import com.indexdata.livedocs.manager.ui.window.users.EditUserWindow;
import com.indexdata.livedocs.manager.ui.window.users.RemoveUserWindow;

/**
 * This class will provide adapter for Table component. It will contain all users sorting functionalities.
 * 
 * @author Adam Dec
 * @since 0.0.4
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class UserTable extends AbstractTable {

    private static final int USERNAME_COLUMN_INDEX = 4;

    private static final int ID_COLUMN_INDEX = 1;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTable.class);

    public Table createUserTable(final ScrolledComposite parent, final List<String> columnNames) {
        final Table table = super.createTable(parent, columnNames, SWT.VIRTUAL | SWT.HIDE_SELECTION);

        if (getColumnList().size() > 0) {
            getColumnList().get(1).addListener(SWT.Selection, new IntegerSortListener(table).createSortListener());
            for (int i = 2; i < getColumnList().size() - 1; i++) {
                getColumnList().get(i).addListener(SWT.Selection, new StringSortListener(table).createSortListener());
            }

            // Set default sorting on first column
            if (getColumnList().size() > 1) {
                table.setSortColumn(getColumnList().get(1));
                table.setSortDirection(SWT.DOWN);
            }
        }
        table.pack(true);
        UIControlUtils.setTableRowHeight(table, 35);

        table.addListener(SWT.SetData, new Listener() {

            @Override
            public void handleEvent(Event event) {
                final TableItem tableItem = (TableItem) event.item;
                UserActionButtonsTableEditor.disposeEditor(tableItem);

                final int rowIndex = table.indexOf(tableItem);
                tableItem.setText(((DiscTableData[]) table.getData())[rowIndex].getFieldValueList());
                LOGGER.debug("Row UserId={} | Row={} | Data={}", tableItem.getText(ID_COLUMN_INDEX), rowIndex,
                        Arrays.toString(((DiscTableData[]) table.getData())[rowIndex].getFieldValueList()));

                if (getUsersAccess().equals(AccessMode.Write) || getUsersAccess().equals(AccessMode.Manage)) {
                    final TableEditor editor = new TableEditor(table);
                    final Composite buttonsComposite = UserActionButtonsComposite.create(table);
                    final SquareButton editUserButton = SquareButtonFactory.getWhiteImageButton(buttonsComposite, 0, 0);
                    editUserButton.setImage(LiveDocsResourceManager.getImage(Resources.EDIT_BUTTON.getValue()));
                    editUserButton.setLayoutData(new GridData(16, 16));
                    editUserButton.setData(new UserToRowMapping(Long.valueOf(tableItem.getText(ID_COLUMN_INDEX)),
                            rowIndex));
                    editUserButton.addSelectionListener(new SelectionAdapter() {

                        @Override
                        public void widgetSelected(SelectionEvent event) {
                            final UserToRowMapping userToRowMapping = (UserToRowMapping) ((SquareButton) event
                                    .getSource()).getData();
                            LOGGER.debug("Selected edit={}", userToRowMapping);
                            new EditUserWindow(table, userToRowMapping).open();
                        }
                    });

                    if (getUsersAccess().equals(AccessMode.Manage)) {
                        final SquareButton deleteUserButton = SquareButtonFactory.getWhiteImageButton(buttonsComposite,
                                0, 0);
                        deleteUserButton.setImage(LiveDocsResourceManager.getImage(Resources.DELETE_BUTTON.getValue()));
                        deleteUserButton.setLayoutData(new GridData(16, 16));
                        deleteUserButton.setData(new UserToRowMapping(Long.valueOf(tableItem.getText(ID_COLUMN_INDEX)),
                                rowIndex));
                        deleteUserButton.addSelectionListener(new SelectionAdapter() {

                            @Override
                            public void widgetSelected(SelectionEvent event) {
                                final UserToRowMapping userToRowMapping = (UserToRowMapping) ((SquareButton) event
                                        .getSource()).getData();
                                LOGGER.debug("Selected delete={}", userToRowMapping);
                                new RemoveUserWindow(table, userToRowMapping, editor, buttonsComposite).open();
                            }
                        });

                        if (userAuthenticationContext.getCurrentUserName().equals(
                                tableItem.getText(USERNAME_COLUMN_INDEX))) {
                            deleteUserButton.setEnabled(false);
                        }
                    }

                    buttonsComposite.pack(true);
                    UserActionButtonsTableEditor.setContent(editor, tableItem, buttonsComposite);
                }
            }
        });
        return table;
    }
}
