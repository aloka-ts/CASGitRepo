Configuration Files:
====================

    The SAS provides the following configuration files.

    a. ase.properties 	             : Contains all the configuration parameters used by SAS.
			               In case of the SAS running with EMS, the configuration values
                                       defined here would be over-ridden with the values defined through EMS.

     
    b. log-4j.xml                    : This file contains the Log4j configuration

    c. measurement-file-config.xml   : This file contains the advanced configurations 
                                       for creating the measurement file.

    d. media-server-config.xml       : This file contains the meta data on all media servers that are to be 
                                       provisioned with the platform and made available for use by SIP Servlet 
	                               applications.

    e. replication-config.xml        : This file contains the advanced configurations for the replication.

    f. server-ocm.xml                : This file contains the advanced configurations for the Overload Control Management.


Procedure for Creating a Non-EMS SAS FT setup:
==============================================

   Pre-requisites:
   ~~~~~~~~~~~~~~
        1. The SAS should be installed in Non-FT mode in each of the machine that should be part of the FT setup.

        2. Each of the machines should have a System Monitor running. 
           This will be used for setting and unsetting the Floating IP addresses.

        3. Baypacket's SIP Load balancer might be optionally required 
           in case SAS has to get the floating IP dynamically for the N+K setup. 
           But this is an OPTIONAL requirement. 
           In case, load balancer is not available, 
           a separate floating IP should be statically set for each of the SAS instance.

   Configuration Changes:
   ~~~~~~~~~~~~~~~~~~~~~	
        1. Go to each of the SAS instance separately and update the following OIDs/properties 
           in the ase.properties file to convert the setup into an FT setup.

            30.1.25     Set this OID with a reference Network IP address. 
                        The reference IP address would be used by the SAS instances 
                        to identify whether they are in the network or got isolated. 
                        If the SAS is able to ping this IP address, 
                        then it will assume it is not isolated. 
                        When it is not able to ping this IP address, 
                        the SAS will assume that it got isolated and it will go down on its own. 

            30.1.31     Peer IP Address. In case of 1+1 FT setup, 
                        set this OID with the IP address of the peer SAS instance. 
                        In case of N+1, set this OID with the comma-separated list of 
                        IP addresses of the all the peer SAS instances.

            30.1.32     Subsystem Name. Set the name for this subsystem. 
                        The subsystem name should be unique for each SAS instance within the cluster. 
                        For example, in case of 2+1 setup, 
                        each of the SAS instance could have a name SAS1, SAS2 and SAS3 respectively.

            30.1.11     Sip Connector IP address. In case of 1+1 setup, 
                        set this OID with the floating IP address. 
                        In case of N+1 setup, if the BayPackets load balancer is used, 
                        set this OID to 0.0.0.0, 
                        otherwise assign a separate floating IP address for each of the SAS instance.

           30.1.33      Installation Mode. In case of 1+1, set this OID with value 2. 
                        In case of N+1 setup, set this OID value to 3. For a HA setup, set this OID value to 4.


           30.1.28      Cluster Members. Set this OID with the comma-separated list of 
                        names of all the SAS instances in the cluster. 
                        This OID should be same for all the SAS instances present in the cluster. 
                        For example, in case of 2+1 setup, 
                        if the SAS instances were named as SAS1, SAS2, SAS3, 
                        then this OID would have a value SAS1,SAS2,SAS3

           68.4.1       Load Balancer IP (OPTIONAL). 
                        This OID is required if the SAS has to be integrated with the Baypackets SIP Load Balancer. 
                        In that case, set this OID with the actual IP/floating IP of the load balancer; 
                        otherwise set this OID to 0.0.0.0

           68.1.3       Floating IP manager port (OPTIONAL). 
                        This OID is required if the SAS has to be integrated with the Baypackets SIP Load Balancer. 
                        In that case, set this OID with the floating IP manager port of the load balancer; 
                        otherwise do not set any value.

           ft.sas.cluster.name          Use this property to set the name for the cluster. 
                                        This OID required to be changed only 
                                        if there are more that one SAS instances running in the same machine 
                                        that is part of different SAS cluster.

           Sysmon_Gen_Port              Set the port where the system monitor is listening.


       2. Restart each of the SAS instances.
