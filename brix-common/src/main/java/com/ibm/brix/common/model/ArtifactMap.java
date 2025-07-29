//=============================================================================
//* Name:         ArtifactMap.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Artifact Map
//*
//* Description:  An Artifact Map holds configuration data for an ArtifactType and
//*               the applicable data mapping to or from a Generic Artifact.
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
//*               20240119 tmueller  New source file created.
//*               20250621 tollefso  Annotated for persistence.
//*
//* Additional notes about the Change Activity:
//*
//=============================================================================
package com.ibm.brix.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

import com.ibm.brix.common.enums.ArtifactType;
import com.ibm.brix.common.model.AbstractEntity;
import com.ibm.brix.common.enums.ResourceSide;

@Entity
public class ArtifactMap extends AbstractEntity {
    /* CopyRight */
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2024 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private ArtifactType artifactType;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private ResourceSide mapType;

    @OneToMany
    private List<GenAttrMap> attributeMaps;

    /* Constructor */
    public ArtifactMap() {
        super();
        artifactType = ArtifactType.UNKNOWN;
        mapType = ResourceSide.UNKNOWN;
        attributeMaps = new ArrayList<GenAttrMap>();

    }

    public ArtifactMap(ArtifactType artifactType, ResourceSide mapType, List<GenAttrMap> attributeMaps) {
        super();
        this.artifactType = artifactType;
        this.mapType = mapType;
        this.attributeMaps = attributeMaps;

    }

    public void updateFrom(ArtifactMap artifactMap) {
        this.artifactType = artifactMap.getArtifactType();
        this.mapType = artifactMap.getMapType();
        this.attributeMaps = artifactMap.getAttributeMaps();
        updateTime();
    }

    public ArtifactType getArtifactType() {
        return artifactType;
    }

    public void setArtifactType(ArtifactType artifactType) {
        this.artifactType = artifactType;
        updateTime();
    }

    public ResourceSide getMapType() {
        return mapType;
    }

    public void setMapType(ResourceSide mapType) {
        this.mapType = mapType;
        updateTime();
    }

    public List<GenAttrMap> getAttributeMaps() {
        return attributeMaps;
    }

    public void setAttributeMaps(List<GenAttrMap> genAttrMapList) {
        this.attributeMaps = genAttrMapList;
        updateTime();
    }

    public String toString() {
        StringBuilder dataObjStr = new StringBuilder();
        dataObjStr.append("ArtifactMap{");
        dataObjStr.append(super.toString());
        dataObjStr.append(", artifactType: ");
        dataObjStr.append(this.getArtifactType());
        dataObjStr.append(", mapType: ");
        dataObjStr.append(this.getMapType());
        // Don't output the List attributeMaps here, but do give the size.
        dataObjStr.append(", attributeMaps count: ");
        dataObjStr.append(this.getAttributeMaps().size());
        dataObjStr.append("}");
        return dataObjStr.toString();
    }
}
