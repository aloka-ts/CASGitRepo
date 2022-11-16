package com.baypackets.ase.ra.diameter.gy.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.gy.generated.enums.EnumTerminationCause;

public enum TerminationCauseEnum
{
ADMINREBOOT,
ADMINRESET,
CALLBACK,
DIAMETERADMINISTRATIVE,
DIAMETERAUTHEXPIRED,
DIAMETERBADANSWER,
DIAMETERLINKBROKEN,
DIAMETERLOGOUT,
DIAMETERSERVICENOTPROVIDED,
DIAMETERSESSIONTIMEOUT,
DIAMETERUSERMOVED,
HOSTREQUEST,
IDLETIMEOUT,
LOSTCARRIER,
LOSTSERVICE,
NASERROR,
NASREBOOT,
NASREQUEST,
PORTDISABLED,
PORTERROR,
PORTPREEMPTED,
PORTREINIT,
PORTSUSPENDED,
PORTUNNEEDED,
REAUTHENTICATIONFAILURE,
SERVICEUNAVAILABLE,
SESSIONTIMEOUT,
SUPPLICANTRESTART,
USERERROR,
USERREQUEST;

private static Hashtable<TerminationCauseEnum,EnumTerminationCause> stackMapping = new Hashtable<TerminationCauseEnum,EnumTerminationCause>();
private static Hashtable<EnumTerminationCause,TerminationCauseEnum> containerMapping = new Hashtable<EnumTerminationCause,TerminationCauseEnum>();

 static {
stackMapping.put(TerminationCauseEnum.ADMINREBOOT, EnumTerminationCause.AdminReboot);
stackMapping.put(TerminationCauseEnum.ADMINRESET, EnumTerminationCause.AdminReset);
stackMapping.put(TerminationCauseEnum.CALLBACK, EnumTerminationCause.Callback);
stackMapping.put(TerminationCauseEnum.DIAMETERADMINISTRATIVE, EnumTerminationCause.DiameterAdministrative);
stackMapping.put(TerminationCauseEnum.DIAMETERAUTHEXPIRED, EnumTerminationCause.DiameterAuthExpired);
stackMapping.put(TerminationCauseEnum.DIAMETERBADANSWER, EnumTerminationCause.DiameterBadAnswer);
stackMapping.put(TerminationCauseEnum.DIAMETERLINKBROKEN, EnumTerminationCause.DiameterLinkBroken);
stackMapping.put(TerminationCauseEnum.DIAMETERLOGOUT, EnumTerminationCause.DiameterLogout);
stackMapping.put(TerminationCauseEnum.DIAMETERSERVICENOTPROVIDED, EnumTerminationCause.DiameterServiceNotProvided);
stackMapping.put(TerminationCauseEnum.DIAMETERSESSIONTIMEOUT, EnumTerminationCause.DiameterSessionTimeout);
stackMapping.put(TerminationCauseEnum.DIAMETERUSERMOVED, EnumTerminationCause.DiameterUserMoved);
stackMapping.put(TerminationCauseEnum.HOSTREQUEST, EnumTerminationCause.HostRequest);
stackMapping.put(TerminationCauseEnum.IDLETIMEOUT, EnumTerminationCause.IdleTimeout);
stackMapping.put(TerminationCauseEnum.LOSTCARRIER, EnumTerminationCause.LostCarrier);
stackMapping.put(TerminationCauseEnum.LOSTSERVICE, EnumTerminationCause.LostService);
stackMapping.put(TerminationCauseEnum.NASERROR, EnumTerminationCause.NASError);
stackMapping.put(TerminationCauseEnum.NASREBOOT, EnumTerminationCause.NASReboot);
stackMapping.put(TerminationCauseEnum.NASREQUEST, EnumTerminationCause.NASRequest);
stackMapping.put(TerminationCauseEnum.PORTDISABLED, EnumTerminationCause.PortDisabled);
stackMapping.put(TerminationCauseEnum.PORTERROR, EnumTerminationCause.PortError);
stackMapping.put(TerminationCauseEnum.PORTPREEMPTED, EnumTerminationCause.PortPreempted);
stackMapping.put(TerminationCauseEnum.PORTREINIT, EnumTerminationCause.PortReinit);
stackMapping.put(TerminationCauseEnum.PORTSUSPENDED, EnumTerminationCause.PortSuspended);
stackMapping.put(TerminationCauseEnum.PORTUNNEEDED, EnumTerminationCause.PortUnneeded);
stackMapping.put(TerminationCauseEnum.REAUTHENTICATIONFAILURE, EnumTerminationCause.ReauthenticationFailure);
stackMapping.put(TerminationCauseEnum.SERVICEUNAVAILABLE, EnumTerminationCause.ServiceUnavailable);
stackMapping.put(TerminationCauseEnum.SESSIONTIMEOUT, EnumTerminationCause.SessionTimeout);
stackMapping.put(TerminationCauseEnum.SUPPLICANTRESTART, EnumTerminationCause.SupplicantRestart);
stackMapping.put(TerminationCauseEnum.USERERROR, EnumTerminationCause.UserError);
stackMapping.put(TerminationCauseEnum.USERREQUEST, EnumTerminationCause.UserRequest);

containerMapping.put(EnumTerminationCause.AdminReboot, TerminationCauseEnum.ADMINREBOOT);
containerMapping.put(EnumTerminationCause.AdminReset, TerminationCauseEnum.ADMINRESET);
containerMapping.put(EnumTerminationCause.Callback, TerminationCauseEnum.CALLBACK);
containerMapping.put(EnumTerminationCause.DiameterAdministrative, TerminationCauseEnum.DIAMETERADMINISTRATIVE);
containerMapping.put(EnumTerminationCause.DiameterAuthExpired, TerminationCauseEnum.DIAMETERAUTHEXPIRED);
containerMapping.put(EnumTerminationCause.DiameterBadAnswer, TerminationCauseEnum.DIAMETERBADANSWER);
containerMapping.put(EnumTerminationCause.DiameterLinkBroken, TerminationCauseEnum.DIAMETERLINKBROKEN);
containerMapping.put(EnumTerminationCause.DiameterLogout, TerminationCauseEnum.DIAMETERLOGOUT);
containerMapping.put(EnumTerminationCause.DiameterServiceNotProvided, TerminationCauseEnum.DIAMETERSERVICENOTPROVIDED);
containerMapping.put(EnumTerminationCause.DiameterSessionTimeout, TerminationCauseEnum.DIAMETERSESSIONTIMEOUT);
containerMapping.put(EnumTerminationCause.DiameterUserMoved, TerminationCauseEnum.DIAMETERUSERMOVED);
containerMapping.put(EnumTerminationCause.HostRequest, TerminationCauseEnum.HOSTREQUEST);
containerMapping.put(EnumTerminationCause.IdleTimeout, TerminationCauseEnum.IDLETIMEOUT);
containerMapping.put(EnumTerminationCause.LostCarrier, TerminationCauseEnum.LOSTCARRIER);
containerMapping.put(EnumTerminationCause.LostService, TerminationCauseEnum.LOSTSERVICE);
containerMapping.put(EnumTerminationCause.NASError, TerminationCauseEnum.NASERROR);
containerMapping.put(EnumTerminationCause.NASReboot, TerminationCauseEnum.NASREBOOT);
containerMapping.put(EnumTerminationCause.NASRequest, TerminationCauseEnum.NASREQUEST);
containerMapping.put(EnumTerminationCause.PortDisabled, TerminationCauseEnum.PORTDISABLED);
containerMapping.put(EnumTerminationCause.PortError, TerminationCauseEnum.PORTERROR);
containerMapping.put(EnumTerminationCause.PortPreempted, TerminationCauseEnum.PORTPREEMPTED);
containerMapping.put(EnumTerminationCause.PortReinit, TerminationCauseEnum.PORTREINIT);
containerMapping.put(EnumTerminationCause.PortSuspended, TerminationCauseEnum.PORTSUSPENDED);
containerMapping.put(EnumTerminationCause.PortUnneeded, TerminationCauseEnum.PORTUNNEEDED);
containerMapping.put(EnumTerminationCause.ReauthenticationFailure, TerminationCauseEnum.REAUTHENTICATIONFAILURE);
containerMapping.put(EnumTerminationCause.ServiceUnavailable, TerminationCauseEnum.SERVICEUNAVAILABLE);
containerMapping.put(EnumTerminationCause.SessionTimeout, TerminationCauseEnum.SESSIONTIMEOUT);
containerMapping.put(EnumTerminationCause.SupplicantRestart, TerminationCauseEnum.SUPPLICANTRESTART);
containerMapping.put(EnumTerminationCause.UserError, TerminationCauseEnum.USERERROR);
containerMapping.put(EnumTerminationCause.UserRequest, TerminationCauseEnum.USERREQUEST);
}

public static final TerminationCauseEnum getContainerObj(EnumTerminationCause stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumTerminationCause getStackObj(TerminationCauseEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static TerminationCauseEnum fromCode(int value){
	return getContainerObj(EnumTerminationCause.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumTerminationCause.getName(key);
}

public static boolean isValid(int value){
	return EnumTerminationCause.isValid(value);
}

public static int[] keys(){
	return EnumTerminationCause.keys();
}
}
