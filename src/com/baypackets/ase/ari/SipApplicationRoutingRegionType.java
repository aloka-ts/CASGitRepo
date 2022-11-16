/*
 * Copyright 2007 GENBAND, Inc. All rights reserved.
 * This software is the proprietary information of GENBAND, Inc.
 * Use is subject to license terms.
 */

package com.baypackets.ase.ari;

/**
 * Enumeration to provide routing region enumeration for application router.
 */
public enum SipApplicationRoutingRegionType {

    /**
     *  Routing region indicating none specified 
     */
    NEUTRAL_REGION, 
    /**
     *  Routing region for originating services 
     */
    ORIGINATING_REGION, 
    /**
     *  Routing region for terminating services
     */
    TERMINATING_REGION;
    
}
