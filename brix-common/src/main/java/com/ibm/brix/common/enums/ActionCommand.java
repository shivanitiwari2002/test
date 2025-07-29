//=============================================================================
//* Name:         ActionCommand.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Action Command Enumeration
//*                                                                
//* Description:  This enumeration lists the action commands that can be performed
//*               on BrIX endpoints or internal to BrIX.
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
//*        129113 20250321 tmueller  New source file created.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.common.enums;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ActionCommand {
	CREATEARTIFACT("CREATEARTIFACT"),
	CHANGESTATE("CHANGESTATE"),
	MODIFYATTRIBUTE("MODIFYATTRIBUTE"),
	ADDCOMMENT("ADDCOMMENT"),
	ADDLINK("ADDLINK"),
	REMOVELINK("REMOVELINK"),
	TRANSFER("TRANSFER"),
	UNDEFINED("UNDEFINED");
	
	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    private String name;
    private static final Map<String, ActionCommand> ENUM_MAP;

    // Build an immutable map of String name to enum pairs
    static {
        Map<String,ActionCommand> map = new HashMap<String,ActionCommand>();
        for (ActionCommand instance : ActionCommand.values()) {
            map.put(instance.getName(),instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    // Constructor
    ActionCommand(String name) {
        this.name = name;
    }

    // Return the pretty name
    public String getName() {
        return this.name;
    }

    // Return the enumeration value associated with the specified string
    public static ActionCommand get(String name) {
        return ENUM_MAP.get(name);
    }

}
