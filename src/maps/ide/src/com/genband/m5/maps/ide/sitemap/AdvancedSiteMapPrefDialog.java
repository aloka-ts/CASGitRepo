package com.genband.m5.maps.ide.sitemap;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Spinner;
import java.util.ArrayList;
import java.util.Locale;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.builder.CPFNature;
import com.genband.m5.maps.ide.model.CPFPortletPreference;

public class AdvancedSiteMapPrefDialog extends TitleAreaDialog {

	public AdvancedSiteMapPrefDialog(Shell shell) {
		super(shell);

	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Sitemap Properties");
		newShell.setToolTipText("Select Theme and Layout for sitemap");
	}

	private void sendErrorMessage(String message) {
		this.setErrorMessage(message);

	}

	public void create() {
		super.create();
		setTitle("Create a new Sitemap");
		setMessage("Select Theme and Layout for Sitemap");
	}

	public Control createDialogArea(Composite com) {

		GridLayout lay = new GridLayout();
		lay.numColumns = 4;
		com.setToolTipText("Select Theme and layout for the Sitemap");
		com.setLayout(lay);

		Group groupM = new Group(com, GridData.FILL_HORIZONTAL);
		GridLayout layout1 = new GridLayout();
		layout1.numColumns = 4;
		groupM.setLayout(layout1);
		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 4;
		groupM.setLayoutData(gridD);
		groupM.setText("SiteMap Theme:");

		new Label(groupM, SWT.LEFT | SWT.WRAP).setText("Select Theme:");
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		final CCombo theme = new CCombo(groupM, SWT.BORDER);
		data.horizontalSpan = 2;
		theme.setLayoutData(data);
		theme.setItems(CPFConstants.THEMES);
		theme.select(2);
		theme.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				themeType = theme.getText();
				Image img=CPFPlugin.getDefault().getImageRegistry().get(themeType);
				if(img!=null){
				themeImg.setImage(img);}
			    else{
		        img = CPFPlugin.getDefault().getImageRegistry().get("sitemapPref.bmp");
		        themeImg.setImage(img);	
			   }

			}
		});
		
		theme.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				themeType = theme.getText();
				Image img=CPFPlugin.getDefault().getImageRegistry().get(themeType);
				if(img!=null){
				themeImg.setImage(img);}
			    else{
		        img = CPFPlugin.getDefault().getImageRegistry().get("sitemapPref.bmp");
		        themeImg.setImage(img);	
			   }

			}
		});

		themeType=theme.getText();
		Image img = CPFPlugin.getDefault().getImageRegistry()
				.get(themeType);
		themeImg = new Label(groupM, SWT.RIGHT | SWT.BORDER);
		themeImg.setImage(img);

		groupM = new Group(com, GridData.FILL_HORIZONTAL);
		layout1 = new GridLayout();
		layout1.numColumns = 4;
		groupM.setLayout(layout1);
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 4;
		groupM.setLayoutData(gridD);
		groupM.setText("SiteMap Layout:");

		new Label(groupM, SWT.LEFT | SWT.WRAP).setText("Select Layout:");
		data = new GridData(GridData.FILL_HORIZONTAL);
		final CCombo layout = new CCombo(groupM, SWT.BORDER | SWT.READ_ONLY);
		data.horizontalSpan = 2;
		layout.setLayoutData(data);
		layout.setItems(CPFConstants.LAYOUTS);
		layout.select(0);
		layoutType=layout.getText();
		layout.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				layoutType= layout.getText();
				Image img=CPFPlugin.getDefault().getImageRegistry().get(layoutType);
				if(img!=null){
				layoutImg.setImage(img);
				}
				else{
			    img = CPFPlugin.getDefault().getImageRegistry().get("sitemapPref.bmp");
			    layoutImg.setImage(img);	
				}
			}
		});

		//Image img = new Image(composite.getShell().getDisplay(), str);
		img = CPFPlugin.getDefault().getImageRegistry().get(layoutType);
		layoutImg = new Label(groupM, SWT.RIGHT | SWT.BORDER);
		layoutImg.setImage(img);

		Composite comp = (Composite) super.createDialogArea(com);
		return comp;
	}

	public void okPressed() {
		layoutImg.dispose();
		themeImg.dispose();
		this.close();
		super.okPressed();
	}

	public String getThemeType() {
		return themeType;
	}

	public String getLayoutType() {
		return layoutType;
	}

	Label layoutImg = null;
	Label themeImg = null;
	String themeType="";
	String layoutType="";

}
