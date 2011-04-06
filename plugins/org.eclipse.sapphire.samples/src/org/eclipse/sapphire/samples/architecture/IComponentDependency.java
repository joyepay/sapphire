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

package org.eclipse.sapphire.samples.architecture;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.NonNullValue;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.samples.architecture.internal.ComponentReferenceService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@GenerateImpl

public interface IComponentDependency 

    extends IModelElement
    
{
	ModelElementType TYPE = new ModelElementType( IComponentDependency.class );
	
    // *** Name ***
    
	@Reference( target = IComponent.class, service = ComponentReferenceService.class )
	@NonNullValue
	@PossibleValues( property = "/Components/Name" )
	@XmlBinding( path = "name" )

	ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );

    ReferenceValue<String,IComponent> getName();
    void setName( String value );
    
}
