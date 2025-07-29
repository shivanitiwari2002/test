//=============================================================================
//* Name:         CompareArtifactsNewjava
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  Compare two Artifacts for equality
//*                                                                
//* Description:  This bean class compares a source generic artifact with a target generic artifact and creates a new delta generic artifact
//*               that contains the differences. It uses the source generic artifact as its basis.
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
//*        xxxxxx 20250403 Shivani-Tiwari3   New stub source file created.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================

package com.ibm.brix.beans;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.jboss.logging.Logger;

import com.ibm.brix.Constants;
import com.ibm.brix.common.model.AttributeProperties;
import com.ibm.brix.model.GenericArtifact;

public class CompareArtifactsNew {
	
	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

	private static final Logger LOG = Logger.getLogger(CompareArtifactsNew.class);
	
	public GenericArtifact compareArtifacts(Exchange exchange) {
		LOG.info("Starting artifact comparison");
		
        GenericArtifact sourceGenericArtifact = (GenericArtifact) exchange.getIn().getHeader(Constants.BRIX_SOURCE_GENERIC_ARTIFACT);
        GenericArtifact targetGenericArtifact = (GenericArtifact) exchange.getIn().getHeader(Constants.BRIX_TARGET_GENERIC_ARTIFACT);

        GenericArtifact deltaGenericArtifact = new GenericArtifact();
        Map<String, AttributeProperties> deltaAttributes = new HashMap<>();

        Map<String, AttributeProperties> sourceAttributes = sourceGenericArtifact.getAttributes();
        Map<String, AttributeProperties> targetAttributes = targetGenericArtifact.getAttributes();
        

        LOG.info("Source attribute count: {}" + sourceAttributes.size());

        int differenceCount = 0;
        for (Map.Entry<String, AttributeProperties> entry : sourceAttributes.entrySet()) {
            String attributeName = entry.getKey();
            AttributeProperties sourceValue = entry.getValue();
            AttributeProperties targetValue = targetAttributes.get(attributeName);

            if (sourceValue == null || targetValue == null || !sourceValue.valueEquals(targetValue)) {
                deltaAttributes.put(attributeName, sourceValue);
                differenceCount++;
            }
        }

        deltaGenericArtifact.setAttributes(deltaAttributes);

        boolean hasDeltas = !deltaAttributes.isEmpty();
        exchange.getIn().setHeader(Constants.BRIX_DELTAS_EXIST, hasDeltas);
        
        LOG.info("Number of differing attributes added to delta: {}");
        LOG.info("Comparison complete. Deltas present: {}");


        return deltaGenericArtifact;
    }

}
