package com.indexdata.livedocs.manager.ui.window.main.elements.table;

import java.awt.Desktop;
import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.indexdata.livedocs.manager.core.events.AppEvents;
import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.resources.MessageSourceAdapter;
import com.indexdata.livedocs.manager.core.utils.DiscFileUtils;
import com.indexdata.livedocs.manager.core.utils.DomainUtils;
import com.indexdata.livedocs.manager.repository.domain.FileEntity;
import com.indexdata.livedocs.manager.service.FileService;
import com.indexdata.livedocs.manager.service.ProfileService;
import com.indexdata.livedocs.manager.service.model.Profile;
import com.indexdata.livedocs.manager.ui.common.components.progress.InfiniteProgressPanel;
import com.indexdata.livedocs.manager.ui.common.components.table.AbstractTable;
import com.indexdata.livedocs.manager.ui.common.components.table.sort.FileSortListener;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;
import com.indexdata.livedocs.manager.ui.window.main.elements.search.TableFileQueryData;

/**
 * This class will provide adapter for Table component. It will contain all disc sorting functionalities.
 * 
 * @author Adam Dec
 * @since 0.0.4
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class DiscTable extends AbstractTable {

    private static Logger LOGGER = LoggerFactory.getLogger(DiscTable.class);

    @Autowired
    private ProfileService profileService;

    @Autowired
    private FileService fileService;

    @Autowired
    private MessageSourceAdapter messageSourceAdapter;

    private final ScrolledComposite scrolledCompositeDiscTable;

    private DiscTableDataAdapter discTableDataAdapter;
    private Table table;

    public DiscTable(final ScrolledComposite scrolledCompositeDiscTable) {
        this.scrolledCompositeDiscTable = scrolledCompositeDiscTable;
    }

    public void create() {
        // This is triggered by DocumentProfilesComboAdapter, AttributeSearchClearButton
        scrolledCompositeDiscTable.addListener(AppEvents.REFRESH_ATTRIBUTES.getValue(), new Listener() {

            @Override
            public void handleEvent(Event event) {
                LOGGER.debug("Received 'REFRESH_ATTRIBUTES' event {}", event);
                UIControlUtils.disposeChildren(scrolledCompositeDiscTable);
                final TableFileQueryData tableFileQueryData = (TableFileQueryData) event.data;
                if (tableFileQueryData.getProfile() != null) {
                    createTable(tableFileQueryData);
                    scrolledCompositeDiscTable.setContent(table);
                    scrolledCompositeDiscTable.layout(true, true);
                }
            }
        });

        scrolledCompositeDiscTable.addListener(AppEvents.TABLE_DATA.getValue(), new Listener() {

            @Override
            public void handleEvent(Event event) {
                LOGGER.debug("Received 'TABLE_DATA' event {}", event);

                if (!table.isDisposed()) {
                    table.removeAll();
                    table.redraw();

                    final InfiniteProgressPanel progressPanel = UIControlUtils.createProgressPanel(
                            I18NResources.getProperty("search.index.files.progress"),
                            scrolledCompositeDiscTable.getShell());
                    progressPanel.start();
                    final Thread performer = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            Display.getDefault().syncExec(() -> {
                                discTableDataAdapter.populateData((TableFileQueryData) event.data);
                                scrolledCompositeDiscTable.setContent(table);
                                scrolledCompositeDiscTable.layout(true, true);
                                progressPanel.getCanvas().dispose();
                            });
                            progressPanel.stop();
                        }
                    }, "RemoveProfileThread");
                    performer.start();
                }
            }
        });
    }

    private void createTable(final TableFileQueryData tableSearchData) {
        final Profile profile = profileService.getProfileById(tableSearchData.getProfile().getId());
        if (profile == null) {
            LOGGER.error("Could not get profile data using id={}", tableSearchData.getProfile().getId());
            return;
        }

        this.table = createTable(scrolledCompositeDiscTable, DomainUtils.convertToStringList(profile.getAttributes()),
                SWT.VIRTUAL | SWT.FULL_SELECTION);

        if (getColumnList().size() > 0) {
            for (TableColumn column : getColumnList()) {
                // First column is always empty (MAN-24)
                if (!"".equals(column.getText())) {
                    column.addListener(SWT.Selection, new FileSortListener(table).createSortListener());
                }
            }

            // Set default sorting on first column
            if (getColumnList().size() > 1) {
                table.setSortColumn(getColumnList().get(1));
                table.setSortDirection(SWT.UP);
            }
        }

        this.table.pack(true);
        UIControlUtils.setTableRowHeight(table, 30);
        this.discTableDataAdapter = new DiscTableDataAdapter(table);

        this.table.addListener(SWT.MouseDoubleClick, new Listener() {

            @Override
            public void handleEvent(Event event) {
                final TableItem tableItem = table.getItem(new Point(event.x, event.y));
                if (tableItem == null) {
                    return;
                }

                try {
                    final DiscTableData discTableData = (DiscTableData) tableItem.getData();
                    final FileEntity fileEntity = fileService.getFileByFileId(discTableData.getFileId());
                    if (fileEntity == null) {
                        UIControlUtils.createErrorMessageBox(UIControlsRepository.getInstance().getMainWindowShell(),
                                messageSourceAdapter.getProperty("main.window.disc.table.selected.file.not.exists",
                                        discTableData.getPath()));
                        return;
                    }

                    final File openDiscFile = DiscFileUtils.openDiscFile(fileEntity.getPath(),
                            fileEntity.getExtension());
                    if (openDiscFile == null) {
                        UIControlUtils.createErrorMessageBox(UIControlsRepository.getInstance().getMainWindowShell(),
                                messageSourceAdapter.getProperty(
                                        "main.window.disc.table.selected.remote.file.not.exists",
                                        discTableData.getPath()));
                        return;
                    }

                    Desktop.getDesktop().open(openDiscFile);
                    LOGGER.debug("Opened file id={} uploaded from path={}", discTableData.getFileId(),
                            discTableData.getPath());
                } catch (Exception e) {
                    LOGGER.error("Error opening file {}", tableItem.getData(), e);
                    UIControlUtils.createErrorMessageBox(UIControlsRepository.getInstance().getMainWindowShell(),
                            "Error opening file " + tableItem.getData() + " due to error\n" + e.getMessage());
                }
            }
        });

        if (!tableSearchData.isNoDataUpdate()) {
            this.discTableDataAdapter.populateData(tableSearchData);
        }
        else {
            // Clear existing table data (Profile was changed in combo box or Clear button was clicked)
            Display.getDefault().syncExec(() -> {
                table.removeAll();
                table.redraw();
                scrolledCompositeDiscTable.setContent(table);
                scrolledCompositeDiscTable.layout(true, true);
            });
        }

        // Resize all columns evenly, this will trigger TableControlResizeAdapter
        scrolledCompositeDiscTable.notifyListeners(SWT.Resize, new Event());
    }
}
