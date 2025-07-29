//=============================================================================
//* Name:         ArtifactType.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Artifact Type Enumeration
//*                                                                
//* Description:  This enumeration lists the major artifact types that the
//*               BrIX application deals with.
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
//*               20231109 tollefso  New source file created.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.common.enums;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ArtifactType {
	SEISSUE("SE Issue"),
	GENERIC("Generic"),
	UNKNOWN("Unknown");

	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2023 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    private String name;
    private static final Map<String, ArtifactType> ENUM_MAP;

    // Build an immutable map of String name to enum pairs
    static {
        Map<String,ArtifactType> map = new HashMap<String,ArtifactType>();
        for (ArtifactType instance : ArtifactType.values()) {
            map.put(instance.getName(),instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    // Constructor
    ArtifactType(String name) {
        this.name = name;
    }

    // Return the pretty name
    public String getName() {
        return this.name;
    }

    // Return the enumeration value associated with the specified string
    public static ArtifactType get(String name) {
        return ENUM_MAP.get(name);
    }
}
