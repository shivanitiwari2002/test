//=============================================================================
//* Name:         ArtifactFactory.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Artifact Factory
//*                                                                
//* Description:  This factory assists with instantiating the various Artifact classes.
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
//*               20240304 tmueller  New source file created.
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
import com.ibm.brix.common.enums.SystemType;
import com.ibm.brix.common.model.Artifact;
import com.ibm.brix.common.simpleendpoint.model.SEArtifact;

public class ArtifactFactory {
	/* CopyRight */
	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2023 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

	private static final Logger LOG = LoggerFactory.getLogger(ArtifactFactory.class);

	//Preallocate space for entries with the expected usage
	private Map<SystemType, Class<? extends Artifact>> registeredArtifacts = new HashMap<>(6);

	/**
	 * Constructor
	 */
    public ArtifactFactory() {
    	initializeFactories();
    }

	/**
	 * Method:  initializeFactories
	 * Initialize and register the Artifact factories.
	 */
	private void initializeFactories() {
		registerArtifact(SystemType.SIMPLEEP, SEArtifact.class);
	}

	/**
	 * Method:  registerArtifact
	 * Registration of Artifact classes.
	 *
	 * @param SystemType systemType
	 * @param Class artifactClass
	 */
	private void registerArtifact(SystemType systemType, Class<? extends Artifact> artifactClass){
		registeredArtifacts.put(systemType, artifactClass);
	}

	//=============================================================================
	//* Method:      getExchangeMgr                                            
	//=============================================================================
	/**
	 * Method: getArtifact
	 * Returns an Artifact constructed from the appropriate concrete Artifact
	 * subclass based on the SystemType.
	 *
	 * @param SystemType systemType
	 */
	public Artifact getArtifact(SystemType systemType) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, BrixException {
		Class<? extends Artifact> artifactClass = registeredArtifacts.get(systemType);
		if (artifactClass == null) {
			throw new BrixException("No Artifact registered for type " + systemType.getName());
		}
        LOG.info("Found registered Artifact class: " + artifactClass.getName());
        Class<? extends Artifact>[] parameterType = null;		//Get the constructor with no parameters
        Constructor<? extends Artifact> artifactConstructor = artifactClass.getConstructor(parameterType);
		Artifact artifact = artifactConstructor.newInstance();
		return artifact;
	}

}
