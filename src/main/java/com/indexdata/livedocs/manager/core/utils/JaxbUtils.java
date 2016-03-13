package com.indexdata.livedocs.manager.core.utils;

import java.io.File;
import java.io.StringReader;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import com.indexdata.livedocs.manager.service.model.jaxb.Indexed;

/**
 * this class will unmarshall XMl into POJO.
 * 
 * @author Adam Dec
 * @since 0.0.5
 */
@Component
public class JaxbUtils {

    @Autowired
    private Jaxb2Marshaller jaxb2Marshaller;

    public Indexed unmarshall(File xmlFile) throws Exception {
        return (Indexed) jaxb2Marshaller.unmarshal(new StreamSource(FileUtils.openInputStream(xmlFile)));
    }

    public Indexed unmarshall(String xmlContent) throws Exception {
        final StreamSource xmlSource = new StreamSource(new StringReader(xmlContent));
        return (Indexed) jaxb2Marshaller.unmarshal(xmlSource);
    }
}