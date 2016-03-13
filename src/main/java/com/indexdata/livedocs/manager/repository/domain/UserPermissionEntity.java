package com.indexdata.livedocs.manager.repository.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import com.indexdata.livedocs.manager.service.model.enums.AccessMode;
import com.indexdata.livedocs.manager.service.model.enums.Section;

/**
 * @author Adam Dec
 * @since 0.0.4
 */
@Entity
@Table(name = "LD_USER_PERMISSION", indexes = {
        @Index(name = "USER_PERMISSION_NAME_IDX", columnList = "SECTION_NAME"),
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "UserPermissionEntity")
public class UserPermissionEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "User_Permission_Seq")
    @SequenceGenerator(name = "User_Permission_Seq", sequenceName = "LD_USER_PERMISSION_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "SECTION_NAME")
    @Enumerated(EnumType.STRING)
    private Section section;

    @Column(name = "ACCESS_MODE")
    @Enumerated(EnumType.STRING)
    private AccessMode accessMode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    public UserEntity user;

    public UserPermissionEntity() {
    }

    public UserPermissionEntity(Section section, AccessMode accessMode, UserEntity user) {
        super();
        this.section = section;
        this.accessMode = accessMode;
        this.user = user;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public AccessMode getAccessMode() {
        return accessMode;
    }

    public void setAccessMode(AccessMode accessMode) {
        this.accessMode = accessMode;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserPermissionEntity [id=" + id + ", section=" + section + ", accessMode=" + accessMode + "]";
    }
}