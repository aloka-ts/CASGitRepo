/*
 * ExternalDeviceDAO.java
 *
 */
package com.baypackets.ase.externaldevice;

import java.util.Collection;

/**
 * The base interface for external devices where access/connectivity is managed
 * by the platform.
 */
public interface ExternalDeviceDAO {
    
    /**
     * This method returns the list of all ExternalDevice objects from the backing
     * store encapsulate the meta data for the device.
     *
     * @return  A Collection of ExternalDevice objects.
     * @see com.baypackets.ase.sbb.ExternalDevice
     */
    public Collection getAllDevices();
    
}
