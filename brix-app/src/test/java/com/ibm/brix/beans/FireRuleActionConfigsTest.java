//=============================================================================
//* Name:         FireRuleActionConfigsTest.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Fire Rule Action Configs bean test.
//*
//* Description:  This class provides tests for the FireRuleActionConfigs bean.
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
//*        129429 20250429 tmueller  New source file created.
//*
//* Additional notes about the Change Activity:
//=============================================================================
package com.ibm.brix.beans;

import java.util.HashMap;
import java.util.List;
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
import com.ibm.brix.common.enums.ActionCommand;
import com.ibm.brix.common.enums.AttributeType;
import com.ibm.brix.model.Action;
import com.ibm.brix.model.ActionList;
import com.ibm.brix.common.model.Artifact;
import com.ibm.brix.model.ArtifactRelationshipList;
import com.ibm.brix.common.model.AttributeProperties;
import com.ibm.brix.common.model.BRoute;
import com.ibm.brix.model.BRouteList;
import com.ibm.brix.model.GenericArtifact;
import com.ibm.brix.common.model.Rule;
import com.ibm.brix.model.RuleList;
import com.ibm.brix.common.simpleendpoint.model.SEArtifact;
import com.ibm.brix.common.simpleendpoint.model.SEEndpoint;
import com.ibm.brix.utils.Config;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.net.URISyntaxException;

@QuarkusTest
public class FireRuleActionConfigsTest {
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
    private static final Logger LOG = Logger.getLogger(FireRuleActionConfigsTest.class);

    private static final String TEST_ENDPOINT_ADDRESS = "http://localhost:8091/";
    private static final String TEST_ID_INTEGER = "1";
    private static final String TEST_DESCRIPTION_TEXT_VALUE = "A sample description for a test issue.";
    private static final String CREATE_RULE_NAME = "Create artifact";
    private static final String GENERIC_ATTRIBUTE_NAME_DESCRIPTION_TEXT = "description_text";
    private static final String GENERIC_ATTRIBUTE_NAME_SEVERITY = "severity";
    private static final String GENERIC_ATTRIBUTE_NAME_INTERNAL_ONLY = "internal_only";
    private static final String GENERIC_ATTRIBUTE_NAME_STATUS = "status";

    private FireRuleActionConfigs fireRuleActionConfigs;
    private SEArtifact seArtifact;
    private GenericArtifact deltaArtifact;
    private RuleList assertedRuleList;
    private BRoute aBRoute;
    private ArtifactRelationshipList artifactRelationshipList;

    
    @Inject
    CamelContext context;

    @BeforeEach
    public void setUp() throws BrixException {
    	// Notes on test data configuration setup:
    	// - This junit test uses the Brix Broutes Config file to load a BRoute and ArtifactRelationshipList for us.
    	fireRuleActionConfigs = new FireRuleActionConfigs();
    	// Setup mock objects
    	deltaArtifact = new GenericArtifact();
        seArtifact = createTestSEArtifact(TEST_ID_INTEGER, TEST_ENDPOINT_ADDRESS);
        aBRoute = getATestBRoute(seArtifact);
    	assertedRuleList = getAssertedRules(aBRoute);
        artifactRelationshipList = getTestArtifactRelationshipList(seArtifact);
    	deltaArtifact = buildDeltaGenericArtifact();
    }

    @Test
    public void testActionsGeneratedForSync() throws Exception {
        LOG.info("Running 'testActionsGeneratedForSync'");

        // The Rules as loaded into the assertedRuleList from the Brix BRoutes Config file contains a "Create Artifact" Rule.
        // For this sync test, we must first remove that Rule to simulate that this Rule did not fire (not in the assertedRuleList).
        int index = 0;
        for (Rule rule : assertedRuleList) {
        	if (rule.getName().equals(CREATE_RULE_NAME)) {
        		break;
        	}
        	index++;
        }
        if (index <= assertedRuleList.size()) {
        	assertedRuleList.remove(index);
        }
        LOG.info("Processing assertedRuleList size = " + assertedRuleList.size());
        
        Exchange exchange = ExchangeBuilder.anExchange(context)
                .withBody(seArtifact)
                .withHeader(Constants.BRIX_BROUTE, aBRoute)
                .withHeader(Constants.BRIX_ARTIFACT_RELATIONSHIPS, artifactRelationshipList)
                .withHeader(Constants.BRIX_ASSERTED_RULES, assertedRuleList)
                .withHeader(Constants.BRIX_DELTA_GENERIC_ARTIFACT, deltaArtifact)
                .build();

        ActionList actions = fireRuleActionConfigs.buildActions(exchange);
        LOG.info("Result: actions.size = " + actions.size());

        // Assertions to make
        Assertions.assertTrue(actions.size() == 3);
        boolean foundCreateArtifactAction = false;
        boolean foundModifyAttributeAction = false;
        boolean foundChangeStateAction = false;
        for (Action action : actions) {
        	if (action.getActionCommand() == ActionCommand.CREATEARTIFACT) {
        		foundCreateArtifactAction = true;
        	}
        	else if (action.getActionCommand() == ActionCommand.CHANGESTATE) {
        		foundChangeStateAction = true;
        	}
        	else if (action.getActionCommand() == ActionCommand.MODIFYATTRIBUTE) {
        		foundModifyAttributeAction = true;
        	}
        }
        Assertions.assertFalse(foundCreateArtifactAction);
        Assertions.assertTrue(foundChangeStateAction);
        Assertions.assertTrue(foundModifyAttributeAction);
    }

    @Test
    public void testActionsGeneratedForCreate() throws Exception {
        LOG.info("Running 'testActionsGeneratedForCreate'");

        // The Rules as loaded into the assertedRuleList from the Brix BRoutes Config file already contains a "Create Artifact" Rule.
        LOG.info("Processing assertedRuleList size = " + assertedRuleList.size());

        Exchange exchange = ExchangeBuilder.anExchange(context)
                .withBody(seArtifact)
                .withHeader(Constants.BRIX_CREATE_TARGET, Constants.TRUE)
                .withHeader(Constants.BRIX_BROUTE, aBRoute)
                .withHeader(Constants.BRIX_ARTIFACT_RELATIONSHIPS, artifactRelationshipList)
                .withHeader(Constants.BRIX_ASSERTED_RULES, assertedRuleList)
                .withHeader(Constants.BRIX_DELTA_GENERIC_ARTIFACT, deltaArtifact)
                .build();

        ActionList actions = fireRuleActionConfigs.buildActions(exchange);
        LOG.info("Result: actions.size = " + actions.size());

        // Assertions to make
        Assertions.assertTrue(actions.size() == 3);
        boolean foundCreateArtifactAction = false;
        boolean foundModifyAttributeAction = false;
        boolean foundChangeStateAction = false;
        for (Action action : actions) {
        	if (action.getActionCommand() == ActionCommand.CREATEARTIFACT) {
        		foundCreateArtifactAction = true;
        	}
        	else if (action.getActionCommand() == ActionCommand.CHANGESTATE) {
        		foundChangeStateAction = true;
        	}
        	else if (action.getActionCommand() == ActionCommand.MODIFYATTRIBUTE) {
        		foundModifyAttributeAction = true;
        	}
        }
        Assertions.assertTrue(foundCreateArtifactAction);
        Assertions.assertTrue(foundChangeStateAction);
        Assertions.assertTrue(foundModifyAttributeAction);
    }

    private SEArtifact createTestSEArtifact(String id, String address) throws BrixException {
        try {
            SEArtifact seArtifact = new SEArtifact();
            SEEndpoint seEndpoint = new SEEndpoint(address);
            seArtifact.setEndpoint(seEndpoint);
            seArtifact.setId_integer(id);
            seArtifact.setDescription(TEST_DESCRIPTION_TEXT_VALUE);
            return seArtifact;
        } catch(URISyntaxException e) {
            throw new BrixException("URISyntaxException:" + e);
        }
    }

    private BRoute getATestBRoute(Artifact artifact) throws BrixException {
        BRouteList brouteList = Config.findMatchingBRoutes(artifact);
        BRoute aBRoute = brouteList.getTheBRouteList().get(0);
        return aBRoute;
    }

    // Note that this method just retrieves all Rules from the BRoute, simulating all Rules were asserted to be true.
    // For this JUNIT test, we're just after the RuleActionConfigs on the Rules that are used by the FireRuleActionConfigs bean.
    private RuleList getAssertedRules(BRoute broute) throws BrixException {
        List<Rule> brouteRules = broute.getRules();
        RuleList ruleList = new RuleList();
        for (Rule rule : brouteRules) {
        	ruleList.append(rule);
        }
        return ruleList;
    }

    private ArtifactRelationshipList getTestArtifactRelationshipList(Artifact artifact) throws BrixException {
        try {
            return Config.findMatchingArtifactRelationship(artifact);
        } catch (Exception e) {
            throw new BrixException("getTestArtifactRelationshipList: " + e.getMessage());
        }

    }

    private GenericArtifact buildDeltaGenericArtifact() throws BrixException {
    	GenericArtifact deltaArtifact = new GenericArtifact();
    	Map<String, AttributeProperties> attributes = new HashMap<>();
    	AttributeProperties descrText = new AttributeProperties("Test Issue - Fire Rule Action Configs", AttributeType.STRING);
        AttributeProperties severity = new AttributeProperties(1, AttributeType.LONG);
        AttributeProperties internal_only = new AttributeProperties("false", AttributeType.STRING);
        AttributeProperties status = new AttributeProperties("closed", AttributeType.STRING);
        attributes.put(GENERIC_ATTRIBUTE_NAME_DESCRIPTION_TEXT, descrText);
        attributes.put(GENERIC_ATTRIBUTE_NAME_SEVERITY, severity);
        attributes.put(GENERIC_ATTRIBUTE_NAME_INTERNAL_ONLY, internal_only);
        attributes.put(GENERIC_ATTRIBUTE_NAME_STATUS, status);
        deltaArtifact.setAttributes(attributes);
        return deltaArtifact;
    }
} 
