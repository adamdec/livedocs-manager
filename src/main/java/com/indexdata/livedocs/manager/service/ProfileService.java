package com.indexdata.livedocs.manager.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.indexdata.livedocs.manager.core.configuration.ManagerConfiguration;
import com.indexdata.livedocs.manager.repository.AttributeRepository;
import com.indexdata.livedocs.manager.repository.ProfileRepository;
import com.indexdata.livedocs.manager.repository.custom.ProfileCustomRepository;
import com.indexdata.livedocs.manager.repository.domain.AttributeEntity;
import com.indexdata.livedocs.manager.repository.domain.DiscEntity;
import com.indexdata.livedocs.manager.repository.domain.ProfileEntity;
import com.indexdata.livedocs.manager.repository.utils.DaoHibernateUtils;
import com.indexdata.livedocs.manager.service.model.Profile;

/**
 * This class will provide service layer for profile domain.
 * 
 * @author Adam Dec
 * @since 0.0.3
 */
@Service
@Transactional
public class ProfileService extends BaseService {

    private static Logger LOGGER = LoggerFactory.getLogger(ProfileService.class);

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private ProfileCustomRepository profileCustomRepository;

    @Autowired
    private ConversionService conversionService;

    private final AtomicInteger counter = new AtomicInteger();

    public Profile getProfileByName(final String profileName) {
        try {
            LOGGER.debug("Getting profile with profileName={}", profileName);
            return conversionService.convert(profileCustomRepository.getProfileByName(profileName), Profile.class);
        } finally {
            logCacheStats();
        }
    }

    public Profile getProfileById(final Long profileId) {
        try {
            LOGGER.debug("Getting profile with profileId={}", profileId);
            return conversionService.convert(profileRepository.findOne(profileId), Profile.class);
        } finally {
            logCacheStats();
        }
    }

    public Profile getLatestProfile() {
        try {
            LOGGER.debug("Getting latest profile");
            final List<Profile> profileList = getAllProfilesSortedByName();
            return profileList.size() > 0 ? profileList.get(0) : null;
        } finally {
            logCacheStats();
        }
    }

    public List<Profile> getAllProfilesSortedByName() {
        try {
            LOGGER.debug("Getting all profiles sorted by name");
            return convert(profileCustomRepository.getAllProfilesSortedByName());
        } finally {
            logCacheStats();
        }
    }

    public void createNewProfile(final String profileName) {
        try {
            LOGGER.debug("Creating new profile with profileName={}", profileName);
            final ProfileEntity profileEntity = new ProfileEntity();
            profileEntity.setProfileName(profileName);
            profileEntity.setDateCreated(new Date());
            profileEntity.setTotalDocumentsNumber(0L);
            profileEntity.setTotalDocumentsSize(0L);
            profileRepository.save(profileEntity);
        } finally {
            logCacheStats();
        }
    }

    public void removeProfileById(final Long profileId) {
        try {
            LOGGER.debug("Deleting profile with profileId={}", profileId);
            profileRepository.delete(profileId);
        } finally {
            logCacheStats();
        }
    }

    public void changeProfileName(final Long profileId, final String profileName) {
        try {
            LOGGER.debug("Changing profile name with profileId={} to name={}", profileId, profileName);
            final ProfileEntity profileEntity = profileRepository.findOne(profileId);
            profileEntity.setProfileName(profileName);
            profileRepository.save(profileEntity);
        } finally {
            logCacheStats();
        }
    }

    public void changeProfileDocumentsDataAfterRemove(final Long profileId, long newTotalDocumentsNumber,
            long newTotalDocumentsSize) {
        try {
            LOGGER.debug(
                    "Chaneging profile documents data with profileId={} to newTotalDocumentsNumber={} and newTotalDocumentsSize]()",
                    profileId, newTotalDocumentsNumber, newTotalDocumentsSize);
            final ProfileEntity profileEntity = profileRepository.getProfileEntityById(profileId);
            profileEntity.setTotalDocumentsNumber(profileEntity.getTotalDocumentsNumber() - newTotalDocumentsNumber);
            profileEntity.setTotalDocumentsSize(profileEntity.getTotalDocumentsSize() - newTotalDocumentsSize);
            for (AttributeEntity attributeEntity : profileEntity.getAttributes()) {
                attributeEntity.setMapped(false);
            }

            safeCheck(profileEntity);
            profileRepository.save(profileEntity);
            LOGGER.debug("Updated profile {} with new totalDocumentsNumber={} and totalDocumentsSize={}", profileId,
                    profileEntity.getTotalDocumentsNumber(), profileEntity.getTotalDocumentsSize());
        } finally {
            logCacheStats();
        }
    }

    public void changeProfileDocumentsDataAfterImport(final ProfileEntity profileEntity, long loadedFilesNumber,
            long loadedFilesSize, final DiscEntity discEntity) {
        try {
            LOGGER.debug(
                    "Changeing profile documents data with profileId={} to loadedFilesNumber={} and loadedFilesSize]()",
                    profileEntity.getId(), loadedFilesNumber, loadedFilesSize);
            profileEntity.setTotalDocumentsNumber(profileEntity.getTotalDocumentsNumber() + loadedFilesNumber);
            profileEntity.setTotalDocumentsSize(profileEntity.getTotalDocumentsSize() + loadedFilesSize);
            profileEntity.getDiscs().add(discEntity);
            profileRepository.save(profileEntity);
            LOGGER.debug("Updated profile with new totalDocumentsNumber={} and totalDocumentsSize={}",
                    loadedFilesNumber, loadedFilesSize);
        } finally {
            logCacheStats();
        }
    }

    public void addAttribute(final Long profileId, final String attributeName) {
        try {
            final ProfileEntity profileEntity = profileRepository.findOne(profileId);
            final AttributeEntity attributeEntity = new AttributeEntity();
            attributeEntity.setAttributeName(attributeName);
            attributeEntity.setDateCreated(new Date());
            attributeEntity.setMapped(false);
            attributeEntity.setProfile(profileEntity);
            attributeRepository.save(attributeEntity);

            profileEntity.getAttributes().add(attributeEntity);
            profileRepository.save(profileEntity);
            LOGGER.debug("Created new attribute [{}] in profile [{}]", attributeEntity, profileEntity.getProfileName());
        } finally {
            logCacheStats();
        }
    }

    private void safeCheck(ProfileEntity profileEntity) {
        if (profileEntity.getTotalDocumentsNumber() < 0) {
            profileEntity.setTotalDocumentsNumber(0L);
        }

        if (profileEntity.getTotalDocumentsSize() < 0) {
            profileEntity.setTotalDocumentsSize(0L);
        }
    }

    private List<Profile> convert(final List<ProfileEntity> profileEntityList) {
        if (profileEntityList == null) {
            return Collections.emptyList();
        }

        final List<Profile> profileList = new ArrayList<Profile>(profileEntityList.size());
        for (ProfileEntity profileEntity : profileEntityList) {
            profileList.add(conversionService.convert(profileEntity, Profile.class));
        }
        return profileList;
    }

    private void logCacheStats() {
        if (ManagerConfiguration.traceCacheStatistics()) {
            int count = counter.incrementAndGet();
            DaoHibernateUtils.printStats(em, ProfileEntity.class, count);
        }
    }
}