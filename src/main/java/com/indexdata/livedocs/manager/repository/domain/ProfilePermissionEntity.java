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

/**
 * @author Adam Dec
 * @since 0.0.4
 */
@Entity
@Table(name = "LD_PROFILE_PERMISSION", indexes = {
        @Index(name = "PROFILE_PERMISSION_NAME_IDX", columnList = "NAME"),
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "ProfilePermissionEntity")
public class ProfilePermissionEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Profile_Permission_Seq")
    @SequenceGenerator(name = "Profile_Permission_Seq", sequenceName = "LD_PROFILE_PERMISSION_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "ACCESS_MODE")
    @Enumerated(EnumType.STRING)
    private AccessMode accessMode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    public UserEntity user;

    public ProfilePermissionEntity() {
    }

    public ProfilePermissionEntity(String name, AccessMode accessMode, UserEntity user) {
        super();
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return "ProfilePermissionEntity [id=" + id + ", name=" + name + ", accessMode=" + accessMode + "]";
    }
}
