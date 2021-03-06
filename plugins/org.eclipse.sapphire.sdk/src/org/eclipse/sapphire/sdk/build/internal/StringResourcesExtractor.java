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

package org.eclipse.sapphire.sdk.build.internal;

import static org.eclipse.sapphire.modeling.util.MiscUtil.createStringDigest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.el.Literal;
import org.eclipse.sapphire.modeling.el.PropertyAccessFunction;
import org.eclipse.sapphire.modeling.localization.Localizable;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.sdk.extensibility.SapphireExtensionDef;
import org.eclipse.sapphire.ui.def.ISapphireUiDef;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class StringResourcesExtractor
{
    public static boolean check( final File file )
    {
        final String fileName = file.getName();
        
        if( fileName.endsWith( ".sdef" ) )
        {
            return true;
        }
        else if( fileName.equals( "sapphire-extension.xml" ) && file.getParentFile().getName().equals( "META-INF" ) )
        {
            return true;
        }
        
        return false;
    }
    
    public static String extract( final File file )
    
        throws Exception
        
    {
        // Gather string resources from the input..
        
        final Set<String> strings = new HashSet<String>();
        final ElementType type;
        
        if( file.getName().endsWith( ".sdef" ) )
        {
            type = ISapphireUiDef.TYPE;
        }
        else
        {
            type = SapphireExtensionDef.TYPE;
            
        }
        
        final Element root = type.instantiate( new RootXmlResource( new XmlResourceStore( file ) ) );
        
        try
        {
            gather( root, strings );
        }
        finally
        {
            root.dispose();
        }
        
        if( strings.isEmpty() )
        {
            return null;
        }
        
        // Build a lookup table with synthesized resource keys.
        
        final Properties resources = new Properties();
        
        for( String string : strings )
        {
            resources.put( createStringDigest( string ), string );
        }
        
        // Serialize the resources file content and return it to caller.
        
        final ByteArrayOutputStream resourcesFileContentBytes = new ByteArrayOutputStream();
        resources.store( resourcesFileContentBytes, null );

        final String resourcesFileContent = new String( resourcesFileContentBytes.toByteArray() );

        return resourcesFileContent;
    }

    private static void gather( final Element element,
                                final Set<String> strings )
    {
        for( Property property : element.properties() )
        {
            if( property instanceof Value<?> )
            {
                final Value<?> value = (Value<?>) property;
                final ValueProperty p = value.definition();
                
                if( p.hasAnnotation( Localizable.class ) )
                {
                    if( p.getTypeClass() == Function.class )
                    {
                        final Function function = (Function) value.content( false );
                        
                        if( function != null )
                        {
                            gather( function, strings );
                        }
                    }
                    else
                    {
                        final String text = value.text( false );
                        
                        if( text != null )
                        {
                            strings.add( text );
                        }
                        
                        final DefaultValue defaultValueAnnotation = p.getAnnotation( DefaultValue.class );
                        
                        if( defaultValueAnnotation != null )
                        {
                            final String defaultValue = defaultValueAnnotation.text();
                            
                            if( defaultValue.length() > 0 )
                            {
                                strings.add( defaultValue );
                            }
                        }
                    }
                }
            }
            else if( property instanceof ElementHandle<?> )
            {
                final Element child = ( (ElementHandle<?>) property ).content();
                
                if( child != null )
                {
                    gather( child, strings );
                }
            }
            else if( property instanceof ElementList<?> )
            {
                for( Element child : (ElementList<?>) property )
                {
                    gather( child, strings );
                }
            }
        }
    }
    
    private static void gather( final Function function,
                                final Set<String> strings )
    {
        if( function instanceof Literal )
        {
            Object value = ( (Literal) function ).value();
            
            if( value instanceof String )
            {
                strings.add( (String) value );
            }
        }
        else if( function instanceof PropertyAccessFunction )
        {
            return;
        }
        else
        {
            for( Function operand : function.operands() )
            {
                gather( operand, strings );
            }
        }
    }
    
}
