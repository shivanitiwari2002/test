//=============================================================================
//* Name:         BrixPredicate.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Predicate Utility Class
//*                                                                
//* Description:  This utility provides the necessary methods for being able to
//*               assert rule conditions to be true or false. This specialized
//*               predicate uses the basic principle of taking a predicate subject
//*               value, an operator, and a pattern to perform the desired "test"
//*               operation on the subject using the pattern. Brix supplies a
//*               number of operators that can be configured. The logic in those
//*               operators is contained in this utility. The utility provides
//*               static "assert" methods that can easily be called to perform
//*               the assertion logic.
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
//*        129161 20250331 tmueller  New source file created.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jboss.logging.Logger;

import com.ibm.brix.BrixException;
import com.ibm.brix.Constants;
import com.ibm.brix.common.enums.RuleOperator;

public class BrixPredicate {
	/* CopyRight */
	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

	private static final Logger LOG = Logger.getLogger(BrixPredicate.class);

	/**
	 * Method:  assertCondition
	 * Asserts the test on the subject or subject value against the pattern to be true or false, based on the operator used.
	 * In some cases, the subject is the name of something such as an attribute name or a Header name, in which case the 
	 * subject's value is passed in.
	 * 
	 * @param String subject
	 * @param String subjectValue
	 * @param RuleOperator operator
	 * @param String pattern
	 * @return boolean (T/F)
	 * @throws BrixException
	 */
	public static boolean assertCondition(String subject, Object subjectValue, RuleOperator operator, String pattern) throws BrixException {
		if (subject == null) {
			// Exception
		}

        String subjectValueString = (subjectValue == null ? "null" : subjectValue.toString());
        String subjectClassType = (subjectValue == null ? "null" : subjectValue.getClass().getName());
		LOG.debug("  BrixPredicate assertCondition: subject='" + subject + "' | subjectValue='" + subjectValueString + "' | RuleOperator='" + operator.getName() + "' | pattern='" + pattern + "' | subject value class type=" + subjectClassType);

		try {
			switch(operator) {
			case EXISTS:
				return exists(subjectValue);
			case NOTEXISTS:
				return (!(exists(subjectValue)));
			case EQUALS:
				return equals(subjectValue, pattern);
			case EQUALSIGNORECASE:
				return equalsIgnoreCase(subjectValue, pattern);
			case NOTEQUALS:
				return (!(equals(subjectValue, pattern)));
			case CONTAINS:
				return contains(subjectValue, pattern);
			case NOTCONTAINS:
				return (!(contains(subjectValue, pattern)));
			case TRUE:
				return isTrue(subjectValue);
			case FALSE:
				return isFalse(subjectValue);
			case EMPTY:
				return isEmpty(subjectValue);
			case NOTEMPTY:
				return (!(isEmpty(subjectValue)));
			case MATCHREGEX:
				return matchRegex(subjectValue, pattern);
			default:
				throw new IllegalArgumentException("BrixPredicate does not support the operator: " + operator.getName());
			}
		}
		catch (Exception e) {
			throw new BrixException("Brix Predicate assertCondition error:\n", e);
		}
	}

	/**
	 * Method:  exists
	 * Asserts a specific case of checking if the subject exists by checking if the subjectValue that was passed in is null or not.
	 * Normally, this is checking if an attribute exists in the deltaArtifact, or if a Header exists on the Exchange. The codes using
	 * EXISTS should have used methods to retrieve the subjects actual value. If the subject is not found, the subjectValue is expected
	 * to be null.
	 * 
	 * @param Object subjectValue
	 * @return boolean (T/F)
	 * @throws BrixException
	 */
	private static boolean exists(Object subjectValue) {
		if (subjectValue == null) {
			return false;
		}
		return true;
	}

	/**
	 * Method:  equals
	 * Asserts the subject value equals the pattern.
	 * 
	 * @param String subjectValue
	 * @param String pattern
	 * @return boolean (T/F)
	 * @throws BrixException
	 */
	private static boolean equals(Object subjectValue, String pattern) {
		if ((subjectValue == null) || (pattern == null)) {
			return false;
		}

		// Someday when using Java 21
		//switch (subjectValue) {
	    //case String s -> return (s.equals(pattern));
	    //case Integer i -> return (Integer.toString(i) == pattern);
	    //default -> return false;
		//}

		if (subjectValue instanceof String) {
			return (((String)subjectValue).equals(pattern));
		}
		else if (subjectValue instanceof Integer) {
			return ((Integer.toString((Integer)subjectValue)).equals(pattern));
		}
		else if (subjectValue instanceof Long) {
			return ((Long.toString((Long)subjectValue)).equals(pattern));
		}
		else {
			return false;					// All other types do not make sense in the context of testing equality on a String pattern.
		}
	}

	/**
	 * Method:  equalsIgnoreCase
	 * Asserts the subject value equals the pattern, but ignoring the case between the value and the pattern.
	 * 
	 * @param String subjectValue
	 * @param String pattern
	 * @return boolean (T/F)
	 * @throws BrixException
	 */
	private static boolean equalsIgnoreCase(Object subjectValue, String pattern) {
		if ((subjectValue == null) || (pattern == null)) {
			return false;
		}
		
		if (subjectValue instanceof String) {
			return (((String)subjectValue).equalsIgnoreCase(pattern));
		}
		else if (subjectValue instanceof Integer) {
			return ((Integer.toString((Integer)subjectValue)).equals(pattern));
		}
		else if (subjectValue instanceof Long) {
			return ((Long.toString((Long)subjectValue)).equals(pattern));
		}
		else {
			return false;					// All other types do not make sense in the context of testing equality on a String pattern.
		}
	}

	/**
	 * Method:  contains
	 * Asserts the subject value contains the pattern.
	 * 
	 * @param String subjectValue
	 * @param String pattern
	 * @return boolean (T/F)
	 * @throws BrixException
	 */
	private static boolean contains(Object subjectValue, String pattern) {
		if ((subjectValue == null) || (pattern == null)) {
			return false;
		}

		if (subjectValue instanceof String) {
			return (((String)subjectValue).contains(pattern));
		}
		else {
			return false;					// All other types do not make sense in the context of testing contains on a String pattern.
		}
	}

	/**
	 * Method:  isTrue
	 * Asserts the subject value is "true". This is primarily used when checking the value of an Exchange header that is passed in.
	 * 
	 * @param String subjectValue
	 * @return boolean (T/F)
	 * @throws BrixException
	 */
	private static boolean isTrue(Object subjectValue) {
		if (subjectValue == null) {
			return false;
		}

		if (subjectValue instanceof String) {
			return (((String)subjectValue).equalsIgnoreCase(Constants.TRUE));
		}
		else if (subjectValue instanceof Boolean) {
			return (Boolean)subjectValue;
		}
		else {
			return false;					// All other types do not make sense in the context of testing the subjectValue is "true".
		}
	}

	/**
	 * Method:  isFalse
	 * Asserts the subject value is "false". This is primarily used when checking the value of an Exchange header that is passed in.
	 * 
	 * @param String subjectValue
	 * @return boolean (T/F)
	 * @throws BrixException
	 */
	private static boolean isFalse(Object subjectValue) {
		if (subjectValue == null) {
			return false;
		}

		if (subjectValue instanceof String) {
			return (((String)subjectValue).equalsIgnoreCase(Constants.FALSE));
		}
		else if (subjectValue instanceof Boolean) {
			return (!(Boolean)subjectValue);
		}
		else {
			return false;					// All other types do not make sense in the context of testing the subjectValue is "true".
		}
	}

	/**
	 * Method:  isEmpty
	 * Asserts the subject value is null or empty. This is primarily used when checking the value of an Exchange header that is passed in.
	 * 
	 * @param String subjectValue
	 * @return boolean (T/F)
	 * @throws BrixException
	 */
	private static boolean isEmpty(Object subjectValue) {
		if (subjectValue == null) {
			return true;
		}

		if (subjectValue instanceof String) {
			return (((String)subjectValue).isEmpty());
		}
		else {
			return false;					// All other types do not make sense in the context of testing the subjectValue is null or an empty String.
		}
	}

	/**
	 * Method:  matchRegex
	 * Asserts the regular expression pattern finds (at least one) match on the subject value string.
	 * 
	 * @param String subjectValue
	 * @param String regexPattern
	 * @return boolean (T/F)
	 * @throws BrixException
	 */
	private static boolean matchRegex(Object subjectValue, String regexPattern) {
		if ((subjectValue == null) || (regexPattern == null)) {
			return false;
		}

		// Note: We only do a single matcher find here to verify a regex pattern found at least one match.
		if (subjectValue instanceof String) {
			Pattern pattern = Pattern.compile(regexPattern);
			Matcher matcher = pattern.matcher((String)subjectValue);
			return matcher.find();
		}
		else {
			return false;					// All other types do not make sense in the context of testing regular expression match.
		}
	}
}
