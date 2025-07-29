//=============================================================================
//* Name:         FindBRoutes.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  Find BRoutes bean
//*                                                                
//* Description:  This bean class provides the business logic for getting the
//*               list of BRoutes and attempting to find all BRoutes for which
//*               the source Artifact being processed is considered "in scope".
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
//*               20231107 tollefso  New source file created.
//*               20250311 tmueller  Refactored as FindBRoutes bean class.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.beans;


import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.brix.BrixException;
import com.ibm.brix.Constants;
import com.ibm.brix.common.model.Artifact;
import com.ibm.brix.model.BRouteList;
import com.ibm.brix.utils.Config;

public class FindBRoutes {
	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

	private static final Logger LOG = LoggerFactory.getLogger(FindBRoutes.class);

	/**
	 * Method:  findBRoutes
	 * Finds the BRoutes in scope for the source Artifact.
	 * Exchange Expected Input:
	 *    - Header BRIX_SOURCE_ARTIFACT
	 * Exchange Output:
	 *    - (none)
	 * 
	 * @param Exchange exchange
	 * @return BRouteList
	 * @throws BrixException
	 */
	public BRouteList findBRoutes(Exchange exchange) throws BrixException {
        LOG.info("Find BRoutes Start");

        // Get the originating artifact.
        Artifact srcArtifact = (Artifact)exchange.getIn().getHeader(Constants.BRIX_SOURCE_ARTIFACT);
        LOG.info("Finding BRoutes for Artifact " + srcArtifact.getFullPath() + " with type " + srcArtifact.getArtifactType().getName());

		BRouteList brouteList = Config.findMatchingBRoutes(srcArtifact);
		if (brouteList.size() > 0) {
	        LOG.info("BRoutes found: " + brouteList.size());
        }
		else {
			LOG.info("No matching BRoute found.");
		}
		return brouteList;
	}
}
