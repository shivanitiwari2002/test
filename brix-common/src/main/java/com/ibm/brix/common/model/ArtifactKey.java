//=============================================================================
//* Name:         ArtifactKey.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX ArtifactKey
//*                                                                
//* Description:  This abstract data object class holds the BrIX's implementation of an
//*               ArtifactKey.  At a high level, an ArtifactKey Identifies an Artifact 
//*               e.g. a Github Issue ID, EWM work item ID, CMVC Defect ID.
//*               Subclassed data classes extend this class to detail the specifics
//*               to each type of Artifact.
//*
//* (C) Copyright IBM Corporation 2025
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
//*               20250305 mitre  New source file created.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.common.model;

import com.ibm.brix.common.simpleendpoint.model.SEArtifactKey;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, // Value of Property added into JSON String, specified below in JsonSubTypes
        include = JsonTypeInfo.As.PROPERTY,
        property = "type") // Property to add into JSON String
@JsonSubTypes({
        @JsonSubTypes.Type(value = SEArtifactKey.class, name = "seartifactkey")
})
public abstract class ArtifactKey {
    /* CopyRight */
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    // Common fields to all subclasses
    protected String artifactKeyString;
    protected Artifact artifact;

    /* Constructor (only called by subclass super()) */
    protected ArtifactKey() {
        super();
    }
    protected ArtifactKey(Artifact artifact) {
        super();
        this.artifact = artifact;
        setArtifactKeyString(artifact.getArtifactKeyString());
    }

    public abstract String getArtifactKeyString();

    public void setArtifactKeyString(String artifactKeyString) {
        this.artifactKeyString = artifactKeyString;
    }

    public Artifact getArtifact(){
        return artifact;
    }

    public void setArtifact(Artifact artifact){
        this.artifact = artifact;
    }

    public abstract String toString();
}
