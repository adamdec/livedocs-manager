package com.indexdata.livedocs.manager.ui.window.main.elements.table;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import com.indexdata.livedocs.manager.core.utils.DomainUtils;
import com.indexdata.livedocs.manager.repository.domain.Field;
import com.indexdata.livedocs.manager.service.FileService;
import com.indexdata.livedocs.manager.service.ProfileService;
import com.indexdata.livedocs.manager.service.model.File;
import com.indexdata.livedocs.manager.ui.common.components.table.BaseTableDataAdapter;
import com.indexdata.livedocs.manager.ui.common.events.FileQueryData;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;
import com.indexdata.livedocs.manager.ui.window.main.elements.search.TableFileQueryData;

/**
 * This class will provide data adapter for Table component. It will provide table data.
 * 
 * @author Adam Dec
 * @since 0.0.4
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class DiscTableDataAdapter extends BaseTableDataAdapter {

    private static Logger LOGGER = LoggerFactory.getLogger(DiscTableDataAdapter.class);

    @Autowired
    private FileService fileService;

    @Autowired
    private ProfileService profileService;

    private Listener listener;

    public DiscTableDataAdapter(Table table) {
        super(table);
    }

    /**
     * Populates disc attributes values into the main table. <br/>
     * Data is paginated <code>app.grid.pagination.limit</code> and sorted using first attribute in a current profile.
     * 
     * @param tableSearchData
     */
    public void populateData(final TableFileQueryData tableSearchData) {
        final List<String> profileAttributeList = DomainUtils.convertToStringList(profileService.getProfileById(
                tableSearchData.getProfile().getId()).getAttributes());
        LOGGER.debug("Fetched {} attributes for profileId={}", profileAttributeList.size(), tableSearchData
                .getProfile().getId());

        final List<File> fileList = fileService.getAllFiles(tableSearchData.getProfile().getId(),
                tableSearchData.getFilePageRequest(), tableSearchData.getFilterList());
        LOGGER.debug("Fetched {} files for current page [{}].", fileList.size(), tableSearchData.getFilePageRequest());

        final long totalFilesNumber = tableSearchData.getFilterList().size() == 0 ? Long.valueOf(
                fileService.getAllFilesCount(tableSearchData.getProfile().getId())).intValue() : fileService
                .getAllFilesCountWithFilter(tableSearchData.getProfile().getId(), tableSearchData.getFilterList());
        LOGGER.debug("TotalFilesNumber number set to {}", totalFilesNumber);

        // Only reset search composite on first page
        if (tableSearchData.getFilePageRequest().getOffset() == 0) {
            LOGGER.debug(
                    "This is new query so SearchResultsComposite has to be informed about totalFilesNumber {} and order={}",
                    totalFilesNumber, tableSearchData.getFilePageRequest().getOrder());
            Display.getDefault().syncExec(
                    () -> {
                        UIControlsRepository.getInstance().sendPaginationAndDocsNoEventToSearchResultsComposite(
                                new FileQueryData(totalFilesNumber, tableSearchData.getFilePageRequest().getOrder()));
                    });
        }

        final Map<Integer, DiscTableData> discTableDataMap = new HashMap<Integer, DiscTableData>();
        int rowNumber = 0;
        for (File file : fileList) {
            final String[] fieldValueList = new String[profileAttributeList.size() + 1];
            // First column is always empty (MAN-24)
            fieldValueList[0] = "";
            for (Field field : file.getFields()) {
                int index = profileAttributeList.indexOf(field.getName());
                if (index != -1) {
                    fieldValueList[index + 1] = field.getValue();
                }
            }
            discTableDataMap.put(rowNumber++, new DiscTableData(file.getId(), file.getPath(), fieldValueList));
        }
        Display.getDefault().syncExec(() -> {
            populateTableData(convertToTableData(discTableDataMap));
        });
    }

    private void populateTableData(final DiscTableData[] tableData) {
        if (table != null) {
            table.setItemCount(tableData.length);
            table.setData(tableData);
            if (listener != null) {
                table.removeListener(SWT.SetData, listener);
            }
            this.listener = new Listener() {

                @Override
                public void handleEvent(Event event) {
                    try {
                        final TableItem item = (TableItem) event.item;
                        final DiscTableData row = tableData[table.indexOf(item)];
                        item.setText(row.getFieldValueList());
                        item.setData(row);
                    } catch (Exception e) {
                        LOGGER.error("Error in populating table data for tableData!", e);
                    }
                }
            };
            table.addListener(SWT.SetData, listener);
        }
    }
}
