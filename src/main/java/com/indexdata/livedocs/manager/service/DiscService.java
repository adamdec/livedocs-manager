package com.indexdata.livedocs.manager.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.indexdata.livedocs.manager.core.configuration.ManagerConfiguration;
import com.indexdata.livedocs.manager.repository.DiscRepository;
import com.indexdata.livedocs.manager.repository.custom.DiscCustomRepository;
import com.indexdata.livedocs.manager.repository.domain.DiscEntity;
import com.indexdata.livedocs.manager.repository.utils.DaoHibernateUtils;
import com.indexdata.livedocs.manager.service.model.Disc;

/**
 * This class will provide service layer for disc domain.
 * 
 * @author Adam Dec
 * @since 0.0.3
 */
@Service
@Transactional
public class DiscService extends BaseService {

    private static Logger LOGGER = LoggerFactory.getLogger(DiscService.class);

    @Autowired
    private ProfileService profileService;

    @Autowired
    private DiscRepository discRepository;

    @Autowired
    private DiscCustomRepository discCustomRepository;

    @Autowired
    private ConversionService conversionService;

    public boolean removeDiscById(final Long discId, final Long profileId, long newTotalDocumentsNumber,
            long newTotalDocumentsSize) {
        try {
            LOGGER.debug("Deleting disc with id={}", discId);
            discRepository.delete(discId);
            profileService.changeProfileDocumentsDataAfterRemove(profileId, newTotalDocumentsNumber,
                    newTotalDocumentsSize);
            return true;
        } catch (EmptyResultDataAccessException e) {
            LOGGER.error("Could not remove disc with id=" + discId, e);
            return false;
        } finally {
            logCacheStats();
        }
    }

    public boolean checkIdDiscExists(final String batchNumber) {
        try {
            LOGGER.debug("Checking if disc exists with batchNumber={}", batchNumber);
            return discRepository.getDiscEntityByBatchNumber(batchNumber) == null ? false : true;
        } finally {
            logCacheStats();
        }
    }

    public List<Disc> getAllDiscsByProfileId(final long profileId) {
        try {
            LOGGER.debug("Getting all discs with profileId={}", profileId);
            final List<DiscEntity> foundDiscs = discCustomRepository.getAllDiscsByProfileId(profileId);
            final List<Disc> discList = new ArrayList<Disc>();
            for (DiscEntity discEntity : foundDiscs) {
                discList.add(conversionService.convert(discEntity, Disc.class));
            }
            return discList;
        } finally {
            logCacheStats();
        }
    }

    public long countByProfileId(final long profileId) {
        try {
            LOGGER.debug("Counting all discs with profileId={}", profileId);
            return discCustomRepository.countAllDiscsByProfileId(profileId);
        } finally {
            logCacheStats();
        }
    }

    private void logCacheStats() {
        if (ManagerConfiguration.traceCacheStatistics()) {
            int count = counter.incrementAndGet();
            DaoHibernateUtils.printStats(em, DiscEntity.class, count);
        }
    }

    public DiscEntity save(DiscEntity discEntity) {
        return discRepository.save(discEntity);
    }
}