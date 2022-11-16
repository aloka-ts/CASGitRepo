/*
 * FileBasedGwDAO.java
 *
 */
package com.baypackets.ase.externaldevice.outboundgateway;

import com.baypackets.ase.sbb.OutboundGateway;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * This implementation of the OutboundGatewayDAO interface reads the meta data
 * on all provisioned outbound gateways from an XML config file.
 *
 */
public class FileBasedGwDAO implements OutboundGatewayDAO {

    private static Logger _logger = Logger.getLogger(FileBasedGwDAO.class);
    private static String PUBLIC_ID="-//Baypackets SIP Application Server//DTD Outbound Gateway Config//EN";
    private static String DTD_FILE="com/baypackets/ase/externaldevice/outboundgateway/outbound-gateway-config.dtd";

    public URI _xmlUri;

    /**
     * Default constructor.
     */
    public FileBasedGwDAO() {
    }


    /**
     * Constructs a new FileBasedGwDAO object and initializes it with the
     * URI of the outbound gateway config file to parse.
     *
     * @param xmlFile  The URI of the config file to parse.
     */
    public FileBasedGwDAO(URI xmlFile) {
        this.setXmlUri(xmlFile);
    }


    /**
     * This method parses an XML file that contains the meta data
     * on all outbound gateways provisioned with the platform.
     * The location of the config file is determined by the URI passed
     * to the constructor or to the "setXmlUri" method.
     *
     * @return  A Collection of OutboundGateway objects each encapsulating the
     * meta data on a outbound gateway specified in the XML config file.
     * @see #setXmlUri
     * @see com.baypackets.ase.sbb.OutboundGateway
     */
    public Collection getAllDevices() {
        if (_logger.isDebugEnabled()) {
            _logger.debug("getAllDevices(): Parsing the outbound gateway config file from location: " + _xmlUri);
        }

        Collection outboundGateways = new ArrayList();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            SAXParser parser = factory.newSAXParser();

            InputStream stream = new BufferedInputStream(_xmlUri.toURL().openStream());
            // For exceptions during parsing return whatever gateways were valid
            try {
                parser.parse(stream, new ParseEventHandler(outboundGateways));
            } catch (Exception e) {
                String msg = "Error occurred while parsing the outbound gateway config file: " + e.getMessage();
                _logger.error(msg, e);
            }

            stream.close();
        } catch (Exception e) {
            String msg = "Error occurred while parsing the outbound gateway config file: " + e.getMessage();
            _logger.error(msg, e);
        }

        return outboundGateways;
    }


    /**
     * Returns the URI of the outbound gateway config file.
     * @return URI of the outbound gateway config file
     */
    public URI getXmlUri() {
        return _xmlUri;
    }

    /**
     * Sets the location of the outbound gateway config file to parse.
     * @param uri URI of the outbound gateway config file
     */
    public void setXmlUri(URI uri) {
        _xmlUri = uri;
    }

    /**
     * This is the handler used to process the parse events.
     */
    private class ParseEventHandler extends DefaultHandler {

        private Collection outboundGateways;
        private OutboundGatewayImpl outboundGateway;
        private String elemName;
        private boolean validObject;

        public ParseEventHandler(Collection outboundGateways) {
            this.outboundGateways = outboundGateways;
            outboundGateway = null;
            validObject = false;
        }

        /**
         * Invoked by the parser whenever a new element is encountered.
         */
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("ParseEventHandler.startElement(): Encountered element with name: " + qName);
            }

            if (qName.equals("outbound-gateway")) {
                this.outboundGateway = new OutboundGatewayImpl();
                validObject = true;
            } else if (qName.equals("noheartbeat")) {
                // noheartbeat is EMPTY process here
                if (outboundGateway != null) {
                    outboundGateway.disableHeartbeat();
                }
            }

            this.elemName = qName;
        }

        /**
         * Invoked by the parser whenever the body content of an element is
         * encountered.
         */
        public void characters(char[] chars, int start, int length) throws SAXException {
            String body = new String(chars, start, length);
            if (_logger.isDebugEnabled()) {
                _logger.debug("ParseEventHandler.characters(): Encountered body content of element with name: " + this.elemName + " and value: " + body);
            }

            if (outboundGateway != null && validObject) {

                try {
                    if (this.elemName.equals("id")) {
                        this.outboundGateway.setId(body);
                    } else if (this.elemName.equals("ip-address")) {
                        this.outboundGateway.setHost(InetAddress.getByName(body));
                    } else if (this.elemName.equals("port")) {
                        this.outboundGateway.setPort(Integer.parseInt(body));
                    } else if (this.elemName.equals("priority")) {
                        this.outboundGateway.setPriority(Integer.parseInt(body));
                    } else if (this.elemName.equals("heartbeaturi")) {
                        this.outboundGateway.setHeartbeatUri(body);
                    } else if (this.elemName.equals("group-id")) {
                        this.outboundGateway.setGroupId(body);
                    }
                } catch (Exception e) {
                    String msg = "The following error occurred while processing element, '" + this.elemName + "': " + e.getMessage();
                    _logger.error(msg, e);
                    validObject = false;
                }
            }
        }

        /**
         * Invoked by the parser whenever the end of an element is encountered.
         */
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (_logger.isDebugEnabled()) {
                _logger.debug("ParseEventHandler.endElement(): Encountered end of element with name: " + qName);
            }
            if (qName.equals("outbound-gateway")) {
                if (outboundGateway != null && validObject) {
                    this.outboundGateways.add(this.outboundGateway);
                }
            }
        }


        /**
         * Invoked by the parser to resolve the entity that refers to the
         * DTD used to validate the outbound gateway config file.
         */
        public InputSource resolveEntity(String publicId, String systemId) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("ParseEventHandler.resolveEntity(): Encountered entity: " + publicId);
            }

            InputStream stream = null;

            if (publicId.equals(PUBLIC_ID)) {
			if(_logger.isDebugEnabled() ) {
                _logger.debug("ParseEventHandler.resolveEntity(): Resolving DTD entity...");
			}
                stream = this.getClass().getClassLoader().getResourceAsStream(DTD_FILE);
            }

            if (_logger.isDebugEnabled()) {
                if (stream != null) {
                    _logger.debug("ParseEventHandler.resolveEntity(): Successfully resolved entity.");
                } else {
                    _logger.error("ParseEventHandler.resolveEntity(): Unable to resolve entity: " + publicId);
                }
            }

            return new InputSource(stream);
            }


        /**
         * Invoked by the parser when an error occurs.
         */
        public void error(SAXParseException e) throws SAXException {
            _logger.error(e.getMessage(), e);
            throw e;
        }

    }
}
