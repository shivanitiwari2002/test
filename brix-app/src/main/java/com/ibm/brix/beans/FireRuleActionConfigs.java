//=============================================================================
//* Name:         FireRuleActionConfigs.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  Fire Rule Action Configs Bean
//*                                                                
//* Description:  This bean class provides the business logic for processing
//*               through the fired Rules to generate a list of Actions from
//*               the configured Rule Actions for each Rule. These Actions
//*               form the basis for what needs to be done to bring the target
//*               Artifact in sync with the source Artifact.
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
//*        129429 20250410 tmueller  Implemented full bean class logic.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.brix.BrixException;
import com.ibm.brix.Constants;
import com.ibm.brix.common.enums.ActionCommand;
import com.ibm.brix.common.enums.AttributeType;
import com.ibm.brix.common.enums.ResourceSide;
import com.ibm.brix.common.enums.Trait;
import com.ibm.brix.model.Action;
import com.ibm.brix.common.model.ActionCommandConfig;
import com.ibm.brix.model.ActionList;
import com.ibm.brix.common.model.Artifact;
import com.ibm.brix.common.model.ArtifactKey;
import com.ibm.brix.common.model.ArtifactRelationship;
import com.ibm.brix.model.ArtifactRelationshipList;
import com.ibm.brix.common.model.AttributeProperties;
import com.ibm.brix.common.model.BRoute;
import com.ibm.brix.common.model.Endpoint;
import com.ibm.brix.model.GenericArtifact;
import com.ibm.brix.common.model.Rule;
import com.ibm.brix.common.model.RuleActionConfig;
import com.ibm.brix.model.RuleList;

public class FireRuleActionConfigs {
	/* CopyRight */
	public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

	private static final Logger LOG = LoggerFactory.getLogger(FireRuleActionConfigs.class);

	/**
	 * Method:  buildActions
	 * Processes each fired Rule to loop through the configured rule actions to build a list of Actions
	 * to be done.
	 * Exchange Expected Input:
	 *    - Header BRIX_BROUTE
	 *    - Header BRIX_ARTIFACT_RELATIONSHIPS
	 *    - Header BRIX_ASSERTED_RULES
	 *    - Header BRIX_DELTA_GENERIC_ARTIFACT
	 * Exchange Output:
	 *    - return Body: ActionList actionList
	 * 
	 * @param Exchange exchange (Provided by Camel)
	 * @return List<Action>
	 * @throws BrixException
	 */
	public ActionList buildActions(Exchange exchange) throws BrixException {
        LOG.info("Processing each fired Rule to generate Actions - Start");

    	int actionSequence = 0;
    	List<Action> actionList = new ArrayList<Action>();
    	// The grouping of Actions follows a basic set of logic, however the ActionCommandConfig's Map of Traits can also dictate further grouping
    	// conditions. This all results in a set of Action groups that other actions could be grouped/merged into. We need to keep track of where these
    	// Action groups are in the actionList we are generating. A Map is used to keep track of these, consisting of the Action group name (key) and the
    	// index location in actionList (value).
    	Map<String,Integer> actionGroupIdxs = new HashMap<String,Integer>();

        ActionList actionListObject = new ActionList();
        BRoute broute = exchange.getIn().getHeader(Constants.BRIX_BROUTE, BRoute.class);
        ArtifactRelationshipList artifactRelationshipList = exchange.getIn().getHeader(Constants.BRIX_ARTIFACT_RELATIONSHIPS, ArtifactRelationshipList.class);
        RuleList assertedRuleList = exchange.getIn().getHeader(Constants.BRIX_ASSERTED_RULES, RuleList.class);
        List<Rule> assertedRules = assertedRuleList.getRuleList();
        GenericArtifact deltaArtifactOrig = exchange.getIn().getHeader(Constants.BRIX_DELTA_GENERIC_ARTIFACT, GenericArtifact.class);
        // Make a copy of the original deltaArtifact so we can modify it as part of this bean processing.
        GenericArtifact deltaArtifact = new GenericArtifact(deltaArtifactOrig);

        // Ensure the list of fired Rules is sorted by its sequence numbers
        Collections.sort(assertedRules, (r1, r2) -> Integer.compare(r1.getSequence(), r2.getSequence()));
		
		// Process through the list of fired Rules.
		LOG.info("Processing " + assertedRules.size() + " fired Rules.");
        for (Rule assertedRule : assertedRules) {
			LOG.debug("Processing fired Rule: [" /*+ assertedRule.getId()*/ + "] '" + assertedRule.getName() + "', sequence " + assertedRule.getSequence());
			List<RuleActionConfig> ruleActionConfigs = assertedRule.getRuleActionConfigs();

	        // Ensure the list of RuleActionConfigs is sorted by its sequence numbers
			Collections.sort(ruleActionConfigs, (rc1, rc2) -> Integer.compare(rc1.getSequence(), rc2.getSequence()));

			LOG.info("Found " + ruleActionConfigs.size() + " RuleActionConfigs for Rule: " + assertedRule.getName());

			// At the start of a new Rule, reset the CHANGESTATE action list index by removing it from the actionGroupIdxs map,
			// as all CHANGESTATE actions must be executed separately. 
			actionGroupIdxs.remove(ActionCommand.CHANGESTATE.getName());
			
			// Process through the list of RuleActionConfgs for this Rule. Each RuleActionConfig is designed to generate its own Action.
			// However, the Action may need to be grouped/merged into another existing Action.
			for (RuleActionConfig ruleActionConfig : ruleActionConfigs) {
				LOG.debug("Processing RuleActionConfig: [" /*+ ruleActionConfig.getId()*/ + "] '" + ruleActionConfig.getName() + "', sequence " + ruleActionConfig.getSequence());

				Action action = generateAction(ruleActionConfig, broute, artifactRelationshipList, deltaArtifact, exchange);

				if (action != null) {
					ActionCommand actionCommand = action.getActionCommand();
					LOG.info("Action was generated for ActionCommand: " + actionCommand.getName());

					// Grouping logic - special (takes precedence over the major grouping logic):
					//   If the ActionCommandConfig on this RuleActionConfig contains a Trait called "group",
					//      merge this Action's Artifact attributes to the named group Action's Artifact attributes in the action list.
					//
					// Grouping logic - major:
					//   If the Action is a MODIFYATTRIBUTE, and a previous CHANGESTATE action exists FOR THIS RULE,
					//      merge this Action's Artifact attributes to the CHANGESTATE Action's Artifact attributes.
					//   If the Action is a MODIFYATTRIBUTE, and a previous CREATEARTIFACT action exists,
					//      merge this Action's Artifact attributes to the CREATEARTIFACT Action's Artifact attributes.
					//   If the Action is a MODIFYATTRIBUTE, and a previous MODIFYATTRIBUTE action exists,
					//      merge this Actions's Artifact attributes to the MODIFYATTRIBUTE Action's Artifact attributes.
					//   Otherwise we can add this Action to the ActionList.
					//
					// Assumptions:
					// - In all cases above, if the Action to merge with does not yet exist, then the current Action is added to the actionList.
					// - There should only ever be at most one CREATEARTIFACT action that applies to all Rules.
					// - A set of MODIFYATTRIBUTE actions following a CHANGESTATE action within the same Rule belong to the CHANGESTATE action.
					//
					// TODO: Other major action types like ADDCOMMENT and ADDLINK/REMOVELINK have not been implemented here yet.
					// TODO: This logic still does not group major actions that could be done together. E.g. for EWM, the main MODIFYATTRIBUTE
					//       action could be combined with the main CHANGESTATE action, but not for other system types. 

					boolean addNewAction = false;
					
					// Look for the "group" Trait on the ActionCommandConfig.
					String groupName = ruleActionConfig.getActionCommandConfig().getCommandTrait(Trait.GROUP);
					LOG.debug("ActionCommandConfig Trait '" + Trait.GROUP.getName() + "' = '" + groupName + "'");

					// A groupName was given and exists in the actionGroupIdxs map, then we'll merge this action with the one referenced. 
					if ((groupName != null) && actionGroupIdxs.containsKey(groupName)) {
						int actionGroupIdx = actionGroupIdxs.get(groupName);
						Action existingAction = actionList.get(actionGroupIdx);
						Action mergedAction = mergeActionAttributes(action, existingAction);
						actionList.set(actionGroupIdx, mergedAction);
						LOG.debug("Action was merged with previous group name '" + groupName + "' Action at index " + actionGroupIdx);
					}
					// A groupName was given, but does not exist in actionGroupIdxs map. Must add to actionList.
					else if ((groupName != null) && !(actionGroupIdxs.containsKey(groupName))) {
						addNewAction = true;
					}
					// This is a MODIFYATTRIBUTE Action and there is an existing CHANGESTATE Action, merge this action with the one referenced.
					else if ((actionCommand == ActionCommand.MODIFYATTRIBUTE) && (actionGroupIdxs.containsKey(ActionCommand.CHANGESTATE.getName()))) {
						int actionChangeStateIdx = actionGroupIdxs.get(ActionCommand.CHANGESTATE.getName());
						Action existingAction = actionList.get(actionChangeStateIdx);
						Action mergedAction = mergeActionAttributes(action, existingAction);
						actionList.set(actionChangeStateIdx, mergedAction);
						LOG.debug("Action was merged with main CHANGESTATE Action at index " + actionChangeStateIdx);
					}
					// This is a MODIFYATTRIBUTE Action and there is an existing CREATEARTIFACT Action, merge this action with the one referenced.
					else if ((actionCommand == ActionCommand.MODIFYATTRIBUTE) && (actionGroupIdxs.containsKey(ActionCommand.CREATEARTIFACT.getName()))) {
						int actionCreateArtifactIdx = actionGroupIdxs.get(ActionCommand.CREATEARTIFACT.getName());
						Action existingAction = actionList.get(actionCreateArtifactIdx);
						Action mergedAction = mergeActionAttributes(action, existingAction);
						actionList.set(actionCreateArtifactIdx, mergedAction);
						LOG.debug("Action was merged with main CREATEARTIFACT Action at index " + actionCreateArtifactIdx);
					}
					// This is a MODIFYATTRIBUTE Action and there is an existing major MODIFYATTRIBUTE Action, merge this action with the one referenced.
					else if ((actionCommand == ActionCommand.MODIFYATTRIBUTE) && (actionGroupIdxs.containsKey(ActionCommand.MODIFYATTRIBUTE.getName()))) {
						int actionModifyAttributeIdx = actionGroupIdxs.get(ActionCommand.MODIFYATTRIBUTE.getName());
						Action existingAction = actionList.get(actionModifyAttributeIdx);
						Action mergedAction = mergeActionAttributes(action, existingAction);
						actionList.set(actionModifyAttributeIdx, mergedAction);
						LOG.debug("Action was merged with main MODIFYATTRIBUTE Action at index " + actionModifyAttributeIdx);
					}
					// Else no grouping merge conditions were found. This Action will be added to the actionList.
					else {
						addNewAction = true;
					}

					if (addNewAction) {
						// Since the Rules and RuleActionConfigs have already been sequenced and sorted, at the time we are adding the
						// generated Action to the action list here, it should already be in a proper sequence processing order.
						// Hence we can use a global sequence to number the actions as we add them to the list.
						actionSequence++;
						action.setSequence(actionSequence);
						actionList.add(action);
						int actionIdx = actionList.size()-1;
						LOG.debug("New Action was added to the Action List as index number " + actionIdx);

						// Based on the type of action we're adding to the list, keep track of the index of where we added this one
						// in the actionGroupIdxs map as these may have other actions to be added to them.
						if (groupName != null) {
							actionGroupIdxs.put(groupName, actionIdx);
						}
						else if (actionCommand == ActionCommand.CREATEARTIFACT) {
							actionGroupIdxs.put(ActionCommand.CREATEARTIFACT.getName(), actionIdx);
						}
						else if (actionCommand == ActionCommand.CHANGESTATE) {
							actionGroupIdxs.put(ActionCommand.CHANGESTATE.getName(), actionIdx);
						}
						else if (actionCommand == ActionCommand.MODIFYATTRIBUTE) {
							actionGroupIdxs.put(ActionCommand.MODIFYATTRIBUTE.getName(), actionIdx);
						}
					}
				}
			}
        }
        Integer actionListSize = actionList.size();
        LOG.info("Number of actions generated: " + actionListSize);
        exchange.getIn().setHeader(Constants.BRIX_ACTIONLIST_SIZE, actionListSize);
		
		actionListObject.appendAll(actionList);
		return actionListObject;
	}

	/**
	 * Method:  generateAction
	 * Generates the Action from the RuleActionConfig
	 * 
	 * @param RuleActionConfig ruleActionConfig
	 * @param GenericArtifact deltaArtifact
	 * @param Exchange exchage
	 * @return Action
	 * @throws BrixException
	 */
	private Action generateAction(RuleActionConfig ruleActionConfig, BRoute broute, ArtifactRelationshipList artifactRelationshipList, GenericArtifact deltaArtifact, Exchange exchange) throws BrixException {

		Action action = new Action();
		ActionCommand actionCommand = ruleActionConfig.getActionCommand();
		action.setActionName(ruleActionConfig.getName());
		action.setActionCommand(actionCommand);
		
		Endpoint endpoint;
		ResourceSide resourceSide = ruleActionConfig.getResourceSide();
		switch(resourceSide) {
		case SOURCE:
			endpoint = broute.getSourceEndpoint();
			break;
		case TARGET:
			endpoint = broute.getTargetEndpoint();
			break;
		default:
			throw new IllegalArgumentException("FireRuleActionConfigs.generateAction() does not support the RuleActionConfig ResourceSide of: " + resourceSide.getName());
		}

		action.setResourceSide(resourceSide);
		action.setEndpointURI(endpoint.getAddressAsURI());
		action.setSystemType(endpoint.getSystemType());
		action.setCredential(endpoint.getCredential());
		action.setArtifactType(deltaArtifact.getArtifactType());	//It will be "generic" at this point, but reassigned at denormalization time.

		// Retrieve the appropriate ArtifactKey if we're syncing two artifacts. If a relationship does not yet exist, we don't have a key yet.
		if (artifactRelationshipList == null) {
			action.setArtifactKey(null);
		}
		else {
			ArtifactRelationship artifactRelationship = artifactRelationshipList.getTheArtifactRelationshipList().get(0);
			Artifact artifact = (resourceSide == ResourceSide.SOURCE) ? artifactRelationship.getSourceArtifact() : artifactRelationship.getTargetArtifact();
			ArtifactKey artifactKey = artifact.getArtifactKey();
			action.setArtifactKey(artifactKey);
		}
		
		LOG.debug("Generating new Action '" + action.getActionName() + "': actionCommand='" + actionCommand.getName() + "', endpointURI='" + action.getEndpointURI() + "', artifactKey='" + action.getArtifactKey() + "'");

		// Set up the Action's Artifact based on the ActionCommand and what is contained in the ActionCommandConfig.
		GenericArtifact actionArtifact = new GenericArtifact();
		ActionCommandConfig actionCommandConfig = ruleActionConfig.getActionCommandConfig();
		
		try {
			switch(actionCommand) {
			case CREATEARTIFACT:
				// We don't actually set up any Artifact attributes here. Those would be defined in subsequent MODIFYATTRIBUTE action command configs for
				// required attributes and normal syncing attributes.
				break;
			case CHANGESTATE:
				// TODO: We have not implemented State processing yet.
				actionArtifact = buildArtifactForCHANGESTATE(actionCommandConfig, deltaArtifact);
				break;
			case MODIFYATTRIBUTE:
				actionArtifact = buildArtifactForMODIFYATTRIBUTE(actionCommandConfig, deltaArtifact);
				break;
			case ADDCOMMENT:
				// TODO: We have not implemented comment processing yet.
				actionArtifact = buildArtifactForADDCOMMENT(actionCommandConfig, deltaArtifact);
				break;
			case ADDLINK:
				// TODO: We have not implemented add link processing yet.
				actionArtifact = buildArtifactForADDLINK(actionCommandConfig, deltaArtifact);
				break;
			case REMOVELINK:
				// TODO: We have not implemented remove link processing yet.
				actionArtifact = buildArtifactForREMOVELINK(actionCommandConfig, deltaArtifact);
				break;
			default:
				throw new IllegalArgumentException("FireRuleActionConfigs does not support the ActionCommand: " + actionCommand.getName());
			}
		}
		catch (Exception e) {
			throw new BrixException("FireRuleActionConfigs generateAction() error:\n", e);
		}

		// If the action Artifact was set to null, it indicates there should be no action to take.
		if (actionArtifact == null) {
			LOG.debug("No Generic Action Artifact attributes were added, therefore no Action is generated.");
			return null;
		}
		
		LOG.debug("Generic Action Artifact was built and added to this Action.");
		action.setActionArtifact(actionArtifact);
		return action;
	}

	/**
	 * Method:  buildArtifactForCHANGESTATE
	 * Generates the Generic action artifact for the CHANGESTATE action command.
	 * 
	 * @param ActionCommandConfig actionCommandConfig
	 * @param GenericArtifact deltaArtifact
	 * @return GenericArtifact
	 * @throws BrixException
	 */
	private GenericArtifact buildArtifactForCHANGESTATE(ActionCommandConfig actionCommandConfig, GenericArtifact deltaArtifact) throws BrixException {
		// TODO: We have not fully implemented State processing yet.
		GenericArtifact actionArtifact = new GenericArtifact();

		// Get the attribute properties from the delta Artifact for the state attribute provided as the Param Name from the ActionCommandConfig.
		// If there are related attributes that also need to be set as a result of the state change, these will be provided as separate RuleActionConfigs for this Rule.
		String stateAttribute = actionCommandConfig.getCommandParamName();
		AttributeProperties attributeProperties = deltaArtifact.getAttributeProperties(stateAttribute);
		if (attributeProperties == null) {
			LOG.debug("Attribute '" + stateAttribute + "' was not found in the Delta Artifact. No Action will be generated.");
			return null;									//Attribute was not found in delta Artifact.
		}

		// TODO: It could be here that additional logic for new methods could be built to do special processing using the ActionCommandConfig properties and Traits
		//       to be performed for the generic Artifact on the Action.

		// Add this to the Action Artifact.
		actionArtifact.addAttribute(stateAttribute, attributeProperties);
		LOG.debug("State attribute '" + stateAttribute + "' was added to the Generic Artifact for this Action.");

		// Once we have used this attribute from the delta Artifact, remove it, so we don't find it again and accidentally generate another action for it.
		deltaArtifact.removeAttribute(stateAttribute);
		
		return actionArtifact;
	}

	/**
	 * Method:  buildArtifactForMODIFYATTRIBUTE
	 * Generates the Generic action artifact for the MODIFYATTRIBUTE action command.
	 * 
	 * @param ActionCommandConfig actionCommandConfig
	 * @param GenericArtifact deltaArtifact
	 * @return GenericArtifact
	 * @throws BrixException
	 */
	private GenericArtifact buildArtifactForMODIFYATTRIBUTE(ActionCommandConfig actionCommandConfig, GenericArtifact deltaArtifact) throws BrixException {
		GenericArtifact actionArtifact = new GenericArtifact();

		// We expect the commandParamName on the ActionCommandConfig to be the attribute name.
		// Attempt to get the AttributeProperties from the delta Artifact, if the attribute exists.
		String attribute = actionCommandConfig.getCommandParamName();
		AttributeProperties attributeProperties = deltaArtifact.getAttributeProperties(attribute);

		// See if the ActionCommandConfig configuration has a default value specified. If so, determine if a default value should be used for this attribute.
		// If this attribute is required, determine if the default value should be used.
		if (actionCommandConfig.isRequired()) {
			attributeProperties = determineDefaultValue(attributeProperties, actionCommandConfig);
		}

		// TODO: It could be here that additional logic for new methods could be built to do special processing using the ActionCommandConfig properties
		//       (such as "substitute") and Traits to be performed for the attribute for the generic Artifact on the Action.

		// If the attributeProperties is null, then the attribute was not found in the delta Artifact, and a default value is not required. Don't create an Action.
		if (attributeProperties == null) {
			LOG.debug("Attribute '" + attribute + "' was not found in the Delta Artifact, or a default value was not required. No Action will be generated.");
			return null;
		}
		
		// Add this to the Action Artifact.
		actionArtifact.addAttribute(attribute, attributeProperties);
		LOG.debug("Attribute '" + attribute + "' was added to the Generic Artifact for this Action.");

		// Once we have used this attribute from the delta Artifact, remove it, so we don't find it again and accidentally generate another action for it.
		deltaArtifact.removeAttribute(attribute);
		
		return actionArtifact;
	}

	/**
	 * Method:  buildArtifactForADDCOMMENT
	 * Generates the Generic action artifact for the ADDCOMMENT action command.
	 * 
	 * @param ActionCommandConfig actionCommandConfig
	 * @param GenericArtifact deltaArtifact
	 * @return GenericArtifact
	 * @throws BrixException
	 */
	private GenericArtifact buildArtifactForADDCOMMENT(ActionCommandConfig actionCommandConfig, GenericArtifact deltaArtifact) throws BrixException {
		// TODO: We have not fully implemented Comment processing yet.
		GenericArtifact actionArtifact = new GenericArtifact();

		// Get the attribute properties from the delta Artifact for the comment attribute provided as the Param Name from the ActionCommandConfig.
		String commentAttribute = actionCommandConfig.getCommandParamName();
		AttributeProperties attributeProperties = deltaArtifact.getAttributeProperties(commentAttribute);
		if (attributeProperties == null) {
			LOG.debug("Attribute '" + commentAttribute + "' was not found in the Delta Artifact. No Action will be generated.");
			return null;									//Attribute was not found in delta Artifact.
		}

		// TODO: It could be here that additional logic for new methods could be built to do special processing using the ActionCommandConfig properties and Traits
		//       to be performed for the generic Artifact on the Action.

		// Add this to the Action Artifact.
		actionArtifact.addAttribute(commentAttribute, attributeProperties);
		LOG.debug("Comment attribute '" + commentAttribute + "' was added to the Generic Artifact for this Action.");
		

		// Once we have used this attribute from the delta Artifact, remove it, so we don't find it again and accidentally generate another action for it.
		deltaArtifact.removeAttribute(commentAttribute);
		
		return actionArtifact;
	}

	/**
	 * Method:  buildArtifactForADDLINK
	 * Generates the Generic action artifact for the ADDLINK action command.
	 * 
	 * @param ActionCommandConfig actionCommandConfig
	 * @param GenericArtifact deltaArtifact
	 * @return GenericArtifact
	 * @throws BrixException
	 */
	private GenericArtifact buildArtifactForADDLINK(ActionCommandConfig actionCommandConfig, GenericArtifact deltaArtifact) throws BrixException {
		// TODO: We have not fully implemented Add Link processing yet.
		GenericArtifact actionArtifact = new GenericArtifact();

		// Will link processing differ based on the SystemType?
		
		return actionArtifact;
	}

	/**
	 * Method:  buildArtifactForREMOVELINK
	 * Generates the Generic action artifact for the REMOVELINK action command.
	 * 
	 * @param ActionCommandConfig actionCommandConfig
	 * @param GenericArtifact deltaArtifact
	 * @return GenericArtifact
	 * @throws BrixException
	 */
	private GenericArtifact buildArtifactForREMOVELINK(ActionCommandConfig actionCommandConfig, GenericArtifact deltaArtifact) throws BrixException {
		// TODO: We have not fully implemented Remove Link processing yet.
		GenericArtifact actionArtifact = new GenericArtifact();

		// Will link processing differ based on the SystemType?
		
		return actionArtifact;
	}

	/**
	 * Method:  determineDefaultValue
	 * Determines if the given ActionCommandConfig default value should be used or not.
	 * 
	 * @param AttributeProperties currentAttributeProperties
	 * @param ActionCommandConfig actionCommandConfig
	 * @return GenericArtifact
	 * @throws BrixException
	 */
	private AttributeProperties determineDefaultValue(AttributeProperties attributeProperties, ActionCommandConfig actionCommandConfig) throws BrixException {
		AttributeProperties newAttributeProperties = null;

		// If attribute was found in the deltaArtifact 
		if (attributeProperties != null) {
			newAttributeProperties = attributeProperties;

			// If the current deltaArtifact attribute value is "blank", then use the configured default value for the new value.
			// Otherwise keep the existing attribute's value.
			// Note: At this time, the default value on actionCommandConfig only supports a String type.
			if (attributeProperties.getAttributeValue().toString().trim().isEmpty()) {
				newAttributeProperties.setAttributeValue(actionCommandConfig.getDefaultValue());
				newAttributeProperties.setAttributeType(AttributeType.STRING);
				LOG.debug("Default value '" + actionCommandConfig.getDefaultValue() + "' was used for attribute: " + actionCommandConfig.getCommandParamName());
			}
		}
		// Else was not in the deltaArtifact, but a default value is required for the attribute.
		// Create a new AttributeProperties for this attribute.
		else {
			// At this time, the default value on actionCommandConfig only supports a String type.
			// TODO: Is this true if the target attribute already has a non-blank value? This implies we need the target's original value.
			//       However, if we got here, it means a Rule fired (conditions asserted true), and the action config indicates to generate an
			//       action for this attribute.
			newAttributeProperties = new AttributeProperties(actionCommandConfig.getDefaultValue(), AttributeType.STRING);
			LOG.debug("Default value was used for attribute: " + actionCommandConfig.getCommandParamName());
		}
		
		return newAttributeProperties;
	}

	/**
	 * Method:  mergeAttributes
	 * Merges the Artifact attributes on the given Action with the Artifact attributes on the existing Action.
	 * 
	 * @param Action newAction
	 * @param int actionListIdx
	 * @return Action (The merged Action)
	 * @throws BrixException
	 */
	private Action mergeActionAttributes(Action newAction, Action existingAction) throws BrixException {

		try {
			Artifact newArtifact = newAction.getActionArtifact();
			Artifact existingArtifact = existingAction.getActionArtifact();
			existingArtifact.addAttributes(newArtifact.getAttributes());
			existingAction.setActionArtifact(existingArtifact);
		}
		catch (Exception e) {
			throw new BrixException("Error in FireRuleActionConfigs in mergeActionAttributes().", e);
		}

		return existingAction;
	}
}
