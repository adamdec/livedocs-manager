package com.indexdata.livedocs.manager.repository.domain;

import javax.persistence.MappedSuperclass;

/**
 * Base class to derive entity classes from.
 *
 * @author Adam Dec
 * @since 1.0.0
 */
@MappedSuperclass
public abstract class AbstractEntity {

    /**
     * Returns the identifier of the entity.
     *
     * @return the id
     */
    public abstract Long getId();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (this.getId() == null || obj == null || !(this.getClass().isInstance(obj))) {
            return false;
        }
        final AbstractEntity that = (AbstractEntity) obj;
        return this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() == null ? 0 : getId().hashCode();
    }
}