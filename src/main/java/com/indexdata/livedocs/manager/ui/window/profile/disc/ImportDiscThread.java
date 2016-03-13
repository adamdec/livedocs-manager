package com.indexdata.livedocs.manager.ui.window.profile.disc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.StopWatch;

import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.core.resources.MessageSourceAdapter;
import com.indexdata.livedocs.manager.core.utils.DiscFileData;
import com.indexdata.livedocs.manager.core.utils.DiscFileUtils;
import com.indexdata.livedocs.manager.repository.ProfileRepository;
import com.indexdata.livedocs.manager.repository.domain.AttributeEntity;
import com.indexdata.livedocs.manager.repository.domain.DiscEntity;
import com.indexdata.livedocs.manager.repository.domain.Field;
import com.indexdata.livedocs.manager.repository.domain.FileEntity;
import com.indexdata.livedocs.manager.repository.domain.ProfileEntity;
import com.indexdata.livedocs.manager.service.ProfileService;
import com.indexdata.livedocs.manager.service.model.File;
import com.indexdata.livedocs.manager.ui.common.utils.UIControlUtils;

/**
 * @author Adam Dec
 * @since 0.0.5
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE, dependencyCheck = true)
public class ImportDiscThread implements Runnable {

    private static Logger LOGGER = LoggerFactory.getLogger(ImportDiscThread.class);

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    protected MessageSourceAdapter messageSourceAdapter;

    private long loadedFilesNumber = 0L, loadedFilesSize = 0L;

    private final ImportDiscData importDiscData;
    private final ImportDiscResult importDiscResult;
    private final Map<String, Field> profileAttribbuteMappping;
    private final List<String> notLoadedFileList;
    private final List<File> directoriesToDelete;

    public ImportDiscThread(final ImportDiscData importDiscData, final Map<String, Field> profileAttribbuteMappping) {
        this.importDiscData = importDiscData;
        this.notLoadedFileList = new ArrayList<String>(0);
        this.directoriesToDelete = new ArrayList<File>(0);
        this.importDiscResult = new ImportDiscResult(importDiscData, notLoadedFileList);
        this.profileAttribbuteMappping = profileAttribbuteMappping;
    }

    @Override
    public void run() {
        if (profileAttribbuteMappping.size() == 0) {
            importDiscResult.showImportResults(
                    messageSourceAdapter.getProperty("import.disc.window.import.no.mapping"), true);
            return;
        }

        final StopWatch watch = new StopWatch();
        watch.start();
        try {
            final ProfileEntity profileEntity = profileRepository.findOne(importDiscData.getProfileId());
            final DiscEntity discEntity = new DiscEntity();

            for (com.indexdata.livedocs.manager.service.model.jaxb.Indexed.Data.File indexDataFile : importDiscData
                    .getIndexedDisc().getData().getFile()) {

                // Copy file to a new destination (without file extension)
                final DiscFileData discFileData = DiscFileUtils.copyDiscFile(
                        FilenameUtils.getFullPath(importDiscData.getDiscDataFile().getPath()),
                        indexDataFile.getPath(), importDiscData.getBatchNumber());
                if (discFileData == null) {
                    notLoadedFileList.add(indexDataFile.getPath());
                    continue;
                } else {
                    directoriesToDelete.add(new File(discFileData.getFileRelativePath()));
                }

                if (importDiscData.getInterruptionFlag().get()) {
                    DiscFileUtils.deleteDiscFiles(directoriesToDelete);
                    importDiscResult.showImportResults(
                            messageSourceAdapter.getProperty("import.disc.window.import.interrupted"), true);
                    return;
                }

                // Process file fields (only the one that have mapped)
                // Please not that the current attribute limit is 10
                final FileEntity fileEntity = new FileEntity();
                int indexDataFieldCounter = 1;
                for (com.indexdata.livedocs.manager.service.model.jaxb.Indexed.Data.File.Field indexDataField : indexDataFile
                        .getField()) {
                    if (profileAttribbuteMappping.containsKey(indexDataField.getName())) {
                        final Field discAttribute = profileAttribbuteMappping.get(indexDataField.getName());
                        final Field entityField = new Field(discAttribute.getName(), indexDataField.getValue()
                                .toUpperCase(), discAttribute.getFilterClassType());
                        try {
                            BeanUtils.setProperty(fileEntity, "field" + indexDataFieldCounter, entityField);
                            LOGGER.debug("Mapped {} -> field{}.", entityField, indexDataFieldCounter);
                            for (AttributeEntity attribute : profileEntity.getAttributes()) {
                                if (attribute.getAttributeName().equals(discAttribute.getName())
                                        && !attribute.getMapped()) {
                                    attribute.setMapped(true);
                                    LOGGER.debug("Marking attribute [{}] as mapped.", attribute);
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.error("Could not map [{}] to a field{}", entityField, indexDataFieldCounter, e);
                        }
                    }
                    indexDataFieldCounter++;
                }

                fileEntity.setSize(discFileData.getFileSize());
                fileEntity.setPath(discFileData.getFileRelativePath());
                fileEntity.setExtension(discFileData.getFileExtension());
                fileEntity.setDisc(discEntity);
                discEntity.getFiles().add(fileEntity);

                LOGGER.debug("Persisted file {} with size {}", importDiscData.getDiscDataFile().getName(),
                        FileUtils.byteCountToDisplaySize(discFileData.getFileSize()));

                loadedFilesSize += discFileData.getFileSize();
                loadedFilesNumber++;

                // Update progress bar
                Display.getDefault().syncExec(
                        () -> {
                            UIControlUtils.setProgressLabel(importDiscData.getImportProgressBar(),
                                    importDiscData.getImportProgressLabel(),
                                    I18NResources.getProperty("import.disc.window.import.disc.progress"));
                        });
            }

            discEntity.setDateImported(new Date());
            discEntity.setBatchNumber(importDiscData.getBatchNumber());
            discEntity.setDiscTitle(importDiscData.getDiscTitle());
            discEntity.setFilesNumber(loadedFilesNumber);
            discEntity.setFilesSize(loadedFilesSize);
            discEntity.setProfile(profileEntity);
            profileService.changeProfileDocumentsDataAfterImport(profileEntity, loadedFilesNumber, loadedFilesSize,
                    discEntity);

            watch.stop();
            final String importMessage = messageSourceAdapter.getProperty("import.disc.window.import.completed",
                    loadedFilesNumber,
                    watch.getTotalTimeSeconds());
            importDiscResult.showImportResults(importMessage, false);
        } catch (Exception e) {
            LOGGER.error("Could not import XML file data", e);
            importDiscResult.showImportResults(e.getMessage(), true);
        } finally {
            directoriesToDelete.clear();
            notLoadedFileList.clear();
        }
    }
}