/*
 * FileBasedMsDAO.java
 *
 * Created on July 2, 2005, 1:19 PM
 */
package com.baypackets.ase.mediaserver;

import com.baypackets.ase.sbb.MediaServer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
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
 * This implementation of the MediaServerDAO interface reads the meta data
 * on all provisioned media servers from an XML config file.
 *
 * @author Baypackets
 */
public class FileBasedMsDAO implements MediaServerDAO {

    private static Logger _logger = Logger.getLogger(FileBasedMsDAO.class);
        private static Pattern _bitSetPattern = Pattern.compile("[01]");

    public URI _xmlUri;

    /**
     * Default constructor.
     */
    public FileBasedMsDAO() {
    }


    /**
     * Constructs a new FileBasedMsDAO object and initializes it with the
     * URI of the media server config file to parse.
     *
     * @param xmlFile  The URI of the config file to parse.
     */
    public FileBasedMsDAO(URI xmlFile) {
        this.setXmlUri(xmlFile);
    }


    /**
     * This method parses an XML file that contains the meta data
     * on all media servers provisioned with the platform.
     * The location of the config file is determined by the URI passed
     * to the constructor or to the "setXmlUri" method.
     *
     * @return  A Collection of MediaServer objects each encapsulating the
     * meta data on a media server specified in the XML config file.
     * @see #setXmlUri
     * @see com.baypackets.ase.ext.mediaserver.MediaServer
     */
    public Collection<MediaServer> getAllMediaServers() {
        if (_logger.isDebugEnabled()) {
            _logger.debug("getAllMediaServers(): Parsing the media server config file from location: " + _xmlUri);
        }

        Collection mediaServers = new ArrayList();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            SAXParser parser = factory.newSAXParser();

            InputStream stream = new BufferedInputStream(_xmlUri.toURL().openStream());
            try {
                parser.parse(stream, new ParseEventHandler(mediaServers));
            } catch (Exception e) {
                String msg = "Error occurred while parsing the media server config file: " + e.getMessage();
                _logger.error(msg, e);
            }

            stream.close();

        } catch (Exception e) {
            String msg = "Error occurred while parsing the media server config file: " + e.getMessage();
            _logger.error(msg, e);
        }
        return mediaServers;
    }

    /**
     * This method parses an XML file that contains the meta data
     * on all media servers provisioned with the platform.
     * The location of the config file is determined by the URI passed
     * to the constructor or to the "setXmlUri" method.
     *
     * @return  A object of MediaServer encapsulating the
     * meta data on a media server specified in the XML config file.
     * @see #setXmlUri
     * @see com.baypackets.ase.ext.mediaserver.MediaServer
     */
    public MediaServer getMediaServer(String id){
    	if (_logger.isDebugEnabled()) {
    		_logger.debug("getMediaServer(id): Parsing the media server config file from location: " + _xmlUri);
    	}

    	MediaServer mediaServer=null;
    	Collection<MediaServer> mediaServers=this.getAllMediaServers();
    	for(MediaServer ms:mediaServers){
    		if(id.equals(ms.getId())){
    			mediaServer=ms;
    			break;
    		}
    	}
    	if (_logger.isDebugEnabled()) {
    		_logger.debug("getMediaServer(id): returning the media server read from config file:" + mediaServer);
    	}
    	return mediaServer;
    }

    
    /**
     * Returns the URI of the media server config file.
     */
    public URI getXmlUri() {
        return _xmlUri;
    }


    /**
     * Sets the location of the media server config file to parse.
     */
    public void setXmlUri(URI uri) {
        _xmlUri = uri;
    }


    /**
     * This is the handler used to process the parse events.
     */
    private class ParseEventHandler extends DefaultHandler {

        private Collection mediaServers;
        private Collection capabilities;
        private MediaServerImpl mediaServer;
        private String elemName;
        private boolean validObject = false;
        private StringBuffer capability = null;
        private StringBuffer msId = null;
        private StringBuffer msName = null;
        private StringBuffer msHost = null;
        private StringBuffer msPort = null;
        private StringBuffer msHBURI = null;
        private StringBuffer msABURI = null;
        private StringBuffer msRBURI = null;
        private StringBuffer msACName = null;
        private StringBuffer msDefaultState = null;
        private StringBuffer msRemote = null;
        private StringBuffer msPrivate = null;

        public ParseEventHandler(Collection mediaServers) {
            this.mediaServers = mediaServers;
        }

        /**
         * Invoked by the parser whenever a new element is encountered.
         */
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("ParseEventHandler.startElement(): Encountered element with name: " + qName);
            }

            if (qName.equals("media-server")) {
                this.capabilities = new ArrayList();
                this.mediaServer = new MediaServerImpl();
                validObject = true;
            } else if (qName.equals("noheartbeat")) {
            // noheartbeat is EMPTY element pick up its existence here.
                this.mediaServer.disableHeartbeat();
            } else if (qName.equals("capability")){
            	capability = new StringBuffer();
            }else if(qName.equals("id")){
            	msId = new StringBuffer();
            }else if(qName.equals("vendor-name")){
            	msName = new StringBuffer();
            }else if(qName.equals("ip-address")){
            	msHost = new StringBuffer();
            }else if(qName.equals("port")){
            	msPort = new StringBuffer();
            }else if(qName.equals("heartbeaturi")){
            	msHBURI = new StringBuffer();
            }else if(qName.equals("announcement-base-uri")){
            	msABURI = new StringBuffer();
            }else if(qName.equals("recording-base-uri")){
            	msRBURI = new StringBuffer();
            }else if(qName.equals("adaptor-class-name")){
            	msACName = new StringBuffer();
            }else if(qName.equals("default-state")){
            	msDefaultState = new StringBuffer();
            }else if(qName.equals("is-remote")){
            	msRemote = new StringBuffer();
            }else if(qName.equals("is-private")){
            	msPrivate = new StringBuffer();
            }

            this.elemName = qName;
        }

        /**
         * Invoked by the parser whenever the body content of an element is
         * encountered.
         */
        public void characters(char[] chars, int start, int length) throws SAXException {
            if (_logger.isDebugEnabled()) {
                _logger.debug("ParseEventHandler.characters(): Encountered body content of element with name: " + this.elemName + " and value: " + new String(chars, start, length));
                _logger.debug("Characters Startes with " + start + " and ends with " + length);
                if (chars != null){
                	_logger.debug("Characters Length is" + chars.length);
                }
            }

            if (mediaServer != null && validObject) {
            	String str = null;

                try {
                    if (this.elemName.equals("id")) {
                        str = new String(chars, start, length);
                        msId.append(str);
                    	//this.mediaServer.setId(new String(chars, start, length));
                    } else if (this.elemName.equals("vendor-name")) {
                        str = new String(chars, start, length);
                        msName.append(str);
                        //this.mediaServer.setName(new String(chars, start, length));
                    } else if (this.elemName.equals("ip-address")) {
                        str = new String(chars, start, length);
                        msHost.append(str);
                        //this.mediaServer.setHost(InetAddress.getByName(new String(chars, start, length)));
                    } else if (this.elemName.equals("port")) {
                        str = new String(chars, start, length);
                    	msPort.append(str);
                        //this.mediaServer.setPort(Integer.parseInt(new String(chars, start, length)));
                    } else if (this.elemName.equals("heartbeaturi")) {
                        str = new String(chars, start, length);
                    	msHBURI.append(str);
                        //this.mediaServer.setHeartbeatUri(new String(chars, start, length));
                    } else if (this.elemName.equals("capability")) {
                    	str = new String(chars, start, length);
                    	capability.append(str);
                    	//this.capabilities.add(new String(chars, start, length));
                    } else if (this.elemName.equals("announcement-base-uri")) {
                        str = new String(chars, start, length);
                    	msABURI.append(str);
                        //this.mediaServer.setAnnouncementBaseURI(new URI(new String(chars, start, length)));
                    } else if (this.elemName.equals("recording-base-uri")) {
                        str = new String(chars, start, length);
                    	msRBURI.append(str);
                        //this.mediaServer.setRecordingBaseURI(new URI(new String(chars, start, length)));
                    } else if (this.elemName.equals("adaptor-class-name")) {
                        str = new String(chars, start, length);
                    	msACName.append(str);
                        //this.mediaServer.setAdaptorClassName(new String(chars, start, length));
                    }else if (this.elemName.equals("default-state")) {
                        str = new String(chars, start, length);
                    	msDefaultState.append(str);
                        //this.mediaServer.setDefaultState(new String(chars, start, length));
                    //is-remote tag is added for GroupedMSSBB functionality
                    }else if (this.elemName.equals("is-remote")) {
                        str = new String(chars, start, length);
                    	msRemote.append(str);
                        //this.mediaServer.setIsRemote(Integer.parseInt(new String(chars, start, length)) == 1 ? 1 : 0);
                    }else if (this.elemName.equals("is-private")) {
                        str = new String(chars, start, length);
                    	msPrivate.append(str);
                        //this.mediaServer.setIsRemote(Integer.parseInt(new String(chars, start, length)) == 1 ? 1 : 0);
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

			try {
            if (qName.equals("media-server")) {
                if (mediaServer != null && validObject) {
                    this.mediaServers.add(this.mediaServer);
                }
            } else if (qName.equals("capabilities")) {

//					try {
                    int[] capabilities = new int[this.capabilities.size()];
                    Iterator iterator = this.capabilities.iterator();

                    for (int i = 0; i < capabilities.length; i++) {
							String capability = (String) iterator.next();

							if (capability
									.equals("CAPABILITY_VAR_ANNOUNCEMENT")) {
                            capabilities[i] = MediaServer.CAPABILITY_VAR_ANNOUNCEMENT;
							} else if (capability
									.equals("CAPABILITY_DIGIT_COLLECTION")) {
                            capabilities[i] = MediaServer.CAPABILITY_DIGIT_COLLECTION;
							} else if (capability
									.equals("CAPABILITY_AUDIO_CONFERENCE")) {
                            capabilities[i] = MediaServer.CAPABILITY_AUDIO_CONFERENCING;
							} else if (capability
									.equals("CAPABILITY_AUDIO_RECORDING")) {
                            capabilities[i] = MediaServer.CAPABILITY_AUDIO_RECORDING;
							} else if (capability
									.equals("CAPABILITY_VOICE_XML")) {
                            capabilities[i] = MediaServer.CAPABILITY_VOICE_XML;
                        } else if (capability.equals("CAPABILITY_VIDEO_CONFERENCE")) {
                            capabilities[i] = MediaServer.CAPABILITY_VIDEO_CONFERENCING;
                        } else if (capability.equals("CAPABILITY_VIDEO_RECORDING")) {
                            capabilities[i] = MediaServer.CAPABILITY_VIDEO_RECORDING;
                        } else {
								// capabilities[i] = parseBitSet(capability);
                            capabilities[i] = Integer.parseInt(capability);
                        }
                    }

                    this.mediaServer.setCapabilities(capabilities);
//					} catch (Exception e) {
//						String msg = "Error occurred while processing the body content of element: "
//								+ this.elemName;
//						_logger.error(msg, e);
//						validObject = false;
//					}
				} else if (qName.equals("capability")) {
					if (this.capability != null)
						this.capabilities.add(new String(capability));
				} else if (qName.equals("id")) {
					if (this.msId != null)
						this.mediaServer.setId(new String(this.msId));
				} else if (qName.equals("vendor-name")) {
					if (this.msName != null)
						this.mediaServer.setName(new String(this.msName));
				} else if (qName.equals("ip-address")) {
					if (this.msHost != null)
						this.mediaServer.setHost(InetAddress
								.getByName(new String(this.msHost)));
				} else if (qName.equals("port")) {
					if (this.msPort != null)
						this.mediaServer.setPort(Integer.parseInt(new String(
								this.msPort)));
				} else if (qName.equals("heartbeaturi")) {
					if (this.msHBURI != null) {
						String mshri = this.msHBURI.toString();
						if (mshri.contains(this.msHost)) {
							String hostAddress = InetAddress.getByName(this.msHost.toString())
									.getHostAddress();
							this.mediaServer.setHeartbeatUri(mshri.replaceAll(
									this.msHost.toString(), hostAddress));
						}else{
							this.mediaServer.setHeartbeatUri(mshri);
						}

					}
				} else if (qName.equals("announcement-base-uri")) {
					if (this.msABURI != null) {

						String maburi = this.msABURI.toString();
						if (maburi.contains(this.msHost)) {
							String hostAddress = InetAddress.getByName(this.msHost.toString()).getHostAddress();
							this.mediaServer.setAnnouncementBaseURI(new URI(
									maburi.replaceAll(this.msHost.toString(),
											hostAddress)));
						} else {
							this.mediaServer.setAnnouncementBaseURI(new URI(
									maburi));
						}
					}
				} else if (qName.equals("recording-base-uri")) {
					if (this.msRBURI != null){
						
						String mrburi = this.msRBURI.toString();
						if (mrburi.contains(this.msHost)) {
							String hostAddress = InetAddress.getByName(this.msHost.toString()).getHostAddress();
							this.mediaServer.setRecordingBaseURI(new URI(
									mrburi.replaceAll(this.msHost.toString(),
											hostAddress)));
						} else {
						    this.mediaServer.setRecordingBaseURI(new URI(mrburi));
						}
					}
					
					
				} else if (qName.equals("adaptor-class-name")) {
					if (this.msACName != null)
						this.mediaServer.setAdaptorClassName(new String(
								this.msACName));
				} else if (qName.equals("default-state")) {
					if (this.msDefaultState != null)
						this.mediaServer.setDefaultState(new String(
								this.msDefaultState));
				} else if (qName.equals("is-remote")) {
					if (this.msRemote != null)
						this.mediaServer.setIsRemote(Integer
								.parseInt(new String(this.msRemote)) == 1 ? 1
								: 0);
				}
				else if (qName.equals("is-private")) {
					if (this.msPrivate != null)
						this.mediaServer.setIsPrivate(Integer
								.parseInt(new String(this.msPrivate)) == 1 ? 1
								: 0);
				}
			} catch (Exception e) {
				String msg = "Error occurred while processing the body content of element: "
						+ this.elemName;
				_logger.error(msg, e);
				validObject = false;
            }
        }


        /**
         * Invoked by the parser to resolve the entity that refers to the
         * DTD used to validate the media server config file.
         */
        public InputSource resolveEntity(String publicId, String systemId) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("ParseEventHandler.resolveEntity(): Encountered entity: " + publicId);
            }

            InputStream stream = null;

            if (publicId.equals("-//Baypackets SIP Application Server//DTD Media Server Config//EN")) {
                _logger.debug("ParseEventHandler.resolveEntity(): Resolving DTD entity...");
                stream = this.getClass().getClassLoader().getResourceAsStream("com/baypackets/ase/mediaserver/media-server-config.dtd");
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


        /**
         * Parses the given string as a bit set and returns it as an
         * integer value.
         */
        private int parseBitSet(String bitSetString) {
            if (!_bitSetPattern.matcher(bitSetString.trim()).matches()) {
                return 0;
            }

            int bitSet = 0;

            for (int i = 0, j = bitSetString.length() - 1; i < 32 && j >= 0; i++) {
                bitSet += Integer.parseInt(String.valueOf(bitSetString.charAt(j--))) * (2 ^ i);
            }

            return bitSet;
        }

    }

}
