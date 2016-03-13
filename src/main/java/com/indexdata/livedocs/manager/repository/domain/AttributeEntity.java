package com.indexdata.livedocs.manager.repository.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author Adam Dec
 * @since 0.0.1
 */
@Entity
@Table(name = "LD_ATTRIBUTE", indexes = {
        @Index(name = "ATTRIBUTE_INDEX", columnList = "ATTRIBUTE_NAME, MAPPED")
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "AttributeEntity")
public class AttributeEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Attribute_Seq")
    @SequenceGenerator(name = "Attribute_Seq", sequenceName = "LD_ATTRIBUTE_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ATTRIBUTE_NAME")
    private String attributeName;

    @Column(name = "MAPPED")
    private boolean mapped;

    @Column(name = "DATE_CREATED")
    private Date dateCreated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    public ProfileEntity profile;

    public AttributeEntity() {
    }

    public AttributeEntity(Long id, String attributeName) {
        super();
        this.id = id;
        this.attributeName = attributeName;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public ProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    public boolean getMapped() {
        return mapped;
    }

    public void setMapped(boolean mapped) {
        this.mapped = mapped;
    }

    @Override
    public String toString() {
        return "AttributeEntity [id=" + id + ", attributeName=" + attributeName + ", mapped=" + mapped
                + ", dateCreated=" + dateCreated + "]";
    }
}