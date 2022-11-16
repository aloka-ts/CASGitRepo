package com.genband.ase.alc.http;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import com.genband.ase.alc.alcml.jaxb.*;
import com.genband.ase.alc.alcml.jaxb.xjc.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

public class ALCHttpServletInterface extends HttpServlet
{
	static Logger logger = Logger.getLogger(ALCHttpServletInterface.class.getName());
    public void init(ServletConfig config)
    throws ServletException
    {
        super.init(config);
    }

    public void destroy()
    {
        try
        {
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
		logger.log(Level.DEBUG, "Entering doGet");

        // set header field first
        res.setContentType("text/html");

        // then get the writer and write the response data
        PrintWriter out = res.getWriter();

		String ns = ServiceDefinition.UNNAMED;
		String serviceName = null;
        Enumeration values = req.getParameterNames();
		while(values.hasMoreElements())
		{
			String name = (String)values.nextElement();
			String value = req.getParameterValues(name)[0];
			if (name.equals("name"))
				serviceName = value;
			if (name.equals("ns"))
				ns = value;
		}

		if (ns != null)
		{
			Iterator<ServiceDefinition> iter = null;
			if (serviceName == null)
			{
				Collection<ServiceDefinition> services = null;
				services = ServiceDefinition.getServiceDefinitionForNamespace(ns);
				if (services != null)
				{
					iter = services.iterator();
					logger.log(Level.DEBUG, "doGet - HTTP Service namespace found " + ns);
				}
			}
			else
			{
				LinkedList<ServiceDefinition> sds = new LinkedList<ServiceDefinition>();
				ServiceDefinition iService = ServiceDefinition.getServiceDefinition(ns, serviceName);
				if (iService != null)
					sds.add(iService);
				iter = sds.iterator();
			}

			if (iter != null && iter.hasNext())
			{
				out.println("<html>");
				out.println("<body>");
				boolean foundSomething = false;
				while (iter.hasNext())
				{
					ServiceDefinition iService = iter.next();
					if (iService.Name.equals("initialize") || iService.Name.equals("destruct"))
						continue;

					Formtype ft = ServiceDefinition.getFormDefinition(ns, iService.getIoForm());

					if (ft != null)
					{

						boolean usingDefaults = ft.isUseDefaults() == null ? true : ft.isUseDefaults();
						foundSomething = true;
						logger.log(Level.DEBUG, "doGet - HTTP Service found " + iService.NameSpace + "::" + iService.Name);

						out.println("<div id=\""+ ns + "_" + serviceName + "\">");
						if (ft.getPrologue() == null)
						{
							out.println(iService.Name + "<br>");
						}
						else
						{
							List<Element> eList = ft.getPrologue().getAny();
							Iterator<Element> eListIterator = eList.iterator();
							if (eListIterator.hasNext())
							{
								try
								{
									DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
									DocumentBuilder db = dbf.newDocumentBuilder();
									Document d = db.newDocument();
									Element htmle = d.createElement("html");
									while (eListIterator.hasNext())
									{
										htmle.appendChild(d.importNode(eListIterator.next(), true));
									}
									ByteArrayOutputStream baos = new ByteArrayOutputStream();
									TransformerFactory transformerFactory = TransformerFactory.newInstance();
									Transformer transformer = transformerFactory.newTransformer();
									transformer.setOutputProperty("indent", "yes");
									transformer.transform(new DOMSource(htmle), new StreamResult(baos));
									logger.log(Level.DEBUG, "xml in form : "+ft.getName()+", for prologue "+ baos);
									out.println(new ALCMLExpression(new ServiceContext(), baos.toString()));
								}
								catch (Exception xmle)
								{
									logger.log(Level.ERROR, "Malformed xml in form: "+ft.getName()+", for prologue ", xmle);
								}
							}
						}
						out.println("<form name=\"input\" action=\"/alcml/service-action?ns="+iService.NameSpace+"&name="+iService.Name+"\" method=\"post\">");

						List<AttributeInputtype> formAttrList = ft.getAttributeInput();
						Iterator<AttributeInputtype> formAttrListIter = formAttrList.iterator();
						if (formAttrListIter.hasNext())
						{
							while(formAttrListIter.hasNext())
							{
								AttributeInputtype attr = formAttrListIter.next();
								List<Element> eList = attr.getAny();
								Iterator<Element> eListIterator = eList.iterator();
								if (eListIterator.hasNext())
								{
									try
									{
										DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
										DocumentBuilder db = dbf.newDocumentBuilder();
										Document d = db.newDocument();
										Element htmle = d.createElement("html");
										while (eListIterator.hasNext())
										{
											htmle.appendChild(d.importNode(eListIterator.next(), true));
										}
										ByteArrayOutputStream baos = new ByteArrayOutputStream();
										TransformerFactory transformerFactory = TransformerFactory.newInstance();
										Transformer transformer = transformerFactory.newTransformer();
										transformer.setOutputProperty("indent", "yes");
										transformer.transform(new DOMSource(htmle), new StreamResult(baos));
										logger.log(Level.DEBUG, "xml in form: "+ft.getName()+", for attribute "+attr.getName() + baos);
										out.println(new ALCMLExpression(new ServiceContext(), baos.toString()));
									}
									catch (Exception xmle)
									{
										logger.log(Level.ERROR, "Malformed xml in form: "+ft.getName()+", for attribute "+attr.getName(), xmle);
									}
								}
								else
								{
									if (usingDefaults)
									{
										out.println(attr.getName() + ":");
										out.println("<input type=\"text\" name=\"" + attr.getName() + "\"/><br>");
									}
								}
							}
						}
						else
						{
							List<Attributetype> serviceAttrList = iService.getAttribute();
							Iterator<Attributetype> serviceAttrListIter = serviceAttrList.iterator();
							while(serviceAttrListIter.hasNext())
							{
								Attributetype attr = serviceAttrListIter.next();
								if (usingDefaults)
								{
									out.println(attr.getName() + ":");
									out.println("<input type=\"text\" name=\"" + attr.getName() + "\"/><br>");
								}
							}
						}
						if (ft.getAction() == null)
							out.println("<input type=\"submit\" value=\"Submit\"/>");
						else
						{
							List<Element> eList = ft.getAction().getAny();
							Iterator<Element> eListIterator = eList.iterator();
							if (eListIterator.hasNext())
							{
								try
								{
									DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
									DocumentBuilder db = dbf.newDocumentBuilder();
									Document d = db.newDocument();
									Element htmle = d.createElement("html");
									while (eListIterator.hasNext())
									{
										htmle.appendChild(d.importNode(eListIterator.next(), true));
									}
									ByteArrayOutputStream baos = new ByteArrayOutputStream();
									TransformerFactory transformerFactory = TransformerFactory.newInstance();
									Transformer transformer = transformerFactory.newTransformer();
									transformer.setOutputProperty("indent", "yes");
									transformer.transform(new DOMSource(htmle), new StreamResult(baos));
									logger.log(Level.DEBUG, "xml in form : "+ft.getName()+", for action "+ baos);
									out.println(new ALCMLExpression(new ServiceContext(), baos.toString()));
								}
								catch (Exception xmle)
								{
									logger.log(Level.ERROR, "Malformed xml in form: "+ft.getName()+", for action ", xmle);
								}
							}
						}

						out.println("</form>");
						if (ft.getEpilogue() != null)
						{
							List<Element> eList = ft.getEpilogue().getAny();
							Iterator<Element> eListIterator = eList.iterator();
							if (eListIterator.hasNext())
							{
								try
								{
									DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
									DocumentBuilder db = dbf.newDocumentBuilder();
									Document d = db.newDocument();
									Element htmle = d.createElement("html");
									while (eListIterator.hasNext())
									{
										htmle.appendChild(d.importNode(eListIterator.next(), true));
									}
									ByteArrayOutputStream baos = new ByteArrayOutputStream();
									TransformerFactory transformerFactory = TransformerFactory.newInstance();
									Transformer transformer = transformerFactory.newTransformer();
									transformer.setOutputProperty("indent", "yes");
									transformer.transform(new DOMSource(htmle), new StreamResult(baos));
									logger.log(Level.DEBUG, "xml in form : "+ft.getName()+", for epilogue "+ baos);
									out.println(new ALCMLExpression(new ServiceContext(), baos.toString()));
								}
								catch (Exception xmle)
								{
									logger.log(Level.ERROR, "Malformed xml in form: "+ft.getName()+", for epilogue ", xmle);
								}
							}
						}
						out.println("</div>");
					}
					else
					{
						logger.log(Level.DEBUG, "doGet - HTTP Service Form NOT found for " + ns + "::" + iService.Name);
					}
				}
				if (!foundSomething)
				{
					out.println("<h1>Service not found.</h1>");
					out.println("<P>The requested service was not found.");
				}
				out.println("</html>");
				out.println("</body>");
				out.close();
				return;
			}
		}

		logger.log(Level.DEBUG, "doGet - HTTP Service NOT found " + ns + "::" + serviceName);
		out.println("<HEAD><TITLE>Service not found.</TITLE></HEAD><BODY>");
		out.println("<h1>Service not found.</h1>");
		out.println("<P>The requested service was not found.");
		out.println("</BODY>");
        out.close();

    }

	private AttributeInputtype getAttributeInput(String name, Formtype ft)
	{
		List<AttributeInputtype> attrlist = ft.getAttributeInput();
		Iterator<AttributeInputtype> attrIter = attrlist.iterator();
		while (attrIter.hasNext())
		{
			AttributeInputtype value = attrIter.next();
			if (value.getName().equals(name))
				return value;
		}
		return null;
	}

	private Attributetype getAttributeInput(String name, ServiceDefinition sd)
	{
		List<Attributetype> attrlist = sd.getAttribute();
		Iterator<Attributetype> attrIter = attrlist.iterator();
		while (attrIter.hasNext())
		{
			Attributetype value = attrIter.next();
			if (value.getName().equals(name))
				return value;
		}
		return null;
	}

    public String getServletInfo()
    {
        return "A simple servlet";
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
		logger.log(Level.DEBUG, "Entering doPost");

        // first, set the "content type" header of the response
        res.setContentType("text/html");

        //Get the response's PrintWriter to return text to the client.
        PrintWriter toClient = res.getWriter();

        try
        {

			String ns = ServiceDefinition.UNNAMED;
			String serviceName = null;
			ServiceContext sdContext = new ServiceContext();
			Enumeration values = req.getParameterNames();
			while(values.hasMoreElements())
			{
				String name = (String)values.nextElement();
				String value = req.getParameterValues(name)[0];
				if (name.equals("name"))
				{
					serviceName = value;
					continue;
				}
				if (name.equals("ns"))
				{
					ns = value;
					continue;
				}
				logger.log(Level.DEBUG, "doPost - setting context attribute " + name + " = " + value);
				sdContext.setAttribute(name, value);
			}
			ServiceDefinition iService = ServiceDefinition.getServiceDefinition(ns, serviceName);

			if (iService == null)
			{
				logger.log(Level.DEBUG, "doPost - HTTP Service NOT found " + ns + "::" + serviceName);
				toClient.println("<html>");
				toClient.println("<title>Thank you!</title>");
				toClient.println("Thank you for participating");
				toClient.println("</html>");
			}
			else
			{
				logger.log(Level.DEBUG, "doPost - HTTP Service found " + ns + "::" + serviceName);
				iService.execute(sdContext);
				toClient.println("<html>");

				Formtype ft = ServiceDefinition.getFormDefinition(ns, iService.getIoForm());
				boolean customOutput = false;
				if (ft != null)
				{
					if (ft.getOutput() != null)
					{
						List<Element> eList = ft.getOutput().getAny();
						Iterator<Element> eListIterator = eList.iterator();
						if (eListIterator.hasNext())
						{
							try
							{
								DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
								DocumentBuilder db = dbf.newDocumentBuilder();
								Document d = db.newDocument();
								Element htmle = d.createElement("html");
								while (eListIterator.hasNext())
								{
									htmle.appendChild(d.importNode(eListIterator.next(), true));
								}
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								TransformerFactory transformerFactory = TransformerFactory.newInstance();
								Transformer transformer = transformerFactory.newTransformer();
								transformer.setOutputProperty("indent", "yes");
								transformer.transform(new DOMSource(htmle), new StreamResult(baos));
								logger.log(Level.DEBUG, "xml in form: "+ft.getName()+" for output " + baos);
								toClient.println(new ALCMLExpression(sdContext, baos.toString()));
								customOutput = true;
							}
							catch (Exception xmle)
							{
								logger.log(Level.ERROR, "Malformed xml in form: "+ft.getName()+", for output ", xmle);
							}
						}

					}
				}
				if (!customOutput)
					toClient.println((String)sdContext.getAttribute("buffer"));
				toClient.println("</html>");

			}
        }

        catch(Exception e)
        {
            e.printStackTrace();
            toClient.println(
                "A problem occured while executing"
            + "Please try again.");
        }

        // Close the writer; the response is done.
        toClient.close();
    }
}

