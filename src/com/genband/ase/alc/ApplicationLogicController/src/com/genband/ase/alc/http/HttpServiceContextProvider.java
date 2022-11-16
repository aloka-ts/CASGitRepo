package com.genband.ase.alc.http;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpServletMessage;

import com.genband.ase.alc.alcml.jaxb.ServiceContext;
import com.genband.ase.alc.alcml.jaxb.ServiceContextProvider;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class HttpServiceContextProvider implements ServiceContextProvider
{
        public String DebugDumpContext()
        {
                return new String(" -- Servlet Context -- ");
        }

        public HttpServiceContextProvider(ServletContext servletcontext)
        {
                        this.servletcontext = servletcontext;
        }

        public HttpServiceContextProvider(ServletContext servletcontext, ServiceContext sContext)
        {
                sContext.setAttribute(Context, servletcontext);
                this.servletcontext = servletcontext;
        }

        public HttpServiceContextProvider(ServletContext servletcontext, HttpServletRequest  req,HttpServletResponse res, ServiceContext sContext)
        {
                sContext.setAttribute(Context, servletcontext);
                sContext.setAttribute(Request, req);
                sContext.setAttribute(Response, res);
                this.servletcontext = servletcontext;
        }

        public Object getAttribute(String nameSpace, String name)
        {
                Object value = servletcontext.getInitParameter(name);
                if (value == null)
                        value = servletcontext.getAttribute(name);
                return value;
        }

        public boolean setGlobalAttribute(String nameSpace, String name, Object value)
        {
                if (servletcontext != null)
                {
                        servletcontext.setAttribute(name, value);
                        return true;
                }
                return false;
        }


       public boolean setAttribute(String nameSpace, String name, Object value)
        {
                if (servletcontext.getAttribute(name) != null)
                {
                        servletcontext.setAttribute(name, value);
                        return true;
                }
                return false;
        }

        public static final String Context = new String("_Context");
        public static final String Request = new String("_Request");
        public static final String Response = new String("_Response");
        public static final String Session = new String("_Session");

        private transient ServletContext servletcontext = null;

}
