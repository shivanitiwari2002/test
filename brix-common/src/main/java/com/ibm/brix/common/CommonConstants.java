//=============================================================================
//* Name:         CommonConstants.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX common constants
//*
//* Description:  Provides common static constants to be referenced throughout the
//*               BrIX application.
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
//*               20240710 tmueller  New source file created.
//*               20250625 tollefso  Added Database and Misc constants.
//*
//* Additional notes about the Change Activity:
//*
//=============================================================================
package com.ibm.brix.common;

public class CommonConstants {
    /* CopyRight */
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2024 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    public static final String TIMEZONE_DISPLAY = "America/Chicago";

    //MDC name for logging
    public static final String MDC_EXCHANGE_ID = "exchangeId";

    // Exchange Headers
    public static final String HEADER_COLLECTORTYPE = "BrixCollectorType";
    public static final String HEADER_SIMPLE_ENDPOINT_ORIGIN = "SimpleEndpointOrigin";
    public static final String HEADER_SYSTEMTYPE = "BrixSystemType";

    // Simple Endpoint REST APIs - used for testing
    public static final String SE_ISSUES_API = "api/issues/";
    public static final String SE_COMMENTS_API = "api/comments/";
    public static final String SE_TOKEN_API = "api/token/";

    // Database
    public static final int DB_DESCRIPTION_LENGTH = 255;
    public static final int DB_DEFAULT_STRING_LENGTH = 255;
    public static final int DB_STATUS_MESSAGE_LENGTH = 3000;
    public static final int DB_TRANSACTION_PAYLOAD_LENGTH = 150000;
    public static final int DB_TRANSACTION_ID_LENGTH = 50;

    // Misc
    public static final String NULL = "null";
    public static final String UNDEFINED = "Undefined";

}
