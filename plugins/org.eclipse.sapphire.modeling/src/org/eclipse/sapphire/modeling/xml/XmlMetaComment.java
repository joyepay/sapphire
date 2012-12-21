/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.modeling.xml;

import org.w3c.dom.Node;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class XmlMetaComment

    extends XmlComment
    
{
    public XmlMetaComment( final Node domNode, final ModelStoreForXml modelStoreForXml )
    {
        super( domNode, modelStoreForXml );
    }
    
    public String getName()
    {
        final String text = super.getTextInternal();
        final int colon = text.indexOf( ':' );
        
        if( colon == -1 )
        {
            return text;
        }
        else
        {
            return text.substring( 0, colon );
        }
    }
    
    public void setName( final String name )
    {
        final String n = ( name == null ? EMPTY_STRING : name.trim() );
        super.setText( n + ":" + getText() );
    }

    @Override
    protected String getTextInternal()
    {
        final String text = super.getTextInternal();
        final int colon = text.indexOf( ':' );
        
        if( colon == -1 || colon + 1 == text.length() )
        {
            return EMPTY_STRING;
        }
        else
        {
            return text.substring( colon + 1 );
        }
    }
    
    @Override
    public void setText( final String text )
    {
        final String txt = ( text == null ? EMPTY_STRING : text.trim() );
        super.setText( getName() + ":" + txt );
    }
    
}