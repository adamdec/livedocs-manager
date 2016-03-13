package com.indexdata.livedocs.manager.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.indexdata.livedocs.manager.core.configuration.ManagerConfiguration;
import com.indexdata.livedocs.manager.repository.AttributeRepository;
import com.indexdata.livedocs.manager.repository.custom.AttributeCustomRepository;
import com.indexdata.livedocs.manager.repository.custom.ProfileCustomRepository;
import com.indexdata.livedocs.manager.repository.domain.AttributeEntity;
import com.indexdata.livedocs.manager.repository.domain.DiscEntity;
import com.indexdata.livedocs.manager.repository.domain.Field;
import com.indexdata.livedocs.manager.repository.domain.FileEntity;
import com.indexdata.livedocs.manager.repository.domain.ProfileEntity;
import com.indexdata.livedocs.manager.repository.utils.DaoHibernateUtils;
import com.indexdata.livedocs.manager.service.model.Attribute;

/**
 * This class will provide service layer for profile attributes.
 * 
 * @author Adam Dec
 * @since 0.0.5
 */
@Service
@Transactional
public class AttributeService extends BaseService {

	private static Logger LOGGER = LoggerFactory.getLogger(AttributeService.class);

	@Autowired
	private ProfileCustomRepository profileCustomRepository;

	@Autowired
	private AttributeCustomRepository attributeCustomRepository;

	@Autowired
	private AttributeRepository attributeRepository;

	public List<Attribute> getAttributesForProfile(final Long profileId) {
		try {
			final List<AttributeEntity> attributeEntityList = attributeCustomRepository
					.getAllAttributesByProfileId(profileId);
			final List<Attribute> attributeList = new ArrayList<Attribute>(attributeEntityList.size());
			for (AttributeEntity attributeEntity : attributeEntityList) {
				attributeList.add(new Attribute(attributeEntity.getId(), attributeEntity.getAttributeName(),
						attributeEntity.getMapped()));
			}
			return attributeList;
		} finally {
			logCacheStats();
		}
	}

	public void changeAttributeNameByAttributeNameAndProfileId(final String newAttributeName, final Long attributeId,
			final Long profileId) {
		try {
			final AttributeEntity attributeEntity = attributeRepository.getAttributeEntityByIdAndProfileId(attributeId,
					profileId);
			final String oldAttributeName = attributeEntity.getAttributeName();
			final ProfileEntity profileEntity = profileCustomRepository.getProfileById(profileId);
			for (DiscEntity disc : profileEntity.getDiscs()) {
				for (FileEntity fileEntity : disc.getFiles()) {
					final Field fieldByName = fileEntity.getFieldByName(attributeEntity.getAttributeName());
					if (fieldByName != null) {
						fieldByName.setName(newAttributeName);
					}
				}
			}
			attributeEntity.setAttributeName(newAttributeName);
			LOGGER.debug("Changed attribute for profile with id [{}] from name [{}] to name=[{}]", profileId,
					oldAttributeName, newAttributeName);

			attributeRepository.save(attributeEntity);
			profileCustomRepository.merge(profileEntity);
		} finally {
			logCacheStats();
		}
	}

	public boolean removeAttributeByAttributeIdAndAttributeName(final Long attributeId, final String attributeName,
			final Long profileId) {
		try {
			final ProfileEntity profileEntity = profileCustomRepository.getProfileById(profileId);
			for (DiscEntity disc : profileEntity.getDiscs()) {
				for (FileEntity fileEntity : disc.getFiles()) {
					fileEntity.nullifyField(attributeName);
				}
			}

			profileEntity.getAttributes().remove(new AttributeEntity(attributeId, attributeName));
			LOGGER.debug("Removed attribute for profile with id [{}] with name [{}]", profileId, attributeName);
			profileCustomRepository.merge(profileEntity);
			return true;
		} finally {
			logCacheStats();
		}
	}

	private void logCacheStats() {
		if (ManagerConfiguration.traceCacheStatistics()) {
			int count = counter.incrementAndGet();
			DaoHibernateUtils.printStats(em, AttributeEntity.class, count);
		}
	}
}