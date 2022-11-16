package com.baypackets.ase.servicemgmt;



public class MessageHolder
{


    private String archiveLabel = null;
    private int serviceId;
    private String serviceVersion;
    private int operation;

    public MessageHolder( int opr , int servId , String servVersion , String archLabel)
    {
        operation = opr;
        serviceId = servId;
        serviceVersion = servVersion;
        archiveLabel = archLabel;

    }

    public MessageHolder( int opr , int servId , String servVersion )
    {
        operation = opr;
        serviceId = servId;
        serviceVersion = servVersion;
    }

    public int getOperation()
    {
        return operation;
    }

    public String getArchiveLabel()
    {
        return archiveLabel;
    }

    public int getServiceId()
    {
        return serviceId;
    }

    public String getServiceVersion()
    {
        return serviceVersion;
    }

}
