package com.baypackets.ase.serializer.kryo;

/**
 * Created by ankitsinghal on 01/03/16.
 *
 * Class containing the fully qualified class names for the classes that needs to be registerd with the kryo object.
 * It is to be noted that only the classes which are in aware of AseClassloader can be added here.
 */
public class KryoRegisterables {

    static final String[] jdkClassesToRegister = new String[]{
            "java.util.Hashtable",
            "java.util.LinkedList",
    };

    static final String[] aseClassesToRegister = new String[]{
            "javax.servlet.sip.ar.SipApplicationRoutingRegion",
            "javax.servlet.sip.ar.SipApplicationRoutingRegionType",
            "com.baypackets.ase.spi.replication.ReplicableMap",
            "com.baypackets.ase.spi.replication.Replicables",
            "com.baypackets.ase.spi.replication.ReplicableList",
            "com.baypackets.ase.spi.replication.Replicable",
            "com.baypackets.ase.dispatcher.Destination",
            "com.baypackets.ase.cdr.CDRImpl",
            "com.baypackets.ase.sbb.CDR",
            "com.baypackets.ase.container.sip.SipApplicationSessionImpl",
            "com.baypackets.ase.container.sip.AseSipApplicationChain",
            "com.baypackets.ase.container.AseChainInfo",
            "com.baypackets.ase.container.AseIc",
            "com.baypackets.ase.sipconnector.AseSipSession",
            "com.baypackets.ase.sipconnector.AseEvictingQueue",
            "com.baypackets.ase.sipconnector.AseSipMessageInfo",
            "com.baypackets.ase.sipconnector.AseSipServletMessage$ReplicatedSessionHolder",
            "com.baypackets.ase.sipconnector.AseSipServerTransactionImpl",
            "com.baypackets.ase.sipconnector.AseSipServletRequest",
            "com.baypackets.ase.sipconnector.AseSipServletMessage",
            "com.baypackets.ase.sipconnector.AseSipURIImpl",
            "com.baypackets.ase.sipconnector.AseSipSessionStateImpl",
            "com.baypackets.ase.sipconnector.AseSipSessionStateImpl$SessionStateAttributes",
            "com.baypackets.ase.sipconnector.AseAddressImpl",
            "com.baypackets.ase.sipconnector.AseSipDialogId",
            "com.baypackets.ase.sipconnector.AseSipClientTransactionIImpl",
            "com.baypackets.ase.sipconnector.AseSipServerTransactionIImpl",
            "com.baypackets.ase.sipconnector.headers.AseSipDiversionHeader",
            "com.baypackets.ase.replication.ReplicatedMessageHolder",
    };

    static final String[] dsClassesToRegister = new String[]{
            "com.dynamicsoft.DsLibs.DsSipObject.DsSipViaHeader",
            "com.dynamicsoft.DsLibs.DsSipObject.DsSipToHeader",
            "com.dynamicsoft.DsLibs.DsSipObject.DsSipFromHeader",
            "com.dynamicsoft.DsLibs.DsSipObject.DsByteString",
            "com.dynamicsoft.DsLibs.DsSipObject.DsSipURL",
            "com.dynamicsoft.DsLibs.DsSipObject.DsSipNameAddress",
            "com.dynamicsoft.DsLibs.DsUtil.DsBindingInfo",
            "com.dynamicsoft.DsLibs.DsSipObject.DsSipDialogID",
            "com.dynamicsoft.DsLibs.DsSipObject.DsSipClassicTransactionKey",
            "com.dynamicsoft.DsLibs.DsSipObject.DsSipByeMessage",
            "com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderInterface",
            "com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderList",
            "com.dynamicsoft.DsLibs.DsSipObject.DsSipMaxForwardsHeader",
            "com.dynamicsoft.DsLibs.DsSipObject.DsParameter",
            "com.dynamicsoft.DsLibs.DsSipObject.DsParameters",
            "com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderString",
            "com.dynamicsoft.DsLibs.DsSipObject.DsSipContactHeader",
            "com.dynamicsoft.DsLibs.DsUtil.DsDiscreteTimerTask",
            "com.dynamicsoft.DsLibs.DsUtil.DsDiscreteTimerTaskNoQ",
            "com.dynamicsoft.DsLibs.DsSipLlApi.DsSipClientTransactionImpl$ConnectionWrapper",
            "com.dynamicsoft.DsLibs.DsSipObject.DsSipInviteMessage",
            "com.dynamicsoft.DsLibs.DsSipObject.DsSipUnknownHeader",
            "com.dynamicsoft.DsLibs.DsSipObject.DsSipSessionExpiresHeader",
            "com.dynamicsoft.DsLibs.DsSipObject.DsSipSupportedHeader",
            "com.dynamicsoft.DsLibs.DsSipObject.DsSipServerObj",
            "com.dynamicsoft.DsLibs.DsSipObject.DsSipResponse",
    };

    static final String[] miscClassesToRegister = new String[]{
            "gnu.trove.list.array.TIntArrayList",
    };
}