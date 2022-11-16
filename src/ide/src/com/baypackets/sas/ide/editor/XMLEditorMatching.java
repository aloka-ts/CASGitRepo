package com.baypackets.sas.ide.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorMatchingStrategy;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.ResourceUtil;

public class XMLEditorMatching implements IEditorMatchingStrategy {

	public boolean matches(IEditorReference editorRef, IEditorInput input) {
	
    	if (!(input instanceof IFileEditorInput))
    		return false;
        IFile inputFile = ResourceUtil.getFile(input);
        if (inputFile != null) {
            String path = inputFile.getProjectRelativePath().toString();
            if (path.equals("sip.xml") || path.equals("sas.xml")||path.equals("web.xml")) {
                try {
                    IFile editorFile = ResourceUtil.getFile(editorRef.getEditorInput());
                    return editorFile != null && inputFile.getProject().equals(editorFile.getProject());
                } catch (PartInitException e) {
                    return false;
                }
            }
        }
        return false;
	}

}
