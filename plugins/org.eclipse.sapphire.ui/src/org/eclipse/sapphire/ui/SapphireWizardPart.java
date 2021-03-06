/******************************************************************************
 * Copyright (c) 2013 Oracle
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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.ui.def.WizardDef;
import org.eclipse.sapphire.ui.def.WizardPageDef;
import org.eclipse.sapphire.util.ListFactory;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SapphireWizardPart extends SapphirePart
{
    private FunctionResult imageFunctionResult;
    private List<SapphireWizardPagePart> pages;
    
    @Override
    protected void init()
    {
        super.init();
        
        final Element element = getModelElement();
        final WizardDef def = definition();
        
        this.imageFunctionResult = initExpression
        (
            def.getImage().content(),
            ImageData.class,
            null,
            new Runnable()
            {
                public void run()
                {
                    broadcast( new ImageChangedEvent( SapphireWizardPart.this ) );
                }
            }
        );
        
        final ListFactory<SapphireWizardPagePart> pagesListFactory = ListFactory.start();
        
        for( WizardPageDef pageDef : def.getPages() )
        {
            pagesListFactory.add( (SapphireWizardPagePart) create( this, element, pageDef, this.params ) );
        }
        
        this.pages = pagesListFactory.result();
    }

    @Override
    public WizardDef definition()
    {
        return (WizardDef) super.definition();
    }
    
    public String getLabel()
    {
        return definition().getLabel().localized( CapitalizationType.TITLE_STYLE, false );
    }
    
    public String getDescription()
    {
        return definition().getDescription().localized( CapitalizationType.NO_CAPS, false );
    }
    
    public ImageData getImage()
    {
        return (ImageData) this.imageFunctionResult.value();
    }
    
    public List<SapphireWizardPagePart> getPages()
    {
        return this.pages;
    }

    @Override
    public void render( final SapphireRenderingContext context )
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void dispose()
    {
        if( this.imageFunctionResult != null )
        {
            this.imageFunctionResult.dispose();
        }
        
        for( SapphireWizardPagePart page : this.pages )
        {
            page.dispose();
        }
        
        super.dispose();
    }
    
}
