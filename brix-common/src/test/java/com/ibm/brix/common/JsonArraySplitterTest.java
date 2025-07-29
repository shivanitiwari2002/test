//=============================================================================
//* Name:         JsonArraySplitterTest.java
//*
//* Application:  BrIX Service common code
//*
//* Module Name:  JsonArraySplitterTest
//*
//* Description:  This class is for testing the JsonArraySplitter bean
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
//*               20250311 tollefso  New source file created.
//*
//* Additional notes about the Change Activity:
//*
//=============================================================================

package com.ibm.brix.common;

import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assertions;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

import com.ibm.brix.common.beans.JsonArraySplitter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.jboss.logging.Logger;

@QuarkusTest
@TestProfile(JsonArraySplitterTest.class)
public class JsonArraySplitterTest extends CamelQuarkusTestSupport {
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
    private static final Logger LOG = Logger.getLogger(JsonArraySplitterTest.class);


    @BeforeAll
    static void setup(){
        //System.out.println("@BeforeAll run");
    }

    @AfterAll
    static void tear(){
        //System.out.println("@AfterAll run");
    }

    @Test
    public void testNullJsonArray() throws Exception {
        JsonArraySplitter jsonSplitter = new JsonArraySplitter();

        ArrayList<String> expectedResult = new ArrayList<String>();

        Exchange exchange = ExchangeBuilder.anExchange(context)
            .withBody(null)
            .build();

        ArrayList<String> splitResult = jsonSplitter.doTheSplit(exchange);
        LOG.info(splitResult);

        Assertions.assertEquals(expectedResult, splitResult);
    }

    @Test
    public void testEmptyJsonArray() throws Exception {
        JsonArraySplitter jsonSplitter = new JsonArraySplitter();

        JSONArray input = new JSONArray();
        ArrayList<String> expectedResult = new ArrayList<String>();

        Exchange exchange = ExchangeBuilder.anExchange(context)
            .withBody(input.toString())
            .build();

        ArrayList<String> splitResult = jsonSplitter.doTheSplit(exchange);
        LOG.info(splitResult);

        Assertions.assertEquals(expectedResult, splitResult);
    }

    @Test
    public void testOneItemJsonArray() throws Exception {
        JsonArraySplitter jsonSplitter = new JsonArraySplitter();

        String inputString = "[{\"hi\":\"there\"}]";
        JSONArray input = new JSONArray(inputString);
        ArrayList<String> expectedResult = new ArrayList<String>();
        expectedResult.add("{\"hi\":\"there\"}");

        Exchange exchange = ExchangeBuilder.anExchange(context)
            .withBody(input.toString())
            .build();

        ArrayList<String> splitResult = jsonSplitter.doTheSplit(exchange);
        LOG.info(splitResult);

        Assertions.assertEquals(expectedResult, splitResult);
    }

    @Test
    public void testTwoItemJsonArray() throws Exception {
        JsonArraySplitter jsonSplitter = new JsonArraySplitter();

        String inputString = "[{\"hi\":\"there\"}, {\"good\":\"bye\"}]";
        JSONArray input = new JSONArray(inputString);
        ArrayList<String> expectedResult = new ArrayList<String>();
        expectedResult.add("{\"hi\":\"there\"}");
        expectedResult.add("{\"good\":\"bye\"}");

        Exchange exchange = ExchangeBuilder.anExchange(context)
            .withBody(input.toString())
            .build();

        ArrayList<String> splitResult = jsonSplitter.doTheSplit(exchange);
        LOG.info(splitResult);

        Assertions.assertEquals(expectedResult, splitResult);
    }

    @Test
    public void testThreeItemJsonArray() throws Exception {
        JsonArraySplitter jsonSplitter = new JsonArraySplitter();

        String inputString = "[{\"hi\":\"there\"}, {\"good\":\"bye\"}, {\"welcome\":\"friend\"}]";
        JSONArray input = new JSONArray(inputString);
        ArrayList<String> expectedResult = new ArrayList<String>();
        expectedResult.add("{\"hi\":\"there\"}");
        expectedResult.add("{\"good\":\"bye\"}");
        expectedResult.add("{\"welcome\":\"friend\"}");

        Exchange exchange = ExchangeBuilder.anExchange(context)
            .withBody(input.toString())
            .build();

        ArrayList<String> splitResult = jsonSplitter.doTheSplit(exchange);
        LOG.info(splitResult);

        Assertions.assertEquals(expectedResult, splitResult);
    }

}