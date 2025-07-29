//=============================================================================
//* Name:         OnException.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX OnException Processor
//*
//* Description:  This provides the process logic for handling exceptions.
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
import com.ibm.brix.common.enums.Status;
import com.ibm.brix.utils.ExchangeInspector;

public class OnException implements Processor {
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2024 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    private static final Logger LOG = Logger.getLogger(OnException.class);

    /**
     * Method:  process
     * Processor method that handles the exception handling process called from a camel route.
     * 
     * @param Exchange exchange
     * @throws Exception
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        LOG.error("[onException] === START EXCEPTION ===");
        Exception e = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

        exchange.getIn().setHeader(Constants.BRIX_TRX_STATUS, Status.ERROR.toString());
        new TransactionBean().updateStatus(exchange, e.toString());

        LOG.error("Exception: ", e);
        LOG.error(new ExchangeInspector(true).format(exchange));
        LOG.error("[onException] === END EXCEPTION ===");
    }
}