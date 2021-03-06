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

package org.eclipse.sapphire.sdk.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.java.JavaTypeReferenceService;
import org.eclipse.sapphire.java.jdt.JdtJavaType;
import org.eclipse.sapphire.modeling.LoggingService;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.services.Service;
import org.eclipse.sapphire.services.ServiceContext;
import org.eclipse.sapphire.services.ServiceFactory;
import org.eclipse.sapphire.ui.def.IPackageReference;
import org.eclipse.sapphire.ui.def.ISapphireUiDef;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SdkJavaTypeReferenceServiceForSdef extends JavaTypeReferenceService
{
    private final IJavaProject project;
    
    public SdkJavaTypeReferenceServiceForSdef( final IProject project )
    {
        this.project = JavaCore.create( project );
    }

    public SdkJavaTypeReferenceServiceForSdef( final IJavaProject project )
    {
        this.project = project;
    }

    @Override
    public JavaType resolve( final String name )
    {
        if( name.trim().length() == 0 || name.startsWith( "." ) || name.endsWith( "." ) )
        {
            return null;
        }
        
        final String n = name.replace( '$', '.' );
        
        try
        {
            IType type = this.project.findType( n );
            
            if( type == null && name.indexOf( '.' ) == -1 )
            {
                for( IPackageReference packageRef : context( ISapphireUiDef.class ).getImportedPackages() )
                {
                    final String packageName = packageRef.getName().text();
                    
                    if( packageName != null )
                    {
                        type = this.project.findType( packageName, n );
        
                        if( type != null && type.exists() && ! type.isAnonymous() )
                        {
                            break;
                        }
                    }
                }
            }
            
            return new JdtJavaType( type );
        }
        catch( JavaModelException e )
        {
            LoggingService.log( e );
        }
        
        return null;
    }
    
    public static final class Factory extends ServiceFactory
    {
        @Override
        public boolean applicable( final ServiceContext context,
                                   final Class<? extends Service> service )
        {
            final ISapphireUiDef def = context.find( ISapphireUiDef.class );
            final ValueProperty property = context.find( ValueProperty.class );
            
            if( def != null && property != null && property.getTypeClass() == JavaTypeName.class )
            {
                final Reference referenceAnnotation = property.getAnnotation( Reference.class );
    
                if( referenceAnnotation != null && referenceAnnotation.target() == JavaType.class )
                {
                    final IProject project = def.adapt( IProject.class );
                    
                    if( project != null )
                    {
                        try
                        {
                            for( String nature : project.getDescription().getNatureIds() )
                            {
                                if( nature.equals( JavaCore.NATURE_ID ) )
                                {
                                    return true;
                                }
                            }
                        }
                        catch( CoreException e )
                        {
                            LoggingService.log( e );
                        }
                    }
                }
            }
            
            return false;
        }

        @Override
        public Service create( final ServiceContext context,
                               final Class<? extends Service> service )
        {
            final IProject project = context.find( Element.class ).adapt( IProject.class );
            return new SdkJavaTypeReferenceServiceForSdef( project );
        }
    }
    
}
