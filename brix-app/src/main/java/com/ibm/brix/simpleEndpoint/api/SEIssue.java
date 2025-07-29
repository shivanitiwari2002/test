//=============================================================================
//* Name:         SEIssue.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Simple Endpoint User data.
//*                                                                
//* Description:  This class provides the SE Issue request body for getting
//*               an SE issue.
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
//*               20231114 tollefso  New source file created.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.simpleEndpoint.api;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SEIssue {
	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2023 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    private String id_integer;
    private String description_text;
    private String severity;
    private String create_date="";
    private String modified_date="";
    private String lastModifier="";
    private String creator="";

    public SEIssue() {}

    public String getId_integer() {
        return id_integer;
    }

    public void setId_integer(String id_integer) {
        this.id_integer = id_integer;
    }

    public String getDescription_text() {
        return description_text;
    }

    public void setDescription_text(String description_text) {
        this.description_text = description_text;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getModified_date() {
        return modified_date;
    }

    public void setModified_date(String modified_date) {
        this.modified_date = modified_date;
    }

    public String getLastModifier() {
        return lastModifier;
    }

    public void setLastModifier(String lastModifier) {
        this.lastModifier = lastModifier;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String toString() {
        return "SEIssue{\"id_integer\":" + getId_integer() +
               ", \"severity\":" + getSeverity() +
               ", \"description_text\":\"" + getDescription_text() + "\"" +
               ", \"create_date\":\"" + getCreate_date() + "\"" +
               ", \"modified_date\":\"" + getModified_date() + "\"" +
               ", \"lastModifier\":" + getLastModifier() +
               ", \"creator\":" + getCreator() +
               "}";
    }

    public static String getIssueAsJson(SEIssue seissue) throws Exception {
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

        return objectWriter.writeValueAsString(seissue);
    }

    public static SEIssue getJsonAsIssue(String jsonIssue) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        SEIssue issue = mapper.readValue(jsonIssue, new TypeReference<SEIssue>(){});
        return issue;
    }

    public static List<SEIssue> getJsonArrayAsIssueList(String jsonArrayOfIssues) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<SEIssue> issues = mapper.readValue(jsonArrayOfIssues,
                                                new TypeReference<List<SEIssue>>(){});
        return issues;
    }
}