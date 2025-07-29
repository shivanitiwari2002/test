//=============================================================================
//* Name:         OnCompletion.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX OnCompletion Processor
//*
//* Description:  This provides the process logic for handling the completion of
//*               an exchange.
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
//*               20240328 tollefso  New source file created.
//*
//* Additional notes about the Change Activity:
//*
//=============================================================================
package com.ibm.brix.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.jboss.logging.Logger;

import com.ibm.brix.Constants;
import com.ibm.brix.beans.TransactionBean;
import com.ibm.brix.utils.ExchangeInspector;

public class OnCompletion implements Processor {
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2024 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    private static final Logger LOG = Logger.getLogger(OnCompletion.class);

    /**
     * Method:  process
     * Processor method that handles on completion handling called from a camel route.
     * 
     * @param Exchange exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        LOG.info("[onCompletion] === START ===");
        LOG.info(new ExchangeInspector(true).format(exchange));
        LOG.info("[onCompletion] === END ===");
    }
}