//=============================================================================
//* Name:         AssertRuleConditionsTest.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Assert Rule Conditions bean test.
//*
//* Description:  This class provides tests for the AssertRuleConditions bean.
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
//*        129161 20250404 tmueller  New source file created.
//*
//* Additional notes about the Change Activity:
//=============================================================================
package com.ibm.brix.beans;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ibm.brix.BrixException;
import com.ibm.brix.Constants;
import com.ibm.brix.beans.AssertRuleConditions;
import com.ibm.brix.common.enums.AttributeType;
import com.ibm.brix.common.model.Artifact;
import com.ibm.brix.common.model.AttributeProperties;
import com.ibm.brix.common.model.BRoute;
import com.ibm.brix.model.BRouteList;
import com.ibm.brix.model.GenericArtifact;
import com.ibm.brix.common.model.Rule;
import com.ibm.brix.common.simpleendpoint.model.SEArtifact;
import com.ibm.brix.common.simpleendpoint.model.SEEndpoint;
import com.ibm.brix.model.RuleList;
import com.ibm.brix.utils.Config;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.net.URISyntaxException;

@QuarkusTest
public class AssertRuleConditionsTest {
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
    private static final Logger LOG = Logger.getLogger(AssertRuleConditionsTest.class);

    private static final String TEST_ENDPOINT_ADDRESS = "http://localhost:8091/";
    private static final String TEST_DESCRIPTION_TEXT_VALUE = "A sample description for a test issue.";
    private static final String GENERIC_ATTRIBUTE_NAME_DESCRIPTION_TEXT = "description_text";
    private static final String GENERIC_ATTRIBUTE_NAME_SEVERITY = "severity";
    private static final String GENERIC_ATTRIBUTE_NAME_INTERNAL_ONLY = "internal_only";

    private AssertRuleConditions assertRuleConditions;
    private GenericArtifact sourceGenericArtifact;
    private GenericArtifact targetGenericArtifact;
    private GenericArtifact deltaArtifact;
    private SEArtifact seArtifact;
    private BRoute aBRoute;
    private Map<String, AttributeProperties> attributes;

    @Inject
    CamelContext context;

    @BeforeEach
    public void setUp() throws BrixException {
    	// Notes on test data configuration setup:
    	// - This junit test uses the Brix Broutes Config file to load a BRoute for us.
    	// - One of the RuleCondition tests uses the MATCHREGEX RuleOpertor with the regex pattern: (?:^|\\s)(?i)EWM-([0-9]+)(?:$|\\s)|\\((?i)EWM-([0-9]+)\\)
    	//   Loosely, this checks if the subject value starts with text "EWM-nnnn" or ends in text "(EWM-nnn)", where nnn is a number.
    	assertRuleConditions = new AssertRuleConditions();
    	// Setup mock objects
    	sourceGenericArtifact = new GenericArtifact();
    	targetGenericArtifact = new GenericArtifact();
    	deltaArtifact = new GenericArtifact();
        seArtifact = createTestSEArtifact(TEST_ENDPOINT_ADDRESS);
        aBRoute = getATestBRoute(seArtifact);
        attributes = new HashMap<>();        
    }

    @Test
    public void testRuleAssertConditionsFired() throws Exception {
        LOG.info("Running 'testRuleAssertConditionsFired'");

        // Setup mock Artifact attributes that will cause all defined Rule Conditions to assert to true.
    	AttributeProperties descrText = new AttributeProperties("Test Issue - Assert Conditions test (EWM-999)", AttributeType.STRING);
        AttributeProperties severity = new AttributeProperties(1, AttributeType.LONG);
        AttributeProperties internal_only = new AttributeProperties("false", AttributeType.STRING);
        attributes.put(GENERIC_ATTRIBUTE_NAME_DESCRIPTION_TEXT, descrText);
        attributes.put(GENERIC_ATTRIBUTE_NAME_SEVERITY, severity);
        deltaArtifact.setAttributes(attributes);

        attributes.put(GENERIC_ATTRIBUTE_NAME_INTERNAL_ONLY, internal_only);
        sourceGenericArtifact.setAttributes(attributes);
        targetGenericArtifact.setAttributes(attributes);

        Exchange exchange = ExchangeBuilder.anExchange(context)
                .withBody(seArtifact)
                .withHeader(Constants.BRIX_DELTAS_EXIST, Constants.TRUE)
                .withHeader(Constants.BRIX_BROUTE, aBRoute)
                .withHeader(Constants.BRIX_SOURCE_GENERIC_ARTIFACT, sourceGenericArtifact)
                .withHeader(Constants.BRIX_TARGET_GENERIC_ARTIFACT, targetGenericArtifact)
                .withHeader(Constants.BRIX_DELTA_GENERIC_ARTIFACT, deltaArtifact)
                .build();

        RuleList rulesToBeFired = assertRuleConditions.assertRuleConditions(exchange);
        LOG.info("Result: rulesToBeFired.size = " + rulesToBeFired.size());

        // Assertions to make
        Assertions.assertEquals(exchange.getIn().getHeader(Constants.BRIX_ASSERTED_RULES_EXIST), Constants.TRUE);
        Assertions.assertTrue(rulesToBeFired.size() > 0);
    }
    
    @Test
    public void testRuleAssertConditionsNotFired() throws Exception {
        LOG.info("Running 'testRuleAssertConditionsNotFired'");

        // Setup mock delta Artifact attributes that will cause all defined Rule Conditions to assert to false.
    	AttributeProperties descrText = new AttributeProperties("Test Issue - Assert Conditions test", AttributeType.STRING);
        AttributeProperties severity = new AttributeProperties(4, AttributeType.LONG);
        AttributeProperties internal_only = new AttributeProperties("true", AttributeType.STRING);
        attributes.put(GENERIC_ATTRIBUTE_NAME_DESCRIPTION_TEXT, descrText);
        attributes.put(GENERIC_ATTRIBUTE_NAME_SEVERITY, severity);
        deltaArtifact.setAttributes(attributes);

        attributes.put(GENERIC_ATTRIBUTE_NAME_INTERNAL_ONLY, internal_only);
        sourceGenericArtifact.setAttributes(attributes);
        targetGenericArtifact.setAttributes(attributes);

        Exchange exchange = ExchangeBuilder.anExchange(context)
                .withBody(seArtifact)
                .withHeader(Constants.BRIX_DELTAS_EXIST, Constants.TRUE)
                .withHeader(Constants.BRIX_BROUTE, aBRoute)
                .withHeader(Constants.BRIX_SOURCE_GENERIC_ARTIFACT, sourceGenericArtifact)
                .withHeader(Constants.BRIX_TARGET_GENERIC_ARTIFACT, targetGenericArtifact)
                .withHeader(Constants.BRIX_DELTA_GENERIC_ARTIFACT, deltaArtifact)
                .build();

        RuleList rulesToBeFired = assertRuleConditions.assertRuleConditions(exchange);
        LOG.info("Result: rulesToBeFired.size = " + rulesToBeFired.size());

        // Assertions to make
        Assertions.assertEquals(exchange.getIn().getHeader(Constants.BRIX_ASSERTED_RULES_EXIST), Constants.FALSE);
        Assertions.assertTrue(rulesToBeFired.size() == 0);
    }

    private SEArtifact createTestSEArtifact(String address) throws BrixException {
        try {
            SEArtifact seArtifact = new SEArtifact();
            SEEndpoint seEndpoint = new SEEndpoint(address);
            seArtifact.setEndpoint(seEndpoint);
            seArtifact.setDescription(TEST_DESCRIPTION_TEXT_VALUE);
            return seArtifact;
        } catch (URISyntaxException e) {
            throw new BrixException("Malformed URL");
        }
    }

    private BRoute getATestBRoute(Artifact artifact) throws BrixException {
        BRouteList brouteList = Config.findMatchingBRoutes(artifact);
        BRoute aBRoute = brouteList.getTheBRouteList().get(0);
        return aBRoute;
    }
}
