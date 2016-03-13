package com.indexdata.livedocs.manager.service.converter;

import org.springframework.core.convert.converter.Converter;

import com.indexdata.livedocs.manager.repository.domain.DiscEntity;
import com.indexdata.livedocs.manager.service.model.Disc;

/**
 * Converts <code>DiscEntity</code> to <code>Disc</code>
 * 
 * @author Adam Dec
 * @since 0.0.4
 */
public class DiscEntityConverter implements Converter<DiscEntity, Disc> {

    @Override
    public Disc convert(DiscEntity discentity) {
        return new Disc(discentity.getId(), discentity.getDiscTitle(), discentity.getBatchNumber(),
                discentity.getDateImported(), discentity.getFilesNumber(), discentity.getFilesSize());
    }
}