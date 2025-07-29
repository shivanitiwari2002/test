//=============================================================================
//* Name:         ActionCommandConfigRepository.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX) REST
//*
//* Module Name:  BrIX Action Command Config Repository
//*
//* Description:  This class provides REST paths for ActionCommandConfigRepository.
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
//*               20250620 shivani tiwari  New source file created.
//*
//* Additional notes about the Change Activity:
//*
//=============================================================================
package com.ibm.brix.common.repositories;

import com.ibm.brix.common.model.ActionCommandConfig;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ActionCommandConfigRepository implements PanacheRepository<ActionCommandConfig> {
    // Inherits all basic CRUD methods like findById(), listAll(), persist(), deleteById(), etc.
	
	 public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";
}