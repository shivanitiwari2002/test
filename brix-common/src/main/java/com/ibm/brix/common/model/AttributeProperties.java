//=============================================================================
//* Name:         AttributeProperties.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Attribute Properties
//*                                                                
//* Description:  When BrIX creates artifacts through its processing (source, generic,
//*               target), each attribute will carry properties along with it,
//*               including it's raw value Object and additional Traits of the 
//*               attribute that are needed through various stages of processing the
//*               artifact's attributes. This data class encapsulates these properties
//*               for an attribute. The properties are used during artifact creation
//*               and rules processing logic. Traits are defined from the attribute
//*               mapping configurations.
//*
//* (C) Copyright IBM Corporation 2024
//*
//* The Source code for this program is not published or otherwise     
//* divested of its trade secrets, irrespective of what has been       
//* deposited with the U.S. Copyright Office.                           
//*
//* Note to U.S. Government Users Restricted Rights:  Use, 
//* duplication or disclosure restricted by GSA ADP Schedule 
//* Contract with IBM Corp.
//*                                                                 
//* Change Log:
//* Flag Reason   Date     User Id   Description
//* ---- -------- -------- --------  ------------------------------------------
//*               20240125 tmueller  New source file created.
//*               20250429 tollefso  Annotated for persistence.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.JdbcTypeCode;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.ibm.brix.common.enums.AttributeType;
import com.ibm.brix.common.enums.Trait;

@Entity
public class AttributeProperties extends AbstractEntity {
    /* CopyRight */
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2024 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    @JdbcTypeCode(SqlTypes.JSON)
    @NotNull
    @Column(nullable = false)
    private Object attributeValue;

    @ElementCollection
    @MapKeyEnumerated(EnumType.STRING)
    private Map<Trait,String> attributeTraits;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private AttributeType attributeType;

    /* Constructor */
    public AttributeProperties() {
        this.attributeValue = null;
        this.attributeTraits = new HashMap<Trait,String>();
        this.attributeType = AttributeType.UNKNOWN;
    }

    /* Constructor */
    public AttributeProperties(Object attrValue, AttributeType attrType) {
        this.attributeValue = attrValue;
        this.attributeTraits = new HashMap<Trait,String>();
        this.attributeType = attrType;
    }

    /* Constructor */
    public AttributeProperties(Object attrValue, Map<Trait,String> attrTraits, AttributeType attrType) {
        this.attributeValue = attrValue;
        this.attributeTraits = attrTraits;
        this.attributeType = attrType;
    }

    public void updateFrom(AttributeProperties attributeProperties) {
        this.attributeValue = attributeProperties.getAttributeValue();
        this.attributeTraits = attributeProperties.getAttributeTraits();
        this.attributeType = attributeProperties.getAttributeType();
        updateTime();
    }

    public Object getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(Object attrValue) {
        this.attributeValue = attrValue;
    }

    public Map<Trait,String> getAttributeTraits() {
        return attributeTraits;
    }

    public String getAttributeTrait(Trait traitName) {
        return attributeTraits.get(traitName);
    }

    public void setAttributeTraits(Map<Trait,String> attrTraits) {
        this.attributeTraits = attrTraits;
    }

    public void setAttributeTrait(Trait traitName, String traitValue) {
        this.attributeTraits.put(traitName, traitValue);
    }
    
    public AttributeType getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(AttributeType attributeType) {
        this.attributeType = attributeType;
    }

    public String toString() {
        StringBuilder dataObjStr = new StringBuilder();
        dataObjStr.append("AttributeProperties{");
        dataObjStr.append("attributeValue: ");
        dataObjStr.append(this.getAttributeValue());
        dataObjStr.append(", attributeTraits: ");
        dataObjStr.append(this.getAttributeTraits());
        dataObjStr.append("}");
        return dataObjStr.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        AttributeProperties that = (AttributeProperties) obj;

        // Compare attribute values
        if (!valueEquals(that)) return false;

        // Compare traits
        if (!Objects.equals(this.attributeTraits, that.attributeTraits)) return false;
        return Objects.equals(this.attributeType, that.attributeType); 
    }

    
    public boolean valueEquals(AttributeProperties other) {
        if (other == null)
            return false;

        Object subjectValue = this.getAttributeValue();
        Object targetValue = other.getAttributeValue();

        AttributeType type = getAttributeType();
        if (type == null) {
            type = AttributeType.UNKNOWN;
        }

        switch (type) {
        case MULTIVALUE:
            if (subjectValue instanceof String s1 && targetValue instanceof String s2) {
                Set<String> set1 = new HashSet<>(Arrays.asList(splitMultiValue(s1)));
                Set<String> set2 = new HashSet<>(Arrays.asList(splitMultiValue(s2)));
                return set1.equals(set2);
            }
            break;

        case MULTILINE:
            if (subjectValue instanceof String s1 && targetValue instanceof String s2) {
                return normalizeMultiline(s1).equals(normalizeMultiline(s2));
            }
            break;

        case IDENTITY:
            if (subjectValue instanceof String s1 && targetValue instanceof String s2) {
                return s1.equals(s2);
            }
            break;

        case STRING:
        case ENUM:
            if (subjectValue instanceof String s1 && targetValue instanceof String s2) {
                return s1.equals(s2);
            }
            break;

        case LONG:
            if (subjectValue instanceof Number n1 && targetValue instanceof Number n2) {
                return n1.longValue() == n2.longValue();
            }
            break;

        case DATE:
            if (subjectValue instanceof Instant dateInstant1 && targetValue instanceof Instant dateInstant2) {
                return dateInstant1.equals(dateInstant2); //
            }
            break;

        case BOOLEAN:
            if (subjectValue instanceof Boolean b1 && targetValue instanceof Boolean b2) {
                return b1.equals(b2);
            } else if (subjectValue instanceof String s1 && targetValue instanceof String s2) {
                return Boolean.parseBoolean(s1) == Boolean.parseBoolean(s2);
            }
            break;

        case UNKNOWN:
        default:
            return Objects.equals(subjectValue, targetValue);
        }
        return false;
    }

    private static String[] splitMultiValue(String value) {
        if (value == null)
            return new String[0];
        return value.trim().split("\\s*[;,|\\n\\t\\s]\\s*");
    }

    private static String normalizeMultiline(String multiline) {
        return Arrays.stream(multiline.split("\\R")) // Split on any line separator
                .map(String::trim).collect(Collectors.joining("\n"));
    }

}
