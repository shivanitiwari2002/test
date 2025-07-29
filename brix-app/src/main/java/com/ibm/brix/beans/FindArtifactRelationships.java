//=============================================================================
//* Name:         FindArtifactRelationships.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  Find Artifact Relationships Bean
//*                                                                
//* Description:  This bean class provides the business logic for searching for
//*               existing Artifact Relationships if they exists. In a typical
//*               case, we are searching for any relationships with the 
//*               current source Artifact being the source side of the relationship
//*               in the context of the current BRoute scope we are working with.
//*               Syncing relationships could exist from the same source Artifact
//*               to multiple target Artifacts. In another use case, if there is
//*               no current BRoute scope, it could be that the source Artifact
//*               was changed so that this source Artifact is now out of scope
//*               for BrIX processing. However, it may still have been in one
//*               or more syncing relationships, so we still need to find these.
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

import java.lang.reflect.InvocationTargetException;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.brix.BrixException;
import com.ibm.brix.Constants;
import com.ibm.brix.common.model.Artifact;
import com.ibm.brix.model.ArtifactRelationshipList;
import com.ibm.brix.utils.Config;

public class FindArtifactRelationships {
	/* CopyRight */
	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

	private static final Logger LOG = LoggerFactory.getLogger(FindArtifactRelationships.class);

	/**
	 * Method:  findArtifactRelationships
	 * Searches for and retrieves the Artifact Relationships for this source Artifact for this BRoute scope if it exists.
	 * Exchange Expected Input:
	 *    - Header ???
	 * Exchange Output:
	 *    - (none)
	 * 
	 * @param Exchange exchange (Provided by Camel)
	 * @return List<ArtifactRelationship>
	 * @throws BrixException
		 * @throws InvocationTargetException 
		 * @throws IllegalAccessException 
		 * @throws InstantiationException 
		 * @throws IllegalArgumentException 
		 * @throws NoSuchMethodException 
		 * @throws SecurityException 
		 */
		public ArtifactRelationshipList findArtifactRelationships(Exchange exchange) throws BrixException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        LOG.info("Search for Artifact Relationship - Start");

		// Get the originating artifact.
        Artifact srcArtifact = (Artifact)exchange.getIn().getHeader(Constants.BRIX_SOURCE_ARTIFACT);
		
        LOG.info("Finding Artifact Key for Artifact " + srcArtifact.getFullPath() + " with type " + srcArtifact.getArtifactType().getName());

		ArtifactRelationshipList artifactRelationshipsList = Config.findMatchingArtifactRelationship(srcArtifact);
		if (artifactRelationshipsList.size() > 0) {
	        LOG.info("Artifact Relationnship found: " + artifactRelationshipsList.size());
        }
		else {
			LOG.info("No matching Artifact Relationnship found.");
		}
        return artifactRelationshipsList;
	}
}
