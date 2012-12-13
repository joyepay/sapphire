/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.swt.gef.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.ui.diagram.shape.def.BackgroundDef;
import org.eclipse.sapphire.ui.diagram.shape.def.GradientBackgroundDef;
import org.eclipse.sapphire.ui.diagram.shape.def.GradientSegmentDef;
import org.eclipse.sapphire.ui.diagram.shape.def.SequenceLayoutDef;
import org.eclipse.sapphire.ui.diagram.shape.def.ShapeLayoutDef;
import org.eclipse.sapphire.ui.diagram.shape.def.SolidBackgroundDef;
import org.eclipse.sapphire.ui.diagram.shape.def.StackLayoutDef;
import org.eclipse.sapphire.ui.swt.gef.layout.SapphireSequenceLayout;
import org.eclipse.sapphire.ui.swt.gef.layout.SapphireStackLayout;
import org.eclipse.sapphire.ui.swt.gef.model.DiagramResourceCache;
import org.eclipse.sapphire.ui.swt.gef.presentation.RectanglePresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class RectangleFigure extends ContainerShapeFigure implements IShapeFigure
{	
	private static final org.eclipse.sapphire.ui.Color SELECTED_BACKGROUND = new org.eclipse.sapphire.ui.Color(0xAC, 0xD2, 0xF4);
    private static final org.eclipse.sapphire.ui.Color DEFAULT_BACKGROUND_START = new org.eclipse.sapphire.ui.Color(0xFF, 0xFF, 0xFF);
    private static final org.eclipse.sapphire.ui.Color DEFAULT_BACKGROUND_END = new org.eclipse.sapphire.ui.Color(0xD4, 0xE7, 0xF8);
    private static final org.eclipse.sapphire.ui.Color OUTLINE_FOREGROUND = new org.eclipse.sapphire.ui.Color(0xFF, 0xA5, 0x00);
	
	private RectanglePresentation rectPresentation;
	private ShapeLayoutDef layout;
	private DiagramResourceCache resourceCache;
    private boolean selected;
	private boolean hasFocus;
	
	public RectangleFigure(RectanglePresentation rectPresentation, DiagramResourceCache resourceCache)
	{
		super(rectPresentation, resourceCache);
		this.rectPresentation = rectPresentation;
		this.layout = rectPresentation.getLayout();
		this.resourceCache = resourceCache;		
		
		if (this.layout instanceof SequenceLayoutDef)
		{
			SapphireSequenceLayout sequenceLayout = new SapphireSequenceLayout((SequenceLayoutDef)layout);
			this.setLayoutManager(sequenceLayout);
		}
		else if (this.layout instanceof StackLayoutDef)
		{
			SapphireStackLayout sapphireStackLayout = new SapphireStackLayout();
			this.setLayoutManager(sapphireStackLayout);
		}
		setBorder(new RectangleBorder(this.rectPresentation, this.resourceCache));
	}
	
	@Override
	protected void fillShape(Graphics graphics) 
	{
		BackgroundDef bg = this.rectPresentation.getBackground();
		if (bg != null)
		{
			org.eclipse.draw2d.geometry.Rectangle fillRectangle = 
					new org.eclipse.draw2d.geometry.Rectangle(getBounds());
			if (this.rectPresentation.getCornerRadius() > 0)
			{
				fillRectangle = fillRectangle.shrink(1, 1);
				fillRectangle.x++;
				fillRectangle.y++;
				fillRectangle.width--;
				fillRectangle.height--;
			}
			final Color foregroundSave = graphics.getForegroundColor();
			final Color backgroundSave = graphics.getBackgroundColor();
			
			if (selected) 
			{
				graphics.setBackgroundColor(resourceCache.getColor(SELECTED_BACKGROUND));
				graphics.fillRectangle(fillRectangle);
			} 
			else 
			{
				if (bg instanceof SolidBackgroundDef)
				{
					org.eclipse.sapphire.ui.Color color = ((SolidBackgroundDef)bg).getColor().getContent();
					if (color != null)
					{
						graphics.setBackgroundColor(resourceCache.getColor(color));
					}
					else
					{
						graphics.setBackgroundColor(resourceCache.getColor(DEFAULT_BACKGROUND_END));
					}
					graphics.fillRectangle(fillRectangle);
				}
				else if (bg instanceof GradientBackgroundDef)
				{
					boolean isVertical = ((GradientBackgroundDef)bg).isVertical().getContent();
					ModelElementList<GradientSegmentDef> segments = ((GradientBackgroundDef)bg).getGradientSegments();
					if (segments.size() == 0)
					{
						graphics.setForegroundColor(resourceCache.getColor(DEFAULT_BACKGROUND_END));
						graphics.setBackgroundColor(resourceCache.getColor(DEFAULT_BACKGROUND_START));
					}
					else
					{
						GradientSegmentDef segment0 = segments.get(0);
						GradientSegmentDef segment1 = segments.get(1);
						graphics.setForegroundColor(resourceCache.getColor(segment0.getColor().getContent()));
						graphics.setBackgroundColor(resourceCache.getColor(segment1.getColor().getContent()));
					}
					
					graphics.fillGradient(fillRectangle.x, fillRectangle.y, fillRectangle.width, fillRectangle.height, isVertical);
				}
			}
			
			graphics.setForegroundColor(foregroundSave);
			graphics.setBackgroundColor(backgroundSave);
		}
	}
	
	@Override
	protected void outlineShape(Graphics graphics) 
	{
		org.eclipse.draw2d.geometry.Rectangle r = 
				org.eclipse.draw2d.geometry.Rectangle.SINGLETON.setBounds(getBounds());
		
		if (hasFocus || selected) 
		{
			// Save existing graphics attributes
			final int oldLineWidth = graphics.getLineWidth();
			final Color oldColor = graphics.getForegroundColor();
			final int oldLineStyle = graphics.getLineStyle();
			
			int cornerRadius = this.rectPresentation.getCornerRadius();
			final Dimension cornerDimension = new Dimension(cornerRadius, cornerRadius); 

			graphics.setForegroundColor(resourceCache.getColor(OUTLINE_FOREGROUND));
			graphics.setLineStyle(SWT.LINE_DASH);
			r.width--;
			r.height--;
			graphics.drawRoundRectangle(r,
					Math.max(0, cornerDimension.width),
					Math.max(0, cornerDimension.height));				

			// Restore previous graphics attributes
			graphics.setLineWidth(oldLineWidth);
			graphics.setForegroundColor(oldColor);
			graphics.setLineStyle(oldLineStyle);
		}
		
	}
	
	public void setSelected(boolean b) 
	{
		selected = b;
		repaint();
	}

	public void setFocus(boolean b) 
	{
		hasFocus = b;
		repaint();
	}
	
}
