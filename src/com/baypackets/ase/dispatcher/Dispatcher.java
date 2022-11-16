/*------------------------------------------
* Dispatcher interface
* Nasir
* Version 1.0   08/19/04
* BayPackets Inc.
* Revisions:
* BugID : Date : Info
*------------------------------------------*/

package com.baypackets.ase.dispatcher;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.container.AseHost;

public interface Dispatcher
{

  public static final int DESTINATION_FOUND = 0;
  public static final int NO_DESTINATION_FOUND = 1;
  public static final int LOOP_DETECTED = 2;
  public static final int PROCESSING_OVER = 3;
  public static final int EXTERNAL_ROUTE = 4;

  public Destination getDestination (SasMessage req, Destination destination, AseHost host);
}
