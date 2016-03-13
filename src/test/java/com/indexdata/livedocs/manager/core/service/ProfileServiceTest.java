package com.indexdata.livedocs.manager.core.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.indexdata.livedocs.manager.repository.custom.ProfileCustomRepository;
import com.indexdata.livedocs.manager.repository.domain.AttributeEntity;
import com.indexdata.livedocs.manager.repository.domain.DiscEntity;
import com.indexdata.livedocs.manager.repository.domain.FileEntity;
import com.indexdata.livedocs.manager.repository.domain.ProfileEntity;
import com.indexdata.livedocs.manager.repository.utils.DaoHibernateUtils;
import com.indexdata.livedocs.manager.service.ProfileService;
import com.indexdata.livedocs.manager.service.model.Profile;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/common-context.xml", "/spring/jpa-context.xml", "/spring/security-context.xml"
})
@Transactional
@TransactionConfiguration(defaultRollback = false)
public class ProfileServiceTest extends BaseServiceTest {

    private static Logger LOGGER = LoggerFactory.getLogger(ProfileServiceTest.class);

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileCustomRepository profileCustomRepository;

    @PersistenceContext(unitName = "LiveDocsPU")
    protected EntityManager em;

    @Ignore
    @Test
    public void shouldGetProfleFromCache1() {
        CriteriaQuery<ProfileEntity> createQuery = em.getCriteriaBuilder().createQuery(ProfileEntity.class);
        createQuery.from(ProfileEntity.class);

        TypedQuery<ProfileEntity> query = em.createQuery(createQuery);

        query.getResultList();
        DaoHibernateUtils.printStats(em, ProfileEntity.class, 1);

        query.getResultList();
        DaoHibernateUtils.printStats(em, ProfileEntity.class, 2);
    }

    @Test
    public void shouldGetProfleFromCache2() {
        // Should invoke select
        profileCustomRepository.getAllProfilesSortedByName();
        DaoHibernateUtils.printStats(em, ProfileEntity.class, 1);

        LOGGER.debug("--------------------------------------------------------------");

        // Should not invoke select and get results from query cache
        profileCustomRepository.getAllProfilesSortedByName();
        DaoHibernateUtils.printStats(em, ProfileEntity.class, 2);

        LOGGER.debug("--------------------------------------------------------------");

        createProfile(2L);

        LOGGER.debug("--------------------------------------------------------------");

        // Should invoke select, there is new profile to be fetched
        profileCustomRepository.getAllProfilesSortedByName();
        DaoHibernateUtils.printStats(em, ProfileEntity.class, 3);
        DaoHibernateUtils.printStats(em, DiscEntity.class, 3);
        DaoHibernateUtils.printStats(em, AttributeEntity.class, 3);
        DaoHibernateUtils.printStats(em, FileEntity.class, 3);

        LOGGER.debug("--------------------------------------------------------------");

        // Should invoke select on lists
        Profile profile = profileService.getProfileById(1L);

        DaoHibernateUtils.printStats(em, DiscEntity.class, 4);
        DaoHibernateUtils.printStats(em, AttributeEntity.class, 4);
        DaoHibernateUtils.printStats(em, FileEntity.class, 4);

        profile = profileService.getProfileById(1L);

        DaoHibernateUtils.printStats(em, DiscEntity.class, 5);
        DaoHibernateUtils.printStats(em, AttributeEntity.class, 5);
        DaoHibernateUtils.printStats(em, FileEntity.class, 5);

        LOGGER.debug(profile.toString());

        LOGGER.debug("--------------------------------------------------------------");

        // Should not invoke select and get results from query cache
        profileCustomRepository.getAllProfilesSortedByName();
        DaoHibernateUtils.printStats(em, ProfileEntity.class, 6);
    }

    @Ignore
    @Test
    public void shouldGetProfleFromCache3() {
        profileService.getAllProfilesSortedByName();
        DaoHibernateUtils.printStats(em, ProfileEntity.class, 1);

        profileService.getAllProfilesSortedByName();
        DaoHibernateUtils.printStats(em, ProfileEntity.class, 1);
    }
}
