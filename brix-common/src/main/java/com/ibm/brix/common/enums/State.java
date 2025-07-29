//=============================================================================
//* Name:         State.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX State Enumeration
//*                                                                
//* Description:  This enumeration lists the ArtifactRelationship states.
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
//*               20250228 mitre  New source file created.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.common.enums;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum State {
    INITIALIZED("INITIALIZED"),
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    ARCHIVED("ARCHIVED");
    
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    private String name;
    private static final Map<String, State> ENUM_MAP;

    // Build an immutable map of String name to enum pairs
    static {
        Map<String,State> map = new HashMap<String,State>();
        for (State instance : State.values()) {
            map.put(instance.getName(),instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    State(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    // Return the enumeration value associated with the specified string
    public static State get(String name) {
        return ENUM_MAP.get(name);
    }
}
