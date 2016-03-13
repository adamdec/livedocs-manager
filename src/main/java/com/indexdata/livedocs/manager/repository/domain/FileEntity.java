package com.indexdata.livedocs.manager.repository.domain;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Adam Dec
 * @since 0.0.4
 */
@Entity
@Table(name = "LD_DATA", indexes = {
        @Index(name = "DATA_INDEX", columnList = "PATH"),
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "FileEntity")
public class FileEntity extends AbstractEntity {

    private static Logger LOGGER = LoggerFactory.getLogger(FileEntity.class);

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "File_Seq")
    @SequenceGenerator(name = "File_Seq", sequenceName = "LD_FILE_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PATH")
    private String path;

    @Column(name = "SIZE")
    private Long size;

    @Column(name = "EXTENSION")
    private String extension;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private DiscEntity disc;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "name1")),
            @AttributeOverride(name = "value", column = @Column(name = "value1")),
            @AttributeOverride(name = "filterClassType", column = @Column(name = "filterClassType1"))
    })
    private Field field1;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "name2")),
            @AttributeOverride(name = "value", column = @Column(name = "value2")),
            @AttributeOverride(name = "filterClassType", column = @Column(name = "filterClassType2"))
    })
    private Field field2;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "name3")),
            @AttributeOverride(name = "value", column = @Column(name = "value3")),
            @AttributeOverride(name = "filterClassType", column = @Column(name = "filterClassType3"))
    })
    private Field field3;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "name4")),
            @AttributeOverride(name = "value", column = @Column(name = "value4")),
            @AttributeOverride(name = "filterClassType", column = @Column(name = "filterClassType4"))
    })
    private Field field4;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "name5")),
            @AttributeOverride(name = "value", column = @Column(name = "value5")),
            @AttributeOverride(name = "filterClassType", column = @Column(name = "filterClassType5"))
    })
    private Field field5;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "name6")),
            @AttributeOverride(name = "value", column = @Column(name = "value6")),
            @AttributeOverride(name = "filterClassType", column = @Column(name = "filterClassType6"))
    })
    private Field field6;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "name7")),
            @AttributeOverride(name = "value", column = @Column(name = "value7")),
            @AttributeOverride(name = "filterClassType", column = @Column(name = "filterClassType7"))
    })
    private Field field7;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "name8")),
            @AttributeOverride(name = "value", column = @Column(name = "value8")),
            @AttributeOverride(name = "filterClassType", column = @Column(name = "filterClassType8"))
    })
    private Field field8;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "name9")),
            @AttributeOverride(name = "value", column = @Column(name = "value9")),
            @AttributeOverride(name = "filterClassType", column = @Column(name = "filterClassType9"))
    })
    private Field field9;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "name10")),
            @AttributeOverride(name = "value", column = @Column(name = "value10")),
            @AttributeOverride(name = "filterClassType", column = @Column(name = "filterClassType10"))
    })
    private Field field10;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public DiscEntity getDisc() {
        return disc;
    }

    public void setDisc(DiscEntity disc) {
        this.disc = disc;
    }

    public Field getField1() {
        return field1;
    }

    public void setField1(Field field1) {
        this.field1 = field1;
    }

    public Field getField2() {
        return field2;
    }

    public void setField2(Field field2) {
        this.field2 = field2;
    }

    public Field getField3() {
        return field3;
    }

    public void setField3(Field field3) {
        this.field3 = field3;
    }

    public Field getField4() {
        return field4;
    }

    public void setField4(Field field4) {
        this.field4 = field4;
    }

    public Field getField5() {
        return field5;
    }

    public void setField5(Field field5) {
        this.field5 = field5;
    }

    public Field getField6() {
        return field6;
    }

    public void setField6(Field field6) {
        this.field6 = field6;
    }

    public Field getField7() {
        return field7;
    }

    public void setField7(Field field7) {
        this.field7 = field7;
    }

    public Field getField8() {
        return field8;
    }

    public void setField8(Field field8) {
        this.field8 = field8;
    }

    public Field getField9() {
        return field9;
    }

    public void setField9(Field field9) {
        this.field9 = field9;
    }

    public Field getField10() {
        return field10;
    }

    public void setField10(Field field10) {
        this.field10 = field10;
    }

    @Override
    public String toString() {
        return "FileEntity [id=" + id + ", path=" + path + ", size=" + size + ", extension=" + extension + ", field1="
                + field1 + ", field2=" + field2 + ", field3=" + field3 + ", field4=" + field4 + ", field5=" + field5
                + ", field6=" + field6 + ", field7=" + field7 + ", field8=" + field8 + ", field9=" + field9
                + ", field10=" + field10 + "]";
    }

    public Field getFieldByName(String fieldName) {
        final Map<String, Field> fieldMap = getFieldMap();
        return fieldMap.get(fieldName);
    }

    public void nullifyField(String fieldName) {
        if (field1 != null && fieldName.equals(field1.getName())) {
            field1 = null;
        }
        if (field2 != null && fieldName.equals(field2.getName())) {
            field2 = null;
        }
        if (field3 != null && fieldName.equals(field3.getName())) {
            field3 = null;
        }
        if (field4 != null && fieldName.equals(field4.getName())) {
            field4 = null;
        }
        if (field5 != null && fieldName.equals(field5.getName())) {
            field5 = null;
        }
        if (field6 != null && fieldName.equals(field6.getName())) {
            field6 = null;
        }
        if (field7 != null && fieldName.equals(field7.getName())) {
            field7 = null;
        }
        if (field8 != null && fieldName.equals(field8.getName())) {
            field8 = null;
        }
        if (field9 != null && fieldName.equals(field9.getName())) {
            field9 = null;
        }
        if (field10 != null && fieldName.equals(field10.getName())) {
            field10 = null;
        }

        final Map<String, Field> fieldMap = getFieldMap();
        int fieldCounter = 1;
        for (Field field : fieldMap.values()) {
            try {
                BeanUtils.setProperty(this, "field" + fieldCounter, field);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOGGER.error("Could not set field{} with data={}", fieldCounter, field, e);
            }
        }
    }

    public Map<String, Field> getFieldMap() {
        final Map<String, Field> fieldMap = new HashMap<String, Field>(10);
        if (field1 != null) {
            fieldMap.put(field1.getName(), field1);
        }
        if (field2 != null) {
            fieldMap.put(field2.getName(), field2);
        }
        if (field3 != null) {
            fieldMap.put(field3.getName(), field3);
        }
        if (field4 != null) {
            fieldMap.put(field4.getName(), field4);
        }
        if (field5 != null) {
            fieldMap.put(field5.getName(), field5);
        }
        if (field6 != null) {
            fieldMap.put(field6.getName(), field6);
        }
        if (field7 != null) {
            fieldMap.put(field7.getName(), field7);
        }
        if (field8 != null) {
            fieldMap.put(field8.getName(), field8);
        }
        if (field9 != null) {
            fieldMap.put(field9.getName(), field9);
        }
        if (field10 != null) {
            fieldMap.put(field10.getName(), field10);
        }
        return fieldMap;
    }
}