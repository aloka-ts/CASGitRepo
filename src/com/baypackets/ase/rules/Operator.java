
package com.baypackets.ase.rules;


import java.io.*;
import javax.servlet.sip.*;

/**
 * Base class for equal, exists, contains, subdomain-of operators.
 * These all tests a request against a variable.
 */ 
public abstract class Operator implements Condition {
//    protected static final int R_METHOD   = 0;
//
//    // request URI variables
//    protected static final int RU         = 1;
//    protected static final int RU_SCHEME  = 2;
//    protected static final int RU_USER    = 3;
//    protected static final int RU_HOST    = 4;
//    protected static final int RU_PORT    = 5;
//    protected static final int RU_TEL     = 6;
//
//    // From variables
//    protected static final int RF         = 7;
//    protected static final int RF_DISPLAY = 8;
//    protected static final int RFU        = 9;
//    protected static final int RFU_SCHEME = 10;
//    protected static final int RFU_USER   = 11;
//    protected static final int RFU_HOST   = 12;
//    protected static final int RFU_PORT   = 13;
//    protected static final int RFU_TEL    = 14;
//
//    // To variables
//    protected static final int RT         = 15;
//    protected static final int RT_DISPLAY = 16;
//    protected static final int RTU        = 17;
//    protected static final int RTU_SCHEME = 18;
//    protected static final int RTU_USER   = 19;
//    protected static final int RTU_HOST   = 20;
//    protected static final int RTU_PORT   = 21;
//    protected static final int RTU_TEL    = 22;
//
//    //static final int RU_PARAM = "request.uri.param";
//    protected static final int FIXED_VAR_MAX    = 23;
//    
//    // the xxx.param.<name> variables are treated specially
//    // because the exact name isn't known a priori
//    protected static final int RU_PARAM   = 24;
//    protected static final int RFU_PARAM  = 25;
//    protected static final int RTU_PARAM  = 26;
//
//    private static final String[] varNames = {
//        "request.method",
//
//        "request.uri",
//        "request.uri.scheme",
//        "request.uri.user",
//        "request.uri.host",
//        "request.uri.port",
//        "request.uri.tel",
//
//        "request.from",
//        "request.from.display-name",
//        "request.from.uri",
//        "request.from.uri.scheme",
//        "request.from.uri.user",
//        "request.from.uri.host",
//        "request.from.uri.port",
//        "request.from.uri.tel",
//
//        "request.to",
//        "request.to.display-name",
//        "request.to.uri",
//        "request.to.uri.scheme",
//        "request.to.uri.user",
//        "request.to.uri.host",
//        "request.to.uri.port",
//        "request.to.uri.tel",
//    };
//
//
//
//
//    protected int var;
//    /** Param name in case we're testing against parameter. May be null. */
//    protected String param;
//  
//    /** In the nesting of Condition objects this index indicates the position
//      * of this particular object */
//    protected int operatorIdx;
//
//    public Operator(String varName) {
//        if ((var = varNameToInt(varName)) < 0) {
//            throw new IllegalArgumentException("Bad variable name: " + varName);
//        }
//        if (var > FIXED_VAR_MAX) {
//            param = getParamName(varName);
//        }
//    }
//    
//    protected static int varNameToInt(String varName) {
//        for (int i = 0; i < FIXED_VAR_MAX; i++) {
//            if (varNames[i].equals(varName)) {
//                return i;
//            }
//        }
//        if (varName.startsWith("request.uri.param.")) {
//            return RU_PARAM;
//        }
//        if (varName.startsWith("request.from.uri.param.")) {
//            return RFU_PARAM;
//        }
//        if (varName.startsWith("request.to.uri.param.")) {
//            return RTU_PARAM;
//        }
//        return -1;
//    }
//
//    public String varName() {
//        if (var < FIXED_VAR_MAX) {
//            return _varName(var);
//        } else if (var == RU_PARAM) {
//            return "request.uri.param." + param;
//        } else if (var == RFU_PARAM) {
//            return "request.uri.from.param." + param;
//        } else if (var == RTU_PARAM) {
//            return "request.uri.to.param." + param;
//        } else {
//            return null;
//        }
//    }
//
//    protected static String _varName(int var) {
//        if (var >= 0 || var < FIXED_VAR_MAX) {
//            return varNames[var];
//        } else {
//            return null;
//        }
//    }
//
//    protected static String getParamName(String varName) {
//        if (varName.startsWith("request.uri.param.")) {
//            return varName.substring(18);
//        }
//        if (varName.startsWith("request.from.uri.param.")) {
//            return varName.substring(23);
//        }
//        if (varName.startsWith("request.to.uri.param.")) {
//            return varName.substring(21);
//        }
//        return null;
//    }
//
//    protected String getValue(AseBaseRequest req) {
//        return getValue(req, var, param);
//    }
//
//    protected static String getValue(AseBaseRequest asbreq,
//                                     int var,
//                                     String param)
//    {
//        SipServletRequest req = (SipServletRequest)asbreq;
//        switch (var) {
//        case R_METHOD:
//            return req.getMethod();
//            
//        case RU: return req.getRequestURI().toString();
//        case RU_SCHEME: return req.getRequestURI().getScheme();
//        case RU_USER: return getUser(req.getRequestURI());
//        case RU_HOST: return getHost(req.getRequestURI());
//        case RU_PORT: return getPort(req.getRequestURI());
//        case RU_TEL: return getTel(req.getRequestURI());
//
//        case RF: return req.getFrom().toString();
//        case RF_DISPLAY: return req.getFrom().getDisplayName();
//        case RFU: return req.getFrom().getURI().toString();
//        case RFU_SCHEME: return req.getFrom().getURI().getScheme();
//        case RFU_USER: return getUser(req.getFrom().getURI());
//        case RFU_HOST: return getHost(req.getFrom().getURI());
//        case RFU_PORT: return getPort(req.getFrom().getURI());
//        case RFU_TEL: return getTel(req.getFrom().getURI());
//
//        case RT: return req.getTo().toString();
//        case RT_DISPLAY: return req.getTo().getDisplayName();
//        case RTU: return req.getTo().getURI().toString();
//        case RTU_SCHEME: return req.getTo().getURI().getScheme();
//        case RTU_USER: return getUser(req.getTo().getURI());
//        case RTU_HOST: return getHost(req.getTo().getURI());
//        case RTU_PORT: return getPort(req.getTo().getURI());
//        case RTU_TEL: return getTel(req.getTo().getURI());
//            
//        case RU_PARAM: return getParam(req.getRequestURI(), param);
//        case RFU_PARAM: return getParam(req.getFrom().getURI(), param);
//        case RTU_PARAM: return getParam(req.getTo().getURI(), param);
//
//        default: return null;
//        }
//    }
//
//    private static String getUser(URI uri) {
//        if (uri.isSipURI()) {
//            return ((SipURI) uri).getUser();
//        } else {
//            return null;
//        }
//    }
//
//    private static String getHost(URI uri) {
//        if (uri.isSipURI()) {
//            return ((SipURI) uri).getHost();
//        } else {
//            return null;
//        }
//    }
//    
//    private static String getPort(URI uri) {
//        if (uri.isSipURI()) {
//            SipURI sipURI = (SipURI) uri;
//            int port = sipURI.getPort();
//            if (port < 0) {
//                return ("sips".equals(sipURI.getScheme()) ? "5061" : "5060");
//            } else {
//                return Integer.toString(port);
//            }
//        } else {
//            return null;
//        }
//    }
//
//    private static String getTel(URI uri) {
//        if (uri.isSipURI()) {
//            SipURI sipURI = (SipURI) uri;
//            if ("phone".equals(sipURI.getUserParam())) {
//                return stripVisuals(sipURI.getUser());
//            }
//        } else if ("tel".equals(uri.getScheme())) {
//            return stripVisuals(((TelURL) uri).getPhoneNumber());
//        }
//        return null;
//    }
//
//    /**
//     * Strips visual separators ("-" / "." / "(" / ")") from the specified
//     * telephone number (see RFC 2806).
//     */
//    private static String stripVisuals(String s) {
//        StringBuffer buf = new StringBuffer(s.length());
//        for (int i = 0; i < s.length(); i++) {
//            char c = s.charAt(i);
//            if ("-.()".indexOf(c) < 0) {
//                buf.append(c);
//            }
//        }
//        return buf.toString();
//    }
//
//    private static String getParam(URI uri, String param) {
//        if (uri.isSipURI()) {
//            return ((SipURI) uri).getParameter(param);
//        } else if ("tel".equals(uri.getScheme())) {
//            return ((TelURL) uri).getParameter(param);
//        }
//        return null;
//    }
//
//    public static void dump(PrintWriter out, SipServletRequest req) {
//        for (int i = 0; i < FIXED_VAR_MAX; i++) {
//            out.println("" + _varName(i) + ": " + getValue(req, i, null));
//        }
//        out.flush();
//    }
//
//
//    public static void dump(PrintWriter out, AseBaseRequest asbreq) {
//        SipServletRequest req = (SipServletRequest)asbreq; 
//        for (int i = 0; i < FIXED_VAR_MAX; i++) {
//            out.println("" + _varName(i) + ": " + getValue(req, i, null));
//        }
//        out.flush();
//
//    public abstract boolean evaluate(AseBaseRequest req);
//
//    public static void main(String[] args) throws Exception {
//        SipServletRequest req =
//            (SipServletRequest) new MessageParser().parse(System.in);
//        System.out.println("parsed request: \n" + req);
//        dump(new PrintWriter(System.out), req);
//        System.out.flush();
//    }
//
}
