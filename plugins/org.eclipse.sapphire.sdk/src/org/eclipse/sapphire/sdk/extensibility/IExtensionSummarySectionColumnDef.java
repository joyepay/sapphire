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

package org.eclipse.sapphire.sdk.extensibility;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NonNullValue;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@GenerateImpl

public interface IExtensionSummarySectionColumnDef

    extends IModelElement
    
{
    ModelElementType TYPE = new ModelElementType( IExtensionSummarySectionColumnDef.class );
    
    // *** Name ***
    
    @Label( standard = "name" )
    @NonNullValue
    
    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );
    
    Value<String> getName();
    void setName( String value );
    
}