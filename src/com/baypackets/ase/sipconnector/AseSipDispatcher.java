
package com.baypackets.ase.sipconnector;

import com.baypackets.ase.dispatcher.Destination;
import com.baypackets.ase.dispatcher.Dispatcher;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.spi.container.SasMessage;

public class AseSipDispatcher implements Dispatcher {
    public int findDestination(Destination destination) {
    return 0;
    }

    public Destination getDestination(SasMessage request,
                  Destination destination,
                  AseHost host) {
    return destination;
    }
}
