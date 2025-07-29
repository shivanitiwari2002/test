//=============================================================================
//* Name:         RestIncomingTest.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Rest Incoming Route Test
//*                                                                
//* Description:  This class provides tests for the rest incoming route flow\
//*               for the BrIX application.
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
//* Change Log:git 
//* Flag Reason   Date     User Id   Description
//* ---- -------- -------- --------  ------------------------------------------
//*               20231012 tollefso  New source file created.
//*               20241106 tollefso  Split to its own file for Quarkus.
//*                                                                 
//* Additional notes about the Change Activity:
//*    
//*               20241017 luism     Fixing Package Name reference Story 128437                                                             
//=============================================================================
package com.ibm.brix.routes;

import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.camel.CamelContext;
import io.quarkus.test.junit.TestProfile;

import com.ibm.brix.common.CommonConstants;
import com.ibm.brix.Constants;
import com.ibm.brix.common.enums.SystemType;

import com.ibm.brix.TestConstants;
import org.apache.camel.builder.AdviceWith;

import org.jboss.logging.Logger;
import java.util.HashMap;

@QuarkusTest
@TestProfile(RestIncomingTest.class)
public class RestIncomingTest extends CamelQuarkusTestSupport {
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2023 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
    private static final Logger LOG = Logger.getLogger(RestIncomingTest.class);

    private static final String validSimpleEndpointWebhookBody =
                            "{\"pk\":23, \"fields\":{\"id_integer\":\"23\", \"key2\":\"value2\"}}";

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
/*    @Override
    protected Properties useOverridePropertiesWithPropertiesComponent() {
        #overrides any properties specified previously
        Properties prop = new Properties();
        prop.setProperty("brix.host", "127.0.0.1");
        prop.setProperty("brix.port", "8080");
        return prop;
    }
*/

    @Test
    @Transactional
    public void testWebhookValidateGeneric() throws Exception {
        final String ROUTE_TO_TEST = "webhook-validate-generic";

        AdviceWith.adviceWith(context, ROUTE_TO_TEST, a ->
            a.replaceFromWith("direct:start")
        );
        AdviceWith.adviceWith(context, ROUTE_TO_TEST, a ->
            a.weaveAddLast().to("mock:result")
        );

        HashMap<String, Object> headers = new HashMap<>();
        headers.put(Constants.HOST, TestConstants.TEST_ENDPOINT_ADDRESS);
        headers.put(CommonConstants.HEADER_SYSTEMTYPE, SystemType.SIMPLEEP);

        MockEndpoint mockOutput = getMockEndpoint("mock:result");
        mockOutput.expectedMessageCount(1);

        context.start();
        template.sendBodyAndHeaders("direct:start", validSimpleEndpointWebhookBody,
            headers);

        Assertions.assertNotNull(getHeader(mockOutput, Constants.BRIX_TRX_ID));
        MockEndpoint.assertIsSatisfied(context);
        context.stop();
    }

    private Object getHeader(MockEndpoint mock, String header) {
        return mock.getExchanges().get(0).getIn().getHeader(header);
    }

}
