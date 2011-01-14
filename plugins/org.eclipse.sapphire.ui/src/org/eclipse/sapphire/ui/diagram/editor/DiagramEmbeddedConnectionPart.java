/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionEndpointDef;
import org.eclipse.sapphire.ui.diagram.def.IDiagramEmbeddedConnectionDef;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class DiagramEmbeddedConnectionPart extends DiagramConnectionPart 
{
	private IDiagramEmbeddedConnectionDef localDefinition;
	private IModelElement srcNodeModel;
	private IModelElement endpointModel;
	private FunctionResult endpointFunctionResult;
	private IDiagramConnectionEndpointDef endpointDef;
	
	public DiagramEmbeddedConnectionPart(DiagramEmbeddedConnectionTemplate connTemplate,
			IModelElement srcNodeModel)
	{
		this.connectionTemplate = connTemplate;
		this.srcNodeModel = srcNodeModel;
	}
	
    @Override
    protected void init()
    {        
        this.localDefinition = (IDiagramEmbeddedConnectionDef)super.definition;
        this.modelElement = super.getModelElement();
        this.labelFunctionResult = initExpression
        ( 
        	this.modelElement,
        	this.localDefinition.getLabel().element().getContent(), 
            new Runnable()
            {
                public void run()
                {
                }
            }
        );
        this.labelProperty = FunctionUtil.getFunctionProperty(this.modelElement, this.labelFunctionResult);
        
        this.idFunctionResult = initExpression
        ( 
        	this.modelElement,
            this.localDefinition.getInstanceId(), 
            new Runnable()
            {
                public void run()
                {
                }
            }
        );        

        this.endpointDef = this.localDefinition.getEndpoint().element();
        this.endpointModel = processEndpoint(this.endpointDef);
        if (this.endpointModel != null)
        {
	        this.endpointFunctionResult = initExpression
	        (
	        	this.endpointModel, 
	        	this.endpointDef.getValue(), 
	            new Runnable()
	        	{
		            public void run()
		            {
		            	refreshEndpoint2();
		            }
	        	}
	        );
        }        
        // Add model property listener
        this.modelPropertyListener = new ModelPropertyListener()
        {
            @Override
            public void handlePropertyChangedEvent( final ModelPropertyChangeEvent event )
            {
                handleModelPropertyChange( event );
            }
        };
        addModelListener();        
    }
    
    @Override
    public IModelElement getEndpoint1()
    {
    	return this.srcNodeModel;
    }
    
    @Override
    public IModelElement getEndpoint2()
    {
    	return this.endpointModel;
    }

    @Override
	public void refreshEndpoint1()
	{
	}
	
    @Override
	public void refreshEndpoint2()
	{
		if (this.endpointFunctionResult != null)
		{
			Object value = this.endpointFunctionResult.value();
			String property = this.endpointDef.getProperty().getContent();
			setModelProperty(this.modelElement, property, value);
		}		
	}
        
    public DiagramNodePart getSourceNodePart()
    {
    	SapphireDiagramEditorPart diagramPart = (SapphireDiagramEditorPart)getParentPart();
    	return diagramPart.getDiagramNodePart(this.srcNodeModel);
    }
    
	@Override
	public void render(SapphireRenderingContext context) 
	{
		// TODO Auto-generated method stub

	}

    @Override
    public void dispose()
    {
    	super.dispose();
    	if (this.endpointFunctionResult != null)
    	{
    		this.endpointFunctionResult.dispose();
    	}    	
    }
    
    @Override
    public void addModelListener()
    {
    	if (this.labelProperty != null)
    	{
	    	this.modelElement.addListener(this.modelPropertyListener, 
	    								this.labelProperty.getName());
    	}
    	this.modelElement.addListener(this.modelPropertyListener, 
    								this.endpointDef.getProperty().getContent());
    }
    
    @Override
    public void removeModelListener()
    {
    	if (this.labelProperty != null)
    	{
	    	this.modelElement.removeListener(this.modelPropertyListener, 
	    								this.labelProperty.getName());
    	}
    	this.modelElement.removeListener(this.modelPropertyListener, 
    								this.endpointDef.getProperty().getContent());
    }

    @Override
    protected void handleModelPropertyChange(final ModelPropertyChangeEvent event)
    {
    	final ModelProperty property = event.getProperty();
    	if (property.getName().equals(this.endpointDef.getProperty().getContent()))
    	{
	    	SapphireDiagramEditorPart diagramEditor = (SapphireDiagramEditorPart)getParentPart();
			final IFeatureProvider fp = diagramEditor.getDiagramEditor().getDiagramTypeProvider().getFeatureProvider();
			final Diagram diagram = diagramEditor.getDiagramEditor().getDiagramTypeProvider().getDiagram();
			final TransactionalEditingDomain ted = TransactionUtil.getEditingDomain(diagram);
			
			removeDiagramConnection(fp, ted);
			handleEndpointChange();
			addNewConnectionIfPossible(fp, ted, diagramEditor);
    	}    			
    }    
    
    private void handleEndpointChange()
    {
        this.endpointModel = processEndpoint(this.endpointDef);
        if (this.endpointFunctionResult != null)
        {
        	this.endpointFunctionResult.dispose();
        	this.endpointFunctionResult = null;
        }
        if (this.endpointModel != null)
        {        	
	        this.endpointFunctionResult = initExpression
	        (
	        	this.endpointModel, 
	        	this.endpointDef.getValue(), 
	            new Runnable()
	        	{
		            public void run()
		            {
		            	refreshEndpoint2();
		            }
	        	}
	        );
        }        
    }
    
}