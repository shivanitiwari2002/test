//=============================================================================
//* Name:         DenormalizerTest.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Denormalizer bean test.
//*
//* Description:  This class provides tests for the Normalizer bean.
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
//*               20240321 tollefso  New source file created.
//*               20241106 tollefso  Converted to Quarkus.
//*            36 20250320 carndt    Store Artifact in Exchange body
//*
//* Additional notes about the Change Activity:
//*
//*               20241017 luism     Fixing Package Name reference Story 128437
//=============================================================================
package com.ibm.brix.beans;

import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;

import com.ibm.brix.BrixException;
import com.ibm.brix.Constants;
import com.ibm.brix.beans.Denormalizer;
import com.ibm.brix.beans.Normalizer;
import com.ibm.brix.common.enums.AttributeType;
import com.ibm.brix.common.model.Artifact;
import com.ibm.brix.model.BRouteList;
import com.ibm.brix.common.model.AttributeProperties;
import com.ibm.brix.common.simpleendpoint.model.SEEndpoint;
import com.ibm.brix.utils.Config;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.apache.camel.CamelContext;
import com.ibm.brix.common.model.BRoute;
import com.ibm.brix.model.GenericArtifact;
import com.ibm.brix.common.simpleendpoint.model.SEArtifact;

import org.jboss.logging.Logger;
import java.net.URISyntaxException;

@QuarkusTest
public class DenormalizerTest {
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2024 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
    private static final Logger LOG = Logger.getLogger(DenormalizerTest.class);

    private static final String GENERIC_ATTRIBUTE_NAME_UNKNOWN = "unknown";
    private static final String TEST_UNKNOWN_FIELD_VALUE = "value for unknown field";
    private static final String SEARTIFACT_ATTRIBUTE_NAME_DESCRIPTION_TEXT = "description_text";
    private static final String SEARTIFACT_ATTRIBUTE_NAME_SEVERITY = "severity";
    private static final String TEST_ENDPOINT_ADDRESS = "http://localhost:8091/";
    private static final String TEST_DESCRIPTION_TEXT_VALUE = "A sample description for a test issue.";
    private static final String TEST_SEVERITY_VALUE = "2";

    @Inject
    CamelContext context;

    @Test
    public void testSimpleHappyData() throws Exception {
        SEArtifact seArtifact = createTestSEArtifact(TEST_ENDPOINT_ADDRESS);
        BRoute aBRoute = getATestBRoute(seArtifact);
        GenericArtifact genericArtifact = createTestGenericArtifact(aBRoute, seArtifact);
        Denormalizer denormalizer = new Denormalizer();

        Exchange exchange = ExchangeBuilder.anExchange(context)
            .withBody(genericArtifact)
            .withHeader(Constants.BRIX_BROUTE, aBRoute)
            .withHeader(Constants.BRIX_TARGET_GENERIC_ARTIFACT, genericArtifact)
            .build();

        Artifact seArtifactFromDenormalizer = (SEArtifact)denormalizer.fromGenericArtifact(exchange);
        LOG.info("genericArtifact = " + genericArtifact);
        LOG.info("seArtifact = " + seArtifact);
        LOG.info("seArtifactFromDenormalizer = " + seArtifactFromDenormalizer);

        assertEquals(
            seArtifact.getAttributeValue(SEARTIFACT_ATTRIBUTE_NAME_DESCRIPTION_TEXT),
            seArtifactFromDenormalizer.getAttributeValue(SEARTIFACT_ATTRIBUTE_NAME_DESCRIPTION_TEXT));
        assertEquals(
            seArtifact.getAttributeValue(SEARTIFACT_ATTRIBUTE_NAME_SEVERITY),
            seArtifactFromDenormalizer.getAttributeValue(SEARTIFACT_ATTRIBUTE_NAME_SEVERITY));
    }

    @Test
    public void testUnknownField() throws Exception {
        // check that unknown attribute was not put into denormalized artifact
        SEArtifact seArtifact = createTestSEArtifact(TEST_ENDPOINT_ADDRESS);
        BRoute aBRoute = getATestBRoute(seArtifact);
        GenericArtifact genericArtifact = createTestGenericArtifact(aBRoute, seArtifact);
        Denormalizer denormalizer = new Denormalizer();
        AttributeProperties genAttrProperties = new AttributeProperties(TEST_UNKNOWN_FIELD_VALUE, AttributeType.UNKNOWN);
       // genAttrProperties.setAttributeValue(TEST_UNKNOWN_FIELD_VALUE);
        genericArtifact.addAttribute(GENERIC_ATTRIBUTE_NAME_UNKNOWN, genAttrProperties);

        Exchange exchange = ExchangeBuilder.anExchange(context)
            .withBody(genericArtifact)
            .withHeader(Constants.BRIX_BROUTE, aBRoute)
            .withHeader(Constants.BRIX_TARGET_GENERIC_ARTIFACT, genericArtifact)
            .build();

        Artifact seArtifactFromDenormalizer = (SEArtifact)denormalizer.fromGenericArtifact(exchange);

        assertEquals(
            null,
            seArtifactFromDenormalizer.getAttributeValue(GENERIC_ATTRIBUTE_NAME_UNKNOWN));
        assertEquals(
            seArtifact.getAttributeValue(SEARTIFACT_ATTRIBUTE_NAME_DESCRIPTION_TEXT),
            seArtifactFromDenormalizer.getAttributeValue(SEARTIFACT_ATTRIBUTE_NAME_DESCRIPTION_TEXT));
        assertEquals(
            seArtifact.getAttributeValue(SEARTIFACT_ATTRIBUTE_NAME_SEVERITY),
            seArtifactFromDenormalizer.getAttributeValue(SEARTIFACT_ATTRIBUTE_NAME_SEVERITY));
    }

    @Test
    public void testNoMatchingBRoute() throws Exception {
        SEArtifact seArtifact = createTestSEArtifact(TEST_ENDPOINT_ADDRESS);

        BRoute aBRoute = getATestBRoute(seArtifact);
        GenericArtifact genericArtifact = createTestGenericArtifact(aBRoute, seArtifact);
        Denormalizer denormalizer = new Denormalizer();

        Exchange exchange = ExchangeBuilder.anExchange(context)
            .withBody(genericArtifact)
            .withHeader(Constants.BRIX_BROUTE, null)
            .withHeader(Constants.BRIX_TARGET_GENERIC_ARTIFACT, genericArtifact)
            .build();

        try {
            Artifact seArtifactFromDenormalizer = (SEArtifact)denormalizer.fromGenericArtifact(exchange);
            fail("BRoute should NOT have been found.");
        } catch (Exception e) {
            assertEquals(1,1);
        }
    }


     private GenericArtifact createTestGenericArtifact(BRoute aBRoute, SEArtifact seArtifact) throws Exception {
        Exchange exchange = ExchangeBuilder.anExchange(context)
            .withBody(seArtifact)
            .withHeader(Constants.BRIX_BROUTE, aBRoute)
            .withHeader(Constants.BRIX_SOURCE_ARTIFACT, seArtifact)
            .build();
        Normalizer normalizer = new Normalizer();

        GenericArtifact genericArtifact = normalizer.toGenericArtifact(exchange);

        return genericArtifact;
    }

    private SEArtifact createTestSEArtifact(String address) throws BrixException {
        try {
            SEArtifact seArtifact = new SEArtifact();
            SEEndpoint seEndpoint = new SEEndpoint(address);
            seArtifact.setEndpoint(seEndpoint);
            seArtifact.setDescription(TEST_DESCRIPTION_TEXT_VALUE);
            seArtifact.setSeverity(TEST_SEVERITY_VALUE);
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

    private Object getBody(MockEndpoint mock) {
        return mock.getExchanges().get(0).getIn().getBody();
    }

    private Object getHeader(MockEndpoint mock, String header) {
        return mock.getExchanges().get(0).getIn().getHeader(header);
    }
}