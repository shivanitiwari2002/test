//=============================================================================
//* Name:         Denormalizer.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Denormalizer Bean
//*                                                                
//* Description:  This bean class provides the business logic for the Artifact
//*               transformation from its normalized generic form.
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
//*               20240215 tmueller  New source file created.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.beans;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.zone.ZoneRulesException;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.brix.BrixException;
import com.ibm.brix.Constants;
import com.ibm.brix.common.enums.ArtifactType;
import com.ibm.brix.common.enums.AttributeType;
import com.ibm.brix.common.enums.ResourceSide;
import com.ibm.brix.common.enums.SystemType;
import com.ibm.brix.common.enums.Trait;
import com.ibm.brix.factories.ArtifactFactory;
import com.ibm.brix.common.model.Artifact;
import com.ibm.brix.common.model.ArtifactMap;
import com.ibm.brix.common.model.AttributeProperties;
import com.ibm.brix.common.model.BRoute;
import com.ibm.brix.common.model.GenAttrMap;
import com.ibm.brix.common.model.GenValueMap;
import com.ibm.brix.model.GenericArtifact;

public class Denormalizer {
	/* CopyRight */
	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2024 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

	private static final Logger LOG = LoggerFactory.getLogger(Denormalizer.class);

	/**
	 * Method:  fromGenericArtifact
	 * Converts a target GenericArtifact to a Target Artifact.
	 * Exchange Expected Input:
	 *    - Header BRIX_BROUTE
	 *    - Body contains the GenericArtifact to be denormalized
	 * Exchange Output:
	 *    - (none)
	 * 
	 * @param Exchange exchange (Provided by Camel)
	 * @return Artifact - automatically put onto the Exchange body
	 * @throws BrixException
	 */
	public Artifact fromGenericArtifact(Exchange exchange) throws BrixException {
        LOG.info("De-normalize from target generic artifact to target artifact - Start");
        Artifact targetArtifact = null;
        
        // The BRoute and Target Generic Artifact should already be stored on the Exchange header.
        BRoute broute = exchange.getIn().getHeader(Constants.BRIX_BROUTE, BRoute.class);

        // TODO: ??? Currently, we are bypassing a lot of processing until this is built out. Eventually, the target generic artifact
        // should be on the Exchange header. For now, we just use the source generic artifact as the thing to process.
        //GenericArtifact genericArtifact = exchange.getIn().getHeader(Constants.BRIX_SOURCE_GENERIC_ARTIFACT, GenericArtifact.class);
        GenericArtifact genericArtifact = exchange.getIn().getBody(GenericArtifact.class);
        if (broute == null) {
        	throw new BrixException("Broute was not found on the Exchange.");
        }
        if (genericArtifact == null) {
        	throw new BrixException("Generic Artifact was not found on the Exchange.");
        }

        // Use the ArtifactFactory to create the proper type of artifact, based on the previously determined target system type.
        SystemType targetSystemType = broute.getTargetEndpoint().getSystemType();
        ArtifactFactory artifactFactory = new ArtifactFactory();
        LOG.info("Target Artifact type is " + targetSystemType.getName());
        try {
        	targetArtifact = artifactFactory.getArtifact(targetSystemType);
        }
        catch (Exception e) {
        	LOG.error("ERROR: ", e);
			throw new BrixException("Error getting target artifact. " + e);
        }
        
        // TODO: ??? Hardcoded now for simple endpoint. These will need to be assigned in system specific code somewhere.
        targetArtifact.setEndpoint(broute.getTargetEndpoint());
		targetArtifact.setArtifactType(ArtifactType.SEISSUE);

        ArtifactType artifactType = targetArtifact.getArtifactType();
		ArtifactMap targetArtifactMap = broute.getTargetArtifactMap(artifactType);
		if (targetArtifactMap == null) {
        	throw new BrixException("The Target Artifact Map configuration for artifact type " + artifactType + " was not found on the BRoute.");
		}
        LOG.info("targetArtifactMap: " + targetArtifactMap.toString());
		List<GenAttrMap> genAttrMaps = targetArtifactMap.getAttributeMaps();

		// Pass 1 - Loop through all the generic attribute mapping configurations, which contains the list of attributes that need to be created on the Target Artifact,
		//          and create a new attribute to be added to the Target Artifact.
        LOG.info("Starting Pass 1");
		for (GenAttrMap genAttrMap : genAttrMaps) {
	        LOG.info("genAttrMap: " + genAttrMap.toString());
			// Take the value from the Generic Artifact attribute and use it for the Target Artifact attibute.
			// Question: are we to assume the only things in this genAttrMaps list are those that actually get sync'd, or will it contain others we need to carry along
			// including "compose" ones (and those with traits)? If so, how are these traits carried forward? 
			String attributeName = genAttrMap.getAttributeName();
			AttributeType attributeType = genAttrMap.getAttributeType();
			String genericName = genAttrMap.getGenericName();
			Object value = genericArtifact.getAttributeValue(genericName);
			LOG.info("Creating target attribute '" + attributeName + "' based on generic attribute '" + genericName + "'");

			// Special handling of the target attribute's value from the generic value based on the attribute type.
			switch (attributeType) {
				case DATE:
					LOG.info("Date type found: Converting generic Instant date to attribute date value.");
					String pattern = genAttrMap.getAttributeTrait(Trait.PATTERN);
					value = convertFromGenericDate(value, pattern);
					LOG.info("Attribute date value: " + value);
					break;
				case ENUM:
					LOG.info("Enum type found: Determining attribute value based on value map for generic value: " + value);
					value = computeAttributeValueFromValueMap(value, genAttrMap.getFromGenericValueMap());
					break;
				default:
					break;
			}

			targetArtifact.addAttribute(attributeName, value, genAttrMap.getAttributeTraits(), attributeType);
		}

		// Pass 2 - Loop through all the new target Artifact attributes. If there is an attribute "compose" trait associated with it, this tells us we need to
		// compose the value based on the trait's incantation. Assign this composed value.
        LOG.info("Starting Pass 2");
		Map<String,AttributeProperties> targetAttributes = targetArtifact.getAttributes();
		for (Map.Entry<String, AttributeProperties> targetAttribute : targetAttributes.entrySet()) {
			String composeTrait = targetArtifact.getAttributeTrait(targetAttribute.getKey(), Trait.COMPOSE);
			if (composeTrait != null) {
				String value = composeValue(composeTrait);
				targetArtifact.setAttributeValue(targetAttribute.getKey(), value);
			}
		}

		LOG.info("Created Target Artifact: " + targetArtifact.toString());

        return targetArtifact;
	}

	/**
	 * Method:  convertFromGenericDate
	 * Convert the given generic date value into a date string, using the pattern to determine the value's format.
	 * 
	 * @param Object value
	 * @param String pattern
	 * @return String The date string representation of the generic Instant date value.
	 * @throws BrixException 
	 */
	private String convertFromGenericDate(Object value, String pattern) throws BrixException {
		try {
			Instant dateInstant = (Instant) value;
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of(Constants.TIMEZONE_UTC));
			String dateStr = formatter.format(dateInstant);
			return dateStr;
		}
		catch (IllegalArgumentException|DateTimeParseException|ZoneRulesException e) {
	        LOG.error("Date conversion exeption: " + e);
	        throw new BrixException(e);
		}
	}


	/**
	 * Method:  computeAttributeValueFromValueMap
	 * Determines the target attribute value to use for this target attribute, based on the value map configuration.
	 * 
	 * @param Object value
	 * @param List<GenValueMap> valueMapList
	 * @return String The generic value to use.
	 */
	private String computeAttributeValueFromValueMap(Object value, List<GenValueMap> valueMapList) {
		if (value == null) {
			return null;
		}
		String genericValueStr = (String) value.toString();
		String attrValueStr = genericValueStr;

		// Look up the current generic value in the value map list. If found, send back the mapped attribute value.
		boolean found = false;
		for (GenValueMap valueMap : valueMapList) {
			// ??? Do we need to presort the list by order?  What does this give us?
			LOG.info("==>Lookup genericValueStr '" + genericValueStr + "' on value map: " + valueMap.toString());
			// Since we are denormalizing a generic attribute to a target attribute, the resource side is 'target'.
			if ((valueMap.getResourceSide() == ResourceSide.TARGET) && (valueMap.getGenericValue().equals(genericValueStr))) {
				attrValueStr = valueMap.getAttributeValue();
				found = true;
				break;
			}
		}
		
		
		if (!found) {
			LOG.warn("Generic enum value '" + genericValueStr + "' was not found in the Generic Value Map configuration. Using the generic enum value for the target attribute value.");
		}
		return attrValueStr;
	}

	/**
	 * Method:  composeValue
	 * Using the "compose" trait on the target attribute, constructs the value for the attribute.
	 * 
	 * @param String trait
	 * @return String The composed attribute value to use.
	 */
	private String composeValue(String composeTrait) {
		String value = "";
		// Process the compose trait for constructing the value.
		// TODO
		return value;
	}
}
