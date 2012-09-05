/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui;

import java.util.List;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelPath;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.def.FormDef;
import org.eclipse.sapphire.ui.def.PartDef;
import org.eclipse.sapphire.util.ListFactory;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public class FormPart extends FormComponentPart
{
    private List<SapphirePart> childParts;
    
    @Override
    protected void init()
    {
        super.init();
        
        this.childParts = ListFactory.unmodifiable( initChildParts() );
        
        final Listener childPartListener = new Listener()
        {
            @Override
            public void handle( final Event event )
            {
                if( event instanceof ValidationChangedEvent )
                {
                    updateValidationState();
                }
            }
        };
        
        for( SapphirePart part : this.childParts )
        {
            part.attach( childPartListener );
        }
        
        updateValidationState();
    }
    
    protected List<SapphirePart> initChildParts()
    {
        final IModelElement element = getLocalModelElement();
        final FormDef def = (FormDef) this.definition;
        final ListFactory<SapphirePart> partsListFactory = ListFactory.start();
        
        for( PartDef childPartDef : def.getContent() )
        {
            partsListFactory.add( create( this, element, childPartDef, this.params ) );
        }
        
        return partsListFactory.result();
    }
    
    public List<? extends SapphirePart> getChildParts()
    {
        return this.childParts;
    }
    
    public void render( final SapphireRenderingContext context )
    {
        for( SapphirePart child : getChildParts() )
        {
            child.render( context );
        }
    }
    
    @Override
    protected Status computeValidationState()
    {
        final Status.CompositeStatusFactory factory = Status.factoryForComposite();

        for( SapphirePart child : getChildParts() )
        {
            factory.merge( child.getValidationState() );
        }
        
        return factory.create();
    }
    
    @Override
    public boolean setFocus()
    {
        for( SapphirePart child : getChildParts() )
        {
            if( child.setFocus() == true )
            {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public boolean setFocus( final ModelPath path )
    {
        for( SapphirePart child : getChildParts() )
        {
            if( child.setFocus( path ) == true )
            {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public void dispose()
    {
        super.dispose();
        
        for( SapphirePart child : getChildParts() )
        {
            child.dispose();
        }
    }

}