//=============================================================================
//* Name:         MainRouteNormalizeTest.java
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
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
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
import java.net.URISyntaxException;
import java.util.HashMap;

@QuarkusTest
@TestProfile(MainRouteNormalizeTest.class)
public class MainRouteNormalizeTest extends CamelQuarkusTestSupport {
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2024 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
    private static final Logger LOG = Logger.getLogger(MainRouteNormalizeTest.class);

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
    public void testNormalizeToGeneric() throws Exception {
        final String ROUTE_TO_TEST = "normalize-to-generic";


        AdviceWith.adviceWith(context, ROUTE_TO_TEST, a ->
            a.replaceFromWith("direct:start")
        );
        AdviceWith.adviceWith(context, ROUTE_TO_TEST, a ->
            a.weaveByToUri("direct:determine-in-domain-path")
                .replace()
                .to("mock:result")
        );

        MockEndpoint mockOutput = getMockEndpoint("mock:result");

        SEArtifact artifact = createTestSEArtifact(TestConstants.TEST_ENDPOINT_ADDRESS);
        BRoute aBRoute = getATestBRoute(artifact);
        HashMap<String, Object> headers = new HashMap<>();
        headers.put(Constants.BRIX_SOURCE_ARTIFACT, artifact);
        headers.put(Constants.BRIX_BROUTE, aBRoute);

        context.start();
        template.sendBodyAndHeaders("direct:start", aBRoute, headers);

        Object body = getBody(mockOutput);
        Assertions.assertNotEquals(body, null);
        Artifact genericArtifactInBody = (Artifact)body;
        Artifact genericArtifactInHeader = (Artifact)getHeader(mockOutput,
            Constants.BRIX_SOURCE_GENERIC_ARTIFACT);

        Assertions.assertEquals(genericArtifactInBody, genericArtifactInHeader);
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
