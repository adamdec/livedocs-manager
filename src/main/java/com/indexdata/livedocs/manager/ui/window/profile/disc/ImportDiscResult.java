package com.indexdata.livedocs.manager.ui.window.profile.disc;

import java.util.List;

import org.eclipse.swt.widgets.Display;

import com.indexdata.livedocs.manager.core.events.AppEvents;
import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlsRepository;

/**
 * @author Adam Dec
 * @since 0.0.5
 */
public class ImportDiscResult {

    private final ImportDiscData importDiscData;
    private final List<String> notLoadedFileList;

    public ImportDiscResult(ImportDiscData importDiscData, List<String> notLoadedFileList) {
        super();
        this.importDiscData = importDiscData;
        this.notLoadedFileList = notLoadedFileList;
    }

    public void showImportResults(final String message, final boolean error) {
        Display.getDefault().syncExec(
                () -> {
                    importDiscData.getCloseAndCancelButton().setText(I18NResources.getProperty("close.button.label"));
                    importDiscData.getImportProgressLabel().setText(message);
                    if (error) {
                        UIControlUtils.createErrorMessageBox(importDiscData.getShell(), message);
                    } else {
                        final StringBuilder importMessage = new StringBuilder();
                        importMessage.append(message);
                        if (!notLoadedFileList.isEmpty()) {
                            importMessage.append(System.lineSeparator());
                            importMessage.append("Failed to upload " + notLoadedFileList.size() + " disc files");
                            importMessage.append(System.lineSeparator());
                            int count = 0;
                            for (String fileName : notLoadedFileList) {
                                if (count > 30) {
                                    break;
                                }
                                importMessage.append(fileName);
                                importMessage.append(System.lineSeparator());
                                count++;
                            }
                        }

                        UIControlsRepository.getInstance().sendRefreshProfilesEventToDetailsProfileWindow();
                        UIControlUtils.refreshControl(importDiscData.getProfileDetailsDiscsComposite(),
                                AppEvents.REFRESH_DISCS);
                        UIControlsRepository.getInstance().sendRefreshAttributesEventToProfileAttributesGroupAdapter(
                                importDiscData.getProfileId());
                        UIControlUtils.createInfoMessageBox(importDiscData.getShell(), importMessage.toString());
                    }

                    if (!importDiscData.getShell().isDisposed()) {
                        importDiscData.getShell().close();
                    }
                });
    }
}