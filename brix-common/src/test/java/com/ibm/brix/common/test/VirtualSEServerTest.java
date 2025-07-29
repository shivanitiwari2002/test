//=============================================================================
//* Name:         VirtualSEServerTest.java
//*
//* Application:  BrIX Service common code
//*
//* Module Name:  VirtualSEServerTest
//*
//* Description:  This class is for testing the VirtualSEServer
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
//*               20250318 tollefso  New source file created.
//*
//* Additional notes about the Change Activity:
//*
//=============================================================================

package com.ibm.brix.common.test;

import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.PropertiesComponent;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assertions;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.ibm.brix.common.test.VirtualSEServer;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.HttpHeaders;

import org.jboss.logging.Logger;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@QuarkusTest
@TestProfile(VirtualSEServerTest.class)
public class VirtualSEServerTest extends CamelQuarkusTestSupport {
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
    private static final Logger LOG = Logger.getLogger(VirtualSEServerTest.class);

    private static VirtualSEServer virtualServer;

    @ConfigProperty(name = "seendpointaddress")
    String seendpointaddress;

    @BeforeAll
    static void setup(){
        //System.out.println("@BeforeAll run");
        virtualServer = new VirtualSEServer();
    }

    @AfterAll
    static void tear(){
        //System.out.println("@AfterAll run");
        if (virtualServer != null) {
            virtualServer.close();
            virtualServer = null;
        }
    }

    @Test
    public void testVirtualSEServer() throws Exception {
        String testURL = seendpointaddress + "/api/issues/1/";
        String expectedResult = VirtualSEServer.SAMPLEISSUE1;
        String result = "";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);

        result = makeGetRequest(testURL, headers);

        Assertions.assertEquals(expectedResult, result);
    }


    private String makeGetRequest(String url, Map<String, String> headers) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        setHeaders(httpGet, headers);

        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);

        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if(statusCode != 200) {
            throw new Exception("Error making GET request: " + statusCode);
        }

        String response = buildResponse(httpResponse);

        httpResponse.close();
        httpClient.close();
        return response;
    }

    private void setHeaders(HttpRequestBase httpMethod, Map<String, String> headers) {
        for (String name : headers.keySet()) {
            httpMethod.setHeader(name, headers.get(name));
        }
    }

    private String buildResponse(CloseableHttpResponse httpResponse) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
        StringBuilder responseBuilder = new StringBuilder(); String line;
        while ((line = reader.readLine()) != null) {
            responseBuilder.append(line);
        }
        String response = responseBuilder.toString();

        reader.close();
        return response;
    }

}