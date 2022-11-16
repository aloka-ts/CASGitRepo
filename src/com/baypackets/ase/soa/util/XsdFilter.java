package com.baypackets.ase.soa.util;


import java.io.File;
import java.io.FilenameFilter;


public class XsdFilter implements FilenameFilter {  
	private String suffix;

	public XsdFilter()	{

	}

	public XsdFilter(String suffix)	{
		this.suffix = suffix;
	}

     public boolean accept(File dir, String name)  {  
		return name.endsWith(suffix) ;
      }  
    }  
