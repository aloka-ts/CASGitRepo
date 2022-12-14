package com.baypackets.ase.ra.diameter.ro.enums;

import java.util.Hashtable;
import com.traffix.openblox.diameter.ro.generated.enums.EnumTypeNumber;

public enum TypeNumberEnum
{
APPLICATION,
APPLICATIONJAVAVM,
APPLICATIONMIKEY,
APPLICATIONPKCS7MIME,
APPLICATIONVNDOMADCD,
APPLICATIONVNDOMADCDC,
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
TEXTXVMESSAGE,
VIDEO,
VNDOMADSEMAILWBXML,
XVBOOKMARK;

private static Hashtable<TypeNumberEnum,EnumTypeNumber> stackMapping = new Hashtable<TypeNumberEnum,EnumTypeNumber>();
private static Hashtable<EnumTypeNumber,TypeNumberEnum> containerMapping = new Hashtable<EnumTypeNumber,TypeNumberEnum>();

 static {
stackMapping.put(TypeNumberEnum.APPLICATION, EnumTypeNumber.Application);
stackMapping.put(TypeNumberEnum.APPLICATIONJAVAVM, EnumTypeNumber.ApplicationJavaVm);
stackMapping.put(TypeNumberEnum.APPLICATIONMIKEY, EnumTypeNumber.ApplicationMikey);
stackMapping.put(TypeNumberEnum.APPLICATIONPKCS7MIME, EnumTypeNumber.ApplicationPkcs7Mime);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDOMADCD, EnumTypeNumber.ApplicationVndOmaDcd);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDOMADCDC, EnumTypeNumber.ApplicationVndOmaDcdc);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDOMADD2XML, EnumTypeNumber.ApplicationVndOmaDd2Xml);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDOMADDXML, EnumTypeNumber.ApplicationVndOmaDdXml);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDOMADRMCONTENT, EnumTypeNumber.ApplicationVndOmaDrmContent);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDOMADRMMESSAGE, EnumTypeNumber.ApplicationVndOmaDrmMessage);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDOMADRMRIGHTSWBXML, EnumTypeNumber.ApplicationVndOmaDrmRightsWbxml);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDOMADRMRIGHTSXML, EnumTypeNumber.ApplicationVndOmaDrmRightsXml);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDSYNCMLDMWBXML, EnumTypeNumber.ApplicationVndSyncmlDmWbxml);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDSYNCMLDMXML, EnumTypeNumber.ApplicationVndSyncmlDmXml);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDSYNCMLDSNOTIFICATION, EnumTypeNumber.ApplicationVndSyncmlDsNotification);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDSYNCMLNOTIFICATION, EnumTypeNumber.ApplicationVndSyncmlNotification);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPCERTRESPONSE, EnumTypeNumber.ApplicationVndWapCertResponse);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPCOC, EnumTypeNumber.ApplicationVndWapCoc);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPCONNECTIVITYWBXML, EnumTypeNumber.ApplicationVndWapConnectivityWbxml);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPHASHEDCERTIFICATE, EnumTypeNumber.ApplicationVndWapHashedCertificate);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPLOCCWBXML, EnumTypeNumber.ApplicationVndWapLoccWbxml);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPLOCXML, EnumTypeNumber.ApplicationVndWapLocXml);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPMMSMESSAGE, EnumTypeNumber.ApplicationVndWapMmsMessage);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPMULTIPART, EnumTypeNumber.ApplicationVndWapMultipart);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPMULTIPARTALTERNATIVE, EnumTypeNumber.ApplicationVndWapMultipartAlternative);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPMULTIPARTBYTERANGES, EnumTypeNumber.ApplicationVndWapMultipartByteranges);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPMULTIPARTFORMDATA, EnumTypeNumber.ApplicationVndWapMultipartFormData);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPMULTIPARTMIXED, EnumTypeNumber.ApplicationVndWapMultipartMixed);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPMULTIPARTRELATED, EnumTypeNumber.ApplicationVndWapMultipartRelated);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPROLLOVERCERTIFICATE, EnumTypeNumber.ApplicationVndWapRolloverCertificate);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPSIA, EnumTypeNumber.ApplicationVndWapSia);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPSIC, EnumTypeNumber.ApplicationVndWapSic);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPSIGNEDCERTIFICATE, EnumTypeNumber.ApplicationVndWapSignedCertificate);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPSLC, EnumTypeNumber.ApplicationVndWapSlc);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPUAPROF, EnumTypeNumber.ApplicationVndWapUaprof);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPWBXML, EnumTypeNumber.ApplicationVndWapWbxml);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPWMLC, EnumTypeNumber.ApplicationVndWapWmlc);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPWMLSCRIPTC, EnumTypeNumber.ApplicationVndWapWmlscriptc);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPWTAEVENTC, EnumTypeNumber.ApplicationVndWapWtaEventc);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPWTLSCACERTIFICATE, EnumTypeNumber.ApplicationVndWapWtlsCaCertificate);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPWTLSUSERCERTIFICATE, EnumTypeNumber.ApplicationVndWapWtlsUserCertificate);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWAPXHTMLXML, EnumTypeNumber.ApplicationVndWapXhtmlXml);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWVCSPCIR, EnumTypeNumber.ApplicationVndWvCspCir);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWVCSPWBXML, EnumTypeNumber.ApplicationVndWvCspWbxml);
stackMapping.put(TypeNumberEnum.APPLICATIONVNDWVCSPXML, EnumTypeNumber.ApplicationVndWvCspXml);
stackMapping.put(TypeNumberEnum.APPLICATIONWMLXML, EnumTypeNumber.ApplicationWmlXml);
stackMapping.put(TypeNumberEnum.APPLICATIONXHDMLC, EnumTypeNumber.ApplicationXHdmlc);
stackMapping.put(TypeNumberEnum.APPLICATIONXHTMLXML, EnumTypeNumber.ApplicationXhtmlXml);
stackMapping.put(TypeNumberEnum.APPLICATIONXML, EnumTypeNumber.ApplicationXml);
stackMapping.put(TypeNumberEnum.APPLICATIONXWWWFORMURLENCODED, EnumTypeNumber.ApplicationXWwwFormUrlencoded);
stackMapping.put(TypeNumberEnum.APPLICATIONXX509CACERT, EnumTypeNumber.ApplicationXX509CaCert);
stackMapping.put(TypeNumberEnum.APPLICATIONXX509USERCERT, EnumTypeNumber.ApplicationXX509UserCert);
stackMapping.put(TypeNumberEnum.APPLICATIONXX968CACERT, EnumTypeNumber.ApplicationXX968CaCert);
stackMapping.put(TypeNumberEnum.APPLICATIONXX968CROSSCERT, EnumTypeNumber.ApplicationXX968CrossCert);
stackMapping.put(TypeNumberEnum.APPLICATIONXX968USERCERT, EnumTypeNumber.ApplicationXX968UserCert);
stackMapping.put(TypeNumberEnum.AUDIO, EnumTypeNumber.Audio);
stackMapping.put(TypeNumberEnum.EMPTY, EnumTypeNumber.Empty);
stackMapping.put(TypeNumberEnum.IMAGE, EnumTypeNumber.Image);
stackMapping.put(TypeNumberEnum.IMAGEGIF, EnumTypeNumber.ImageGif);
stackMapping.put(TypeNumberEnum.IMAGEJPEG, EnumTypeNumber.ImageJpeg);
stackMapping.put(TypeNumberEnum.IMAGEPNG, EnumTypeNumber.ImagePng);
stackMapping.put(TypeNumberEnum.IMAGETIFF, EnumTypeNumber.ImageTiff);
stackMapping.put(TypeNumberEnum.IMAGEVNDWAPWBMP, EnumTypeNumber.ImageVndWapWbmp);
stackMapping.put(TypeNumberEnum.MULTIPART, EnumTypeNumber.Multipart);
stackMapping.put(TypeNumberEnum.MULTIPARTALTERNATIVE, EnumTypeNumber.MultipartAlternative);
stackMapping.put(TypeNumberEnum.MULTIPARTBYTERANTES, EnumTypeNumber.MultipartByterantes);
stackMapping.put(TypeNumberEnum.MULTIPARTFORMDATA, EnumTypeNumber.MultipartFormData);
stackMapping.put(TypeNumberEnum.MULTIPARTMIXED, EnumTypeNumber.MultipartMixed);
stackMapping.put(TypeNumberEnum.TEXT, EnumTypeNumber.Text);
stackMapping.put(TypeNumberEnum.TEXTCSS, EnumTypeNumber.TextCss);
stackMapping.put(TypeNumberEnum.TEXTHTML, EnumTypeNumber.TextHtml);
stackMapping.put(TypeNumberEnum.TEXTPLAIN, EnumTypeNumber.TextPlain);
stackMapping.put(TypeNumberEnum.TEXTVNDWAPCO, EnumTypeNumber.TextVndWapCo);
stackMapping.put(TypeNumberEnum.TEXTVNDWAPCONNECTIVITYXML, EnumTypeNumber.TextVndWapConnectivityXml);
stackMapping.put(TypeNumberEnum.TEXTVNDWAPSI, EnumTypeNumber.TextVndWapSi);
stackMapping.put(TypeNumberEnum.TEXTVNDWAPSL, EnumTypeNumber.TextVndWapSl);
stackMapping.put(TypeNumberEnum.TEXTVNDWAPWML, EnumTypeNumber.TextVndWapWml);
stackMapping.put(TypeNumberEnum.TEXTVNDWAPWMLSCRIPT, EnumTypeNumber.TextVndWapWmlscript);
stackMapping.put(TypeNumberEnum.TEXTVNDWAPWTAEVENT, EnumTypeNumber.TextVndWapWtaEvent);
stackMapping.put(TypeNumberEnum.TEXTXHDML, EnumTypeNumber.TextXHdml);
stackMapping.put(TypeNumberEnum.TEXTXML, EnumTypeNumber.TextXml);
stackMapping.put(TypeNumberEnum.TEXTXTTML, EnumTypeNumber.TextXTtml);
stackMapping.put(TypeNumberEnum.TEXTXVCALENDAR, EnumTypeNumber.TextXVCalendar);
stackMapping.put(TypeNumberEnum.TEXTXVCARD, EnumTypeNumber.TextXVCard);
stackMapping.put(TypeNumberEnum.TEXTXVMESSAGE, EnumTypeNumber.TextXVMessage);
stackMapping.put(TypeNumberEnum.VIDEO, EnumTypeNumber.Video);
stackMapping.put(TypeNumberEnum.VNDOMADSEMAILWBXML, EnumTypeNumber.VndOmadsEmailWbXml);
stackMapping.put(TypeNumberEnum.XVBOOKMARK, EnumTypeNumber.XVBookmark);

containerMapping.put(EnumTypeNumber.Application, TypeNumberEnum.APPLICATION);
containerMapping.put(EnumTypeNumber.ApplicationJavaVm, TypeNumberEnum.APPLICATIONJAVAVM);
containerMapping.put(EnumTypeNumber.ApplicationMikey, TypeNumberEnum.APPLICATIONMIKEY);
containerMapping.put(EnumTypeNumber.ApplicationPkcs7Mime, TypeNumberEnum.APPLICATIONPKCS7MIME);
containerMapping.put(EnumTypeNumber.ApplicationVndOmaDcd, TypeNumberEnum.APPLICATIONVNDOMADCD);
containerMapping.put(EnumTypeNumber.ApplicationVndOmaDcdc, TypeNumberEnum.APPLICATIONVNDOMADCDC);
containerMapping.put(EnumTypeNumber.ApplicationVndOmaDd2Xml, TypeNumberEnum.APPLICATIONVNDOMADD2XML);
containerMapping.put(EnumTypeNumber.ApplicationVndOmaDdXml, TypeNumberEnum.APPLICATIONVNDOMADDXML);
containerMapping.put(EnumTypeNumber.ApplicationVndOmaDrmContent, TypeNumberEnum.APPLICATIONVNDOMADRMCONTENT);
containerMapping.put(EnumTypeNumber.ApplicationVndOmaDrmMessage, TypeNumberEnum.APPLICATIONVNDOMADRMMESSAGE);
containerMapping.put(EnumTypeNumber.ApplicationVndOmaDrmRightsWbxml, TypeNumberEnum.APPLICATIONVNDOMADRMRIGHTSWBXML);
containerMapping.put(EnumTypeNumber.ApplicationVndOmaDrmRightsXml, TypeNumberEnum.APPLICATIONVNDOMADRMRIGHTSXML);
containerMapping.put(EnumTypeNumber.ApplicationVndSyncmlDmWbxml, TypeNumberEnum.APPLICATIONVNDSYNCMLDMWBXML);
containerMapping.put(EnumTypeNumber.ApplicationVndSyncmlDmXml, TypeNumberEnum.APPLICATIONVNDSYNCMLDMXML);
containerMapping.put(EnumTypeNumber.ApplicationVndSyncmlDsNotification, TypeNumberEnum.APPLICATIONVNDSYNCMLDSNOTIFICATION);
containerMapping.put(EnumTypeNumber.ApplicationVndSyncmlNotification, TypeNumberEnum.APPLICATIONVNDSYNCMLNOTIFICATION);
containerMapping.put(EnumTypeNumber.ApplicationVndWapCertResponse, TypeNumberEnum.APPLICATIONVNDWAPCERTRESPONSE);
containerMapping.put(EnumTypeNumber.ApplicationVndWapCoc, TypeNumberEnum.APPLICATIONVNDWAPCOC);
containerMapping.put(EnumTypeNumber.ApplicationVndWapConnectivityWbxml, TypeNumberEnum.APPLICATIONVNDWAPCONNECTIVITYWBXML);
containerMapping.put(EnumTypeNumber.ApplicationVndWapHashedCertificate, TypeNumberEnum.APPLICATIONVNDWAPHASHEDCERTIFICATE);
containerMapping.put(EnumTypeNumber.ApplicationVndWapLoccWbxml, TypeNumberEnum.APPLICATIONVNDWAPLOCCWBXML);
containerMapping.put(EnumTypeNumber.ApplicationVndWapLocXml, TypeNumberEnum.APPLICATIONVNDWAPLOCXML);
containerMapping.put(EnumTypeNumber.ApplicationVndWapMmsMessage, TypeNumberEnum.APPLICATIONVNDWAPMMSMESSAGE);
containerMapping.put(EnumTypeNumber.ApplicationVndWapMultipart, TypeNumberEnum.APPLICATIONVNDWAPMULTIPART);
containerMapping.put(EnumTypeNumber.ApplicationVndWapMultipartAlternative, TypeNumberEnum.APPLICATIONVNDWAPMULTIPARTALTERNATIVE);
containerMapping.put(EnumTypeNumber.ApplicationVndWapMultipartByteranges, TypeNumberEnum.APPLICATIONVNDWAPMULTIPARTBYTERANGES);
containerMapping.put(EnumTypeNumber.ApplicationVndWapMultipartFormData, TypeNumberEnum.APPLICATIONVNDWAPMULTIPARTFORMDATA);
containerMapping.put(EnumTypeNumber.ApplicationVndWapMultipartMixed, TypeNumberEnum.APPLICATIONVNDWAPMULTIPARTMIXED);
containerMapping.put(EnumTypeNumber.ApplicationVndWapMultipartRelated, TypeNumberEnum.APPLICATIONVNDWAPMULTIPARTRELATED);
containerMapping.put(EnumTypeNumber.ApplicationVndWapRolloverCertificate, TypeNumberEnum.APPLICATIONVNDWAPROLLOVERCERTIFICATE);
containerMapping.put(EnumTypeNumber.ApplicationVndWapSia, TypeNumberEnum.APPLICATIONVNDWAPSIA);
containerMapping.put(EnumTypeNumber.ApplicationVndWapSic, TypeNumberEnum.APPLICATIONVNDWAPSIC);
containerMapping.put(EnumTypeNumber.ApplicationVndWapSignedCertificate, TypeNumberEnum.APPLICATIONVNDWAPSIGNEDCERTIFICATE);
containerMapping.put(EnumTypeNumber.ApplicationVndWapSlc, TypeNumberEnum.APPLICATIONVNDWAPSLC);
containerMapping.put(EnumTypeNumber.ApplicationVndWapUaprof, TypeNumberEnum.APPLICATIONVNDWAPUAPROF);
containerMapping.put(EnumTypeNumber.ApplicationVndWapWbxml, TypeNumberEnum.APPLICATIONVNDWAPWBXML);
containerMapping.put(EnumTypeNumber.ApplicationVndWapWmlc, TypeNumberEnum.APPLICATIONVNDWAPWMLC);
containerMapping.put(EnumTypeNumber.ApplicationVndWapWmlscriptc, TypeNumberEnum.APPLICATIONVNDWAPWMLSCRIPTC);
containerMapping.put(EnumTypeNumber.ApplicationVndWapWtaEventc, TypeNumberEnum.APPLICATIONVNDWAPWTAEVENTC);
containerMapping.put(EnumTypeNumber.ApplicationVndWapWtlsCaCertificate, TypeNumberEnum.APPLICATIONVNDWAPWTLSCACERTIFICATE);
containerMapping.put(EnumTypeNumber.ApplicationVndWapWtlsUserCertificate, TypeNumberEnum.APPLICATIONVNDWAPWTLSUSERCERTIFICATE);
containerMapping.put(EnumTypeNumber.ApplicationVndWapXhtmlXml, TypeNumberEnum.APPLICATIONVNDWAPXHTMLXML);
containerMapping.put(EnumTypeNumber.ApplicationVndWvCspCir, TypeNumberEnum.APPLICATIONVNDWVCSPCIR);
containerMapping.put(EnumTypeNumber.ApplicationVndWvCspWbxml, TypeNumberEnum.APPLICATIONVNDWVCSPWBXML);
containerMapping.put(EnumTypeNumber.ApplicationVndWvCspXml, TypeNumberEnum.APPLICATIONVNDWVCSPXML);
containerMapping.put(EnumTypeNumber.ApplicationWmlXml, TypeNumberEnum.APPLICATIONWMLXML);
containerMapping.put(EnumTypeNumber.ApplicationXHdmlc, TypeNumberEnum.APPLICATIONXHDMLC);
containerMapping.put(EnumTypeNumber.ApplicationXhtmlXml, TypeNumberEnum.APPLICATIONXHTMLXML);
containerMapping.put(EnumTypeNumber.ApplicationXml, TypeNumberEnum.APPLICATIONXML);
containerMapping.put(EnumTypeNumber.ApplicationXWwwFormUrlencoded, TypeNumberEnum.APPLICATIONXWWWFORMURLENCODED);
containerMapping.put(EnumTypeNumber.ApplicationXX509CaCert, TypeNumberEnum.APPLICATIONXX509CACERT);
containerMapping.put(EnumTypeNumber.ApplicationXX509UserCert, TypeNumberEnum.APPLICATIONXX509USERCERT);
containerMapping.put(EnumTypeNumber.ApplicationXX968CaCert, TypeNumberEnum.APPLICATIONXX968CACERT);
containerMapping.put(EnumTypeNumber.ApplicationXX968CrossCert, TypeNumberEnum.APPLICATIONXX968CROSSCERT);
containerMapping.put(EnumTypeNumber.ApplicationXX968UserCert, TypeNumberEnum.APPLICATIONXX968USERCERT);
containerMapping.put(EnumTypeNumber.Audio, TypeNumberEnum.AUDIO);
containerMapping.put(EnumTypeNumber.Empty, TypeNumberEnum.EMPTY);
containerMapping.put(EnumTypeNumber.Image, TypeNumberEnum.IMAGE);
containerMapping.put(EnumTypeNumber.ImageGif, TypeNumberEnum.IMAGEGIF);
containerMapping.put(EnumTypeNumber.ImageJpeg, TypeNumberEnum.IMAGEJPEG);
containerMapping.put(EnumTypeNumber.ImagePng, TypeNumberEnum.IMAGEPNG);
containerMapping.put(EnumTypeNumber.ImageTiff, TypeNumberEnum.IMAGETIFF);
containerMapping.put(EnumTypeNumber.ImageVndWapWbmp, TypeNumberEnum.IMAGEVNDWAPWBMP);
containerMapping.put(EnumTypeNumber.Multipart, TypeNumberEnum.MULTIPART);
containerMapping.put(EnumTypeNumber.MultipartAlternative, TypeNumberEnum.MULTIPARTALTERNATIVE);
containerMapping.put(EnumTypeNumber.MultipartByterantes, TypeNumberEnum.MULTIPARTBYTERANTES);
containerMapping.put(EnumTypeNumber.MultipartFormData, TypeNumberEnum.MULTIPARTFORMDATA);
containerMapping.put(EnumTypeNumber.MultipartMixed, TypeNumberEnum.MULTIPARTMIXED);
containerMapping.put(EnumTypeNumber.Text, TypeNumberEnum.TEXT);
containerMapping.put(EnumTypeNumber.TextCss, TypeNumberEnum.TEXTCSS);
containerMapping.put(EnumTypeNumber.TextHtml, TypeNumberEnum.TEXTHTML);
containerMapping.put(EnumTypeNumber.TextPlain, TypeNumberEnum.TEXTPLAIN);
containerMapping.put(EnumTypeNumber.TextVndWapCo, TypeNumberEnum.TEXTVNDWAPCO);
containerMapping.put(EnumTypeNumber.TextVndWapConnectivityXml, TypeNumberEnum.TEXTVNDWAPCONNECTIVITYXML);
containerMapping.put(EnumTypeNumber.TextVndWapSi, TypeNumberEnum.TEXTVNDWAPSI);
containerMapping.put(EnumTypeNumber.TextVndWapSl, TypeNumberEnum.TEXTVNDWAPSL);
containerMapping.put(EnumTypeNumber.TextVndWapWml, TypeNumberEnum.TEXTVNDWAPWML);
containerMapping.put(EnumTypeNumber.TextVndWapWmlscript, TypeNumberEnum.TEXTVNDWAPWMLSCRIPT);
containerMapping.put(EnumTypeNumber.TextVndWapWtaEvent, TypeNumberEnum.TEXTVNDWAPWTAEVENT);
containerMapping.put(EnumTypeNumber.TextXHdml, TypeNumberEnum.TEXTXHDML);
containerMapping.put(EnumTypeNumber.TextXml, TypeNumberEnum.TEXTXML);
containerMapping.put(EnumTypeNumber.TextXTtml, TypeNumberEnum.TEXTXTTML);
containerMapping.put(EnumTypeNumber.TextXVCalendar, TypeNumberEnum.TEXTXVCALENDAR);
containerMapping.put(EnumTypeNumber.TextXVCard, TypeNumberEnum.TEXTXVCARD);
containerMapping.put(EnumTypeNumber.TextXVMessage, TypeNumberEnum.TEXTXVMESSAGE);
containerMapping.put(EnumTypeNumber.Video, TypeNumberEnum.VIDEO);
containerMapping.put(EnumTypeNumber.VndOmadsEmailWbXml, TypeNumberEnum.VNDOMADSEMAILWBXML);
containerMapping.put(EnumTypeNumber.XVBookmark, TypeNumberEnum.XVBOOKMARK);
}

public static final TypeNumberEnum getContainerObj(EnumTypeNumber stkEnum){
	return containerMapping.get(stkEnum);
}

public static final EnumTypeNumber getStackObj(TypeNumberEnum cntrEnum){
	return stackMapping.get(cntrEnum);
}

public static TypeNumberEnum fromCode(int value){
	return getContainerObj(EnumTypeNumber.fromCode(value));
}

public static java.lang.String getName(int key){
	return EnumTypeNumber.getName(key);
}

public static boolean isValid(int value){
	return EnumTypeNumber.isValid(value);
}

public static int[] keys(){
	return EnumTypeNumber.keys();
}
}
