//***********************************************************************************
//GENBAND, Inc. Confidential and Proprietary
//
//This work contains valuable confidential and proprietary
//information.
//Disclosure, use or reproduction without the written authorization of
//GENBAND, Inc. is prohibited. This unpublished work by GENBAND, Inc.
//is protected by laws of United States and other countries.
//If publication of work should occur the following notice shall
//apply:
//
//"Copyright 2007 GENBAND, Inc. All right reserved."
//***********************************************************************************
                                                                                                                      
                                                                                                                      
//***********************************************************************************
//
//      File:   CodeGenerator.java
//
//      Desc:   This is a utility class which will store class bytecode after compilation.
//				A file object that retains contents in memory.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Rajendra Singh                  19/01/08        Initial Creation
//
//***********************************************************************************



package com.baypackets.ase.soa.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * A file object that retains contents in memory and does not write
 * out to disk.
 * 
 * @author prunge
 */
public class MemoryOutputJavaFileObject extends SimpleJavaFileObject
{
	private ByteArrayOutputStream outputStream;

	/**
	 * Constructs a <code>MemoryOutputJavaFileObject</code>.
	 *
	 * @param uri the URI of the output file.
	 * @param kind the file type.
	 */
	public MemoryOutputJavaFileObject(final URI uri, final Kind kind)
	{
		super(uri, kind);
	}

	public ByteArrayOutputStream getOutputStream()	{
		return outputStream;
	}

	/**
	 * Opens an output stream to write to the file.  This writes to 
	 * memory.  This clears any existing output in the file.
	 * 
	 * @return an output stream.
	 * 
	 * @throws IOException if an I/O error occurs.
	 */
	@Override
	public OutputStream openOutputStream() 
	throws IOException
	{
		outputStream = new ByteArrayOutputStream();
		return(outputStream);
	}

	/**
	 * Opens an input stream to the file data.  If the file has never
	 * been written the input stream will contain no data (i.e. length=0).
	 * 
	 * @return an input stream.
	 * 
	 * @throws IOException if an I/O error occurs.
	 */
	@Override
	public InputStream openInputStream() throws IOException
	{
		if (outputStream != null)
			return(new ByteArrayInputStream(outputStream.toByteArray()));
		else
			return(new ByteArrayInputStream(new byte[0]));
	}
}
