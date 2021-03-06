/******************************************************************************
 * Copyright (c) 2013 SAP and Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP - initial implementation
 *    Shenxue Zhou - adaptation for Sapphire and ongoing maintenance
 *    Gregory Amerson - [376200] Support floating palette around diagram node
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.contextbuttons;

import static org.eclipse.sapphire.modeling.util.MiscUtil.normalizeToEmptyString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Tool;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.tools.AbstractConnectionCreationTool;
import org.eclipse.gef.tools.CreationTool;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionGroup;
import org.eclipse.sapphire.ui.SapphireActionSystem;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.swt.gef.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.swt.gef.parts.DiagramNodeEditPart;

/**
 * The context button manager shows and hides the context button pad. Mostly
 * showing/hiding the context button pad is triggered by mouse events.
 * 
 * @author SAP
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 * @author <a href="mailto:gregory.amerson@liferay.com">Gregory Amerson</a>
 */

public class ContextButtonManager {
	
	private static final String DIAGRAM_NODE_DEFAULT_ACTION = "Sapphire.Diagram.Node.Default";
	/**
	 * The context button pad is not shown, when the zoom level is below this
	 * minimum value.
	 */
	protected static final double MINIMUM_ZOOM_LEVEL = 0.75d;

	/**
	 * The editor on which this context button manager works, see
	 * {@link #getEditor()}. It is set in the constructor.
	 */
	private SapphireDiagramEditor editor;

	/**
	 * A backward-map from the edit-part figures to their edit-parts as
	 * described in {@link #getFigure2EditPart()}.
	 */
	private Map<IFigure, EditPart> figure2EditPart = new HashMap<IFigure, EditPart>();

	/**
	 * The currently active figure as described in {@link #getActiveFigure()}.
	 */
	private IFigure activeFigure;

	/**
	 * The currently active figure as described in
	 * {@link #getActiveContextButtonPad()}.
	 */
	private ContextButtonPad activeContextButtonPad;

	// ============================= listener =================================

	/**
	 * The zoom-listener is registered on the editor and calls
	 * {@link #handleZoomChanged()} on zoom level changes.
	 */
	private ZoomListener zoomListener = new ZoomListener() {
		public void zoomChanged(double newZoom) {
			handleZoomChanged();
		}
	};

	/**
	 * The mouse motion listener is registered on the relevant figures. It calls
	 * {@link #showContextButtonsInstantly()} when the mouse enters the figure.
	 */
	private MouseMotionListener mouseMotionListener = new MouseMotionListener.Stub() {
		@Override
		public void mouseEntered(MouseEvent me) {
			reactOnMouse(me);
		}

		@Override
		public void mouseMoved(MouseEvent me) {
			reactOnMouse(me);
		}

		private void reactOnMouse(MouseEvent me) {
			SapphireDiagramEditor ed = getEditor();
			
			// TODO Evaluate whether we should allow context pad to show when the 
			// editor is in direct edit mode.
			// We should not show the context buttons if the editor is in direct editor mode.
			// Context button pad interferes with the keyboard events.
			if (ed.isDirectEditingActive()) {
				return;
			}
			
			// Bug 380728 - Floating toolbar appears on a node when multiple nodes are selected 
			if (ed.getSelectedParts().size() > 1) {
				return;
			}
				
			Tool activeTool = ed.getEditDomain().getActiveTool();
			if (activeTool instanceof CreationTool || activeTool instanceof AbstractConnectionCreationTool) {
				return;
			}

			Object source = me.getSource();
			showContextButtonsInstantly((IFigure) source, me.getLocation());
		}

	};

	// ============================ constructor ===============================

	/**
	 * Creates a new ContextButtonManagerForPad.
	 * 
	 * @param editor
	 *            The editor on which this context button manager works, see
	 *            {@link #getEditor()}.
	 */
	public ContextButtonManager(SapphireDiagramEditor editor) {
		this.editor = editor;

		ZoomManager zoomMgr = (ZoomManager) getEditor().getGraphicalViewer().getProperty(ZoomManager.class.toString());
		if (zoomMgr != null) {
			zoomMgr.addZoomListener(zoomListener);
		}
	}

	// ====================== getter/setter for fields ========================

	/**
	 * Returns the editor this context button manager works on. It is set in the
	 * constructor and can not be changed.
	 * 
	 * @return The editor this context button manager works on.
	 */
	public SapphireDiagramEditor getEditor() {
		return editor;
	}

	/**
	 * Returns a backward-map from the edit-part figures to their edit-parts. So
	 * it delivers the opposite of GraphicalEditPart.getFigure(). This map is
	 * maintained in {@link #register(GraphicalEditPart)} and
	 * {@link #deRegister(GraphicalEditPart)}.
	 * 
	 * @return A backward-map from the edit-part figures to their edit-parts.
	 */
	private Map<IFigure, EditPart> getFigure2EditPart() {
		return figure2EditPart;
	}

	/**
	 * Sets the active figure and context button pad. A figure is called active,
	 * when a context button pad is currently active (shown) for this figure.
	 * There can only be one active figure and context button pad at a time.
	 * Figure and context button pad are either both null or both not null.
	 * 
	 * @param activeFigure
	 *            The figure to set active.
	 * @param activeContextButtonPad
	 *            The context button pad to set active.
	 */
	private void setActive(IFigure activeFigure, ContextButtonPad activeContextButtonPad) {
		this.activeFigure = activeFigure;
		this.activeContextButtonPad = activeContextButtonPad;
	}

	/**
	 * Returns the active figure as described in
	 * {@link #setActive(IFigure, ContextButtonPad)}.
	 * 
	 * @return The active figure as described in
	 *         {@link #setActive(IFigure, ContextButtonPad)}.
	 */
	private IFigure getActiveFigure() {
		return activeFigure;
	}

	/**
	 * Returns the active context button pad as described in
	 * {@link #setActive(IFigure, ContextButtonPad)}.
	 * 
	 * @return The active context button pad as described in
	 *         {@link #setActive(IFigure, ContextButtonPad)}.
	 */
	private ContextButtonPad getActiveContextButtonPad() {
		return activeContextButtonPad;
	}

	// =================== interface IContextButtonManager ====================

	/**
	 * Registers a given edit-part. This means, that a context button pad will
	 * be shown for this edit-part when the mouse enters its figure. Typically
	 * this method is called, when an edit-part is activated.
	 */
	public void register(GraphicalEditPart graphicalEditPart) {
		getFigure2EditPart().put(graphicalEditPart.getFigure(), graphicalEditPart);

		graphicalEditPart.getFigure().addMouseMotionListener(mouseMotionListener);
	}

	/**
	 * Deregisters a given edit-part, which is opposite to
	 * {@link #register(GraphicalEditPart)}. If a context-button pad is
	 * currently shown for this edit-part / figure, it is hidden first.
	 * Typically this method is called, when an edit-part is deactivated.
	 */
	public void deRegister(GraphicalEditPart graphicalEditPart) {
		if (graphicalEditPart.getFigure().equals(getActiveFigure())) {
			hideContextButtonsInstantly();
		}

		getFigure2EditPart().remove(graphicalEditPart.getFigure());

		graphicalEditPart.getFigure().removeMouseMotionListener(mouseMotionListener);
	}

	/**
	 * Hides the context button pad (if there is currently a context button pad
	 * active).
	 */
	public void hideContextButtonsInstantly() {
		if (getActiveContextButtonPad() != null) {
			synchronized (this) {
				ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) getEditor().getGraphicalViewer()
						.getRootEditPart();
				IFigure feedbackLayer = rootEditPart.getLayer(LayerConstants.HANDLE_LAYER);
				feedbackLayer.remove(getActiveContextButtonPad());
				setActive(null, null);
			}
		}
	}

	/**
	 * Returns true, if for the given figure a replacement of the context button
	 * pad is required. For example it returns false, if there is already a
	 * context button pad shown for this figure or if the mouse is still on the
	 * context button pad, and it returns true, if there is currently no context
	 * button pad.
	 * 
	 * @param figure
	 *            The figure which to check.
	 * @return true, if for the given figure a replacement of the context button
	 *         pad is required.
	 */
	private boolean replaceContextButtonPad(IFigure figure) {
		// requires new context buttons, if there is no active figure
		if (getActiveFigure() == null) {
			return true;
		}

		// requires no changed context buttons, if the given figure equals
		// the active figure
		if (figure.equals(getActiveFigure()))
			return false;

		// requires changed context buttons, if the given figure is a child of
		// the active figure (otherwise children would not have context buttons
		// when the mouse moves from parent to child -- see next check)
		IFigure parent = figure.getParent();
		while (parent != null) {
			if (parent.equals(getActiveFigure()))
				return true;
			parent = parent.getParent();
		}

		// requires no (new) context buttons, if the the mouse is still in the
		// sensitive area of the active context button pad
		if (getActiveContextButtonPad() != null) {
			if (getActiveContextButtonPad().isMouseInOverlappingArea()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Shows the context button pad for the given figure, but only if
	 * {@link #replaceContextButtonPad(IFigure)} return true and the zoom-level
	 * is at least {@link #MINIMUM_ZOOM_LEVEL}.
	 * 
	 * @param figure
	 *            The figure for which to show the context button pad.
	 * @param mouse
	 *            The current location of the mouse.
	 */
	private void showContextButtonsInstantly(IFigure figure, Point mouse) {
		if (!replaceContextButtonPad(figure))
			return;

		synchronized (this) {
			hideContextButtonsInstantly();

			// determine zoom level
			ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) getEditor().getGraphicalViewer().getRootEditPart();
			double zoom = rootEditPart.getZoomManager().getZoom();
			if (zoom < MINIMUM_ZOOM_LEVEL) {
				return;
			}

			EditPart activeEditPart = getFigure2EditPart().get(figure);
			if (!(activeEditPart instanceof DiagramNodeEditPart)) {
				return;
			}
			
			DiagramNodeEditPart nodeEditPart = (DiagramNodeEditPart)activeEditPart;
			ContextButtonPadData contextButtonPadData = getContextButtonPad(nodeEditPart);
			
			
			if (contextButtonPadData.getRightContextButtons().size() == 0
					&& contextButtonPadData.getTopContextButtons().size() == 0) {					
				return; // no context buttons to show
			}

			if (!contextButtonPadData.getPadLocation().contains(mouse.x, mouse.y)) {
				return; // mouse outside area of context button pad
			}

			IContextButtonPadDeclaration declaration = new StandardContextButtonPadDeclaration(contextButtonPadData);

			// create context button pad and add to handle layer
			ContextButtonPad contextButtonPad = new ContextButtonPad(declaration, zoom, getEditor(), activeEditPart);
			setActive(figure, contextButtonPad);

			IFigure feedbackLayer = rootEditPart.getLayer(LayerConstants.HANDLE_LAYER);
			feedbackLayer.add(contextButtonPad);
		}
	}

	/**
	 * Is called when the zoom-level changes and hides the context buttons.
	 */
	private void handleZoomChanged() {
		hideContextButtonsInstantly();

		// It would be possible to show a new context button pad, depending
		// on the new mouse location. But to avoid problems we skip this.
		// The scenario, that the zoom changes when context buttons are
		// visible is not so typical anyway.
	}
	
	/**
	 * Split the node part actions into two sets: one set to be displayed along the 
	 * top edge, another set to be displayed along the right and bottom edge. Honor 
	 * actions groups when splitting actions.
	 * 
	 * @param nodeEditPart node edit part
	 * @return ContextButtonPadData in which the actions are splitted into two sets
	 */
	private ContextButtonPadData getContextButtonPad(DiagramNodeEditPart nodeEditPart) 
	{
		ContextButtonPadData contextButtonPadData = new ContextButtonPadData();
		org.eclipse.draw2d.geometry.Rectangle bounds = nodeEditPart.getFigure().getBounds();
		Point loc = bounds.getLocation();
		Point botRight = bounds.getBottomRight();
		contextButtonPadData.getPadLocation().set(loc.x, loc.y,
				botRight.x - loc.x, botRight.y - loc.y);
		
		DiagramNodePart nodePart = nodeEditPart.getCastedModel().getModelPart();
		SapphireActionGroup actionGroup = nodePart.getActions(SapphireActionSystem.CONTEXT_DIAGRAM_NODE);
		List<SapphireAction> originalActions = actionGroup.getActions();		
		
		// Filter out the "default" action and actions without active handlers
		List<SapphireAction> actions = new ArrayList<SapphireAction>(originalActions.size());
		for (SapphireAction action : originalActions)
		{
			if (!(action.getId().equals(DIAGRAM_NODE_DEFAULT_ACTION)) && action.getActiveHandlers().size() > 0)
			{
				actions.add(action);
			}
		}
		
		// Split actions into two sets according to their groups.
		
		int numOfActions = actions.size();
		int half = numOfActions / 2;

		final Map<String,List<SapphireAction>> buckets = new LinkedHashMap<String,List<SapphireAction>>();		
		for( SapphireAction action : actions )
        {
            final String group = normalizeToEmptyString( action.getGroup() );
            
            List<SapphireAction> bucket = buckets.get( group );
            
            if( bucket == null )
            {
                bucket = new ArrayList<SapphireAction>();
                buckets.put( group, bucket );
            }            
            bucket.add( action );
        }
		
		int numTopActions = 0;
		if (buckets.size() < 2)
		{
			numTopActions = half;
		}
		else 
		{	
			int i = 0;
			for( List<SapphireAction> bucket : buckets.values() )
			{
				numTopActions += bucket.size();
				if (buckets.size() == 2)
				{
					break;
				}
				if (numTopActions >= half || i == buckets.size() - 2)
				{
					break;
				}
				i++;
			}
			
		}
		// Add top actions in reverse order
		for (int i = numTopActions - 1; i >= 0; i--)
		{
			SapphireAction action = actions.get(i);

            contextButtonPadData.getTopContextButtons().add(action);
		}
		for (int i = numTopActions; i < numOfActions; i++)
		{
			SapphireAction action = actions.get(i);

		    contextButtonPadData.getRightContextButtons().add(action);
		}
		return contextButtonPadData;
	}

}
