//=============================================================================
//* Name:         ActionMgr.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Action Manager
//*                                                                
//* Description:  This abstract class provides services for working with the
//*               actions that can be peformed on Endpoints.
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
package com.ibm.brix.managers;

import org.apache.camel.Exchange;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.brix.BrixException;
import com.ibm.brix.Constants;
import com.ibm.brix.common.enums.SystemType;
import com.ibm.brix.factories.ActionMgrFactory;
import com.ibm.brix.simpleEndpoint.SEActionMgr;


public abstract class ActionMgr {
    /* CopyRight */
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2023 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    private static final Logger LOG = LoggerFactory.getLogger(ActionMgr.class);

    private static ActionMgrFactory actionMgrFactory;
    private SystemType systemType;
    protected Exchange exchange;


    protected ActionMgr(SystemType type, Exchange exchange) throws ParseException {
        this.systemType = type;
        this.exchange = exchange;

        initializeFactories();
    }

    /**
     * Initialize and register with the action factories. It is here that all Action Managers must be registered.
     */
    private static void initializeFactories() {
        // Create the action manager factory and register its action managers
        actionMgrFactory = new ActionMgrFactory();
        actionMgrFactory.registerActionMgr(SystemType.SIMPLEEP, SEActionMgr.class);
    }

    /**
     * Method:  getActionMgr
     * Determines the appropriate target system type Action Manager that is used
     * for various methods in working with the Camel Exchange.
     * 
     * @param Exchange exchange
     * @return ActionMgr
     */
    public static ActionMgr getActionMgr(SystemType type, Exchange exchange) throws BrixException {
        ActionMgr actionMgr = null;
        SystemType systemType = SystemType.UNKNOWN;

        // Since this is a static method, initialize the factories here.
        initializeFactories();

        // Get the system type from the exchange header.
        systemType = exchange.getIn().getHeader(Constants.BRIX_SOURCE_SYSTEM_TYPE, SystemType.class);

        LOG.info("Action Manager system type is " + systemType.getName());
        
        // Instantiate the appropriate Action Manager for this system type.
        try {
            actionMgr = actionMgrFactory.getActionMgr(systemType, exchange);
        }
        catch (Exception e) {
            LOG.error("ERROR: ", e);
            throw new BrixException("Error getting Action Manager. " + e);
        }

        return actionMgr;
    }

}
