package com.indexdata.livedocs.manager.service.converter;

import org.springframework.core.convert.converter.Converter;

import com.indexdata.livedocs.manager.repository.domain.FileEntity;
import com.indexdata.livedocs.manager.service.model.File;

/**
 * Converts <code>FileEntity</code> to <code>File</code>
 * 
 * @author Adam Dec
 * @since 0.0.4
 */
public class FileEntityConverter implements Converter<FileEntity, File> {

    @Override
    public File convert(final FileEntity fileEntity) {
        return new File(fileEntity.getId(), fileEntity.getPath(), fileEntity.getExtension(), fileEntity.getFieldMap()
                .values());
    }
}