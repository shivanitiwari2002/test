//=============================================================================
//* Name:         SEExchangeMgr.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Simple Endpoint Exchange Manager
//*                                                                
//* Description:  This class is the Simple Endpoint specific Exchange
//*               Manager for providing services for working with a
//*               Simple Endpoint Camel exchange.
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
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.simpleEndpoint;

import org.apache.camel.Exchange;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONObject;

import com.ibm.brix.BrixException;
import com.ibm.brix.common.CommonConstants;
import com.ibm.brix.common.enums.AttributeType;
import com.ibm.brix.common.enums.SystemType;
import com.ibm.brix.managers.ExchangeMgr;
import com.ibm.brix.common.model.Artifact;
import com.ibm.brix.common.model.AttributeProperties;
import com.ibm.brix.common.simpleendpoint.model.SEEndpoint;
import com.ibm.brix.common.simpleendpoint.model.SEArtifact;

import java.net.URISyntaxException;

public class SEExchangeMgr extends ExchangeMgr {
    /* CopyRight */
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2023 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    private static final Logger LOG = LoggerFactory.getLogger(SEExchangeMgr.class);

    public static final String PAYLOAD_ID_PROP        = "pk";
    private static final String FIELDS_PROPERTY = "fields";

    private String origin;

    /**
     * Constructor
     * 
     * @param Exchange exchange
     * @throws ParseException 
     */
    public SEExchangeMgr(Exchange exchange) throws ParseException {
        super(SystemType.SIMPLEEP, exchange);

        origin = (String)exchange.getIn().getHeader(CommonConstants.HEADER_SIMPLE_ENDPOINT_ORIGIN);
    }

    /**
     * Method:  validHeaders
     * Determines if the headers on the exchange are valid for processing this type of exchange.
     * 
     * @return boolean (true = valid, false = invalid)
     */
    public boolean validHeaders() {
        boolean allHeadersValid = true;

        // Check for the existence of required headers on the exchange
/*
        String event = exchange.getIn().getHeader(Constants.EWM_EVENT, String.class);
        if (event == null) {
            LOG.debug("validHeaders(): Required header '" + Constants.EWM_EVENT + "' missing.");
            allHeadersValid = false;
        }
        else if (!(event.equals(Constants.EWM_EVENT_TYPE))) {       // TODO Make this extensible (enumeration or properties file)
            LOG.debug("validHeaders(): Required header '" + Constants.EWM_EVENT + "' with value '" + event + "' not a valid event.");
            allHeadersValid = false;
        }
*/
        LOG.info("validHeaders(): Simple Endpoint headers are valid!");
        return allHeadersValid;
    }

    /**
     * Method:  validWebhookType
     * Determines if the webhook payload in the exchange is of a type that BrIX
     * is interested in. 
     * 
     * @return boolean (true = valid, false = invalid)
     */
    public boolean validWebhookType() {
        boolean webhookValid = false;

        // Check the payload for valid EWM specific JSON elements
        // TODO
        
        LOG.info("validWebhookType() result: " + webhookValid);
        return webhookValid;
    }


    /**
     * Method:  readArtifactFromPollingExchange
     * Reads through the JSON payload to load the attribute data into an Artifact.
     * 
     * @return Artifact
     * @throws BrixException 
     */
    public Artifact readArtifactFromPollingExchange() throws BrixException {

        LOG.info("Reading polling payload for Simple Endpoint.");
        SEArtifact seArtifact = new SEArtifact();

        if (payload == null) {
            throw new BrixException(String.format("Payload was null."));
        }

        JSONObject fields = payload;

        for (Object key : fields.keySet()) {
            String keyStr = (String)key;
            Object keyValue = fields.get(keyStr);
            seArtifact.addAttribute(keyStr, new AttributeProperties(keyValue, AttributeType.UNKNOWN));
        }
        seArtifact.setArtifactKeyString(seArtifact.getId_integer());
        try {
            seArtifact.setEndpoint(new SEEndpoint(origin));
        }
        catch (URISyntaxException e) {
            throw new BrixException("Malformed URI:" + e.toString());
        }
        LOG.info("SEArtifact:\n" + seArtifact.toString());
        return seArtifact;
    }

    /**
     * Method:  readArtifactFromWebhookExchange
     * Reads through the JSON payload to load the attribute data into an Artifact.
     * 
     * @return Artifact
     * @throws BrixException 
     */
    public Artifact readArtifactFromWebhookExchange() throws BrixException {

        LOG.info("Reading webhook payload for Simple Endpoint.");
        SEArtifact seArtifact = new SEArtifact();
        String msgTemplate = "Invalid Simple Endpoint payload. Required %s property missing from JSON payload.";

        if (payload == null) {
            throw new BrixException(String.format("Payload was null."));
        }
        if ( ! payload.containsKey(PAYLOAD_ID_PROP) ) {
            throw new BrixException(String.format(msgTemplate, PAYLOAD_ID_PROP));
        }
        seArtifact.setArtifactKeyString(Long.toString((Long)payload.get(PAYLOAD_ID_PROP)));
        JSONObject fields = (JSONObject)payload.get(FIELDS_PROPERTY);

        for (Object key : fields.keySet()) {
            String keyStr = (String)key;
            Object keyValue = fields.get(keyStr);
            seArtifact.addAttribute(keyStr, new AttributeProperties(keyValue, AttributeType.UNKNOWN));
        }
        try {
            seArtifact.setEndpoint(new SEEndpoint(origin));
        }
        catch (URISyntaxException e) {
            throw new BrixException("Malformed URI: " + e.toString());
        }
    
        LOG.info("SEArtifact:\n" + seArtifact.toString());
        return seArtifact;
    }

}
