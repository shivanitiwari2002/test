//=============================================================================
//* Name:         Constants.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX application wide constants
//*                                                                
//* Description:  Provides static constants to be referenced throughout the
//*               BrIX application.
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
//*               20230822 tmueller  New source file created.
//*      129153   20250224 tmueller  Support for Artifact Relationship
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix;

public class Constants {
	/* CopyRight */
	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2023 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

	// Environment
	public static final String CAMEL_CONTEXT_UNIT = "camel";
	public static final String ROOT_PATH_REGISTRY_NAME = "rootPath";
	public static String ROOT_PATH = null;
	public static final String WORKING_DIR_ENV = "WORKING_DIR";
	public static final String TIMEZONE_UTC = "UTC";
	
	// Configuration Properties
	public static final String APPCONFIG_FILE = "config.properties";
	public static final String TEST_APPCONFIG_FILE = "config-test.properties";
	public static String BRIX_HOST = null;
	public static String BRIX_PORT = null;
	public static String HAWTIO_WAR = null;
	public static String HAWTIO_HOST = null;
	public static String HAWTIO_PORT = null;
	public static String BROUTELIST_CFG_FILENAME = "brix.broutes.cfg";

	// Rest Routes
	public static final String REST_ROOT = "/";
	public static final String REST_INCOMING = "/incoming";
	public static final String REST_SHUTDOWN = "/shutdown";
	public static final String REST_CATCH = "/catch";

	// Direct Routes
	public static final String ENTRY_VALIDATE = "entry-validate";
	public static final String ENTRY_ASSOCIATION = "entry-association";
	public static final String ENTRY_AGGREGATE_STRATEGY = "entry-aggregate-strategy";
	public static final String ENTRY_RESULTS = "entry-results";
	public static final String ENTRY_PERSIST_SOURCE = "entry-persiste-source";

	// Exchange headers
	public static final String HOST = "Host";
	public static final String HMAC_SIGNATURE = "X-Hub-Signature";

	// BrIX Exchange headers
	public static final String BRIX_TRX_ID = "BRIXTrxId";
	public static final String BRIX_SOURCE_SYSTEM_TYPE = "BRIXSourceSystemType";
	public static final String BRIX_TARGET_SYSTEM_TYPE = "BRIXTargetSystemType";
	public static final String BRIX_OPERATION_TYPE = "BRIXOperationType";
	public static final String BRIX_VALID_WEBHOOK = "BRIXValidWebhook";
	public static final String BRIX_PAYLOAD_LOADED = "BRIXPayloadLoaded";
	public static final String BRIX_BROUTE = "BRIXBroute";
	public static final String BRIX_BROUTE_DOMAIN_CHANGED = "BRIXBrouteDomainChanged";
	public static final String BRIX_ARTIFACT_RELATIONSHIPS = "BRIXArtifactRelationships";
	public static final String BRIX_GUID = "BRIXGuid";
	public static final String BRIX_RESULT = "BRIXResult";
	public static final String BRIX_TRX_STATUS = "BRIXTrxStatus";
	public static final String BRIX_RESULT_SUCCESS = "BRIXResultSuccess";
	public static final String BRIX_SOURCE_ARTIFACT = "BRIXSourceArtifact";
	public static final String BRIX_TARGET_ARTIFACT = "BRIXTargetArtifact";
	public static final String BRIX_TARGET_EXISTS = "BRIXTargetExists";
	public static final String BRIX_CREATE_TARGET = "BRIXCreateTarget";
	public static final String BRIX_DELTAS_EXIST = "BRIXDeltasExist";
	public static final String BRIX_SOURCE_GENERIC_ARTIFACT = "BRIXSourceGenericArtifact";
	public static final String BRIX_TARGET_GENERIC_ARTIFACT = "BRIXTargetGenericArtifact";
	public static final String BRIX_DELTA_GENERIC_ARTIFACT = "BRIXDeltaGenericArtifact";
	public static final String BRIX_ASSERTED_RULES_EXIST = "BRIXAssertedRulesExist";
	public static final String BRIX_ASSERTED_RULES = "BRIXAssertedRules";
	public static final String BRIX_ACTIONS = "BRIXActions";
	public static final String BRIX_ACTIONLIST_SIZE = "BRIXActionListSize";
	public static final String BRIX_ENDPOINT_EXTRACT_URI = "BRIXEndpointExtractURI";
	public static final String BRIX_ENDPOINT_SYSTEM_TYPE = "BRIXEndpointSystemType";
	public static final String BRIX_EXTRACT_RESOURCE_SIDE = "BRIXExtractResourceSide";
	public static final String BRIX_DETERMINE_FULL_EXTRACT = "BRIXDetermineFullExtract"; // <--- Adding constant for now to determine full extract

	// Route Stage Status
	public static final String ROUTE_STAGE_CURRENT = "Route stage current";
	public static final String ROUTE_STAGE_COMPLETED = "Route stage completed";

	// Attribute related constants
	public static final String LABEL_PREFIX = "label:";
	
	// Misc
	public static final String USER = "*USER";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String ON = "On";
	public static final String OFF = "Off";
	public static final String COLON = ":";
	
	public static final int STG_JSON_OK = 200;
	
	// Private constructor to prevent instantiation
	private Constants() {
    }
}
