package com.baypackets.sas.ide.logger.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;

import com.baypackets.sas.ide.SasPlugin;

/**
 * Utilities for dealing with images, icons, etc.
 */
public class ImageUtils
{
   
	/**
	 * Create an image descriptor for the given filename (relative to the
	 * plugin install directory)
	 */
	public static ImageDescriptor createImageDescriptor(String filename)
	{
		try {
			URL url = new URL(SasPlugin.getDefault().getDescriptor().getInstallURL(), filename);
			return ImageDescriptor.createFromURL(url);
		}
		catch (MalformedURLException e) {
			return null;
		}
	}
}
