//=============================================================================
//* Name:         PrepArtifactExtractTest.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX PrepArtifactExtract bean test.
//*
//* Description:  This class provides tests for the PrepArtifactExtract bean.
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
//*        129157 20250409 carndt    New source file created.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.beans;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ibm.brix.common.test.VirtualSEServer;
import com.ibm.brix.common.enums.ResourceSide;
import com.ibm.brix.common.enums.SystemType;
import com.ibm.brix.BrixException;
import com.ibm.brix.Constants;
import com.ibm.brix.common.model.Artifact;
import com.ibm.brix.model.ArtifactRelationshipList;
import com.ibm.brix.common.model.BRoute;
import com.ibm.brix.model.BRouteList;
import com.ibm.brix.common.simpleendpoint.model.SEArtifact;
import com.ibm.brix.common.simpleendpoint.model.SEEndpoint;
import com.ibm.brix.utils.Config;

import io.quarkus.test.junit.QuarkusTest;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

@QuarkusTest
@Transactional
public class PrepArtifactExtractTest {
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
    private static final Logger LOG = Logger.getLogger(PrepArtifactExtractTest.class);

    private static final String TEST_SOURCE_ENDPOINT_ADDRESS = "http://localhost:8000/";
    private static final String TEST_SOURCE_ENDPOINT_NAME = "Source Test Name";
    private static final String TEST_TARGET_ENDPOINT_ADDRESS = "http://localhost:8001/";
    private static final String TEST_TARGET_ENDPOINT_NAME = "Target Test Name";
    private static final String TEST_SOURCE_ID_INTEGER = "1";
    private static final String TEST_TARGET_ID_INTEGER = "1";

    private static VirtualSEServer virtualServer;

    // private static final Logger LOG = Logger.getLogger(PrepArtifactExtractTest.class);

    @Inject
    CamelContext context;

    @BeforeAll
    static void setup() {
        virtualServer = new VirtualSEServer();
    }

    @AfterAll
    static void tear() {
        if (virtualServer != null) {
            virtualServer.close();
            virtualServer = null;
        }
    }

    @Test
    public void testPrepSourceArtifactExtract() throws Exception {
        PrepArtifactExtract prepArtifactExtract = new PrepArtifactExtract();

        SEArtifact seArtifact = createTestSEArtifact(TEST_SOURCE_ID_INTEGER, TEST_SOURCE_ENDPOINT_ADDRESS);
        BRoute aBRoute = getATestBRoute(seArtifact);
        ArtifactRelationshipList artifactRelationshipList = getTestArtifactRelationshipList(seArtifact);

        Exchange exchange = ExchangeBuilder.anExchange(context)
            .withBody(seArtifact)
            .withHeader(Constants.BRIX_BROUTE, aBRoute)
            .withHeader(Constants.BRIX_ARTIFACT_RELATIONSHIPS, artifactRelationshipList)
            .withHeader(Constants.BRIX_EXTRACT_RESOURCE_SIDE, ResourceSide.SOURCE)
            .build();

        String extractKey = prepArtifactExtract.prepArtifactExtract(exchange);
        Assertions.assertNotNull(extractKey);
        Assertions.assertEquals(extractKey, TEST_SOURCE_ID_INTEGER);
        String endpointExtractURI = exchange.getIn().getHeader(Constants.BRIX_ENDPOINT_EXTRACT_URI).toString();
        Assertions.assertTrue(endpointExtractURI.contains(TEST_SOURCE_ENDPOINT_ADDRESS));
    }

    @Test
    public void testPrepTargetArtifactExtract() throws Exception {
        PrepArtifactExtract prepArtifactExtract = new PrepArtifactExtract();

        SEArtifact seArtifact = createTestSEArtifact(TEST_SOURCE_ID_INTEGER, TEST_SOURCE_ENDPOINT_ADDRESS);
        BRoute aBRoute = getATestBRoute(seArtifact);
        ArtifactRelationshipList artifactRelationshipList = getTestArtifactRelationshipList(seArtifact);

        Exchange exchange = ExchangeBuilder.anExchange(context)
            .withBody(seArtifact)
            .withHeader(Constants.BRIX_BROUTE, aBRoute)
            .withHeader(Constants.BRIX_ARTIFACT_RELATIONSHIPS, artifactRelationshipList)
            .withHeader(Constants.BRIX_EXTRACT_RESOURCE_SIDE, ResourceSide.TARGET)
            .build();

        String extractKey = prepArtifactExtract.prepArtifactExtract(exchange);
        Assertions.assertNotNull(extractKey);
        Assertions.assertEquals(extractKey, TEST_TARGET_ID_INTEGER);
        String endpointExtractURI = exchange.getIn().getHeader(Constants.BRIX_ENDPOINT_EXTRACT_URI).toString();
        Assertions.assertTrue(endpointExtractURI.contains(TEST_TARGET_ENDPOINT_ADDRESS));
    }

    private SEArtifact createTestSEArtifact(String id, String address) throws Exception {
        SEArtifact seArtifact = new SEArtifact();
        SEEndpoint seEndpoint = new SEEndpoint(address);
        seEndpoint.setName("TEST_SOURCE_ENDPOINT_NAME");
        seEndpoint.setSystemType(SystemType.SIMPLEEP);
        seEndpoint.persist();
        seArtifact.setEndpoint(seEndpoint);
        seArtifact.setId_integer(id);
        seArtifact.persist();
        return seArtifact;
    }

    private BRoute getATestBRoute(Artifact artifact) throws BrixException {
        try {
            BRouteList brouteList = Config.findMatchingBRoutes(artifact);
            BRoute aBRoute = brouteList.getTheBRouteList().get(0);
            return aBRoute;
        } catch (Exception e) {
            throw new BrixException("getATestBRoute: " + e.getMessage());
        }
    }

    private ArtifactRelationshipList getTestArtifactRelationshipList(Artifact artifact) throws BrixException {
        try {
            return Config.findMatchingArtifactRelationship(artifact);
        } catch (Exception e) {
            throw new BrixException("getTestArtifactRelationship: " + e.getMessage());
        }
        
    }
}
