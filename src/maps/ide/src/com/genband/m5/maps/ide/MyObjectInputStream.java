package com.genband.m5.maps.ide;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.net.URLClassLoader;

import com.genband.m5.maps.ide.CPFPlugin;
public class MyObjectInputStream extends ObjectInputStream {

    URLClassLoader myLoader = null;
    
    public MyObjectInputStream(URLClassLoader newLoader, InputStream theStream) throws IOException, StreamCorruptedException
    {
        super(theStream);
        myLoader = newLoader;
    }
    
    protected Class resolveClass(ObjectStreamClass osc) throws IOException, ClassNotFoundException
    {
        CPFPlugin.getDefault().log("I'm in my resolveClass");
        Class theClass = null;
        CPFPlugin.getDefault().log("" + osc.getName());
        try
        {
            theClass = Class.forName(osc.getName(), false, myLoader);
            
        }
        catch (Exception e)
        {
            CPFPlugin.getDefault().error("An Error in my ResolveClass:",e);
            //CPFPlugin.getDefault().log("super returned: " + super.resolveClass(osc));
            //CPFPlugin.getDefault().log("super returned: " + super.resolveClass(osc).getName());
            theClass =  super.resolveClass(osc);
        }
        CPFPlugin.getDefault().log("the class is : " + theClass);
        return theClass;
    }
}
