//=============================================================================
//* Name:         NormalizerTest.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Normalizer bean test.
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
//*               20240318 tollefso  New source file created.
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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;

import com.ibm.brix.BrixException;
import com.ibm.brix.Constants;
import com.ibm.brix.beans.Normalizer;
import com.ibm.brix.common.enums.AttributeType;
import com.ibm.brix.common.model.Artifact;
import com.ibm.brix.common.model.AttributeProperties;
import com.ibm.brix.model.BRouteList;
import com.ibm.brix.common.simpleendpoint.model.SEEndpoint;
import com.ibm.brix.utils.Config;

import com.ibm.brix.common.model.BRoute;
import com.ibm.brix.model.GenericArtifact;
import com.ibm.brix.common.simpleendpoint.model.SEArtifact;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.apache.camel.CamelContext;

import org.jboss.logging.Logger;
import java.net.URISyntaxException;


@QuarkusTest
public class NormalizerTest {
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2024 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
    private static final Logger LOG = Logger.getLogger(NormalizerTest.class);

    private static final String GENERIC_ATTRIBUTE_NAME_DESCRIPTION_TEXT = "description_text";
    private static final String GENERIC_ATTRIBUTE_NAME_SEVERITY = "severity";
    private static final String GENERIC_ATTRIBUTE_NAME_UNKNOWN = "unknown";
    private static final String TEST_ENDPOINT_ADDRESS = "http://localhost:8091/";
    private static final String TEST_DESCRIPTION_TEXT_VALUE = "A sample description for a test issue.";
    private static final String TEST_SEVERITY_VALUE = "2";
    private static final String TEST_UNKNOWN_FIELD_VALUE = "value for unknown field";


    @Inject
    CamelContext context;

    @Test
    public void testSimpleHappyData() throws Exception {
        Normalizer normalizer = new Normalizer();
        SEArtifact seArtifact = createTestSEArtifact(TEST_ENDPOINT_ADDRESS);
        BRoute aBRoute = getATestBRoute(seArtifact);

        Exchange exchange = ExchangeBuilder.anExchange(context)
            .withBody(seArtifact)
            .withHeader(Constants.BRIX_BROUTE, aBRoute)
            .withHeader(Constants.BRIX_SOURCE_ARTIFACT, seArtifact)
            .build();

        GenericArtifact genericArtifact = normalizer.toGenericArtifact(
            exchange);
        LOG.info(genericArtifact);

        assertEquals(
            TEST_DESCRIPTION_TEXT_VALUE,
            genericArtifact.getAttributeValue(GENERIC_ATTRIBUTE_NAME_DESCRIPTION_TEXT));
        assertEquals(
            TEST_SEVERITY_VALUE,
            genericArtifact.getAttributeValue(GENERIC_ATTRIBUTE_NAME_SEVERITY));
    }

    @Test
    public void testUnknownField() throws Exception {
        //Unknown field should NOT be added to generic artifact
        Normalizer normalizer = new Normalizer();
        SEArtifact seArtifact = createTestSEArtifact(TEST_ENDPOINT_ADDRESS);
        BRoute aBRoute = getATestBRoute(seArtifact);
        seArtifact.addAttribute(GENERIC_ATTRIBUTE_NAME_UNKNOWN, new AttributeProperties(TEST_UNKNOWN_FIELD_VALUE, AttributeType.UNKNOWN));

        assertEquals(TEST_UNKNOWN_FIELD_VALUE,
            seArtifact.getAttributeValue(GENERIC_ATTRIBUTE_NAME_UNKNOWN));
        Exchange exchange = ExchangeBuilder.anExchange(context)
            .withBody(seArtifact)
            .withHeader(Constants.BRIX_BROUTE, aBRoute)
            .withHeader(Constants.BRIX_SOURCE_ARTIFACT, seArtifact)
            .build();

        GenericArtifact genericArtifact = normalizer.toGenericArtifact(
            exchange);
        LOG.info(genericArtifact);

        assertNotEquals(
            TEST_UNKNOWN_FIELD_VALUE,
            genericArtifact.getAttributeValue(GENERIC_ATTRIBUTE_NAME_DESCRIPTION_TEXT));
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