//=============================================================================
//* Name:         PrepArtifactExtract.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  Prepare Artifact Extract Bean
//*
//* Description:  This bean class provides the business logic for preparing
//*               to extract a specified artifact from a specified endpoint system.
//*               This is done in the scenario whereby we need to fetch an artifact
//*               which is in an existing syncing relationship with another
//*               artifact so that we can compare their attributes.
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
//*        129157 20250326 carndt    Implementation
//*
//* Additional notes about the Change Activity:
//*
//=============================================================================
package com.ibm.brix.beans;

import org.apache.camel.Exchange;
import org.jboss.logging.Logger;

import com.ibm.brix.BrixException;
import com.ibm.brix.Constants;
import com.ibm.brix.common.enums.ResourceSide;
import com.ibm.brix.common.model.ArtifactKey;
import com.ibm.brix.common.model.ArtifactRelationship;
import com.ibm.brix.model.ArtifactRelationshipList;
import com.ibm.brix.common.model.BRoute;
import com.ibm.brix.common.model.Endpoint;

public class PrepArtifactExtract {
    /* CopyRight */
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM "
            + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    private static final Logger LOG = Logger.getLogger(PrepArtifactExtract.class);

    /**
     * Method: prepArtifactExtract
     * Prepares the information needed to extract an artifact from a specified endpoint system.
     * Exchange Expected Input:
     * - Header BRIX_BROUTE
     * - Header BRIX_ARTIFACT_RELATIONSHIPS
     * - Header BRIX_EXTRACT_RESOURCE_SIDE (for determining source or target)
     * Exchange Output:
     * - Header BRIX_ENDPOINT_SYSTEM_TYPE
     * - Header BRIX_ENDPOINT_EXTRACT_URI
     * - Body endpoint extract key
     * 
     * @param Exchange exchange (Provided by Camel)
     * @return JSON string representation of the extracted artifact
     * @throws BrixException
     */
    public String prepArtifactExtract(Exchange exchange) throws BrixException {
        LOG.info("Prepare to extract artifact - Start");

        String result = "";
        ResourceSide resourceSide = exchange.getIn().getHeader(Constants.BRIX_EXTRACT_RESOURCE_SIDE, ResourceSide.class);
        LOG.info("Retrieved ResourceSide to extract from = " + resourceSide.getName());

        // Determine the correct endpoint component to call
        BRoute bRoute = exchange.getIn().getHeader(Constants.BRIX_BROUTE, BRoute.class);
        if (bRoute == null) {
            throw new BrixException("BRoute missing from Exchange header");
        }

        LOG.info("Retrieved BRoute: source = " + bRoute.getSourceEndpoint().getName() + "; target = " + bRoute.getTargetEndpoint().getName());
        Endpoint extractEndpoint = null;

        switch (resourceSide) {
            case SOURCE:
            extractEndpoint = bRoute.getSourceEndpoint();
            break;

            case TARGET:
            extractEndpoint = bRoute.getTargetEndpoint();
            break;

            default:
            throw new BrixException("unknown ResourceSide from Exchange header");
        }

        LOG.debug("[DEBUG] Retrieved extract endpoint: " + extractEndpoint);

        // Retrieve the ArtifactRelationship list from the exchange
        ArtifactRelationshipList artifactRelationshipList = exchange.getIn()
                .getHeader(Constants.BRIX_ARTIFACT_RELATIONSHIPS, ArtifactRelationshipList.class);
        if (artifactRelationshipList == null) {
            throw new BrixException("ArtifactRelationshipList missing from Exchange header");
        }

        try {
            // Assume we are working with the first ArtifactRelationship in the array list
            ArtifactRelationship artifactRelationship = artifactRelationshipList.getTheArtifactRelationshipList().get(0);
            LOG.debug("Retrieved ArtifactRelationship: " + artifactRelationship);
            String artifactKey = (resourceSide.equals(ResourceSide.SOURCE)
                ? artifactRelationship.getSourceArtifactKeyString()
                : artifactRelationship.getTargetArtifactKeyString());
            String endpointExtractURI = extractEndpoint.getFetchArtifactURI();
            String endpointExtractKey = artifactKey;
            LOG.info("extract uri: " + endpointExtractURI);
            LOG.info("extract key: " + endpointExtractKey);

            exchange.getIn().setHeader(Constants.BRIX_ENDPOINT_SYSTEM_TYPE, extractEndpoint.getSystemType());
            exchange.getIn().setHeader(Constants.BRIX_ENDPOINT_EXTRACT_URI, endpointExtractURI);
            exchange.getIn().setHeader(com.ibm.brix.simpleendpoint.utils.Constants.ISSUEKEY_HEADER, endpointExtractKey);    // TODO: ISSUEKEY_HEADER is SE specific ...

            // Return the target ID needed by the caller when performing the actual extract
            result = endpointExtractKey;
            
        } catch (Exception e) {
            throw new BrixException(e);
        }

        return result;
    }

}
