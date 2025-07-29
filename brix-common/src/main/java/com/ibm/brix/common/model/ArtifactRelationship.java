//=============================================================================
//* Name:         ArtifactRelationship.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX ArtifactRelationship
//*
//* Description:  This abstract data object class holds the BrIX's implementation of an
//*               ArtifactRelationship.  At a high level, an ArtifactRelationship is a 
//*
//* Notes:
//* > 
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
//*               20250225 mitre  New source file created.
//*               20250604 tollefso  Annotated for persistence.
//*
//* Additional notes about the Change Activity:
//*
//=============================================================================
package com.ibm.brix.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;

import com.ibm.brix.common.CommonConstants;
import com.ibm.brix.common.enums.State;
import com.ibm.brix.common.enums.Status;
import com.ibm.brix.common.model.AbstractEntity;

@Entity
public class ArtifactRelationship extends AbstractEntity implements Serializable {
    /* CopyRight */
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    private static final long serialVersionUID = 1L;

    @Size(max = 255)
    private String name;

    @Size(max = CommonConstants.DB_DESCRIPTION_LENGTH)
    @Column(length = CommonConstants.DB_DESCRIPTION_LENGTH)
    private String description;

    @OneToOne
    private Artifact sourceArtifact;

    @OneToOne
    private Artifact targetArtifact;

    /* Indicates if transaction is active or not. */
    @Enumerated(EnumType.STRING)
    private State  state;

    /* For active transactions indicates the status of its processing. */
    @Enumerated(EnumType.STRING)
    private Status status;

    private Instant lastTransactionTime;

    public ArtifactRelationship() {
        super();
        this.name = "";
        this.description = "";
        this.sourceArtifact = null;
        this.targetArtifact = null;
        this.state = State.INITIALIZED;
        this.status = Status.INITIALIZED;
        this.lastTransactionTime = Instant.now();
    }

    public ArtifactRelationship(String name, String description, Artifact sourceArtifact,
                                Artifact targetArtifact, State  state, Status status) {
        super();
        this.name = name;
        this.description = description;
        this.sourceArtifact = sourceArtifact;
        this.targetArtifact = targetArtifact;
        this.state = state;
        this.status = status;
        this.lastTransactionTime = Instant.now();
    }

    public void updateFrom(ArtifactRelationship artifactRelationship) {
        this.name = artifactRelationship.getName();
        this.description = artifactRelationship.getDescription();
        this.sourceArtifact = artifactRelationship.getSourceArtifact();
        this.targetArtifact = artifactRelationship.getTargetArtifact();
        this.state = artifactRelationship.getState();
        this.status = artifactRelationship.getStatus();
        this.lastTransactionTime = artifactRelationship.getLastTransactionTime();
        updateTime();
    }

    public boolean equals(ArtifactRelationship artifactRelationship){
        // TODO: Need to implement an equals method in Artifact and its subclasses
        if(!getName().equals(artifactRelationship.getName())){
            return false;
        }
        if(!getDescription().equals(artifactRelationship.getDescription())){
            return false;
        }
        if(!getSourceArtifact().equals(artifactRelationship.getSourceArtifact())){
            return false;
        }
        if(!getTargetArtifact().equals(artifactRelationship.getTargetArtifact())){
            return false;
        }
        if(!getState().equals(artifactRelationship.getState())){
            return false;
        }
        if(!getStatus().equals(artifactRelationship.getStatus())){
            return false;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        updateTime();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        updateTime();
    }

    public String getSourceArtifactKeyString() {
        if(this.sourceArtifact == null) {
            return CommonConstants.NULL;
        } else {
            return this.sourceArtifact.getArtifactKeyString();
        }
    }

    public String getTargetArtifactKeyString() {
        if(this.targetArtifact == null) {
            return CommonConstants.NULL;
        } else {
            return this.targetArtifact.getArtifactKeyString();
        }
    }

    public Artifact getSourceArtifact() {
        return sourceArtifact;
    }

    public void setSourceArtifact(Artifact sourceArtifact) {
        this.sourceArtifact = sourceArtifact;
        updateTime();
    }

    public Artifact getTargetArtifact() {
        return targetArtifact;
    }

    public void setTargetArtifact(Artifact targetArtifact) {
        this.targetArtifact = targetArtifact;
        updateTime();
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
        updateTime();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        updateTime();
    }

    public Instant getLastTransactionTime() {
        return lastTransactionTime;
    }

    public void setLastTransactionTime(Instant lastTransactionTime) {
        this.lastTransactionTime = lastTransactionTime;
    }

    public String toString() {
        ZoneId zoneId = ZoneId.of(CommonConstants.TIMEZONE_DISPLAY) ; 
        String tempSourceArtifactKeyString = (this.getSourceArtifactKeyString() == null
            ? CommonConstants.NULL
            : this.getSourceArtifactKeyString());
        String tempTargetArtifactKeyString = (this.getTargetArtifactKeyString() == null
            ? CommonConstants.NULL
            : this.getTargetArtifactKeyString());

        StringBuilder dataObjStr = new StringBuilder();           
        dataObjStr.append("ArtifactRelationship{");     
        dataObjStr.append("name: ");
        dataObjStr.append(this.getName());
        dataObjStr.append(", description: ");
        dataObjStr.append(this.getDescription());
        dataObjStr.append(", sourceArtifactKeyString: ");
        dataObjStr.append(tempSourceArtifactKeyString);
        dataObjStr.append(", targetArtifactKeyString: ");
        dataObjStr.append(tempTargetArtifactKeyString);
        dataObjStr.append(", state: ");
        dataObjStr.append(this.getState());
        dataObjStr.append(", status: ");
        dataObjStr.append(this.getStatus());
        dataObjStr.append(", lastTransactionTime: ");
        dataObjStr.append(this.getLastTransactionTime().atZone(zoneId));
        dataObjStr.append("}");
        return dataObjStr.toString();
    }
}
