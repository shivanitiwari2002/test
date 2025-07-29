//=============================================================================
//* Name:         ActionCommandConfig.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Action Command Configuration
//*                                                                
//* Description:  The Action Command Config class contains information on how the
//*               Action Command is to be generated in order to tell "what" the
//*               command is to do. Examples include:
//*               - giving a default value to an attribute that maybe isn't set
//*               - substitute or add additional text into an attribute
//*               - being able to add a comment with specific text
//*               An Action Command Config also provides the ability to conditionally
//*               set the Action based on a predicate, value, and operator definition.
//*               As a means to add additional logic, the Action Command Config
//*               contains a simple map of Traits and values that can be used
//*               for specialized processing.
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
//*        129113 20250324 tmueller  New source file created.
//*                                                                 
//* Additional notes about the Change Activity:
//*                                                                 
//=============================================================================
package com.ibm.brix.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

import java.util.HashMap;
import java.util.Map;

import com.ibm.brix.common.CommonConstants;
import com.ibm.brix.common.enums.PredicateType;
import com.ibm.brix.common.enums.RuleOperator;
import com.ibm.brix.common.enums.Trait;
import com.ibm.brix.common.model.AbstractEntity;

@Entity
public class ActionCommandConfig extends AbstractEntity {
    /* CopyRight */
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2025 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    @Column(length = 255, unique = true, nullable = false)
    private String name;

    private String commandParamName;
    private boolean required;
    private String substituteString;
    private String defaultValue;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private PredicateType commandParamType;

    private String commandParamPattern;

    @Enumerated(EnumType.STRING)
    private RuleOperator commandParamOperator;

    @ElementCollection
    @MapKeyEnumerated(EnumType.STRING)
    private Map<Trait,String> commandTraits;    //Should required, substitute, and defaultValue just be included in the Traits map?

    /* Constructor */
    public ActionCommandConfig() {
        super();
        this.name = CommonConstants.UNDEFINED;
        this.commandParamName = CommonConstants.UNDEFINED;
        this.required = false;
        this.substituteString = "";
        this.defaultValue = "";
        this.commandParamType = PredicateType.UNDEFINED;
        this.commandParamPattern = "";
        this.commandParamOperator = RuleOperator.UNDEFINED;
        this.commandTraits = new HashMap<Trait,String>();
    }

    /* Constructor */
    public ActionCommandConfig(String name, String commandParamName, boolean required, String substituteString, String defaultValue, PredicateType commandParamType, String commandParamPattern, RuleOperator commandParamOperator, Map<Trait,String> commandTraits) {
        super();
        this.name = name;
        this.commandParamName = commandParamName;
        this.required = required;
        this.substituteString = substituteString;
        this.defaultValue = defaultValue;
        this.commandParamType = commandParamType;
        this.commandParamPattern = commandParamPattern;
        this.commandParamOperator = commandParamOperator;
        this.commandTraits = commandTraits;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        updateTime();
    }

    public String getCommandParamName() {
        return this.commandParamName;
    }

    public void setCommandParamName(String commandParamName) {
        this.commandParamName = commandParamName;
        updateTime();
    }

    public boolean isRequired() {
        return this.required;
    }

    public void setRequired(boolean required) {
        this.required = required;
        updateTime();
    }

    public String getSubstituteString() {
        return this.substituteString;
    }

    public void setSubstituteString(String substituteString) {
        this.substituteString = substituteString;
        updateTime();
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        updateTime();
    }

    public PredicateType getCommandParamType() {
        return this.commandParamType;
    }

    public void setCommandParamType(PredicateType commandParamType) {
        this.commandParamType = commandParamType;
        updateTime();
    }

    public String getCommandParamPattern() {
        return this.commandParamPattern;
    }

    public void setCommandParamPattern(String commandParamPattern) {
        this.commandParamPattern = commandParamPattern;
        updateTime();
    }

    public RuleOperator getCommandParamOperator() {
        return this.commandParamOperator;
    }

    public void setCommandParamOperator(RuleOperator commandParamOperator) {
        this.commandParamOperator = commandParamOperator;
        updateTime();
    }

    public Map<Trait,String> getCommandTraits() {
        return this.commandTraits;
    }

    public void setCommandTraits(Map<Trait,String> commandTraits) {
        this.commandTraits = commandTraits;
        updateTime();
    }

    public String getCommandTrait(Trait traitName) {
        return this.commandTraits.get(traitName);
    }

    public String toString() {
        StringBuilder dataObjStr = new StringBuilder();
        dataObjStr.append("ActionCommandConfig{");
        dataObjStr.append(super.toString());
        dataObjStr.append(", name: ");
        dataObjStr.append(this.getName());
        dataObjStr.append(", commandParamName: ");
        dataObjStr.append(this.getCommandParamName());
        dataObjStr.append(", required: ");
        dataObjStr.append(this.isRequired());
        dataObjStr.append(", substituteString: ");
        dataObjStr.append(this.getSubstituteString());
        dataObjStr.append(", defaultValue: ");
        dataObjStr.append(this.getDefaultValue());
        dataObjStr.append(", commandParamType: ");
        dataObjStr.append(this.getCommandParamType().getName());
        dataObjStr.append(", commandParamPattern: ");
        dataObjStr.append(this.getCommandParamPattern());
        dataObjStr.append(", commandParamOperator: ");
        dataObjStr.append(this.getCommandParamOperator().getName());
        dataObjStr.append(", commandTraits: ");
        dataObjStr.append(this.getCommandTraits().toString());
        dataObjStr.append("}");

        return dataObjStr.toString();
    }
   
    public void updateFrom(ActionCommandConfig updated) {
        this.setName(updated.getName());
        this.setCommandParamName(updated.getCommandParamName());
        this.setRequired(updated.isRequired());
        this.setSubstituteString(updated.getSubstituteString());
        this.setDefaultValue(updated.getDefaultValue());
        this.setCommandParamType(updated.getCommandParamType());
        this.setCommandParamPattern(updated.getCommandParamPattern());
        this.setCommandParamOperator(updated.getCommandParamOperator());
        this.setCommandTraits(updated.getCommandTraits());
    }
}
