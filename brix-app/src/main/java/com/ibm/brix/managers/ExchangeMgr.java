//=============================================================================
//* Name:         ExchangeMgr.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Exchange Manager
//*                                                                
//* Description:  This abstract class provides services for working with the
//*               Camel exchange, as it pertains to the source system type that
//*               is being processed.
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
//*               20230915 tmueller  New source file created.
//*        129157 20250501 carndt    Switch to BRIX_ENDPOINT_SYSTEM_TYPE header
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.managers;

import org.apache.camel.Exchange;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.brix.BrixException;
import com.ibm.brix.Constants;
import com.ibm.brix.common.CommonConstants;
import com.ibm.brix.common.enums.SystemType;
import com.ibm.brix.factories.ExchangeMgrFactory;
import com.ibm.brix.common.model.Artifact;
import com.ibm.brix.simpleEndpoint.SEExchangeMgr;

public abstract class ExchangeMgr {
	/* CopyRight */
	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2023 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

	private static final Logger LOG = LoggerFactory.getLogger(ExchangeMgr.class);

	private static ExchangeMgrFactory exchangeMgrFactory;
	private SystemType systemType;
	protected Exchange exchange;
    protected JSONObject payload;

	/**
	 * Constructor
	 * 
	 * @param SystemType type
	 * @param Exchange exchange
	 * @throws ParseException 
	 */
    protected ExchangeMgr(SystemType type, Exchange exchange) throws ParseException {
    	this.systemType = type;
    	this.exchange = exchange;
    	//this.payload = this.exchange.getIn().getBody(JSONObject.class);
        String body = exchange.getIn().getBody(String.class);
        JSONParser parser = new JSONParser();
        this.payload = (JSONObject) parser.parse(body);

    	initializeFactories();
    }

	/**
	 * Method:  initializeFactories
	 * Initialize and register with the rule factories. It is here that all Exchange Managers must be registered.
	 */
	private static void initializeFactories() {
		// Create the exchange manager factory and register its exchange managers
		exchangeMgrFactory = new ExchangeMgrFactory();
		exchangeMgrFactory.registerExchangeMgr(SystemType.SIMPLEEP, SEExchangeMgr.class);
	}

	/**
	 * Method:  getExchangeMgr
	 * Determines the appropriate source system type Exchange Manager that is used
	 * for various methods in working with the Camel Exchange.
	 * Exchange Expected Input:
	 * - Header BRIX_ENDPOINT_SYSTEM_TYPE -or- HEADER_SYSTEMTYPE
	 * 
	 * @param Exchange exchange
	 * @return ExchangeMgr
	 */
	public static ExchangeMgr getExchangeMgr(Exchange exchange) throws BrixException {
		ExchangeMgr exchangeMgr = null;
        SystemType systemType = SystemType.UNKNOWN;

        // Since this is a static method, initialize the factories here.
        initializeFactories();

        // Get the system type from the exchange header.
		// CCA Feature 129157: switch from BRIX_SOURCE_SYSTEM_TYPE to BRIX_ENDPOINT_SYSTEM_TYPE
		// If BRIX_ENDPOINT_SYSTEM_TYPE does not return a systemType, check CommonConstants.HEADER_SYSTEMTYPE
        systemType = exchange.getIn().getHeader(Constants.BRIX_ENDPOINT_SYSTEM_TYPE, SystemType.class);
		if (systemType == null) {
			systemType = exchange.getIn().getHeader(CommonConstants.HEADER_SYSTEMTYPE, SystemType.class);
		}
        
        LOG.info("Exchange Manager system type is " + systemType.getName());
        
        // Instantiate the appropriate Exchange Manager for this system type.
        try {
        	exchangeMgr = exchangeMgrFactory.getExchangeMgr(systemType, exchange);
        }
        catch (Exception e) {
        	LOG.error("ERROR: ", e);
			throw new BrixException("Error getting Exchange Manager. " + e);
        }

        return exchangeMgr;
	}

	/**
	 * Method:  getSystemType
	 */
	public SystemType getSystemType() {
		return this.systemType;
	}

	/**
	 * Method:  getPayload
	 */
	public JSONObject getPayload() {
		return this.payload;
	}

	/**
	 * Method:  validHeaders
	 * Determines if the headers on the exchange are valid for processing this type of exchange.
	 * This abstract method must be implemented by subclasses.
	 * 
	 * @param Exchange exchange
	 * @return boolean (true = valid, false = invalid)
	 */
	public abstract boolean validHeaders();

	/**
	 * Method: validWebhookType
	 * Determines if the webhook payload in the exchange is of a type that BrIX
	 * is interested in. 
	 * This abstract method must be implemented by subclasses.
	 * 
	 * @return boolean (true = valid, false = invalid)
	 */
	public abstract boolean validWebhookType();

	/**
	 * Method: readArtifactFromWebhookExchange
	 * Reads through the JSON payload to load the attribute data into an Artifact.
	 * This abstract method must be implemented by subclasses.
	 * 
	 * @return Artifact created with data available from the webhook exchange
	 */
	public abstract Artifact readArtifactFromWebhookExchange() throws BrixException;

	/**
	 * Method: readArtifactFromPollingExchange
	 * Reads through the JSON payload to load the attribute data into an Artifact.
	 * This abstract method must be implemented by subclasses.
	 * 
	 * @return Artifact created with data available from the polling exchange
	 */
	public abstract Artifact readArtifactFromPollingExchange() throws BrixException;
}
