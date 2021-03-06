/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.policies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.handles.AbstractHandle;
import org.eclipse.sapphire.ui.swt.gef.figures.IShapeFigure;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;
import org.eclipse.sapphire.ui.swt.gef.parts.DiagramNodeEditPart;
import org.eclipse.sapphire.ui.swt.gef.utils.SapphireSurroundingHandle;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramNodeSelectionEditPolicy extends NonResizableEditPolicy {

	private IShapeFigure getNodeFigure() 
	{
		DiagramNodeEditPart part = (DiagramNodeEditPart) getHost();
		if (part.getFigure() instanceof IShapeFigure)
		{
			return ((IShapeFigure) part.getFigure());
		}
		return null;
	}

	@Override
	protected List<?> createSelectionHandles() 
	{
		List<AbstractHandle> list = new ArrayList<AbstractHandle>();
		GraphicalEditPart owner = (GraphicalEditPart) getHost();
		DiagramResourceCache resourceCache = ((DiagramNodeEditPart)owner).getCastedModel().getDiagramModel().getResourceCache();
		list.add(new SapphireSurroundingHandle(owner, ((DiagramNodeEditPart)owner).getConfigurationManager(),
				resourceCache, isDragAllowed()));
		return list;
	}
	
	@Override
	protected void hideFocus() 
	{
		IShapeFigure shapeFigure = getNodeFigure();
		if (shapeFigure != null)
		{
			shapeFigure.setFocus(false);
		}
	}

	@Override
	protected void showFocus() {
		IShapeFigure shapeFigure = getNodeFigure();
		if (shapeFigure != null)
		{
			shapeFigure.setFocus(true);
		}
	}

	@Override
	protected void hideSelection() 
	{
		IShapeFigure shapeFigure = getNodeFigure();
		if (shapeFigure != null)
		{
			shapeFigure.setSelected(false);
			shapeFigure.setFocus(false);
			removeSelectionHandles();
		}
	}

	@Override
	protected void showPrimarySelection() 
	{
		IShapeFigure shapeFigure = getNodeFigure();
		if (shapeFigure != null)
		{
			shapeFigure.setSelected(true);
			shapeFigure.setFocus(true);
			addSelectionHandles();
		}
	}

	@Override
	protected void showSelection() 
	{
		IShapeFigure shapeFigure = getNodeFigure();
		if (shapeFigure != null)
		{
			shapeFigure.setSelected(true);
			shapeFigure.setFocus(false);
			addSelectionHandles();
		}
	}

}
