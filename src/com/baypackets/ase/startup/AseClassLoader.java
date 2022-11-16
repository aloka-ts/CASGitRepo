package com.baypackets.ase.startup;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.net.URL;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AppClassLoader;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.util.Constants;


public class AseClassLoader extends URLClassLoader
{
    public AseClassLoader()
    {
        super(new URL[0]);
    }

    public AseClassLoader(URL[] urls)
    {
        super(urls);
    }

    public AseClassLoader(ClassLoader parent)
    {
        super(new URL[0], parent);
    }

    public AseClassLoader(URL[] urls, ClassLoader parent)
    {
        super(urls,parent);
    }

    public AseClassLoader createLoaderExtension()
    {
        AseClassLoader returnVal = new AseClassLoader();
        returnVal.setOwner(this);

        loaderList.add(returnVal);
        return returnVal;
    }

    public void addRepository(URL repository)
    {
        super.addURL(repository);
    }

    public void removeLoader(AseClassLoader loader)
    {
        if (loader != null)
        {
            if (loaderList.contains(loader))
				loaderList.remove(loader);
        }
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException
    {
		/* this has to be loadClass and NOT findClass d/t findClass super methodology ALWAYS tries to instantiate */
		/* a class, even if he's found it before */
    	return topLevelLoader().search(name);
    	//Reverted this code as SBB classes and VPN,ATF common classes are now added to the 
    	//URL class path of the AseClassLoader
/*    	try{
    		return topLevelLoader().search(name);	
    	}catch (ClassNotFoundException e) {
        	//Trying to load the class SBB Servlet from Sbb Class Loaded when 
        	//GMS SBB is getting serialized
    		
    		Object aseHostObj = null;
    		try{
    			Class registry = Class.forName("com.baypackets.ase.common.Registry", true, this);
    			Class constants = Class.forName("com.baypackets.ase.util.Constants", true, this);
    			Field hostField = constants.getField("NAME_HOST");
    			aseHostObj = registry.getMethod("lookup",String.class).invoke(null, hostField.get(null));
    			
    			if (aseHostObj != null){
    				Class aseHost = Class.forName("com.baypackets.ase.container.AseHost", true, this);
    				Object appClassLoaderObj = aseHost.getMethod("getLatestSbbCL").invoke(aseHostObj);
    				Class appClassLoader = Class.forName("com.baypackets.ase.container.AppClassLoader", true, this);
    				Object classObj = appClassLoader.getMethod("loadClass",String.class).invoke(appClassLoaderObj,name);
     				return (Class) classObj;
     			}else{
     				throw e;
     			}
    		}catch (Throwable e2) {
				//In case of class specific to Toll Free and VPN we need to load them from respective class loaders
    			int size=0;
    			Class appClassLoader = null;
    			List<ClassLoader> tcapProviderSiblings = null;
   				if (aseHostObj != null){
   					try{
   						Class aseHost = Class.forName("com.baypackets.ase.container.AseHost", true, this);
   						Object appClassLoaderObj = aseHost.getMethod("getTcapProviderCL").invoke(aseHostObj);
   						appClassLoader = Class.forName("com.baypackets.ase.container.AppClassLoader", true, this);
   						Object listObj = appClassLoader.getMethod("getSiblings").invoke(appClassLoaderObj);
   						tcapProviderSiblings = (List<ClassLoader>) listObj;
   						size = tcapProviderSiblings.size();
   					}catch (Throwable e3) {
   						throw new ClassNotFoundException("AseClassLoader1", e3);
					}
					Object classObj = null;
					for (int i=0;i<size;i++ ){
   						try{
   							classObj = appClassLoader.getMethod("loadClass",String.class).invoke(tcapProviderSiblings.get(i),name);
   							System.out.println("Class being returned from Listener's Class Loader " + name);
   							if (classObj != null)
   								break;
   						}catch (Throwable cnfe) {
   							if (i == (size-1))
   								throw new ClassNotFoundException("AseClassLoader2", cnfe);
						}
   					}
   					return (Class) classObj;
   				}else{
   					throw e;
   				}
			}
		}*/
	}
    
    Class<?> search(String name) throws ClassNotFoundException
    {
        Class<?> returnVal = null;
        try
        {
            /* first look in my class */
            returnVal = super.loadClass(name);
        }
        catch (ClassNotFoundException cnfe)
        {
        }

        if (returnVal == null)
        {
            /* now search my extensions */
            for (AseClassLoader internalLoader : loaderList)
            {
                try
                {
                    returnVal = internalLoader.search(name);
                }
                catch (ClassNotFoundException cnfe)
                {
                }
                if (returnVal != null)
					return returnVal;
            }
        }

        if (returnVal == null)
        	throw new ClassNotFoundException("class " + name + " not found.");

        return returnVal;
    }

    AseClassLoader topLevelLoader()
    {
        if (myOwner == null)
        	return this;
        return myOwner.topLevelLoader();
    }

    void setOwner(AseClassLoader myOwner)
    {
        this.myOwner = myOwner;
    }

    protected LinkedList<AseClassLoader> loaderList = new LinkedList<AseClassLoader>();
    protected AseClassLoader myOwner = null;
}


