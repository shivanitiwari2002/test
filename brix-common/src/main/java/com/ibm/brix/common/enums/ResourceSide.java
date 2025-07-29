//=============================================================================
//* Name:         ResourceSide.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Resource Side Enumeration
//*                                                                
//* Description:  This enumeration depicts the "sides" of the system resources
//*               that are available for processing against.
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
//*               20240124 tmueller  New source file created.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.common.enums;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ResourceSide {
	SOURCE("SOURCE"),
	TARGET("TARGET"),
	UNKNOWN("UNKNOWN");
	
	/* CopyRight */
	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2024 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    private String name;
    private static final Map<String, ResourceSide> ENUM_MAP;

    // Build an immutable map of String name to enum pairs
    static {
        Map<String,ResourceSide> map = new HashMap<String,ResourceSide>();
        for (ResourceSide instance : ResourceSide.values()) {
            map.put(instance.getName(),instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    // Constructor
    ResourceSide(String name) {
        this.name = name;
    }

    // Return the pretty name
    public String getName() {
        return this.name;
    }

    // Return the enumeration value associated with the specified string
    public static ResourceSide get(String name) {
        return ENUM_MAP.get(name);
    }

}
