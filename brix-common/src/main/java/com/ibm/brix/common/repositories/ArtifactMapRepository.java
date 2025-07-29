//=============================================================================
//* Name:         ArtifactMapRepository.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX) REST
//*
//* Module Name:  BrIX ArtifactMapRepository
//*
//* Description:  This class provides access to ArtifactMaps in the DB.
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
//*               20250621 tollefso  New source file created.
//*
//* Additional notes about the Change Activity:
//*
//=============================================================================

package com.ibm.brix.common.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

import com.ibm.brix.common.model.ArtifactMap;

@ApplicationScoped
public class ArtifactMapRepository implements PanacheRepository<ArtifactMap> {
     public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

   // put your custom logic here as instance methods

}