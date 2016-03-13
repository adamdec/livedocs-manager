package com.indexdata.livedocs.manager.service.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.convert.converter.Converter;

import com.indexdata.livedocs.manager.repository.domain.AttributeEntity;
import com.indexdata.livedocs.manager.repository.domain.ProfileEntity;
import com.indexdata.livedocs.manager.service.model.Attribute;
import com.indexdata.livedocs.manager.service.model.Profile;

/**
 * Converts <code>ProfileEntity</code> to <code>Profile</code>
 * 
 * @author Adam Dec
 * @since 0.0.1
 */
public class ProfileEntityConverter implements Converter<ProfileEntity, Profile> {

    @Override
    public Profile convert(final ProfileEntity profileEntity) {
        if (profileEntity == null) {
            return null;
        }

        final List<Attribute> attributeList = new ArrayList<Attribute>(profileEntity.getAttributes().size());
        if (profileEntity.getAttributes().size() > 0) {
            for (AttributeEntity attribute : profileEntity.getAttributes()) {
                attributeList
                        .add(new Attribute(attribute.getId(), attribute.getAttributeName(), attribute.getMapped()));
            }
        }

        return new Profile(profileEntity.getId(), profileEntity.getProfileName(), profileEntity.getDateCreated(),
                profileEntity.getTotalDocumentsNumber(), profileEntity.getTotalDocumentsSize(), attributeList);
    }
}
