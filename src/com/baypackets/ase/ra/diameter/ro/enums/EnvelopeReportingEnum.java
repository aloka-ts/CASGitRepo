package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumEnvelopeReporting;

public enum EnvelopeReportingEnum
{
DO_NOT_REPORT_ENVELOPES,
REPORT_ENVELOPES,
REPORT_ENVELOPES_WITH_EVENTS,
REPORT_ENVELOPES_WITH_VOLUME,
REPORT_ENVELOPES_WITH_VOLUME_AND_EVENTS;

private static Hashtable<EnvelopeReportingEnum,EnumEnvelopeReporting> stackMapping = new Hashtable<EnvelopeReportingEnum,EnumEnvelopeReporting>();
private static Hashtable<EnumEnvelopeReporting,EnvelopeReportingEnum> containerMapping = new Hashtable<EnumEnvelopeReporting,EnvelopeReportingEnum>();

 static {
stackMapping.put(EnvelopeReportingEnum.DO_NOT_REPORT_ENVELOPES, EnumEnvelopeReporting.DO_NOT_REPORT_ENVELOPES);
stackMapping.put(EnvelopeReportingEnum.REPORT_ENVELOPES, EnumEnvelopeReporting.REPORT_ENVELOPES);
stackMapping.put(EnvelopeReportingEnum.REPORT_ENVELOPES_WITH_EVENTS, EnumEnvelopeReporting.REPORT_ENVELOPES_WITH_EVENTS);
stackMapping.put(EnvelopeReportingEnum.REPORT_ENVELOPES_WITH_VOLUME, EnumEnvelopeReporting.REPORT_ENVELOPES_WITH_VOLUME);
stackMapping.put(EnvelopeReportingEnum.REPORT_ENVELOPES_WITH_VOLUME_AND_EVENTS, EnumEnvelopeReporting.REPORT_ENVELOPES_WITH_VOLUME_AND_EVENTS);

containerMapping.put(EnumEnvelopeReporting.DO_NOT_REPORT_ENVELOPES, EnvelopeReportingEnum.DO_NOT_REPORT_ENVELOPES);
containerMapping.put(EnumEnvelopeReporting.REPORT_ENVELOPES, EnvelopeReportingEnum.REPORT_ENVELOPES);
containerMapping.put(EnumEnvelopeReporting.REPORT_ENVELOPES_WITH_EVENTS, EnvelopeReportingEnum.REPORT_ENVELOPES_WITH_EVENTS);
containerMapping.put(EnumEnvelopeReporting.REPORT_ENVELOPES_WITH_VOLUME, EnvelopeReportingEnum.REPORT_ENVELOPES_WITH_VOLUME);
containerMapping.put(EnumEnvelopeReporting.REPORT_ENVELOPES_WITH_VOLUME_AND_EVENTS, EnvelopeReportingEnum.REPORT_ENVELOPES_WITH_VOLUME_AND_EVENTS);
}

public static final EnvelopeReportingEnum getContainerObj(EnumEnvelopeReporting stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumEnvelopeReporting getStackObj(EnvelopeReportingEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static EnvelopeReportingEnum fromCode(int value){
	return getContainerObj(EnumEnvelopeReporting.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumEnvelopeReporting.getName(key);
}

public static boolean isValid(int value){
	return EnumEnvelopeReporting.isValid(value);
}

public static int[] keys(){
	return EnumEnvelopeReporting.keys();
}
}
