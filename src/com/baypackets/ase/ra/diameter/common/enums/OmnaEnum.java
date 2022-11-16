package com.baypackets.ase.ra.diameter.common.enums;

import java.util.Hashtable;

import com.traffix.openblox.diameter.enums.EnumOmna;

public enum OmnaEnum
{
APPLICATION,
APPLICATIONJAVAVM,
APPLICATIONPKCS7MIME,
APPLICATIONVNDOMADD2XML,
APPLICATIONVNDOMADDXML,
APPLICATIONVNDOMADRMCONTENT,
APPLICATIONVNDOMADRMMESSAGE,
APPLICATIONVNDOMADRMRIGHTSWBXML,
APPLICATIONVNDOMADRMRIGHTSXML,
APPLICATIONVNDSYNCMLDMWBXML,
APPLICATIONVNDSYNCMLDMXML,
APPLICATIONVNDSYNCMLDSNOTIFICATION,
APPLICATIONVNDSYNCMLNOTIFICATION,
APPLICATIONVNDWAPCERTRESPONSE,
APPLICATIONVNDWAPCOC,
APPLICATIONVNDWAPCONNECTIVITYWBXML,
APPLICATIONVNDWAPHASHEDCERTIFICATE,
APPLICATIONVNDWAPLOCCWBXML,
APPLICATIONVNDWAPLOCXML,
APPLICATIONVNDWAPMMSMESSAGE,
APPLICATIONVNDWAPMULTIPART,
APPLICATIONVNDWAPMULTIPARTALTERNATIVE,
APPLICATIONVNDWAPMULTIPARTBYTERANGES,
APPLICATIONVNDWAPMULTIPARTFORMDATA,
APPLICATIONVNDWAPMULTIPARTMIXED,
APPLICATIONVNDWAPMULTIPARTRELATED,
APPLICATIONVNDWAPROLLOVERCERTIFICATE,
APPLICATIONVNDWAPSIA,
APPLICATIONVNDWAPSIC,
APPLICATIONVNDWAPSIGNEDCERTIFICATE,
APPLICATIONVNDWAPSLC,
APPLICATIONVNDWAPUAPROF,
APPLICATIONVNDWAPWBXML,
APPLICATIONVNDWAPWMLC,
APPLICATIONVNDWAPWMLSCRIPTC,
APPLICATIONVNDWAPWTAEVENTC,
APPLICATIONVNDWAPWTLSCACERTIFICATE,
APPLICATIONVNDWAPWTLSUSERCERTIFICATE,
APPLICATIONVNDWAPXHTMLXML,
APPLICATIONVNDWVCSPCIR,
APPLICATIONVNDWVCSPWBXML,
APPLICATIONVNDWVCSPXML,
APPLICATIONWMLXML,
APPLICATIONXHDMLC,
APPLICATIONXHTMLXML,
APPLICATIONXML,
APPLICATIONXWWWFORMURLENCODED,
APPLICATIONXX509CACERT,
APPLICATIONXX509USERCERT,
APPLICATIONXX968CACERT,
APPLICATIONXX968CROSSCERT,
APPLICATIONXX968USERCERT,
AUDIO,
EMPTY,
IMAGE,
IMAGEGIF,
IMAGEJPEG,
IMAGEPNG,
IMAGETIFF,
IMAGEVNDWAPWBMP,
MULTIPART,
MULTIPARTALTERNATIVE,
MULTIPARTBYTERANTES,
MULTIPARTFORMDATA,
MULTIPARTMIXED,
TEXT,
TEXTCSS,
TEXTHTML,
TEXTPLAIN,
TEXTVNDWAPCO,
TEXTVNDWAPCONNECTIVITYXML,
TEXTVNDWAPSI,
TEXTVNDWAPSL,
TEXTVNDWAPWML,
TEXTVNDWAPWMLSCRIPT,
TEXTVNDWAPWTAEVENT,
TEXTXHDML,
TEXTXML,
TEXTXTTML,
TEXTXVCALENDAR,
TEXTXVCARD,
VIDEO;

private static Hashtable<OmnaEnum,EnumOmna> stackMapping = new Hashtable<OmnaEnum,EnumOmna>();
private static Hashtable<EnumOmna,OmnaEnum> containerMapping = new Hashtable<EnumOmna,OmnaEnum>();

 static {
stackMapping.put(OmnaEnum.APPLICATION, EnumOmna.Application);
stackMapping.put(OmnaEnum.APPLICATIONJAVAVM, EnumOmna.ApplicationJavaVm);
stackMapping.put(OmnaEnum.APPLICATIONPKCS7MIME, EnumOmna.ApplicationPkcs7Mime);
stackMapping.put(OmnaEnum.APPLICATIONVNDOMADD2XML, EnumOmna.ApplicationVndOmaDd2Xml);
stackMapping.put(OmnaEnum.APPLICATIONVNDOMADDXML, EnumOmna.ApplicationVndOmaDdXml);
stackMapping.put(OmnaEnum.APPLICATIONVNDOMADRMCONTENT, EnumOmna.ApplicationVndOmaDrmContent);
stackMapping.put(OmnaEnum.APPLICATIONVNDOMADRMMESSAGE, EnumOmna.ApplicationVndOmaDrmMessage);
stackMapping.put(OmnaEnum.APPLICATIONVNDOMADRMRIGHTSWBXML, EnumOmna.ApplicationVndOmaDrmRightsWbxml);
stackMapping.put(OmnaEnum.APPLICATIONVNDOMADRMRIGHTSXML, EnumOmna.ApplicationVndOmaDrmRightsXml);
stackMapping.put(OmnaEnum.APPLICATIONVNDSYNCMLDMWBXML, EnumOmna.ApplicationVndSyncmlDmWbxml);
stackMapping.put(OmnaEnum.APPLICATIONVNDSYNCMLDMXML, EnumOmna.ApplicationVndSyncmlDmXml);
stackMapping.put(OmnaEnum.APPLICATIONVNDSYNCMLDSNOTIFICATION, EnumOmna.ApplicationVndSyncmlDsNotification);
stackMapping.put(OmnaEnum.APPLICATIONVNDSYNCMLNOTIFICATION, EnumOmna.ApplicationVndSyncmlNotification);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPCERTRESPONSE, EnumOmna.ApplicationVndWapCertResponse);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPCOC, EnumOmna.ApplicationVndWapCoc);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPCONNECTIVITYWBXML, EnumOmna.ApplicationVndWapConnectivityWbxml);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPHASHEDCERTIFICATE, EnumOmna.ApplicationVndWapHashedCertificate);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPLOCCWBXML, EnumOmna.ApplicationVndWapLoccWbxml);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPLOCXML, EnumOmna.ApplicationVndWapLocXml);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPMMSMESSAGE, EnumOmna.ApplicationVndWapMmsMessage);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPMULTIPART, EnumOmna.ApplicationVndWapMultipart);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPMULTIPARTALTERNATIVE, EnumOmna.ApplicationVndWapMultipartAlternative);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPMULTIPARTBYTERANGES, EnumOmna.ApplicationVndWapMultipartByteranges);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPMULTIPARTFORMDATA, EnumOmna.ApplicationVndWapMultipartFormData);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPMULTIPARTMIXED, EnumOmna.ApplicationVndWapMultipartMixed);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPMULTIPARTRELATED, EnumOmna.ApplicationVndWapMultipartRelated);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPROLLOVERCERTIFICATE, EnumOmna.ApplicationVndWapRolloverCertificate);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPSIA, EnumOmna.ApplicationVndWapSia);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPSIC, EnumOmna.ApplicationVndWapSic);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPSIGNEDCERTIFICATE, EnumOmna.ApplicationVndWapSignedCertificate);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPSLC, EnumOmna.ApplicationVndWapSlc);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPUAPROF, EnumOmna.ApplicationVndWapUaprof);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPWBXML, EnumOmna.ApplicationVndWapWbxml);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPWMLC, EnumOmna.ApplicationVndWapWmlc);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPWMLSCRIPTC, EnumOmna.ApplicationVndWapWmlscriptc);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPWTAEVENTC, EnumOmna.ApplicationVndWapWtaEventc);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPWTLSCACERTIFICATE, EnumOmna.ApplicationVndWapWtlsCaCertificate);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPWTLSUSERCERTIFICATE, EnumOmna.ApplicationVndWapWtlsUserCertificate);
stackMapping.put(OmnaEnum.APPLICATIONVNDWAPXHTMLXML, EnumOmna.ApplicationVndWapXhtmlXml);
stackMapping.put(OmnaEnum.APPLICATIONVNDWVCSPCIR, EnumOmna.ApplicationVndWvCspCir);
stackMapping.put(OmnaEnum.APPLICATIONVNDWVCSPWBXML, EnumOmna.ApplicationVndWvCspWbxml);
stackMapping.put(OmnaEnum.APPLICATIONVNDWVCSPXML, EnumOmna.ApplicationVndWvCspXml);
stackMapping.put(OmnaEnum.APPLICATIONWMLXML, EnumOmna.ApplicationWmlXml);
stackMapping.put(OmnaEnum.APPLICATIONXHDMLC, EnumOmna.ApplicationXHdmlc);
stackMapping.put(OmnaEnum.APPLICATIONXHTMLXML, EnumOmna.ApplicationXhtmlXml);
stackMapping.put(OmnaEnum.APPLICATIONXML, EnumOmna.ApplicationXml);
stackMapping.put(OmnaEnum.APPLICATIONXWWWFORMURLENCODED, EnumOmna.ApplicationXWwwFormUrlencoded);
stackMapping.put(OmnaEnum.APPLICATIONXX509CACERT, EnumOmna.ApplicationXX509CaCert);
stackMapping.put(OmnaEnum.APPLICATIONXX509USERCERT, EnumOmna.ApplicationXX509UserCert);
stackMapping.put(OmnaEnum.APPLICATIONXX968CACERT, EnumOmna.ApplicationXX968CaCert);
stackMapping.put(OmnaEnum.APPLICATIONXX968CROSSCERT, EnumOmna.ApplicationXX968CrossCert);
stackMapping.put(OmnaEnum.APPLICATIONXX968USERCERT, EnumOmna.ApplicationXX968UserCert);
stackMapping.put(OmnaEnum.AUDIO, EnumOmna.Audio);
stackMapping.put(OmnaEnum.EMPTY, EnumOmna.Empty);
stackMapping.put(OmnaEnum.IMAGE, EnumOmna.Image);
stackMapping.put(OmnaEnum.IMAGEGIF, EnumOmna.ImageGif);
stackMapping.put(OmnaEnum.IMAGEJPEG, EnumOmna.ImageJpeg);
stackMapping.put(OmnaEnum.IMAGEPNG, EnumOmna.ImagePng);
stackMapping.put(OmnaEnum.IMAGETIFF, EnumOmna.ImageTiff);
stackMapping.put(OmnaEnum.IMAGEVNDWAPWBMP, EnumOmna.ImageVndWapWbmp);
stackMapping.put(OmnaEnum.MULTIPART, EnumOmna.Multipart);
stackMapping.put(OmnaEnum.MULTIPARTALTERNATIVE, EnumOmna.MultipartAlternative);
stackMapping.put(OmnaEnum.MULTIPARTBYTERANTES, EnumOmna.MultipartByterantes);
stackMapping.put(OmnaEnum.MULTIPARTFORMDATA, EnumOmna.MultipartFormData);
stackMapping.put(OmnaEnum.MULTIPARTMIXED, EnumOmna.MultipartMixed);
stackMapping.put(OmnaEnum.TEXT, EnumOmna.Text);
stackMapping.put(OmnaEnum.TEXTCSS, EnumOmna.TextCss);
stackMapping.put(OmnaEnum.TEXTHTML, EnumOmna.TextHtml);
stackMapping.put(OmnaEnum.TEXTPLAIN, EnumOmna.TextPlain);
stackMapping.put(OmnaEnum.TEXTVNDWAPCO, EnumOmna.TextVndWapCo);
stackMapping.put(OmnaEnum.TEXTVNDWAPCONNECTIVITYXML, EnumOmna.TextVndWapConnectivityXml);
stackMapping.put(OmnaEnum.TEXTVNDWAPSI, EnumOmna.TextVndWapSi);
stackMapping.put(OmnaEnum.TEXTVNDWAPSL, EnumOmna.TextVndWapSl);
stackMapping.put(OmnaEnum.TEXTVNDWAPWML, EnumOmna.TextVndWapWml);
stackMapping.put(OmnaEnum.TEXTVNDWAPWMLSCRIPT, EnumOmna.TextVndWapWmlscript);
stackMapping.put(OmnaEnum.TEXTVNDWAPWTAEVENT, EnumOmna.TextVndWapWtaEvent);
stackMapping.put(OmnaEnum.TEXTXHDML, EnumOmna.TextXHdml);
stackMapping.put(OmnaEnum.TEXTXML, EnumOmna.TextXml);
stackMapping.put(OmnaEnum.TEXTXTTML, EnumOmna.TextXTtml);
stackMapping.put(OmnaEnum.TEXTXVCALENDAR, EnumOmna.TextXVCalendar);
stackMapping.put(OmnaEnum.TEXTXVCARD, EnumOmna.TextXVCard);
stackMapping.put(OmnaEnum.VIDEO, EnumOmna.Video);

containerMapping.put(EnumOmna.Application, OmnaEnum.APPLICATION);
containerMapping.put(EnumOmna.ApplicationJavaVm, OmnaEnum.APPLICATIONJAVAVM);
containerMapping.put(EnumOmna.ApplicationPkcs7Mime, OmnaEnum.APPLICATIONPKCS7MIME);
containerMapping.put(EnumOmna.ApplicationVndOmaDd2Xml, OmnaEnum.APPLICATIONVNDOMADD2XML);
containerMapping.put(EnumOmna.ApplicationVndOmaDdXml, OmnaEnum.APPLICATIONVNDOMADDXML);
containerMapping.put(EnumOmna.ApplicationVndOmaDrmContent, OmnaEnum.APPLICATIONVNDOMADRMCONTENT);
containerMapping.put(EnumOmna.ApplicationVndOmaDrmMessage, OmnaEnum.APPLICATIONVNDOMADRMMESSAGE);
containerMapping.put(EnumOmna.ApplicationVndOmaDrmRightsWbxml, OmnaEnum.APPLICATIONVNDOMADRMRIGHTSWBXML);
containerMapping.put(EnumOmna.ApplicationVndOmaDrmRightsXml, OmnaEnum.APPLICATIONVNDOMADRMRIGHTSXML);
containerMapping.put(EnumOmna.ApplicationVndSyncmlDmWbxml, OmnaEnum.APPLICATIONVNDSYNCMLDMWBXML);
containerMapping.put(EnumOmna.ApplicationVndSyncmlDmXml, OmnaEnum.APPLICATIONVNDSYNCMLDMXML);
containerMapping.put(EnumOmna.ApplicationVndSyncmlDsNotification, OmnaEnum.APPLICATIONVNDSYNCMLDSNOTIFICATION);
containerMapping.put(EnumOmna.ApplicationVndSyncmlNotification, OmnaEnum.APPLICATIONVNDSYNCMLNOTIFICATION);
containerMapping.put(EnumOmna.ApplicationVndWapCertResponse, OmnaEnum.APPLICATIONVNDWAPCERTRESPONSE);
containerMapping.put(EnumOmna.ApplicationVndWapCoc, OmnaEnum.APPLICATIONVNDWAPCOC);
containerMapping.put(EnumOmna.ApplicationVndWapConnectivityWbxml, OmnaEnum.APPLICATIONVNDWAPCONNECTIVITYWBXML);
containerMapping.put(EnumOmna.ApplicationVndWapHashedCertificate, OmnaEnum.APPLICATIONVNDWAPHASHEDCERTIFICATE);
containerMapping.put(EnumOmna.ApplicationVndWapLoccWbxml, OmnaEnum.APPLICATIONVNDWAPLOCCWBXML);
containerMapping.put(EnumOmna.ApplicationVndWapLocXml, OmnaEnum.APPLICATIONVNDWAPLOCXML);
containerMapping.put(EnumOmna.ApplicationVndWapMmsMessage, OmnaEnum.APPLICATIONVNDWAPMMSMESSAGE);
containerMapping.put(EnumOmna.ApplicationVndWapMultipart, OmnaEnum.APPLICATIONVNDWAPMULTIPART);
containerMapping.put(EnumOmna.ApplicationVndWapMultipartAlternative, OmnaEnum.APPLICATIONVNDWAPMULTIPARTALTERNATIVE);
containerMapping.put(EnumOmna.ApplicationVndWapMultipartByteranges, OmnaEnum.APPLICATIONVNDWAPMULTIPARTBYTERANGES);
containerMapping.put(EnumOmna.ApplicationVndWapMultipartFormData, OmnaEnum.APPLICATIONVNDWAPMULTIPARTFORMDATA);
containerMapping.put(EnumOmna.ApplicationVndWapMultipartMixed, OmnaEnum.APPLICATIONVNDWAPMULTIPARTMIXED);
containerMapping.put(EnumOmna.ApplicationVndWapMultipartRelated, OmnaEnum.APPLICATIONVNDWAPMULTIPARTRELATED);
containerMapping.put(EnumOmna.ApplicationVndWapRolloverCertificate, OmnaEnum.APPLICATIONVNDWAPROLLOVERCERTIFICATE);
containerMapping.put(EnumOmna.ApplicationVndWapSia, OmnaEnum.APPLICATIONVNDWAPSIA);
containerMapping.put(EnumOmna.ApplicationVndWapSic, OmnaEnum.APPLICATIONVNDWAPSIC);
containerMapping.put(EnumOmna.ApplicationVndWapSignedCertificate, OmnaEnum.APPLICATIONVNDWAPSIGNEDCERTIFICATE);
containerMapping.put(EnumOmna.ApplicationVndWapSlc, OmnaEnum.APPLICATIONVNDWAPSLC);
containerMapping.put(EnumOmna.ApplicationVndWapUaprof, OmnaEnum.APPLICATIONVNDWAPUAPROF);
containerMapping.put(EnumOmna.ApplicationVndWapWbxml, OmnaEnum.APPLICATIONVNDWAPWBXML);
containerMapping.put(EnumOmna.ApplicationVndWapWmlc, OmnaEnum.APPLICATIONVNDWAPWMLC);
containerMapping.put(EnumOmna.ApplicationVndWapWmlscriptc, OmnaEnum.APPLICATIONVNDWAPWMLSCRIPTC);
containerMapping.put(EnumOmna.ApplicationVndWapWtaEventc, OmnaEnum.APPLICATIONVNDWAPWTAEVENTC);
containerMapping.put(EnumOmna.ApplicationVndWapWtlsCaCertificate, OmnaEnum.APPLICATIONVNDWAPWTLSCACERTIFICATE);
containerMapping.put(EnumOmna.ApplicationVndWapWtlsUserCertificate, OmnaEnum.APPLICATIONVNDWAPWTLSUSERCERTIFICATE);
containerMapping.put(EnumOmna.ApplicationVndWapXhtmlXml, OmnaEnum.APPLICATIONVNDWAPXHTMLXML);
containerMapping.put(EnumOmna.ApplicationVndWvCspCir, OmnaEnum.APPLICATIONVNDWVCSPCIR);
containerMapping.put(EnumOmna.ApplicationVndWvCspWbxml, OmnaEnum.APPLICATIONVNDWVCSPWBXML);
containerMapping.put(EnumOmna.ApplicationVndWvCspXml, OmnaEnum.APPLICATIONVNDWVCSPXML);
containerMapping.put(EnumOmna.ApplicationWmlXml, OmnaEnum.APPLICATIONWMLXML);
containerMapping.put(EnumOmna.ApplicationXHdmlc, OmnaEnum.APPLICATIONXHDMLC);
containerMapping.put(EnumOmna.ApplicationXhtmlXml, OmnaEnum.APPLICATIONXHTMLXML);
containerMapping.put(EnumOmna.ApplicationXml, OmnaEnum.APPLICATIONXML);
containerMapping.put(EnumOmna.ApplicationXWwwFormUrlencoded, OmnaEnum.APPLICATIONXWWWFORMURLENCODED);
containerMapping.put(EnumOmna.ApplicationXX509CaCert, OmnaEnum.APPLICATIONXX509CACERT);
containerMapping.put(EnumOmna.ApplicationXX509UserCert, OmnaEnum.APPLICATIONXX509USERCERT);
containerMapping.put(EnumOmna.ApplicationXX968CaCert, OmnaEnum.APPLICATIONXX968CACERT);
containerMapping.put(EnumOmna.ApplicationXX968CrossCert, OmnaEnum.APPLICATIONXX968CROSSCERT);
containerMapping.put(EnumOmna.ApplicationXX968UserCert, OmnaEnum.APPLICATIONXX968USERCERT);
containerMapping.put(EnumOmna.Audio, OmnaEnum.AUDIO);
containerMapping.put(EnumOmna.Empty, OmnaEnum.EMPTY);
containerMapping.put(EnumOmna.Image, OmnaEnum.IMAGE);
containerMapping.put(EnumOmna.ImageGif, OmnaEnum.IMAGEGIF);
containerMapping.put(EnumOmna.ImageJpeg, OmnaEnum.IMAGEJPEG);
containerMapping.put(EnumOmna.ImagePng, OmnaEnum.IMAGEPNG);
containerMapping.put(EnumOmna.ImageTiff, OmnaEnum.IMAGETIFF);
containerMapping.put(EnumOmna.ImageVndWapWbmp, OmnaEnum.IMAGEVNDWAPWBMP);
containerMapping.put(EnumOmna.Multipart, OmnaEnum.MULTIPART);
containerMapping.put(EnumOmna.MultipartAlternative, OmnaEnum.MULTIPARTALTERNATIVE);
containerMapping.put(EnumOmna.MultipartByterantes, OmnaEnum.MULTIPARTBYTERANTES);
containerMapping.put(EnumOmna.MultipartFormData, OmnaEnum.MULTIPARTFORMDATA);
containerMapping.put(EnumOmna.MultipartMixed, OmnaEnum.MULTIPARTMIXED);
containerMapping.put(EnumOmna.Text, OmnaEnum.TEXT);
containerMapping.put(EnumOmna.TextCss, OmnaEnum.TEXTCSS);
containerMapping.put(EnumOmna.TextHtml, OmnaEnum.TEXTHTML);
containerMapping.put(EnumOmna.TextPlain, OmnaEnum.TEXTPLAIN);
containerMapping.put(EnumOmna.TextVndWapCo, OmnaEnum.TEXTVNDWAPCO);
containerMapping.put(EnumOmna.TextVndWapConnectivityXml, OmnaEnum.TEXTVNDWAPCONNECTIVITYXML);
containerMapping.put(EnumOmna.TextVndWapSi, OmnaEnum.TEXTVNDWAPSI);
containerMapping.put(EnumOmna.TextVndWapSl, OmnaEnum.TEXTVNDWAPSL);
containerMapping.put(EnumOmna.TextVndWapWml, OmnaEnum.TEXTVNDWAPWML);
containerMapping.put(EnumOmna.TextVndWapWmlscript, OmnaEnum.TEXTVNDWAPWMLSCRIPT);
containerMapping.put(EnumOmna.TextVndWapWtaEvent, OmnaEnum.TEXTVNDWAPWTAEVENT);
containerMapping.put(EnumOmna.TextXHdml, OmnaEnum.TEXTXHDML);
containerMapping.put(EnumOmna.TextXml, OmnaEnum.TEXTXML);
containerMapping.put(EnumOmna.TextXTtml, OmnaEnum.TEXTXTTML);
containerMapping.put(EnumOmna.TextXVCalendar, OmnaEnum.TEXTXVCALENDAR);
containerMapping.put(EnumOmna.TextXVCard, OmnaEnum.TEXTXVCARD);
containerMapping.put(EnumOmna.Video, OmnaEnum.VIDEO);
}

public static final OmnaEnum getContainerObj(EnumOmna stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumOmna getStackObj(OmnaEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static OmnaEnum fromCode(int value){
	return getContainerObj(EnumOmna.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumOmna.getName(key);
}

public static boolean isValid(int value){
	return EnumOmna.isValid(value);
}

public static int[] keys(){
	return EnumOmna.keys();
}
}
