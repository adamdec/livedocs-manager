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
 * @since 0.0.4
 */
@Entity
@Table(name = "LD_USER", indexes = {
        @Index(name = "USER_INDEX", columnList = "USER_NAME, EMAIL")
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "UserEntity")
public class UserEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "User_Seq")
    @SequenceGenerator(name = "User_Seq", sequenceName = "LD_USER_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "USER_NAME", unique = true)
    private String userName;

    @Column(name = "EMAIL", unique = true)
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "DATE_CREATED")
    private Date dateCreated;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<UserPermissionEntity> userPermissions = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<ProfilePermissionEntity> profilePermissions = new ArrayList<>();

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public List<UserPermissionEntity> getUserPermissions() {
        return userPermissions;
    }

    public void setUserPermissions(List<UserPermissionEntity> userPermissions) {
        this.userPermissions = userPermissions;
    }

    public List<ProfilePermissionEntity> getProfilePermissions() {
        return profilePermissions;
    }

    public void setProfilePermissions(List<ProfilePermissionEntity> profilePermissions) {
        this.profilePermissions = profilePermissions;
    }

    @Override
    public String toString() {
        return "UserEntity [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", userName="
                + userName + ", email=" + email + ", password=" + password + ", dateCreated=" + dateCreated + "]";
    }
}