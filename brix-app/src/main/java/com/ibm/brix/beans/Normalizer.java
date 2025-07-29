//=============================================================================
//* Name:         Normalizer.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Normalizer Bean
//*                                                                
//* Description:  This bean class provides the business logic for the Artifact
//*               transformation to its normalized generic form.
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import com.ibm.brix.common.enums.Trait;
import com.ibm.brix.common.model.Artifact;
import com.ibm.brix.common.model.ArtifactMap;
import com.ibm.brix.common.model.AttributeProperties;
import com.ibm.brix.common.model.BRoute;
import com.ibm.brix.common.model.GenAttrMap;
import com.ibm.brix.common.model.GenValueMap;
import com.ibm.brix.model.GenericArtifact;


public class Normalizer {

	/* CopyRight */
	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2024 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

	private static final Logger LOG = LoggerFactory.getLogger(Normalizer.class);

	/**
	 * Method:  toGenericArtifact
	 * Converts an Artifact to its Generic Artifact form.
	 * Exchange Expected Input:
	 *    - Header BRIX_BROUTE
	 *    - Body contains the Artifact to be normalized
	 * Exchange Output:
	 *    - (none)
	 * 
	 * @param Exchange exchange (Provided by Camel)
	 * @return GenericArtifact - automatically put onto the Exchange body
	 * @throws BrixException
	 */
	public GenericArtifact toGenericArtifact(Exchange exchange) throws BrixException {
        LOG.info("Normalize an Artifact to a Generic Artifact - Start");

        // The BRoute and Artifact to be normalized should already be stored on the Exchange.
        BRoute broute = exchange.getIn().getHeader(Constants.BRIX_BROUTE, BRoute.class);
        Artifact artifact = exchange.getIn().getBody(Artifact.class);
        if (broute == null) {
        	throw new BrixException("Broute was not found on the Exchange.");
        }
        if (artifact  == null) {
        	throw new BrixException("Artifact was not found on the Exchange.");
        }

        LOG.info("broute: " + broute.toString());

        // When normalizing, we get the artifact's source artifact mappings.
        ArtifactType artifactType = artifact.getArtifactType();
		ArtifactMap sourceArtifactMap = broute.getSourceArtifactMap(artifactType);
		if (sourceArtifactMap == null) {
        	throw new BrixException("The Source Artifact Map configuration for artifact type " + artifactType + " was not found on the BRoute.");
		}
        LOG.info("sourceArtifactMap: " + sourceArtifactMap.toString());
		List<GenAttrMap> genAttrMaps = sourceArtifactMap.getAttributeMaps();

		// Create a new Generic Artifact and load its contents
		GenericArtifact genericArtifact = new GenericArtifact();
		
		// Pass 1 - Loop through all the generic attribute mapping configurations, which contains the list of attributes that need to be created on the Generic Artifact,
		//          and create a new generic attribute to be added to the Generic Artifact.
        LOG.info("Starting Pass 1");
		for (GenAttrMap genAttrMap : genAttrMaps) {
	        LOG.info("genAttrMap: " + genAttrMap.toString());
			// Attempt to use the original value object from the Source Artifact. If it doesn't exist, then the value will be null (for now).
			String attributeName = genAttrMap.getAttributeName();
			AttributeType attributeType = genAttrMap.getAttributeType();
			String genericName = genAttrMap.getGenericName();
			Object value = artifact.getAttributeValue(attributeName);
			LOG.info("Creating generic attribute '" + genericName + "' based on source attribute '" + attributeName + "'");
			
			// Special handling of the source attribute's value to the generic value based on the attribute type.
			switch (attributeType) {
				case DATE:
					LOG.info("Date type found: Converting attribute date value to generic Instant: " + value);
					String pattern = genAttrMap.getAttributeTrait(Trait.PATTERN);
					value = convertToGenericDate(value, pattern);
					break;
				case ENUM:
					LOG.info("Enum type found: Determining generic attribute based on value map for attribute value: " + value);
					value = computeGenericValueFromValueMap(value, genAttrMap.getToGenericValueMap());
					break;
				default:
					break;
			}
			
			// Add the newly formed generic attribute to the Generic Artifact.
			genericArtifact.addAttribute(genericName, value, genAttrMap.getAttributeTraits(), attributeType);
		}
		
		// Pass 2 - Loop through all the new Generic Artifact attributes. If there is an attribute "compose" trait associated with it, this tells us we need to
		// compose the value based on the trait's incantation. Assign this composed value.
        LOG.info("Starting Pass 2");
		Map<String,AttributeProperties> genericAttributes = genericArtifact.getAttributes();
		for (Map.Entry<String, AttributeProperties> genericAttribute : genericAttributes.entrySet()) {
			String composeTrait = genericArtifact.getAttributeTrait(genericAttribute.getKey(), Trait.COMPOSE);
			if (composeTrait != null) {
				String value = composeValue(composeTrait);
				genericArtifact.setAttributeValue(genericAttribute.getKey(), value);
			}
		}

        LOG.info("Created GenericArtifact: " + genericArtifact.toString());

        return genericArtifact;
	}


	/**
	 * Method:  convertToGenericDate
	 * Convert the given source attribute date value into an Instant date object, using the pattern to determine the value's format.
	 * 
	 * @param Object value
	 * @param String pattern
	 * @return Instant The date object representation of the attribute value.
	 * @throws BrixException 
	 */
	private Instant convertToGenericDate(Object value, String pattern) throws BrixException {
		try {
			String dateStr = (String) value.toString();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
			LocalDateTime localDateTime = LocalDateTime.parse(dateStr, formatter);
			ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(Constants.TIMEZONE_UTC)); 
			Instant dateInstant = zonedDateTime.toInstant();
			return dateInstant;
		}
		catch (IllegalArgumentException|DateTimeParseException|ZoneRulesException e) {
	        LOG.error("Date conversion exeption: " + e);
	        throw new BrixException(e);
		}
	}


	/**
	 * Method:  computeGenericValueFromValueMap
	 * Determines the generic value to use for this generic attribute, based on the value map configuration.
	 * 
	 * @param Object value
	 * @param List<GenValueMap> valueMapList
	 * @return String The generic value to use.
	 */
	private String computeGenericValueFromValueMap(Object value, List<GenValueMap> valueMapList) {
		if (value == null) {
			return null;
		}
		String valueStr = (String) value.toString();
		String genericValueStr = valueStr;

		// Look up the current attribute value in the value map list. If found, send back the mapped generic value.
		boolean found = false;
		for (GenValueMap valueMap : valueMapList) {
			// ??? Do we need to presort the list by order?  What does this give us?
			LOG.info("==>Lookup valueStr '" + genericValueStr + "' on value map: " + valueMap.toString());
			// Since we are normalizing a source attribute to a generic attribute, the resource side is 'source'.
			if ((valueMap.getResourceSide() == ResourceSide.SOURCE) && (valueMap.getAttributeValue().equals(valueStr))) {
				genericValueStr = valueMap.getGenericValue();
				found = true;
				break;
			}
		}
		
		if (!found) {
			LOG.warn("Attribute enum value '" + valueStr + "' was not found in the Generic Value Map configuration. Using the original enum value for the generic attribute value.");
		}
		return genericValueStr;
	}


	/**
	 * Method:  composeValue
	 * Using the "compose" trait on the generic attribute, constructs the value for the attribute.
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
