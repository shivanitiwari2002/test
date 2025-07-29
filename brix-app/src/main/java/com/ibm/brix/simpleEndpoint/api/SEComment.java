//=============================================================================
//* Name:         SEComment.java
//*
//* Application:  System DevOps Simple Endpoint Component
//*
//* Module Name:  Simple Endpoint credential comment data.
//*
//* Description:  This class provides the SE comment data returned as a
//*               response body from the SE.
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
//*               20231211 tollefso  New source file created.
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
import java.io.Serializable;


public class SEComment implements Serializable {
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2023 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    private String id;
    private String issue;
    private String comment_text;
    private String create_date="";
    private String creator="";

    public SEComment() {}
    public SEComment(String comment_text, String issue) {
        this.comment_text = comment_text;
        this.issue = issue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String toString() {
        return "SEComment{\"id\":" + getId() +
                ", \"comment_text\":\"" + this.getComment_text() + "\"" +
                ", \"issue\":" + getIssue() +
                ", \"create_date\":\"" + getCreate_date() + "\"" +
                ", \"creator\":" + getCreator() +
                "}";
    }

    public static String getCommentAsJson(SEComment secomment) throws Exception {
        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

        return objectWriter.writeValueAsString(secomment);
    }

    public static List<SEComment> getJsonArrayAsCommentList(String jsonComments) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        List<SEComment> comments = mapper.readValue(jsonComments, new TypeReference<List<SEComment>>(){});
        return comments;
    }

}