/*
 * ServletContextAdapter.java
 *
 * Created on September 2, 2004, 4:05 PM
 */
package com.baypackets.ase.tomcat;

import com.baypackets.ase.container.AseContext;

import java.net.URL;
import java.io.InputStream;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.descriptor.JspConfigDescriptor;


/**
 * This class provides an implementation of ServletContext that delegates
 * to both Tomcat's and the ASE's implementation of ServletContext.
 *
 * @author  Zoltan Medveczky
 */
public class MergedServletContext implements ServletContext {

    private ServletContext _tomcatContext;
    private AseContext _aseContext;

    /**
     *
     *
     */
    @SuppressWarnings("unchecked")
	public MergedServletContext(ServletContext tomcatContext, AseContext aseContext) {
        _tomcatContext = tomcatContext;
        _aseContext = aseContext;

        // Synch up the attributes of both ServletContexts
        Enumeration names = aseContext.getAttributeNames();
        if (names != null) {
            while (names.hasMoreElements()) {
                String name = (String)names.nextElement();
                _tomcatContext.setAttribute(name, _aseContext.getAttribute(name));
            }
        }
    }


    /**
     * 
     *
     */
    public Object getAttribute(String name) {
        Object attribute = _tomcatContext.getAttribute(name);
        if (attribute == null) {
            attribute = _aseContext.getAttribute(name);
        }
        return attribute;
    }


    /**
     *
     *
     */
    @SuppressWarnings("unchecked")
    public Enumeration getAttributeNames() {
        return _tomcatContext.getAttributeNames();
    }


    /**
     *
     *
     */
    public ServletContext getContext(String uri) {
        return _tomcatContext.getContext(uri) != null ? _tomcatContext.getContext(uri) : _aseContext.getContext(uri);
    }


    /**
     *
     *
     */
    public String getInitParameter(String name) {
        return _tomcatContext.getInitParameter(name) != null ? _tomcatContext.getInitParameter(name) : _aseContext.getInitParameter(name);
    }


    /**
     * Returns the initialization parameter names of both Tomcat's and
     * the ASE's ServletContext objects.
     *
     */
    @SuppressWarnings("unchecked")
    public Enumeration getInitParameterNames() {
        Vector list = new Vector();

        Enumeration names = _tomcatContext.getInitParameterNames();

        if (names != null) {
            while (names.hasMoreElements()) {
                list.add(names.nextElement());
            }
        }
        
        names = _aseContext.getInitParameterNames();
        
        if (names != null) {
            while (names.hasMoreElements()) {
                list.add(names.nextElement());
            }
        }

        return list.elements();
    }


    /**
     *
     *
     */
    public int getMajorVersion() {
        return _aseContext.getMajorVersion();
    }


    /**
     *
     *
     */
    public String getMimeType(String str) {
        return _tomcatContext.getMimeType(str);
    }


    /**
     *
     *
     */
    public int getMinorVersion() {
        return _aseContext.getMinorVersion();
    }


    /**
     *
     *
     */
    public RequestDispatcher getNamedDispatcher(String name) {
        RequestDispatcher dispatcher = _tomcatContext.getNamedDispatcher(name);
        if (dispatcher == null) {
            dispatcher = _aseContext.getNamedDispatcher(name);
        }
        return dispatcher;
    }


    /**
     *
     *
     */
    public String getRealPath(String path) {
        return _tomcatContext.getRealPath(path);
    }


    /**
     *
     *
     */
    public RequestDispatcher getRequestDispatcher(String name) {
        RequestDispatcher dispatcher = _tomcatContext.getRequestDispatcher(name);
        if (dispatcher == null) {
            dispatcher = _aseContext.getNamedDispatcher(name);
        }
        return dispatcher;
    }


    /**
     *
     *
     */
    public URL getResource(String name) throws java.net.MalformedURLException {
        return _tomcatContext.getResource(name);
    }


    /**
     *
     *
     */
    public InputStream getResourceAsStream(String name) {
        return _tomcatContext.getResourceAsStream(name);
    }


    /**
     *
     *
     */
    @SuppressWarnings("unchecked")
    public java.util.Set getResourcePaths(String name) {
        return _tomcatContext.getResourcePaths(name);
        }


    /**
     *
     *
     */
    public String getServerInfo() {
        return _aseContext.getServerInfo();
    }


    /**
     *
     * @deprecated
     */
    public javax.servlet.Servlet getServlet(String name) throws javax.servlet.ServletException {
        return _tomcatContext.getServlet(name);
    }


    /**
     *
     * @deprecated
     */
    public String getServletContextName() {
        return _aseContext.getServletContextName();
    }


    /**
     *
     *@deprecated
     */
    @SuppressWarnings("unchecked")
    public Enumeration getServletNames() {
        return _tomcatContext.getServletNames();
    }


    /**
     *
     *@deprecated
     */
    @SuppressWarnings("unchecked")
    public Enumeration getServlets() {
        return _tomcatContext.getServlets();
    }


    /**
     *
     *@deprecated
     */
    public void log(String msg) {
        _aseContext.log(msg);
    }


    /**
     *
     *@deprecated
     */
    public void log(Exception exception, String msg) {
        _aseContext.log(exception, msg);
    }


    /**
     *
     */
    public void log(String msg, Throwable throwable) {
        _aseContext.log(msg, throwable);
    }


    /**
     *
     *
     */
    public void removeAttribute(String name) {
        _aseContext.removeAttribute(name);
    }


    /**
     *
     *
     */
    public void setAttribute(String name, Object obj) {
        _tomcatContext.setAttribute(name, obj);
        _aseContext.setAttribute(name, obj);
    }
    public AseContext getAseContext() {
                return  _aseContext;
    }


	@Override
	public Dynamic addFilter(String arg0, String arg1) {
		return _tomcatContext.addFilter(arg0, arg1);
	}


	@Override
	public Dynamic addFilter(String arg0, Filter arg1) {
		return _tomcatContext.addFilter(arg0, arg1);
	}


	@Override
	public Dynamic addFilter(String arg0, Class<? extends Filter> arg1) {
		return _tomcatContext.addFilter(arg0, arg1);
	}


	@Override
	public void addListener(Class<? extends EventListener> arg0) {
		_tomcatContext.addListener(arg0);
		
	}


	@Override
	public void addListener(String arg0) {
		_tomcatContext.addListener(arg0);		
	}


	@Override
	public <T extends EventListener> void addListener(T arg0) {
		_tomcatContext.addListener(arg0);
	}


	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			String arg1) {
		return _tomcatContext.addServlet(arg0, arg1);
	}


	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			Servlet arg1) {
		return _tomcatContext.addServlet(arg0, arg1);
	}


	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			Class<? extends Servlet> arg1) {
		return _tomcatContext.addServlet(arg0, arg1);
	}


	@Override
	public <T extends Filter> T createFilter(Class<T> arg0)
			throws ServletException {
		return _tomcatContext.createFilter(arg0);
	}


	@Override
	public <T extends EventListener> T createListener(Class<T> arg0)
			throws ServletException {
		return _tomcatContext.createListener(arg0);
	}


	@Override
	public <T extends Servlet> T createServlet(Class<T> arg0)
			throws ServletException {
		return _tomcatContext.createServlet(arg0);
	}


	@Override
	public void declareRoles(String... arg0) {
		_tomcatContext.declareRoles(arg0);
	}


	@Override
	public ClassLoader getClassLoader() {
		return _tomcatContext.getClassLoader();
	}


	@Override
	public String getContextPath() {
		 return _tomcatContext.getContextPath();
	}


	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		 return _tomcatContext.getDefaultSessionTrackingModes();
	}


	@Override
	public int getEffectiveMajorVersion() {
		return _tomcatContext.getEffectiveMajorVersion();
	}


	@Override
	public int getEffectiveMinorVersion() {
		return _tomcatContext.getEffectiveMinorVersion();
	}


	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		return _tomcatContext.getEffectiveSessionTrackingModes();
	}


	@Override
	public FilterRegistration getFilterRegistration(String arg0) {
		return _tomcatContext.getFilterRegistration(arg0);
	}


	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		return _tomcatContext.getFilterRegistrations();
	}


	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		return _tomcatContext.getJspConfigDescriptor();
	}


	@Override
	public ServletRegistration getServletRegistration(String arg0) {
		return _tomcatContext.getServletRegistration(arg0);
	}


	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		return _tomcatContext.getServletRegistrations();
	}


	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		return _tomcatContext.getSessionCookieConfig();
	}


	@Override
	public boolean setInitParameter(String arg0, String arg1) {
		return _tomcatContext.setInitParameter(arg0, arg1);
	}


	@Override
	public void setSessionTrackingModes(Set<SessionTrackingMode> arg0)
			throws IllegalStateException, IllegalArgumentException {
		_tomcatContext.setSessionTrackingModes(arg0);
		
	}

}
