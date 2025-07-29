//=============================================================================
//* Name:         ProcessOutOfDomain.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  Process Out Of Domain Bean
//*                                                                
//* Description:  This bean class provides the business logic for what happens
//*               when the source artifact being processed was found to have at
//*               least one syncing Artifact Relationship, but the BRoute domain
//*               does not exist. This implies the source artifact was changed
//*               in a way that moved it out of domain for any syncing. However,
//*               we still need to deal with condition based on what the customer
//*               configurations indicate. It may be gracefully dismantling the
//*               the syncing relationship, or perhaps the customer chose to provide
//*               some final sync.
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
//*        129153 20250304 tmueller  New stub source file created.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.beans;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.brix.BrixException;

public class ProcessOutOfDomain {
	/* CopyRight */
	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

	private static final Logger LOG = LoggerFactory.getLogger(ProcessOutOfDomain.class);

	/**
	 * Method:  processOutOfDomain
	 * Handles the out-of-domain condition for the source artifact being processed.
	 * Exchange Expected Input:
	 *    - Header ???
	 * Exchange Output:
	 *    - (none)
	 * 
	 * @param Exchange exchange (Provided by Camel)
	 * @return (void)
	 * @throws BrixException
	 */
	public void processOutOfDomain(Exchange exchange) throws BrixException {
        LOG.info("Processing source artifact out-of-domain condition - Start");

        return;
	}
}
