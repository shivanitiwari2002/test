//=============================================================================
//* Name:         AbstractEntity.java
//*
//* Application:  System DevOps Bridge-Interceptor eXtreme (BrIX)
//*
//* Module Name:  BrIX Entity
//*
//* Description:  This abstract data object class holds the BrIX's implementation of an
//*               Entity.  At high level, an Entity represents any configuration data object
//*               within BrIX. All TABLE_PER_CLASS table configuration data model classes
//*               should extend this class.  See AbstractEntitySingle.java for
//*               SINGLE_TABLE inheritance.
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
//*               20250412 tollefso  Support for persistence.
//*
//* Additional notes about the Change Activity:
//*
//=============================================================================
package com.ibm.brix.common.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;

import com.ibm.brix.common.CommonConstants;

import java.time.Instant;
import java.time.ZoneId;

@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Entity
public abstract class AbstractEntity extends PanacheEntity {
    /* CopyRight */
    public static final String COPYRIGHT = "Licensed Materials - Property of IBM " + "(C) COPYRIGHT 2023 All Rights Reserved. US Government Users restricted Rights - Use, Duplication or Disclosure restricted by GSA ADP Schedule Contract with IBM Corp.";

    // Common fields to all subclasses
    protected Instant createTime = Instant.now();
    protected Instant updateTime;

    /* Constructor */
    protected AbstractEntity() {
        this.createTime = this.updateTime = Instant.now();
    }

    public long getId() {
        return id;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Instant created) {
        this.createTime = created;
    }

    public Instant getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Instant lastModified) {
        this.updateTime = lastModified;
    }

    /**
     * Method: updateTime
     * Assigns current date/time to the update_time field.
     */
    protected void updateTime() {
        this.updateTime = Instant.now();
    }

    /**
     * Method: toString
     * Returns a simple string representation of the data at this object level. Can be called by super.toString() from subclasses.
     */
    public String toString() {
        ZoneId zoneId = ZoneId.of(CommonConstants.TIMEZONE_DISPLAY) ; 
        StringBuilder dataObjStr = new StringBuilder();
        dataObjStr.append("id: ");
        dataObjStr.append(this.id);
        dataObjStr.append(", createTime: ");
        dataObjStr.append(this.getCreateTime().atZone(zoneId));
        dataObjStr.append(", updateTime: ");
        dataObjStr.append(this.getUpdateTime().atZone(zoneId));
        return dataObjStr.toString();
    }
}
