//=============================================================================
//* Name:         JsonArraySplitter.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  JsonArraySplitter
//*
//* Description:  It splits an exchange holding a JSON array
//*               into multiple JSON elements.
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
//*               20240108 tollefso  New source file created.
//*
//* Additional notes about the Change Activity:
//*
//=============================================================================

package com.ibm.brix.common.beans;

import org.apache.camel.Exchange;
import org.json.JSONArray;
import java.util.ArrayList;


public class JsonArraySplitter {
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2024 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";


    public ArrayList<String> doTheSplit(Exchange exchange) {
        String json = (String)exchange.getMessage().getBody();
        ArrayList<String> arrayList = new ArrayList<String>();
        if (json!=null) {
            try {
                JSONArray jsonArray = new JSONArray(json);

                if (jsonArray != null) {
                    for (int i=0; i<jsonArray.length(); i++) {
                        arrayList.add(jsonArray.getJSONObject(i).toString());
                    }
                }
            } catch(Exception e) {
                System.out.println("JsonArraySplitter:Exception:" + e);
            }
        }
        return arrayList;
    }

}