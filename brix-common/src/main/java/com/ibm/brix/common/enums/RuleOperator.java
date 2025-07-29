//=============================================================================
//* Name:         RuleOperator.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Rule Operator Enumeration
//*                                                                
//* Description:  This enumeration lists the operators that a RuleCondition or
//*               RuleActionCommandConfig can use for asserting testing a 
//*               condition to be true or false.
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

public enum RuleOperator {
	EXISTS("Exists"),
	NOTEXISTS("Not Exists"),
	EQUALS("Equals"),
	EQUALSIGNORECASE("Equals Ignore Case"),
	NOTEQUALS("Not Equals"),
	CONTAINS("Contains"),
	NOTCONTAINS("Not Contains"),
	TRUE("True"),
	FALSE("False"),
	EMPTY("Empty"),
	NOTEMPTY("Not Empty"),
	MATCHREGEX("Match RegEx"),
	UNDEFINED("Undefined");

	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    private String name;
    private static final Map<String, RuleOperator> ENUM_MAP;

    // Build an immutable map of String name to enum pairs
    static {
        Map<String,RuleOperator> map = new HashMap<String,RuleOperator>();
        for (RuleOperator instance : RuleOperator.values()) {
            map.put(instance.getName(),instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    // Constructor
    RuleOperator(String name) {
        this.name = name;
    }

    // Return the pretty name
    public String getName() {
        return this.name;
    }

    // Return the enumeration value associated with the specified string
    public static RuleOperator get(String name) {
        return ENUM_MAP.get(name);
    }

}
