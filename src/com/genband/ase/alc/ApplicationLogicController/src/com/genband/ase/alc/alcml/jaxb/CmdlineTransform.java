package com.genband.ase.alc.alcml.jaxb;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.util.*;
import java.io.*;
import java.net.*;

public class CmdlineTransform
{
	void CmdlineTransform() {}

	public static void main(String[] args)
	{
		try
		{
			InputStream xsltStream = (new URL(args[0])).openStream();
			InputStream xmlStream = (new URL(args[1])).openStream();
			OutputStream outStream = new FileOutputStream (new File(new URI(args[2])));

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer t = transformerFactory.newTransformer(new StreamSource(xsltStream));
			t.transform(new StreamSource(xmlStream), new StreamResult(outStream));

			outStream.close ();
			xsltStream.close ();
			xmlStream.close ();
		}
		catch (Exception e)
		{
			System.out.println(e);
			System.out.println("Transform failed.");
			System.out.println("Usage: java <class> <xslt url> <source document url> <output file>");
			System.out.println("Usage: java <class> "+args[0]+" "+args[1]+" "+args[2]);
		}

	}

}