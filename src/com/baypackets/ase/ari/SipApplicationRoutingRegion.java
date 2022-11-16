/*
 * Copyright 2007 GENBAND, Inc. All rights reserved.
 * This software is the proprietary information of GENBAND, Inc.
 * Use is subject to license terms.
 */

package com.baypackets.ase.ari;

/**
 * Enumeration to provide routing region enumeration for application router.
 */
public final class SipApplicationRoutingRegion {

    public static final SipApplicationRoutingRegion ORIGINATING_REGION =
        new SipApplicationRoutingRegion("ORIGINATING_REGION",
                                        SipApplicationRoutingRegionType.ORIGINATING_REGION);

    public static final SipApplicationRoutingRegion TERMINATING_REGION =
        new SipApplicationRoutingRegion("TERMINATING_REGION",
                                        SipApplicationRoutingRegionType.TERMINATING_REGION);

    public static final SipApplicationRoutingRegion NEUTRAL_REGION =
        new SipApplicationRoutingRegion("NEUTRAL_REGION",
                                        SipApplicationRoutingRegionType.NEUTRAL_REGION);

    public SipApplicationRoutingRegion(String label, SipApplicationRoutingRegionType type)
    {
        this.label = label;
        this.type = type;
    }

    public String getLabel() {
        return this.label;
    }

    public SipApplicationRoutingRegionType getType() {
        return this.type;
    }

    public String toString () {
        return(label);
    }

    public static SipApplicationRoutingRegion valueOf(String region) throws IllegalArgumentException {
	if (region == null) {
	    throw new IllegalArgumentException("Invalid value for SipApplicationRoutingRegionType");
	} else if (region.equals("ORIGINATING_REGION")) {
	    return ORIGINATING_REGION;
	} else 	if (region.equals("TERMINATING_REGION")) {
	    return TERMINATING_REGION;
	} else 	if (region.equals("NEUTRAL_REGION")) {
	    return NEUTRAL_REGION;
	} else {
	    throw new IllegalArgumentException("Invalid value for SipApplicationRoutingRegionType");
	}
    }

    private String label;
    private SipApplicationRoutingRegionType type;
}
