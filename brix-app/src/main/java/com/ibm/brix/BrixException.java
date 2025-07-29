//=============================================================================
//* Name:         BrixException.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Exception
//*                                                                
//* Description:  Used as a general BrIX exception.
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
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix;

// Thrown when an expected value is not defined.  Indicates a logic error.

public class BrixException extends Exception {

  private static final long serialVersionUID = 1L;

  public BrixException() {
      super();
  }

  public BrixException(String msg) {
      super(msg);
  }
  
  public BrixException(String msg, Throwable cause) {
	  super(msg, cause);
  }

  public BrixException(Throwable cause) {
	  super(cause);
  }
}
