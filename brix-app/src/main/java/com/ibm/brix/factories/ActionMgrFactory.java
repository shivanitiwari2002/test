//=============================================================================
//* Name:         ActionMgrFactory.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Action Manager Factory
//*                                                                
//* Description:  This factory assists with instantiating the various action
//*               manager classes.
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
//*               20231023 tollefso  New source file created.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.factories;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.brix.BrixException;
import com.ibm.brix.common.enums.SystemType;
import com.ibm.brix.managers.ActionMgr;

public class ActionMgrFactory {
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2023 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    private static final Logger LOG = LoggerFactory.getLogger(ActionMgrFactory.class);

    //Preallocate space for entries with the expected usage
    private Map<SystemType, Class<? extends ActionMgr>> registeredActionManagers = new HashMap<>(6);

    /**
     * Method:  registerActionMgr
     * Registration of Action Manager classes.
     *
     * @param SystemType systemType
     * @param Class actionMgrClass
     */
    public void registerActionMgr(SystemType systemType, Class<? extends ActionMgr> actionMgrClass){
        registeredActionManagers.put(systemType, actionMgrClass);
    }

    //=============================================================================
    //* Method:      getActionMgr                                            
    //=============================================================================
    /**
     * Method: getActionMgr
     * Returns an Action Manager constructed from the appropriate concrete Action Manager
     * subclass based on the SystemType.
     *
     * @param SystemType systemType
     */
    public ActionMgr getActionMgr(SystemType systemType, Exchange exchange) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException, BrixException{
        Class<? extends ActionMgr> actionMgrClass = registeredActionManagers.get(systemType);
        if (actionMgrClass == null) {
            throw new BrixException("No ActionMgr registered for type " + systemType.getName());
        }
        LOG.info("Found registered Action Manager class: " + actionMgrClass.getName());
        Constructor<? extends ActionMgr> actionMgrConstructor = actionMgrClass.getConstructor(Exchange.class);
        ActionMgr actionMgr = actionMgrConstructor.newInstance(exchange);
        return actionMgr;
    }
}