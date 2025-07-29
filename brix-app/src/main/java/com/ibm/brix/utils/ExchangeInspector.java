//=============================================================================
//* Name:         ExchangeInspector.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Exchange Inspector
//*                                                                
//* Description:  Utility to inspect and output the contents of an Exchange.
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
//*               20230823 tmueller  New source file created.
//*               20241120 tollefso  Implement ExchangeFormatter
//*
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.component.exec.ExecResult;
import org.jboss.logging.Logger;

import org.apache.camel.spi.ExchangeFormatter;
import com.ibm.brix.common.model.Artifact;

public class ExchangeInspector implements ExchangeFormatter {
	/* CopyRight */
	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2023 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    private static final Logger LOG = Logger.getLogger(ExchangeInspector.class);

    private StringBuilder exchangeContents = new StringBuilder();
    private String message = "";
    private Boolean printBody = false;

    public ExchangeInspector(Boolean printBody) {
        this(null, printBody);
    }

    public ExchangeInspector(String message) {
        this(message, false);
    }

    public ExchangeInspector(String message, Boolean printBody) {
        this.message = message;
        this.printBody = printBody;
        LOG.debug("ExchangeInspector:message:" + message==null?message:"" + ", printBody:" + printBody.toString());
    }

    /*
     * Implement ExchangeFormatter interface.
    */
    public String format(Exchange exchange) {
        exchangeContents = new StringBuilder();
        exchangeContents.append("\n-------------------------[EXCHANGE INSPECTOR START]-------------------------\n");
        if (message != null) {
            exchangeContents.append("MESSAGE: ");
            exchangeContents.append(message);
            exchangeContents.append("\n");
        }
        printExchangeID(exchange);
        printExchangeProperties(exchange);
        printHeaders(exchange);
        if (printBody) {
            printBody(exchange);
        }
        exchangeContents.append("-------------------------[EXCHANGE INSPECTOR END]--------------------------\n");
        return exchangeContents.toString();
    }

    public void printExchangeID(Exchange exchange) {
        exchangeContents.append("EXCHANGE ID: ");
        exchangeContents.append(exchange.getExchangeId());
        exchangeContents.append("\n");
    }

    public void printExchangeProperties(Exchange exchange) {
        exchangeContents.append("EXCHANGE PROPERTIES:\n");
        Map<String, Object> properties = exchange.getProperties();
        properties.forEach((k,v) -> {
            printCollections(k,v);
        });
    }

    public void printHeaders(Exchange exchange) {
        exchangeContents.append("EXCHANGE HEADERS:\n");
        Map<String, Object> headers = exchange.getIn().getHeaders();
        headers.forEach((k,v) -> {
            printCollections(k,v);
        });
    }

    private void printCollections(Object k, Object v) {
        // In the future, we will most likely run into values that are objects. We'll need to deal with those as we put those on the Exchange.
        if (v instanceof Artifact) {
            exchangeContents.append(k + ":" + v + ":\n" + ((Artifact)v).toString() + "\n");
        }
        else if (!((String)k).contains("Common")) {
            if (!((String)k).equals("")) {
                if (k instanceof String && ((String)k).endsWith(" body")) {
                    String prefix = new String(new char[((String)k).length()]).replace('\0', ' ');
                    exchangeContents.append(k + ": " + ((String)v).replaceAll("\n", "\n" + prefix + ":‚ ") + "\n");
                } else if (k instanceof String && ((String)k).endsWith(" link")) {
                    String prefix = new String(new char[((String)k).length()]).replace('\0', ' ');
                    exchangeContents.append(k + ": " + (v).toString().replaceAll(", ", ", \n" + prefix + ":‚ ") + "\n");
                } else {
                    exchangeContents.append(k + ": " + (v) + "\n");
                }
            }
        }
        else {
            exchangeContents.append(k + ": " + "\n");
        }

        //List type values
        if (v instanceof List) {
            ((List <Object>)v).forEach((vv) -> {
               printCollections("   === ",vv);
            });
        } else if (v instanceof Map || v instanceof HashMap) {
            ((Map<String,Object>)v).forEach((kk,vv) -> {
                if (((String)k).contains("Common")) {
                    printCollections("   === " + kk,vv);
                }
            });
        }
    }

    public void printBody(Exchange exchange) {
        exchangeContents.append("EXCHANGE BODY:\n");
        String inBody = exchange.getIn().getBody(String.class);
        exchangeContents.append(inBody);
        exchangeContents.append("\n");
    }

    public void printSTDERR(Exchange exchange) throws IOException {
        ExecResult body = exchange.getIn().getBody(ExecResult.class); //get ExecResult to see outputs
        InputStream err = body.getStderr(); //get stdout of command
        if (err != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream) err));
            String line = reader.readLine();
            while (line != null) {
                exchangeContents.append(line + "\n");
                line = reader.readLine();
            }
        }
        exchangeContents.append("CMD STDERR: ");
        exchangeContents.append(err);
        exchangeContents.append("\n");
    }

    public void printSTDOUT(Exchange exchange) {
        ExecResult body = exchange.getIn().getBody(ExecResult.class); //get ExecResult to see outputs
        InputStream out = body.getStdout(); //get stdout of command
        exchangeContents.append("CMD STDOUT: ");
        exchangeContents.append(out);
        exchangeContents.append("\n");
    }

    private static boolean isCollection(Object obj) {
          return obj instanceof Collection || obj instanceof Map || obj instanceof List;
    }
}
