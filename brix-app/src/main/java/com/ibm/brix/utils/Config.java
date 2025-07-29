//=============================================================================
//* Name:         Config.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Config
//*
//* Description:  This class is for accessing BrIX's configuration.
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
//*               20240206 tollefso  New source file created.
//*      127205   20250320 mitre     Adding readArtifactRelationshipsFromFile 
//*                                  method to read pre-loaded artifact
//*                                  relationships from JSON file
//*
//* Additional notes about the Change Activity:
//*
//=============================================================================
package com.ibm.brix.utils;

import com.ibm.brix.BrixException;
import com.ibm.brix.factories.ArtifactKeyFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

import com.ibm.brix.common.model.Artifact;
import com.ibm.brix.common.model.ArtifactKey;
import com.ibm.brix.common.model.ArtifactRelationship;
import com.ibm.brix.model.ArtifactRelationshipList;
import com.ibm.brix.model.BRouteList;
import com.ibm.brix.common.model.Endpoint;
import com.ibm.brix.common.model.Transaction;
import com.ibm.brix.model.TransactionList;

import jakarta.transaction.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;

import java.util.List;

public class Config {
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2024 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
    private static final Logger LOG = LoggerFactory.getLogger(Config.class);

    private static BRouteList brouteList = new BRouteList();
    private static TransactionList transactionList = new TransactionList();
    private static ArtifactRelationshipList artifactRelationshipList = new ArtifactRelationshipList();


    public static BRouteList getBRouteList() {
        return brouteList;
    }

    public static ArtifactRelationshipList getArtifactRelationshipList() {
        return artifactRelationshipList;
    }

    public static TransactionList getTransactionList() {
        return transactionList;
    }

    /*
     * Returns null if not found.
     */
    public static Transaction getTransaction(String trxid) {
        return getTransactionList().get(trxid);
    }

    public static final void readBroutesFromFile(String fileName) {
        LOG.info("reading from " + fileName);

        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        BRouteList brouteListFromFile = null;

        try {
            InputStream inputStream = Config.class.getClassLoader()
                .getResourceAsStream(fileName);
            String content = readInputStreamToString(inputStream);
            //String content = new String(Files.readAllBytes(Paths.get(fileName)));
            brouteListFromFile = mapper.readValue(content, BRouteList.class);

        } catch (Exception e) {
            LOG.error("Unable to parse JSON:'" + e + "'");
        }

        //Append the broutes read-in to the master broute list
        //  to ensure there are no duplicates
        getBRouteList().append(brouteListFromFile);

        LOG.info("new parsed route=" + brouteList);
    }

    @Transactional
    public static final void readArtifactRelationshipsFromFile(String fileName) {
        InputStream inputStream = Config.class.getClassLoader()
            .getResourceAsStream(fileName);
        if (inputStream != null) {
            LOG.info("Reading from file:" + fileName);

            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ArtifactRelationship artifactRelationshipListFromFile = null;

            try {
                String content = readInputStreamToString(inputStream);
                //String content = new String(Files.readAllBytes(Paths.get(fileName)));
                artifactRelationshipListFromFile = mapper.readValue(content, ArtifactRelationship.class);

            } catch (Exception e) {
                LOG.error("Unable to parse JSON:'" + e + "'");
            }

            //Append the artifact relatinship read-in to the master artifact relationship list
            //  to ensure there are no duplicates
            getArtifactRelationshipList().append(artifactRelationshipListFromFile);
        } else { //if not from a file then get from DB
            List<ArtifactRelationship> artifactRelationships = ArtifactRelationship.listAll();
            for (int i = 0; i < artifactRelationships.size(); i++) {
                getArtifactRelationshipList().append(artifactRelationships.get(i));
            }
        }
        LOG.info("new parsed artifact relationship=" + artifactRelationshipList);
    }

    private static final String readInputStreamToString(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            LOG.error("Unable to read InputStream: " + e);
            throw e;
        }
        return sb.toString();
    }

    public static final BRouteList findMatchingBRoutes(Artifact sourceArtifact)
        throws BrixException {
        if (sourceArtifact == null)
            throw new BrixException("findMatchingBRoutes: Null sourceArtifact");

        Endpoint sourceEndpoint = sourceArtifact.getEndpoint();
        LOG.info("findMatchingBRoutes: " + sourceEndpoint);
        BRouteList brouteList = getBRouteList().findSourcesMatching(sourceEndpoint);

        LOG.info("Matching broutes:" + brouteList);
        return brouteList;
    }

    public static final ArtifactRelationshipList findMatchingArtifactRelationship(Artifact sourceArtifact)
        throws BrixException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if (sourceArtifact == null)
            throw new BrixException("findMatchingArtifactRelationship: Null sourceArtifact");

        ArtifactKeyFactory artifactKeyFactory = new ArtifactKeyFactory();
		ArtifactKey artifactKey = artifactKeyFactory.getArtifactKey(sourceArtifact.getArtifactType(), sourceArtifact);
        LOG.info("findMatchingArtifactRelationship Key: " + artifactKey);
        ArtifactRelationshipList artifactRelationshipList = getArtifactRelationshipList().findSourcesMatching(artifactKey);

        LOG.info("Matching artifact relationships:" + artifactRelationshipList);
        return artifactRelationshipList;
    }

}