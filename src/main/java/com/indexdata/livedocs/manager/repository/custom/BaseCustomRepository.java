package com.indexdata.livedocs.manager.repository.custom;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.LobCreator;
import org.springframework.transaction.annotation.Transactional;

public class BaseCustomRepository {

    @PersistenceContext
    protected EntityManager em;

    public void writeLock(Object entity) {
        em.lock(entity, LockModeType.PESSIMISTIC_WRITE);
    }

    public void flush() {
        em.flush();
    }

    public <T> T merge(T profileEntity) {
        return em.merge(profileEntity);
    }

    @Transactional
    public LobCreator getLobCreator() {
        return Hibernate.getLobCreator(em.unwrap(Session.class));
    }
}
