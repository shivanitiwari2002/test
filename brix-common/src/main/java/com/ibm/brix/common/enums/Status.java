//=============================================================================
//* Name:         Status.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Status Enumeration
//*                                                                
//* Description:  This enumeration lists the Transaction statuses.
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
//*               20240327 tollefso  New source file created.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.common.enums;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Status {
        INITIALIZED("INITIALIZED"),
        QUEUED("QUEUED"),
        INPROGRESS("INPROGRESS"),
        ERROR("ERROR"),
        COMPLETED("COMPLETED");
    
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2024 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    private String name;
    private static final Map<String, Status> ENUM_MAP;

    // Build an immutable map of String name to enum pairs
    static {
        Map<String,Status> map = new HashMap<String,Status>();
        for (Status instance : Status.values()) {
            map.put(instance.getName(),instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    Status(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    // Return the enumeration value associated with the specified string
    public static Status get(String name) {
        return ENUM_MAP.get(name);
    }
}
