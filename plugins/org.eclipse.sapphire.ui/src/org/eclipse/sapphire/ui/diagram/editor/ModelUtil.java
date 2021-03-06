/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.modeling.ModelPath;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class ModelUtil 
{
    public static PropertyDef resolve(final Element modelElement, 
            String propertyName)
    {
        if (propertyName != null)
        {
            final ElementType type = modelElement.type();
            final PropertyDef property = type.property( propertyName );
            if( property == null )
            {
                throw new RuntimeException( "Could not find property " + propertyName + " in " + type.getQualifiedName() );
            }
            return property;
        }    
        return null;
    }

    public static PropertyDef resolve(ElementType modelElementType, ModelPath path)
    {
        if (path.length() == 1)
        {
            String propertyName = ((ModelPath.PropertySegment)path.head()).getPropertyName();
            PropertyDef modelProperty = modelElementType.property(propertyName);
            return modelProperty;
        }
        else
        {
            ModelPath.Segment head = path.head();
            if (head instanceof ModelPath.PropertySegment)
            {
                final String propertyName = ((ModelPath.PropertySegment)head).getPropertyName();
                final PropertyDef property = modelElementType.property(propertyName);
                if (property instanceof ListProperty)
                {
                    ElementType type = ((ListProperty)property).getType();
                    return resolve(type, path.tail());
                }
                else
                {
                    throw new RuntimeException("Invalid model path <" + path + "> in ModelElementType " + modelElementType.getSimpleName());
                }
            }
            else 
            {
                throw new RuntimeException("Invalid model path <" + path + "> in ModelElementType " + modelElementType.getSimpleName());
            }
        }
    }

    public static PropertyDef resolve(Element modelElement, ModelPath path)
    {
        if (path.length() == 1)
        {
            String propertyName = ((ModelPath.PropertySegment)path.head()).getPropertyName();            
            return resolve(modelElement, propertyName);
        }
        else
        {
            ModelPath.Segment head = path.head();
            if (head instanceof ModelPath.PropertySegment)
            {
                final String propertyName = ((ModelPath.PropertySegment)head).getPropertyName();
                final Property property = modelElement.property(propertyName);
                if (property != null && property.definition() instanceof ListProperty)
                {
                    ElementType type = ((ListProperty)property.definition()).getType();
                    return resolve(type, path.tail());
                }
                else
                {
                    throw new RuntimeException("Invalid model path <" + path + "> in model element " + modelElement);
                }
            }
            else if (head instanceof ModelPath.ParentElementSegment)
            {
                final Property parent = modelElement.parent();
                return resolve(parent.element(), path.tail());
            }
            else 
            {
                throw new RuntimeException("Invalid model path <" + path + "> in model element " + modelElement);
            }
        }
    }
}
