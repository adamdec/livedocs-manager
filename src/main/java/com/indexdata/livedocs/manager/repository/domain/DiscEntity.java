package com.indexdata.livedocs.manager.repository.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author Adam Dec
 * @since 0.0.3
 */
@Entity
@Table(name = "LD_DISC", indexes = {
        @Index(name = "DISC_INDEX", columnList = "DISC_TITLE, BATCH_NUMBER")
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "DiscEntity")
public class DiscEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Disc_Seq")
    @SequenceGenerator(name = "Disc_Seq", sequenceName = "LD_DISC_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "DISC_TITLE")
    private String discTitle;

    @Column(name = "BATCH_NUMBER", unique = true)
    private String batchNumber;

    @Column(name = "DATE_IMPORTED")
    private Date dateImported;

    @Column(name = "FILES_NUMBER")
    private Long filesNumber;

    @Column(name = "FILES_SIZE")
    private Long filesSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    public ProfileEntity profile;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "disc", orphanRemoval = true)
    private List<FileEntity> files = new ArrayList<>();

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getDiscTitle() {
        return discTitle;
    }

    public void setDiscTitle(String discTitle) {
        this.discTitle = discTitle;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Date getDateImported() {
        return dateImported;
    }

    public void setDateImported(Date dateImported) {
        this.dateImported = dateImported;
    }

    public Long getFilesNumber() {
        return filesNumber;
    }

    public void setFilesNumber(Long filesNumber) {
        this.filesNumber = filesNumber;
    }

    public Long getFilesSize() {
        return filesSize;
    }

    public void setFilesSize(Long filesSize) {
        this.filesSize = filesSize;
    }

    public ProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    public List<FileEntity> getFiles() {
        return files;
    }

    public void setFiles(List<FileEntity> files) {
        this.files = files;
    }

    @Override
    public String toString() {
        return "DiscEntity [id=" + id + ", discTitle=" + discTitle + ", batchNumber=" + batchNumber + ", dateImported="
                + dateImported + ", filesNumber=" + filesNumber + ", filesSize=" + filesSize + "]";
    }
}
