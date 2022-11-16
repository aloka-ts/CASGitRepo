package com.baypackets.ase.jndi_jdbc.util;
import com.baypackets.ase.jndi_jdbc.ds.*;

/** This interface provides mechanism for the pool to create the object
*/
public interface PooledObjectFactory
{
    Object create();
    void cleanup();
}
