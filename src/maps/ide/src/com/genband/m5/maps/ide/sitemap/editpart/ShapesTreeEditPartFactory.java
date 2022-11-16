package com.genband.m5.maps.ide.sitemap.editpart;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import com.genband.m5.maps.ide.sitemap.model.HeaderFooter;
import com.genband.m5.maps.ide.sitemap.model.Page;
import com.genband.m5.maps.ide.sitemap.model.PlaceHolder;
import com.genband.m5.maps.ide.sitemap.model.Shape;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;


/**
 * Factory that maps model elements to TreeEditParts.
 * TreeEditParts are used in the outline view of the SiteMapEditor.
 * @author Genband
 */
public class ShapesTreeEditPartFactory implements EditPartFactory {

/* (non-Javadoc)
 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
 */
public EditPart createEditPart(EditPart context, Object model) {
	if (model instanceof SiteMap) {
		return new DiagramTreeEditPart((SiteMap) model);
	}
	if (model instanceof HeaderFooter) {
		return new HeaderFooterTreeEditPart((HeaderFooter) model);
	}
	if (model instanceof Page) {
		return new PageTreeEditPart((Page) model);
	}if (model instanceof PlaceHolder) {
		return new PlaceHolderTreeEditPart((PlaceHolder) model);
	}
	if (model instanceof Shape) {
		return new ShapeTreeEditPart((Shape) model);
	}
	return null; // will not show an entry for the corresponding model instance
}

}
