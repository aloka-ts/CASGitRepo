/*
 * FileBasedCallDAO.java
 *
 * Created on September 16, 2004, 12:32 PM
 */
package com.baypackets.clicktodial.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;


/**
 * This implementation of the CallDAO interface utilizes object serialization 
 * to persist and retrieve Call objects to and from a file based backing store.
 */
public class FileBasedCallDAO implements CallDAO {
    
    private static Logger _logger = Logger.getLogger(FileBasedCallDAO.class);
    
    private String dbDir;
        
    /**
     * Performs initialization.
     *
     * @param dbPath  The absolute path of the directory where the Call
     * objects will be persisted.
     */
    public FileBasedCallDAO(String dbDir) {
        this.dbDir = dbDir;
    }
        
    /**
     * Persists the given Call object to the backing store.
     */
    public void persist(Call call) {
        try {
            File file = new File(this.dbDir, call.getCallID() + ".ser");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(call);
            oos.close();
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            throw new RuntimeException(e.toString());
        }
    }
        
    /**
     * Retrieves the specified Call object from the backing store.
     */
    public Call findByID(String callID) {
        try {
            File file = new File(this.dbDir, callID + ".ser");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            Call call = (Call)ois.readObject();
            ois.close();
            return call;
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            throw new RuntimeException(e.toString());
        }
    }
    
}
