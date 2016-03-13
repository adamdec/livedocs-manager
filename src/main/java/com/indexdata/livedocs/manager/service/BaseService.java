package com.indexdata.livedocs.manager.service;

import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

public class BaseService {

    @PersistenceContext(unitName = "LiveDocsPU")
    protected EntityManager em;

    protected final AtomicInteger counter = new AtomicInteger();

    @Transactional
    public void flush() {
        em.flush();
        em.clear();
    }

    @Transactional
    public <T> void merge(T entity) {
        em.merge(entity);
    }
}