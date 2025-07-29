//=============================================================================
//* Name:         LoadPayload.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Load Payload Processor
//*                                                                
//* Description:  This provides the process logic for reading the Exchange payload
//*               with the intent to loading BrIX concrete data class(es) with 
//*               the data from the JSON payload.
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
//*               20230918 tmueller  New source file created.
//*        129157 20250501 carndt    Added CollectorType constructor
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.brix.BrixException;
import com.ibm.brix.managers.ExchangeMgr;
import com.ibm.brix.common.model.Artifact;

import com.ibm.brix.common.enums.CollectorType;
import com.ibm.brix.common.CommonConstants;

public class LoadPayload implements Processor {
	/* CopyRight */
	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2023 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

	private static final Logger LOG = LoggerFactory.getLogger(LoadPayload.class);

	CollectorType collectorType = null;		// CCA Feature 129157

	// CCA Feature 129157
	public LoadPayload() {
		// No action ...
	}

	// CCA Feature 129157
	public LoadPayload(CollectorType collectorType) {
		LOG.debug("setting collectorType = " + collectorType);
		this.collectorType = collectorType;
	}

	/**
	 * Method:  process
	 * Processor method that handles the main process called from a camel route.
	 * 
	 * @param Exchange exchange
	 * @throws Exception
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
        LOG.info("LoadPayload Process Start");

        // Get an appropriate exchange manager to work with the exchange in order to read the Exchange payload.
        ExchangeMgr exchangeMgr = ExchangeMgr.getExchangeMgr(exchange);

        Artifact artifact;

		// CCA Feature 129157
		LOG.debug("checking collectorType = " + collectorType);
		if (collectorType == null) {
			collectorType = CollectorType.get((String)exchange.getIn().getHeader(
				CommonConstants.HEADER_COLLECTORTYPE));
				LOG.debug("collectorType from header = " + collectorType);
		}

        if (collectorType.equals(CollectorType.WEBHOOK)) {
	        artifact = exchangeMgr.readArtifactFromWebhookExchange();
	    } else if (collectorType.equals(CollectorType.POLLING)) {
	    	artifact = exchangeMgr.readArtifactFromPollingExchange();
	    } else {
	    	throw new BrixException("Invalid CollectorType");
	    }

        exchange.getIn().setBody(artifact);
	}
}
