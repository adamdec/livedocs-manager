package com.indexdata.livedocs.manager.service.model;

import java.util.Date;

import javax.annotation.concurrent.Immutable;

/**
 * POJO representation of XML disc.
 * 
 * @author Adam Dec
 * @since 0.0.1
 */
@Immutable
public class Disc {

    private final Long id;
    private final String title;
    private final String batchNumber;
    private final Date importDate;
    private final Long filesNumber;
    private final Long filesSize;

    public Disc(final Long id, final String title, final String batchNumber, final Date importDate,
            final Long filesNumber, final Long filesSize) {
        super();
        this.id = id;
        this.title = title;
        this.batchNumber = batchNumber;
        this.importDate = importDate;
        this.filesNumber = filesNumber;
        this.filesSize = filesSize;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public Date getImportDate() {
        return importDate;
    }

    public Long getFilesNumber() {
        return filesNumber;
    }

    public Long getFilesSize() {
        return filesSize;
    }

    @Override
    public String toString() {
        return "Disc [id=" + id + ", title=" + title + ", batchNumber=" + batchNumber + ", importDate=" + importDate
                + ", filesNumber=" + filesNumber + ", filesSize=" + filesSize + "]";
    }
}
