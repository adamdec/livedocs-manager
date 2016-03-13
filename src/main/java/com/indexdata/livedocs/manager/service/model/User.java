package com.indexdata.livedocs.manager.service.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @author Adam Dec
 * @since 0.0.4
 */
@Immutable
public class User {

    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String password;
    private String orginalPassword;
    private List<Permission> userPermissions;
    private List<Permission> profilePermissions;

    public User() {
        this.userPermissions = new ArrayList<Permission>(3);
        this.profilePermissions = new ArrayList<Permission>(12);
    }

    public User(Long id, String firstName, String lastName, String userName, String email, String password) {
        super();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.orginalPassword = password;
        this.userPermissions = Collections.emptyList();
        this.profilePermissions = Collections.emptyList();
    }

    public User(Long id, String firstName, String lastName, String userName, String email, String password,
            List<Permission> userPermissions, List<Permission> profilePermissions) {
        super();
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.orginalPassword = password;
        this.userPermissions = userPermissions;
        this.profilePermissions = profilePermissions;
    }

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

    public String getOrginalPassword() {
        return orginalPassword;
    }

    public void setOrginalPassword(String orginalPassword) {
        this.orginalPassword = orginalPassword;
    }

    public List<Permission> getUserPermissions() {
        return userPermissions;
    }

    public void setUserPermissions(List<Permission> userPermissions) {
        this.userPermissions = userPermissions;
    }

    public List<Permission> getProfilePermissions() {
        return profilePermissions;
    }

    public void setProfilePermissions(List<Permission> profilePermissions) {
        this.profilePermissions = profilePermissions;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((profilePermissions == null) ? 0 : profilePermissions.hashCode());
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
        result = prime * result + ((userPermissions == null) ? 0 : userPermissions.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        User other = (User) obj;
        if (email == null) {
            if (other.email != null) {
                return false;
            }
        } else if (!email.equals(other.email)) {
            return false;
        }
        if (firstName == null) {
            if (other.firstName != null) {
                return false;
            }
        } else if (!firstName.equals(other.firstName)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (lastName == null) {
            if (other.lastName != null) {
                return false;
            }
        } else if (!lastName.equals(other.lastName)) {
            return false;
        }
        if (password == null) {
            if (other.password != null) {
                return false;
            }
        } else if (!password.equals(other.password)) {
            return false;
        }
        if (profilePermissions == null) {
            if (other.profilePermissions != null) {
                return false;
            }
        } else if (!profilePermissions.equals(other.profilePermissions)) {
            return false;
        }
        if (userName == null) {
            if (other.userName != null) {
                return false;
            }
        } else if (!userName.equals(other.userName)) {
            return false;
        }
        if (userPermissions == null) {
            if (other.userPermissions != null) {
                return false;
            }
        } else if (!userPermissions.equals(other.userPermissions)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}