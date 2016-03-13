package com.indexdata.livedocs.manager.service.model;

import java.util.Collection;

import javax.annotation.concurrent.Immutable;

import com.indexdata.livedocs.manager.repository.domain.Field;

/**
 * @author Adam Dec
 * @since 0.0.4
 */
@Immutable
public class File {

    private final Long id;
    private final String path;
    private final String extension;
    private final Collection<Field> fields;

    public File(final String path) {
        this.id = null;
        this.path = path;
        this.extension = null;
        this.fields = null;
    }

    public File(final Long id, final String path, final String extension, final Collection<Field> fields) {
        this.id = id;
        this.path = path;
        this.extension = extension;
        this.fields = fields;
    }

    public Long getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getExtension() {
        return extension;
    }

    public Collection<Field> getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return "File [id=" + id + ", path=" + path + ", extension=" + extension + ", fields=" + fields + "]";
    }
}
