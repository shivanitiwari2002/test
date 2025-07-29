//=============================================================================
//* Name:         RestIncomingValidateTest.java
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
//* Change Log:
//* Flag Reason   Date     User Id   Description
//* ---- -------- -------- --------  ------------------------------------------
//*               20231012 tollefso  New source file created.
//*               20241106 tollefso  Split to its own file for Quarkus.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.routes;

import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import jakarta.inject.Inject;
import org.apache.camel.CamelContext;
import io.quarkus.test.junit.TestProfile;


import com.ibm.brix.Constants;
import org.apache.camel.builder.AdviceWith;

import org.jboss.logging.Logger;

@QuarkusTest
@TestProfile(RestIncomingValidateTest.class)
public class RestIncomingValidateTest extends CamelQuarkusTestSupport {
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2023 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
    private static final Logger LOG = Logger.getLogger(RestIncomingValidateTest.class);

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
    public void testWebhookValidate() throws Exception {

        AdviceWith.adviceWith(context, "webhook-validate", a ->
            a.replaceFromWith("direct:start")
        );
        AdviceWith.adviceWith(context, "webhook-validate", a ->
            a.weaveAddLast().to("mock:result")
        );

        MockEndpoint mockOutput = getMockEndpoint("mock:result");
        mockOutput.expectedBodiesReceived(validSimpleEndpointWebhookBody);
        mockOutput.expectedHeaderReceived(Constants.BRIX_VALID_WEBHOOK,
            Constants.TRUE);

        context.start();
        template.sendBody("direct:start", validSimpleEndpointWebhookBody);

        MockEndpoint.assertIsSatisfied(context);
        context.stop();
    }
}
