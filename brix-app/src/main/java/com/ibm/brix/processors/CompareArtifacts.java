//=============================================================================
//* Name:         CompareArtifacts.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Load Payload Processor
//*                                                                
//* Description:  This provides the process logic for compare the source and
//*               target artifacts.  Sets a flag in exchange if target needs
//*               to be updated. 
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
//*               20231116 tollefso  New source file created.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.processors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.brix.Constants;
import com.ibm.brix.common.model.AttributeProperties;
import com.ibm.brix.common.simpleendpoint.model.SEArtifact;

public class CompareArtifacts implements Processor {
	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2023 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

	private static final Logger LOG = LoggerFactory.getLogger(CompareArtifacts.class);

    //list of attributes that should be bridged/brixed
	static final Map<Integer, String> attributeList = new HashMap<Integer, String>() {{
		put(1, new String("severity"));
        put(2, new String("description_text"));
	}};

	static final boolean findAttribute(String attributeName) {
		Iterator<Map.Entry<Integer, String>> iterator = attributeList.entrySet().iterator();

		LOG.info("findAttribute: " + attributeName);
		while (iterator.hasNext()) {
			Map.Entry<Integer, String> entry = iterator.next();
			Integer key = entry.getKey();
			String name = entry.getValue();
			LOG.info("Looking for: Key:" + key + ", name: " + name);
            if (name.equals(attributeName)) {
                return true;
            }
		}
		LOG.info("NO MATCH FOUND in attribute list.");
		return false;
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
        LOG.info("process(): CompareArtifacts Process Start");
        boolean updatingNeeded = false;

        SEArtifact srcArtifact = (SEArtifact)exchange.getIn().getHeader(Constants.BRIX_SOURCE_ARTIFACT);
        SEArtifact targetArtifact = (SEArtifact)exchange.getIn().getHeader(Constants.BRIX_TARGET_ARTIFACT);

        Map<String, AttributeProperties> srcAttributes = srcArtifact.getAttributes();
        Map<String, AttributeProperties> targetAttributes = targetArtifact.getAttributes();

		LOG.info("CompareArtifacts:\n" + srcArtifact.toString() + "\to:\n" + targetArtifact.toString());
		for (Map.Entry<String, AttributeProperties> srcAttribute : srcAttributes.entrySet()) {
            String key = srcAttribute.getKey();
			Object value = srcAttribute.getValue().getAttributeValue();
            //TODO: consider other types besides String
            if (value instanceof String) {
                String srcValue = (String)value;
                boolean mappable = findAttribute(key);
                //TODO: need a map/table of which fields to compare
                if (mappable == true) {
                    Object targetObject = targetAttributes.get(key);
                    String targetValue;
                    if (targetObject instanceof Long) {
                        targetValue = Long.toString((long)targetObject);
                    } else {
                        targetValue = targetObject.toString();
                    }
                    LOG.info("Comparing: " + key + ": source '" + srcValue + "' vs target '" + targetValue + "'");
                    if ((targetValue != null && ! targetValue.equals(srcValue))) {
                        LOG.info("key: " + key + " needs updating.");
                        updatingNeeded = true;
                    }
                }
            }
		}

        exchange.getIn().setHeader(Constants.BRIX_DELTAS_EXIST, updatingNeeded);
	}
}
