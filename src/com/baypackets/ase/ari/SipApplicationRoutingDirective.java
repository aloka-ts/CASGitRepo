
/*
 * Copyright 2007 GENBAND, Inc. All rights reserved.
 * This software is the proprietary information of GENBAND, Inc.
 * Use is subject to license terms.
 */

package com.baypackets.ase.ari;

/**
 * Enumeration to provide routing directive enumeration for application router.
 */
public enum SipApplicationRoutingDirective {
    /**
     *  Routing directive indicating a continued request
     */
    CONTINUE,
    /**
     *  Routing directive indicating a new request
     */
    NEW,
    /**
     *  Routing directive indicating a service reversal (not currently supported)
     */
    REVERSE;
}
