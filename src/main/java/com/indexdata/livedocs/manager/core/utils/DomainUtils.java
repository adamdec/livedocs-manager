package com.indexdata.livedocs.manager.core.utils;

import java.util.ArrayList;
import java.util.List;

import com.indexdata.livedocs.manager.core.resources.I18NResources;
import com.indexdata.livedocs.manager.service.model.Attribute;
import com.indexdata.livedocs.manager.service.model.Profile;

public class DomainUtils {

    public static List<String> convertToStringList(List<Attribute> columnNames) {
        final List<String> stringList = new ArrayList<String>(columnNames.size());
        for (Attribute attribute : columnNames) {
            stringList.add(attribute.getName());
        }
        return stringList;
    }

    public static String[] convertProfiles(List<Profile> profileList) {
        if (profileList.size() == 0) {
            return new String[0];
        }
        final String[] stringProfiles = new String[profileList.size()];
        int i = 0;
        for (Profile profile : profileList) {
            stringProfiles[i++] = profile.getName();
        }
        return stringProfiles;
    }

    public static String[] convertAttributes(List<Attribute> attributeList) {
        if (attributeList.size() == 0) {
            return new String[0];
        }
        final String[] stringAttributes = new String[attributeList.size() + 1];
        int i = 0;
        for (Attribute attribute : attributeList) {
            stringAttributes[i++] = attribute.getName();
        }
        stringAttributes[attributeList.size()] = I18NResources.getProperty("import.disc.window.do.not.import");
        return stringAttributes;
    }
}
