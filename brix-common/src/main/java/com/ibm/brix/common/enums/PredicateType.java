//=============================================================================
//* Name:         PredicateType.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Predicate Type Enumeration
//*                                                                
//* Description:  This enumeration lists the types of predicate names that are
//*               used on a RuleCondition.
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
//*        129113 20250320 tmueller  New source file created.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.common.enums;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum PredicateType {
	ATTRIBUTE("Attribute"),
	EXCHANGEHEADER("Exchange header"),
	CLASS("Class"),
	UNDEFINED("Undefined");

	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    private String name;
    private static final Map<String, PredicateType> ENUM_MAP;

    // Build an immutable map of String name to enum pairs
    static {
        Map<String,PredicateType> map = new HashMap<String,PredicateType>();
        for (PredicateType instance : PredicateType.values()) {
            map.put(instance.getName(),instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    // Constructor
    PredicateType(String name) {
        this.name = name;
    }

    // Return the pretty name
    public String getName() {
        return this.name;
    }

    // Return the enumeration value associated with the specified string
    public static PredicateType get(String name) {
        return ENUM_MAP.get(name);
    }
}
