//=============================================================================
//* Name:         ExchangeMgrFactory.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Exchange Manager Factory
//*                                                                
//* Description:  This factory assists with instantiating the various exchange
//*               manager classes.
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
//*               20230915 tmueller  New source file created.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.factories;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.brix.BrixException;
import com.ibm.brix.common.enums.SystemType;
import com.ibm.brix.managers.ExchangeMgr;

public class ExchangeMgrFactory {
	/* CopyRight */
	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2023 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

	private static final Logger LOG = LoggerFactory.getLogger(ExchangeMgrFactory.class);

	//Preallocate space for entries with the expected usage
	private Map<SystemType, Class<? extends ExchangeMgr>> registeredExchangeManagers = new HashMap<>(6);

	/**
	 * Method:  registerExchangeMgr
	 * Registration of Exchange Manager classes.
	 *
	 * @param SystemType systemType
	 * @param Class exchangeMgrClass
	 */
	public void registerExchangeMgr(SystemType systemType, Class<? extends ExchangeMgr> exchangeMgrClass){
		registeredExchangeManagers.put(systemType, exchangeMgrClass);
	}

	//=============================================================================
	//* Method:      getExchangeMgr                                            
	//=============================================================================
	/**
	 * Method: getExchangeMgr
	 * Returns an Exchange Manager constructed from the appropriate concrete Exchange Manager
	 * subclass based on the SystemType.
	 *
	 * @param SystemType systemType
	 */
	public ExchangeMgr getExchangeMgr(SystemType systemType, Exchange exchange) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, ParseException, BrixException{
		Class<? extends ExchangeMgr> exchangeMgrClass = registeredExchangeManagers.get(systemType);
		if (exchangeMgrClass == null) {
			throw new BrixException("No ExchangeMgr registered for type " + systemType.getName());
		}
        LOG.info("Found registered Exchange Manager class: " + exchangeMgrClass.getName());
        Constructor<? extends ExchangeMgr> exchangeMgrConstructor = exchangeMgrClass.getConstructor(Exchange.class);
		ExchangeMgr exchangeMgr = exchangeMgrConstructor.newInstance(exchange);
		return exchangeMgr;
	}
}
