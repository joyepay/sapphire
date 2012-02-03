/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 *    Shenxue Zhou - double click handling
 ******************************************************************************/

package org.eclipse.sapphire.ui.gef.diagram.editor.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.sapphire.ui.Bounds;
import org.eclipse.sapphire.ui.gef.diagram.editor.commands.DoubleClickNodeCommand;
import org.eclipse.sapphire.ui.gef.diagram.editor.figures.NodeFigure;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramConnectionModel;
import org.eclipse.sapphire.ui.gef.diagram.editor.model.DiagramNodeModel;
import org.eclipse.sapphire.ui.gef.diagram.editor.policies.DiagramNodeEditPolicy;
import org.eclipse.sapphire.ui.gef.diagram.editor.policies.NodeEditPolicy;
import org.eclipse.sapphire.ui.gef.diagram.editor.policies.NodeLabelDirectEditPolicy;
import org.eclipse.sapphire.ui.gef.diagram.editor.policies.NodeLayoutEditPolicy;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DiagramNodeEditPart extends AbstractGraphicalEditPart implements NodeEditPart, PropertyChangeListener {

    private NodeDirectEditManager manager;
    
    private ConnectionAnchor sourceAnchor;
    private ConnectionAnchor targetAnchor;
    
    private List<IFigure> decorators = new ArrayList<IFigure>();

    @Override
	protected IFigure createFigure() {
    	String imageId = getCastedModel().getModelPart().getImageId();
		return new NodeFigure(imageId != null, getCastedModel().getDiagramModel().getResourceCache());
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new DiagramNodeEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new NodeLabelDirectEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new NodeEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new NodeLayoutEditPolicy());
	}

	@Override
	public void activate() {
		if (!isActive()) {
			super.activate();
			getCastedModel().addPropertyChangeListener(this);
		}
	}

	@Override
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			getCastedModel().removePropertyChangeListener(this);
		}
	}

	private void performDirectEdit() {
		if (manager == null) {
			Label label = getNodeFigure().getLabelFigure();
			manager = new NodeDirectEditManager(this, new NodeCellEditorLocator(label), label);
		}
		manager.show();
	}

	@Override
	public void performRequest(Request request) 
	{
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT)
		{
			Point pt = null;
			if (request.getType() == RequestConstants.REQ_DIRECT_EDIT && !(request instanceof DirectEditRequest))
			{
				// Direct edit invoked using key command
				performDirectEdit();
			}
			else if (request.getType() == RequestConstants.REQ_DIRECT_EDIT)
			{
				DirectEditRequest deReq = (DirectEditRequest)request;
				pt = deReq.getLocation();
				if (mouseInLabelRegion(pt))
				{
					performDirectEdit();
				}
			}
		}
		else if (request.getType().equals(REQ_OPEN))
		{
			SelectionRequest selRequest = (SelectionRequest)request;
			Point pt = selRequest.getLocation();
			if (mouseInLabelRegion(pt))
			{
				performDirectEdit();
			}
			else
			{
				Command cmd = new DoubleClickNodeCommand(getCastedModel().getModelPart());
				// If executing the command from edit domain's command stack, we'd get an 
				// invalid cursor before the double click cmd is executed.
				// Bypassing the command stack
				//this.getViewer().getEditDomain().getCommandStack().execute(cmd);
				if (cmd.canExecute())
				{
					cmd.execute();
				}
			}
		}
		else
		{
			super.performRequest(request);
		}
	}

	private boolean mouseInLabelRegion(Point pt)
	{
		NodeFigure nodeFig = getNodeFigure();
		Rectangle bounds = nodeFig.getLabelFigure().getBounds();
		if (bounds.contains(pt))
		{
			return true;
		}
		return false;
	}
	
	@Override
	protected List<DiagramConnectionModel> getModelSourceConnections() {
		return getCastedModel().getSourceConnections();
	}

	@Override
	protected List<DiagramConnectionModel> getModelTargetConnections() {
		return getCastedModel().getTargetConnections();
	}

	public DiagramNodeModel getCastedModel() {
		return (DiagramNodeModel)getModel();
	}
	
	protected NodeFigure getNodeFigure() {
		return (NodeFigure)getFigure();
	}
	
	private void addDecorators(Bounds labelBounds, Bounds imageBounds) {
		NodeFigure nodeFigure = getNodeFigure();
		
		// first remove all decorators
		for (IFigure decorator : decorators) {
			nodeFigure.remove(decorator);
		}
		decorators.clear();

		NodeDecorator util = new NodeDecorator(getCastedModel(), labelBounds, imageBounds);
		decorators.addAll(util.decorate(getNodeFigure()));
	}

	@Override
	protected void refreshVisuals() {
		getNodeFigure().setText(getCastedModel().getLabel());
		getNodeFigure().setImage(getCastedModel().getImage());
		
		Bounds nb = getCastedModel().getNodeBounds();
		Bounds labelBounds = getCastedModel().getLabelBounds(nb);
		Bounds imageBounds = getCastedModel().getImageBounds(nb);
		
		getNodeFigure().refreshConstraints(labelBounds, imageBounds);
		
		addDecorators(labelBounds, imageBounds);
		
		Rectangle bounds = new Rectangle(nb.getX(), nb.getY(), nb.getWidth(), nb.getHeight());
		((GraphicalEditPart) getParent()).setLayoutConstraint(this,	getFigure(), bounds);
	}
	
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		if (sourceAnchor == null) {
			sourceAnchor = new ChopboxAnchor(getFigure());
		}
		return sourceAnchor;
	}

	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		if (targetAnchor == null) {
			targetAnchor = new ChopboxAnchor(getFigure());
		}
		return targetAnchor;
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		if (sourceAnchor == null) {
			sourceAnchor = new ChopboxAnchor(getFigure());
		}
		return sourceAnchor;
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		// when moving or creating connections, the line should always end
		// directly at the mouse-pointer.
		return null;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (DiagramNodeModel.SOURCE_CONNECTIONS.equals(prop)) {
			refreshSourceConnections();
		} else if (DiagramNodeModel.TARGET_CONNECTIONS.equals(prop)) {
			refreshTargetConnections();
		} else if (DiagramNodeModel.NODE_BOUNDS.equals(prop)) {
			refreshVisuals();
		} else if (DiagramNodeModel.NODE_UPDATES.equals(prop)) {
			refreshVisuals();
		} else if (DiagramNodeModel.NODE_START_EDITING.equals(prop)) {
			performDirectEdit();
		}
	}
	
}
