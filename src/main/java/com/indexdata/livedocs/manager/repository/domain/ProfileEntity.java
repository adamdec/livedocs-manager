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
@Table(name = "LD_PROFILE", indexes = {
        @Index(name = "PROFILE_INDEX", columnList = "PROFILE_NAME"),
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ProfileEntity")
public class ProfileEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Profile_Seq")
    @SequenceGenerator(name = "Profile_Seq", sequenceName = "LD_PROFILE_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PROFILE_NAME", unique = true)
    private String profileName;

    @Column(name = "DATE_CREATED")
    private Date dateCreated;

    @Column(name = "TOTAL_DOCUMENTS_NUMBER")
    private Long totalDocumentsNumber;

    @Column(name = "TOTAL_DOCUMENTS_SIZE")
    private Long totalDocumentsSize;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "profile", orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<DiscEntity> discs = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "profile", orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<AttributeEntity> attributes = new ArrayList<>();

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Long getTotalDocumentsNumber() {
        return totalDocumentsNumber;
    }

    public void setTotalDocumentsNumber(Long totalDocumentsNumber) {
        this.totalDocumentsNumber = totalDocumentsNumber;
    }

    public Long getTotalDocumentsSize() {
        return totalDocumentsSize;
    }

    public void setTotalDocumentsSize(Long totalDocumentsSize) {
        this.totalDocumentsSize = totalDocumentsSize;
    }

    public List<DiscEntity> getDiscs() {
        return discs;
    }

    public void setDiscs(List<DiscEntity> discs) {
        this.discs = discs;
    }

    public List<AttributeEntity> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeEntity> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "ProfileEntity [id=" + id + ", profileName=" + profileName + ", dateCreated=" + dateCreated
                + ", totalDocumentsNumber=" + totalDocumentsNumber + ", totalDocumentsSize=" + totalDocumentsSize + "]";
    }
}
