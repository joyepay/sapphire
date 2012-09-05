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

package org.eclipse.sapphire.services;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.util.NLS;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class PossibleValuesService extends Service
{
    private final String invalidValueMessageTemplate;
    private final Status.Severity invalidValueSeverity;
    private final boolean caseSensitive;
    
    public PossibleValuesService( final String invalidValueMessageTemplate,
                                  final Status.Severity invalidValueSeverity,
                                  final boolean caseSensitive )
    {
        if( invalidValueMessageTemplate == null || invalidValueMessageTemplate.length() == 0 )
        {
            this.invalidValueMessageTemplate = Resources.defaultInvalidValueMessage;
        }
        else
        {
            this.invalidValueMessageTemplate = invalidValueMessageTemplate;
        }

        this.invalidValueSeverity = ( invalidValueSeverity == null ? Status.Severity.ERROR : invalidValueSeverity );
        this.caseSensitive = caseSensitive;
    }
    
    public PossibleValuesService()
    {
        this( null, null, true );
    }
    
    public final SortedSet<String> values()
    {
        final TreeSet<String> values = new TreeSet<String>();
        fillPossibleValues( values );
        return Collections.unmodifiableSortedSet( values );
    }
    
    protected abstract void fillPossibleValues( final SortedSet<String> values );
    
    public String getInvalidValueMessage( final String invalidValue )
    {
        return NLS.bind( this.invalidValueMessageTemplate, invalidValue, context( ModelProperty.class ).getLabel( true, CapitalizationType.NO_CAPS, false ) );
    }
    
    public Status.Severity getInvalidValueSeverity( final String invalidValue )
    {
        return this.invalidValueSeverity;
    }
    
    public boolean isCaseSensitive()
    {
        return this.caseSensitive;
    }
    
    private static final class Resources extends NLS
    {
        public static String defaultInvalidValueMessage;
        
        static
        {
            initializeMessages( PossibleValuesService.class.getName(), Resources.class );
        }
    }
    
}