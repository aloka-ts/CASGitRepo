/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
package com.baypackets.sas.ide.alc;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import com.baypackets.sas.ide.SasPlugin;



public class AlcExtensionNature implements IProjectNature {

	/**
	 * ID of this project nature
	 */
	public static final String NATURE_ID = "com.agnity.cas.ide.alcExtensionNature";

	private IProject project;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	public void configure() throws CoreException {
//		IProjectDescription desc = project.getDescription();
//		ICommand[] commands = desc.getBuildSpec();
//
//		for (int i = 0; i < commands.length; ++i) {
//			if (commands[i].getBuilderName().equals(AlcBuilder.BUILDER_ID)) {
//				return;
//			}
//		}
//
//		ICommand[] newCommands = new ICommand[commands.length + 1];
//		System.arraycopy(commands, 0, newCommands, 0, commands.length);
//		ICommand command = desc.newCommand();
//		command.setBuilderName(AlcBuilder.BUILDER_ID);
//		newCommands[newCommands.length - 1] = command;
//		desc.setBuildSpec(newCommands);
//		SasPlugin.getDefault().log("Configuring project nature!!!!!!!!!!!!");
//		project.setDescription(desc, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	public void deconfigure() throws CoreException {
//		IProjectDescription description = getProject().getDescription();
//		ICommand[] commands = description.getBuildSpec();
//		for (int i = 0; i < commands.length; ++i) {
//			if (commands[i].getBuilderName().equals(AlcBuilder.BUILDER_ID)) {
//				ICommand[] newCommands = new ICommand[commands.length - 1];
//				System.arraycopy(commands, 0, newCommands, 0, i);
//				System.arraycopy(commands, i + 1, newCommands, i,
//						commands.length - i - 1);
//				description.setBuildSpec(newCommands);
//				return;
//			}
//		}
		SasPlugin.getDefault().log("Deconfiguring project nature!!!!!!!!!!!!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	/**
	 * @return  the project
	 * @uml.property  name="project"
	 */
	public IProject getProject() {
		return project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
	 */
	/**
	 * @param project  the project to set
	 * @uml.property  name="project"
	 */
	public void setProject(IProject project) {
		this.project = project;
	}

}
