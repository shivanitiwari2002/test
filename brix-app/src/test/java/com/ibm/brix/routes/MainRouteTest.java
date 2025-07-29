//=============================================================================
//* Name:         MainRouteTest.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Main Route Test
//*                                                                
//* Description:  This class provides tests for the main route flow
//*               for the BrIX application.
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
//*               20240312 tollefso  New source file created.
//*               20241106 tollefso  Split to its own file for Quarkus.
//*            36 20250320 carndt    Store Artifact in Exchange body
//*                                                                 
//* Additional notes about the Change Activity:
//*   
//*               20241017 luism     Fixing Package Name reference Story 128437                                                              
//=============================================================================
package com.ibm.brix.routes;

import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import jakarta.inject.Inject;
import org.apache.camel.CamelContext;
import io.quarkus.test.junit.TestProfile;

import com.ibm.brix.Constants;
import com.ibm.brix.TestConstants;
import com.ibm.brix.BrixException;
import com.ibm.brix.common.model.Artifact;
import com.ibm.brix.common.model.BRoute;
import com.ibm.brix.model.BRouteList;
import com.ibm.brix.model.GenericArtifact;
import com.ibm.brix.common.simpleendpoint.model.SEArtifact;
import com.ibm.brix.common.simpleendpoint.model.SEEndpoint;
import com.ibm.brix.utils.Config;
import org.apache.camel.builder.AdviceWith;

import org.jboss.logging.Logger;
import java.util.HashMap;
import java.net.URISyntaxException;

// See updates to test support -> https://camel.apache.org/blog/2024/09/modernizing-test-support/
// CamelQuarkusTestSupport is deprecated - will have to change - unclear what at this time
@QuarkusTest
@TestProfile(MainRouteTest.class)
public class MainRouteTest extends CamelQuarkusTestSupport {
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2024 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
    private static final Logger LOG = Logger.getLogger(MainRouteTest.class);

    @Inject
    CamelContext context;

    @Override
    public boolean isUseAdviceWith() {
        return true;
    }
    @Override
    public boolean isUseRouteBuilder() {
        return false;
    }

    @Test
    public void testDeNormalizeFromGeneric() throws Exception {
        final String ROUTE_TO_TEST = "denormalize-from-generic";

        AdviceWith.adviceWith(context, ROUTE_TO_TEST, a ->
            a.replaceFromWith("direct:start")
        );
        AdviceWith.adviceWith(context, ROUTE_TO_TEST, a ->
            a.weaveByToUri("direct:load-target")
                .replace()
                .to("mock:result")
        );

        MockEndpoint mockOutput = getMockEndpoint("mock:result");

        GenericArtifact genericArtifact = createTestGenericArtifact(TestConstants.TEST_ENDPOINT_ADDRESS);
        SEArtifact sourceArtifact = createTestSEArtifact(TestConstants.TEST_ENDPOINT_ADDRESS);
        //TODO: create the genericArtifact by calling Normalizer
        BRoute aBRoute = getATestBRoute(sourceArtifact);
        HashMap<String, Object> headers = new HashMap<>();
        headers.put(Constants.BRIX_SOURCE_GENERIC_ARTIFACT, genericArtifact);
        headers.put(Constants.BRIX_BROUTE, aBRoute);

        context.start();
        template.sendBodyAndHeaders("direct:start", genericArtifact, headers);

        Object body = getBody(mockOutput);
        Assertions.assertNotEquals(body, null);
        Artifact targetArtifactInBody = (Artifact)body;
        Artifact targetArtifactInHeader = (Artifact)getHeader(mockOutput,
            Constants.BRIX_TARGET_ARTIFACT);

        Assertions.assertEquals(targetArtifactInBody, targetArtifactInHeader);
        MockEndpoint.assertIsSatisfied(context);
        context.stop();
    }

    private GenericArtifact createTestGenericArtifact(String address) {
        GenericArtifact genericArtifact = new GenericArtifact();
        return genericArtifact;
    }

    private SEArtifact createTestSEArtifact(String address) throws URISyntaxException {
        SEArtifact seArtifact = new SEArtifact();
        SEEndpoint endpoint = new SEEndpoint(address);
        seArtifact.setEndpoint(endpoint);
        return seArtifact;
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
