package com.baypackets.ase.util;

import org.apache.log4j.Logger;

import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.IOException;
import java.io.InputStream;


/**
 * This class provides a customizable ObjectInputStream capable of being
 * configured with different class loaders to be used when de-serializing
 * objects from an input stream.
 */
public class AseObjectInputStream extends ObjectInputStream {
    
    private static Logger _logger = Logger.getLogger(AseObjectInputStream.class);
    
    private ClassLoader loader;
    
    private ClassLoader sbbLoader;
    
	/**
     * Default Constructor.
     */
    protected AseObjectInputStream() throws IOException, SecurityException {
        super();
    }
    
    /**
     * @param stream  The input stream to de-serialize objects from.
     */
    public AseObjectInputStream(InputStream stream) throws IOException {
        super(stream);
    }

    /**
     * Sets the class loader to use when loading the classes of objects being
		 * de-serialized from the input stream.
     */
    public void setClassLoader(ClassLoader loader) {
        this.loader = loader;
    }    
    
    /**
     *
     */
    public ClassLoader getClassLoader() {
        return loader;        
    }
        
    /**
     * This method is invoked by the "readObject()" method to load the class
		 * of object that is currently being read from the input stream.  If any
		 * classloader is currently set on this object, that classloader will be
		 * used to load the class.  If no classloader is currently set, this method
		 * will delegate to the super class's implementation of this method which
		 * uses the system classloader.
		 *
		 * @param descriptor  Contains the meta data on the class of object 
		 * currently being read from the stream that includes the class's name.
		 * @see #setClassLoader(ClassLoader)
     */
    public Class resolveClass(ObjectStreamClass descriptor) throws IOException, ClassNotFoundException {
        boolean loggerEnabled = _logger.isDebugEnabled();
				
				if (loggerEnabled) {
            _logger.debug("resolveClass(): Loading class, " + descriptor.getName() + " from input stream...");
        }
        
		if (this.sbbLoader != null) {
		    if (loggerEnabled) {
		         _logger.debug("resolveClass(): SBB classloader is currently set on this object, so loading class using SBB classloader.");
		    }
		    return Class.forName(descriptor.getName(), true, this.sbbLoader);
		}
        
		if (this.loader == null) {
            if (loggerEnabled) {
                _logger.debug("resolveClass(): No classloader is currently set on this object, so loading class using system classloader.");
            }
            return super.resolveClass(descriptor);
        }
        
        if (loggerEnabled) {
            _logger.debug("resolveClass(): Using classloader, " + this.loader.getClass().getName() + " to load class...");
        }
        return Class.forName(descriptor.getName(), true, this.loader);        
    }
    
    public ClassLoader getSbbLoader() {
		return sbbLoader;
	}

	public void setSbbLoader(ClassLoader sbbLoader) {
		this.sbbLoader = sbbLoader;
	}

}
