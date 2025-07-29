//=============================================================================
//* Name:         SEActionMgr.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Simple Endpoint Action Manager
//*                                                                
//* Description:  This class is the Simple Endpoint specific Action
//*               Manager for providing services for working with a
//*               Simple Endpoint Camel exchange.
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
package com.ibm.brix.simpleEndpoint;

import org.apache.camel.Exchange;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.brix.BrixException;
import com.ibm.brix.common.enums.SystemType;
import com.ibm.brix.managers.ActionMgr;
import com.ibm.brix.common.model.Artifact;

public class SEActionMgr extends ActionMgr {
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2023 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    private static final Logger LOG = LoggerFactory.getLogger(SEActionMgr.class);


    /**
     * Constructor
     * 
     * @param Exchange exchange
     * @throws ParseException 
     */
    public SEActionMgr(Exchange exchange) throws ParseException {
        super(SystemType.SIMPLEEP, exchange);
    }

}
