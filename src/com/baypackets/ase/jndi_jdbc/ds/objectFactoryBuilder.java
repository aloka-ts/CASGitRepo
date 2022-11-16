package com.baypackets.ase.jndi_jdbc.ds;
import javax.naming.*;
import java.util.*;
import javax.naming.spi.*;
import javax.naming.spi.ObjectFactory;

public class objectFactoryBuilder implements ObjectFactoryBuilder
{

        public ObjectFactory createObjectFactory(Object object, Hashtable env) throws NamingException
        {
                DataSourceImplFactory dataSourceImplFactory=new DataSourceImplFactory();
                return dataSourceImplFactory;
        }
}




