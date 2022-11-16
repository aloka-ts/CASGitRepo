package com.genband.m5.maps.ide.sitemap;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class SiteMapPerspective implements IPerspectiveFactory{
	public void createInitialLayout(IPageLayout layout) {
    defineActions(layout);
    defineLayout(layout);
	}
	
	public void defineActions(IPageLayout layout) {
    // Add "new wizards".
    layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
    layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");

    // Add "show views".
    layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
    layout.addShowViewShortcut(IPageLayout.ID_BOOKMARKS);
    layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
    layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
    layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
	}
	
	public void defineLayout(IPageLayout layout) {
    // Editors are placed for free.
    String editorArea = layout.getEditorArea();

    // Place navigator and outline to left of
    // editor area.
    IFolderLayout left =
            layout.createFolder("left", IPageLayout.LEFT, (float) 0.26, editorArea);
    left.addView(IPageLayout.ID_RES_NAV);
    left.addView(IPageLayout.ID_OUTLINE);

    IFolderLayout right =
      layout.createFolder("right", IPageLayout.RIGHT, (float) 0.16, editorArea);
    right.addView(IPageLayout.ID_PROP_SHEET);

	
	}
	
}
