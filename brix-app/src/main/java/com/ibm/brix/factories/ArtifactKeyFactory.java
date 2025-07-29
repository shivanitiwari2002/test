//=============================================================================
//* Name:         ArtifactKeyFactory.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Artifact Key Factory
//*                                                                
//* Description:  This factory assists with instantiating the various Artifact Key
//*			 	  classes.
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
//*               20250306 mitre  New source file created.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.factories;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.brix.BrixException;
import com.ibm.brix.common.enums.ArtifactType;
import com.ibm.brix.common.model.Artifact;
import com.ibm.brix.common.model.ArtifactKey;
import com.ibm.brix.common.simpleendpoint.model.SEArtifactKey;

public class ArtifactKeyFactory {
    /* CopyRight */
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    private static final Logger LOG = LoggerFactory.getLogger(ArtifactKeyFactory.class);

    //Preallocate space for entries with the expected usage
    private Map<ArtifactType, Class<? extends ArtifactKey>> registeredArtifactKeys = new HashMap<>(6);

    /**
     * Constructor
     */
    public ArtifactKeyFactory() {
    	initializeFactories();
    }

    /**
     * Method:  initializeFactories
     * Initialize and register the Artifact Key factories.
     */
    private void initializeFactories() {
	    registerArtifactKey(ArtifactType.SEISSUE, SEArtifactKey.class);
    }    

    /**
     * Method:  registerArtifactKey
     * Registration of Artifact Key classes.
     *
     * @param SystemType systemType
     * @param Class artifactKeyClass
     */
    private void registerArtifactKey(ArtifactType artifactType, Class<? extends ArtifactKey> artifactKeyClass){
	    registeredArtifactKeys.put(artifactType, artifactKeyClass);
    }

    //=============================================================================
    //* Method:      getArtifactKey                                            
    //=============================================================================
    /**
     * Method: getArtifactKey
     * Returns an Artifact Key constructed from the appropriate concrete Artifact
     * subclass based on the SystemType.
     *
     * @param SystemType systemType
     */
    public ArtifactKey getArtifactKey(ArtifactType artifactType, Artifact artifact) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, BrixException {
        Class<? extends ArtifactKey> artifactKeyClass = registeredArtifactKeys.get(artifactType);
	    if (artifactKeyClass == null) {
            throw new BrixException("No Artifact Key registered for type " + artifactType.getName());
	    }
        LOG.info("Found registered ArtifactKey class: " + artifactKeyClass.getName());
        Constructor<? extends ArtifactKey> artifactKeyConstructor = artifactKeyClass.getDeclaredConstructor(Artifact.class);
        ArtifactKey artifactKey = artifactKeyConstructor.newInstance(artifact);
        
        return artifactKey;
    }
}
