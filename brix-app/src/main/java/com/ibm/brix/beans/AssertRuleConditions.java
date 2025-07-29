//=============================================================================
//* Name:         AssertRuleConditions.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  Assert Rule Conditions Bean
//*                                                                
//* Description:  This bean class provides the business logic for processing
//*               the list of configured Rules for this BRoute and going through
//*               the list of Rule Conditions for each Rule. It attempts to
//*               assert each Rule Condition to be true or false. If all Rule
//*               Conditions on a Rule assert to be true, then this Rule is
//*               added to a list of Rules to be "fired".
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
//*        129153 20250306 tmueller  New stub source file created.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.camel.Exchange;
import org.jboss.logging.Logger;

import com.ibm.brix.BrixException;
import com.ibm.brix.Constants;
import com.ibm.brix.common.enums.PredicateType;
import com.ibm.brix.common.enums.ResourceSide;
import com.ibm.brix.common.enums.RuleOperator;
import com.ibm.brix.common.model.BRoute;
import com.ibm.brix.model.GenericArtifact;
import com.ibm.brix.common.model.Rule;
import com.ibm.brix.common.model.RuleCondition;
import com.ibm.brix.common.model.RuleCondition;
import com.ibm.brix.model.RuleList;
import com.ibm.brix.utils.BrixPredicate;

public class AssertRuleConditions {
	/* CopyRight */
	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

	private static final Logger LOG = Logger.getLogger(AssertRuleConditions.class);

	// Globals
	private String markedDeltaAttribute;

	/**
	 * Method:  assertRuleConditions
	 * Processes each Rule for this BRoute and attempts to assert the list of Rule Conditions for each one.
	 * Exchange Expected Input:
	 *    - Header: BRIX_BROUTE (includes the Rule configurations)
	 *    - Header: BRIX_SOURCE_GENERIC_ARTIFACT
	 *    - Header: BRIX_TARGET_GENERIC_ARTIFACT
	 *    - Header: BRIX_DELTA_GENERIC_ARTIFACT
	 * Exchange Output:
	 *    - Header: BRIX_ASSERTED_RULES_EXIST
	 *    - return Body: RuleList rulesToFire
	 * 
	 * @param Exchange exchange (Provided by Camel)
	 * @return List<Rule>
	 * @throws BrixException
	 */
	public RuleList assertRuleConditions(Exchange exchange) throws BrixException {
        LOG.info("Processing each Rule for this BRoute and attempt to assert the list of Rule Conditions for each one. - Start");

        RuleList rulesToFire = new RuleList();
        BRoute broute = exchange.getIn().getHeader(Constants.BRIX_BROUTE, BRoute.class);
        GenericArtifact sourceGenericArtifact = exchange.getIn().getHeader(Constants.BRIX_SOURCE_GENERIC_ARTIFACT, GenericArtifact.class);
        GenericArtifact targetGenericArtifact = exchange.getIn().getHeader(Constants.BRIX_TARGET_GENERIC_ARTIFACT, GenericArtifact.class);
        GenericArtifact deltaArtifactOrig = exchange.getIn().getHeader(Constants.BRIX_DELTA_GENERIC_ARTIFACT, GenericArtifact.class);
        // Make a copy of the deltaArtifact so we can modify it as part of this bean processing.
        GenericArtifact deltaArtifact = new GenericArtifact(deltaArtifactOrig);
        
		LOG.debug("Before RuleCondition processing, " + deltaArtifact.getAttributes().size() + " attributes exist in deltaArtifact.");

		List<Rule> rules = broute.getRules();
        // Ensure the list of Rules is sorted by its sequence numbers
		Collections.sort(rules, (r1, r2) -> Integer.compare(r1.getSequence(), r2.getSequence()));
		
		// Process through the list of Rules on this BRoute.
		LOG.info("Processing " + rules.size() + " Rules.");
        for (Rule rule : rules) {
			LOG.debug("Processing Rule: [" + /*rule.getId() +*/ "] '" + rule.getName() + "', sequence " + rule.getSequence());
			boolean ruleAsserted = true;											//Assume true unless we find a condition asserts false.
			List<RuleCondition> ruleConditions = rule.getRuleConditions();

	        // Ensure the list of RuleConditions is sorted by its sequence numbers
			Collections.sort(ruleConditions, (rc1, rc2) -> Integer.compare(rc1.getSequence(), rc2.getSequence()));

			// Logic notes: As each Rule is processed, it may be tied to an attribute in the deltaArtifact. Once we assert all Rule Conditions
			//              for a Rule to be true, remove the attribute from the deltaArtifact. We assume no other Rules should also assert to
			//              be true for the same attribute. By removing it, any other Rules for this attribute won't find it in deltaArtifact and
			//              can stop processing those Rule Conditions. 
			LOG.info("Found " + ruleConditions.size() + " RuleConditions for Rule: " + rule.getName());
			markedDeltaAttribute = null;

			// For each Rule, attempt to assert each RuleCondition.
			for (RuleCondition ruleCondition : ruleConditions) {
				LOG.debug("Asserting RuleCondition: [" + /*ruleCondition.getId() +*/ "] '" + ruleCondition.getName() + "', sequence " + ruleCondition.getSequence());

				boolean ruleConditionAsserted = assertRuleCondition(ruleCondition, sourceGenericArtifact, targetGenericArtifact, exchange);
				
				// If a RuleCondition asserts to be false, then we can assert the entire Rule to be false, and stop processing conditions.
				if (!ruleConditionAsserted) {
					LOG.debug("RuleCondition asserted to be false. Skipping all other Rule Conditions.");
					ruleAsserted = false;
					break;
				}
				else {
					LOG.debug("RuleCondition asserted to be true.");
				}
			}
			
			if (ruleAsserted) {
				LOG.debug("Rule asserted to be true. Adding to rulesToFire and removing the related attribute from the deltaArtifact.");
				rulesToFire.append(rule);
				if (markedDeltaAttribute != null) {
					deltaArtifact.removeAttribute(markedDeltaAttribute);
				}
			}
			else {
				LOG.debug("Rule asserted to be false. Skipping.");
			}
		}
        
        // At the end of all Rules processing, we should have covered all applicable changed attributes in the deltaArtifact.
        // Those that are not applicable are ignored.
		LOG.debug("After RuleCondition processing, " + deltaArtifact.getAttributes().size() + " attributes remain in deltaArtifact.");
		int numRulesToFire = rulesToFire.size();
		String assertedRulesExist = (numRulesToFire > 0 ? Constants.TRUE : Constants.FALSE);
		exchange.getIn().setHeader(Constants.BRIX_ASSERTED_RULES_EXIST, assertedRulesExist);
		
		// We return the List<Rule> rulesToFire, which gets put into the Exchange body for us.
		LOG.info("Number of asserted rulesToFire: " + numRulesToFire);
		return rulesToFire;
	}

	/**
	 * Method:  assertRuleCondition
	 * Asserts a rule condition.
	 * 
	 * @param RuleCondition ruleCondition
	 * @param GenericArtifact sourceGenericArtifact
	 * @param GenericArtifact targetGenericArtifact
	 * @param Exchange exchage
	 * @return boolean (T/F)
	 * @throws BrixException
	 */
	private boolean assertRuleCondition(RuleCondition ruleCondition, GenericArtifact sourceGenericArtifact, GenericArtifact targetGenericArtifact, Exchange exchange) throws BrixException {
		boolean ruleConditionAsserted = false;
		String subject = ruleCondition.getPredicateSubject();
		String pattern = ruleCondition.getPredicatePattern();
		RuleOperator operator = ruleCondition.getRuleConditionOperator();
		PredicateType predicateType = ruleCondition.getPredicateType();
		LOG.debug("  Processing RuleCondition predicate type: '" + predicateType.getName() + "'");

		// The PredicateType dictates how the rule predicate subject is to be asserted.
		switch(predicateType) {
		case ATTRIBUTE:
			// Testing an attribute, attempt to get the value of the attribute. Will be null if the attribute is not found.
			ResourceSide resourceSide = ruleCondition.getResourceSide();
			Object attributeValue = (resourceSide == ResourceSide.SOURCE ? sourceGenericArtifact.getAttributeValue(subject) : targetGenericArtifact.getAttributeValue(subject));
			ruleConditionAsserted = assertAttributeCondition(subject, attributeValue, operator, pattern);

			// If we have established an EXISTS RuleCondition for a changed attribute in deltaArtifact, mark this attribute.
			if ((ruleConditionAsserted == true) && (operator == RuleOperator.EXISTS)) {
				markedDeltaAttribute = subject;
			}
			break;
		case EXCHANGEHEADER:
			// Testing an Exchange header. Retrieve the Exchange header value. The subject should be the name of the Exchange header. The value is expected to be of type String.
	        Object headerValue = exchange.getIn().getHeader(subject, Object.class);
			ruleConditionAsserted = assertExchangeHeaderCondition(subject, headerValue, operator, pattern);
			break;
		case CLASS:
			// Test using a custom class.
			ruleConditionAsserted = assertClassCondition(subject);
			break;
		default:
			throw new IllegalArgumentException("RuleCondition does not support the PredicateType: " + predicateType.getName());
		}
		
		return ruleConditionAsserted;
	}

	/**
	 * Method:  assertAttributeCondition
	 * Asserts a rule condition for an "attribute" type of predicate.
	 * 
	 * @param String attributeName
	 * @param String attributeValue
	 * @param RuleOperator operator
	 * @param String pattern
	 * @return boolean (T/F)
	 * @throws BrixException
	 */
	private boolean assertAttributeCondition(String attributeName, Object attributeValue, RuleOperator operator, String pattern) throws BrixException {
		// Use the BrixPredicate utility to test the condition.
		return BrixPredicate.assertCondition(attributeName, attributeValue, operator, pattern);
	}

	/**
	 * Method:  assertExchangeHeaderCondition
	 * Asserts a rule condition for an Exchange header string value.
	 * 
	 * @param String headerName
	 * @param String headerValue
	 * @param RuleOperator operator
	 * @param String pattern
	 * @return boolean (T/F)
	 * @throws BrixException
	 */
	private boolean assertExchangeHeaderCondition(String headerName, Object headerValue, RuleOperator operator, String pattern) throws BrixException {
		// Use the BrixPredicate utility to test the condition.
		return BrixPredicate.assertCondition(headerName, headerValue, operator, pattern);
	}

	/**
	 * Method:  assertClassCondition
	 * Uses a specialized class to assert a rule condition.
	 * 
	 * @param String className
	 * @return boolean (T/F)
	 * @throws BrixException
	 */
	private boolean assertClassCondition(String className) throws BrixException {
		// Use the provided class to run special logic in asserting a rule condtion.
		// TODO: Stubbed out for now until we need to implement.

		return false;
	}
}
