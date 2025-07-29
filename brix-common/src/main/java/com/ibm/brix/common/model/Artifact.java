//=============================================================================
//* Name:         Artifact.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Artifact
//*                                                                
//* Description:  This abstract data object class holds the BrIX's implementation of an
//*               Artifact.  At a high level, an Artifact is a work entity that
//*               is being worked on, e.g. a Github Issue, EWM work item, CMVC Defect.
//*               Subclassed data classes extend this class to detail the specifics
//*               to each type of Artifact.
//*
//* Notes:
//* > An artifact generally contains a list of attributes. Each attribute at minimum
//*   consists of a name and a value (Object). As BrIX creates artifacts through its 
//*   processing cycles (source, generic, target artifacts), every attribute will carry
//*   a set of properties along with it, including it's raw value Object and additional
//*   Traits of the attribute that are needed through the various stages of processing
//*   the artifact's attributes. These Traits are defined from the attribute mapping
//*   configurations. Thus as a whole, an Artifact stores a map of the attributes and
//*   the associated attribute properties.
//*
//* (C) Copyright IBM Corporation 2023
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
//*               20230915 tmueller  New source file created.
//*               20250429 tollefso  Annotated for persistence.
//*
//* Additional notes about the Change Activity:
//*
//=============================================================================
package com.ibm.brix.common.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.ibm.brix.common.CommonConstants;
import com.ibm.brix.common.enums.ArtifactType;
import com.ibm.brix.common.enums.AttributeType;
import com.ibm.brix.common.enums.Trait;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
@DiscriminatorColumn(name = "artifactclass_type")
public class Artifact extends PanacheEntity {
    /* CopyRight */
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2023 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    // Common fields to all subclasses
    @Embedded
    protected AuditTracker auditTracker;

    @Enumerated(EnumType.STRING)
    protected ArtifactType artifactType;

    protected String artifactKeyString;

    @Transient
    protected String fullPath;

    @ManyToOne
    protected Endpoint endpoint;

    @Transient
    protected Map<String,AttributeProperties> attributes;

    /* Constructor (only called by subclass super()) */
    public Artifact() {
        super();
        auditTracker = new AuditTracker();
        artifactType = ArtifactType.UNKNOWN;
        attributes = new HashMap<String,AttributeProperties>();
    }

    protected Artifact(Artifact artifact) {
        super();
        auditTracker = new AuditTracker();
        artifactType = artifact.getArtifactType();
        attributes = artifact.getAttributes();
    }

    public void updateFrom(Artifact artifact) {
        this.artifactType = artifact.getArtifactType();
        this.artifactKeyString = artifact.getArtifactKeyString();
        this.endpoint = artifact.getEndpoint();
        this.fullPath = artifact.getFullPath();
        this.attributes = artifact.getAttributes();
        updateTime();
    }

    public void setAuditTracker(AuditTracker auditTracker) {
        this.auditTracker = auditTracker;
    }

    public AuditTracker getAuditTracker() {
        return auditTracker;
    }

    /**
     * Method: updateTime
     * Assigns current date/time to the update_time field.
     */
    protected void updateTime() {
        this.auditTracker.updateTime();
    }

    public void setArtifactType(ArtifactType artifactType) {
        this.artifactType = artifactType;
    }

    public ArtifactType getArtifactType() {
        return artifactType;
    }

    public ArtifactKey getArtifactKey() {
        return null;
    }

    public String getArtifactKeyString() {
        return artifactKeyString;
    }

    public void setArtifactKeyString(String artifactKeyString) {
        this.artifactKeyString = artifactKeyString;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
        updateTime();
    }

    public void setEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
        updateTime();
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public Map<String,AttributeProperties> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String,AttributeProperties> attributes) {
        this.attributes = attributes;
    }

    public AttributeProperties getAttributeProperties(String attributeName) {
        return (AttributeProperties)attributes.get(attributeName);
    }

    public void setAttributeProperties(String attributeName, AttributeProperties attrProps) {
        this.attributes.replace(attributeName, attrProps);
    }

    public Object getAttributeValue(String attributeName) {
        AttributeProperties attrProps = (AttributeProperties)attributes.get(attributeName);
        if (attrProps != null)
            return attrProps.getAttributeValue();
        else
            return null;
    }

    public void setAttributeValue(String attributeName, Object value) {
        AttributeProperties attrProps = (AttributeProperties)attributes.get(attributeName);
        if (attrProps != null) {
            attrProps.setAttributeValue(value);
            attributes.replace(attributeName, attrProps);
        }
    }

    public Map<Trait,String> getAttributeTraits(String attributeName) {
        AttributeProperties attrProps = (AttributeProperties)attributes.get(attributeName);
        return attrProps.getAttributeTraits();
    }

    public String getAttributeTrait(String attributeName, Trait traitName) {
        AttributeProperties attrProps = (AttributeProperties)attributes.get(attributeName);
        return attrProps.getAttributeTrait(traitName);
    }

    public void addAttribute(String attributeName, AttributeProperties attributeProps) {
        this.attributes.put(attributeName, attributeProps);
    }

    public void addAttribute(String attributeName, Object value, Map<Trait,String> attrTraits ,AttributeType attrtype) {
        AttributeProperties attrProps = new AttributeProperties(value, attrTraits, attrtype);
        addAttribute(attributeName,  attrProps);
    }

    public void addAttributes(Map<String,AttributeProperties> attributes) {
        this.attributes.putAll(attributes);
    }

    public void removeAttribute(String attributeName) {
        this.attributes.remove(attributeName);
    }

    public String toString() {
        StringBuilder dataObjStr = new StringBuilder();
        dataObjStr.append("Artifact{");
        dataObjStr.append("artifactType: ");
        dataObjStr.append(this.getArtifactType());
        dataObjStr.append(", artifactKeyString: ");
        dataObjStr.append(this.getArtifactKeyString());
        dataObjStr.append(", fullPath: ");
        dataObjStr.append(this.getFullPath());
        dataObjStr.append(", endpoint: ");
        Endpoint endpoint = this.getEndpoint();
        if (endpoint != null) {
            dataObjStr.append(endpoint.toString());
        } else {
            dataObjStr.append(CommonConstants.NULL);
        }
        dataObjStr.append(", attributes: ");
        if (this.getAttributes() == null) {
            dataObjStr.append(CommonConstants.NULL);
        } else {
            dataObjStr.append(this.getAttributes());
        }
        dataObjStr.append("}");
        return dataObjStr.toString();


    }
}
