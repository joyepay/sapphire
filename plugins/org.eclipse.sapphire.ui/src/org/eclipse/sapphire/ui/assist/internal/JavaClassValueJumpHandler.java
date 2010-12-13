/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.assist.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.java.JavaTypeName;
import org.eclipse.sapphire.ui.SapphirePart;
import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.assist.JumpHandler;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class JavaClassValueJumpHandler 

    extends JumpHandler
    
{
    @Override
    public boolean isApplicable( final ValueProperty property )
    {
        return property.isOfType( JavaTypeName.class );
    }
    
    @Override
    public boolean canLocateJumpTarget( final SapphirePart part,
                                        final SapphireRenderingContext context,
                                        final IModelElement modelElement,
                                        final ValueProperty property )
    {
        final String typeName 
            = ( (Value<?>) property.invokeGetterMethod( modelElement ) ).getText( true );
        
        if( typeName != null && getType( typeName, getProject( modelElement ) ) != null )
        {
            return true;
        }
        
        return false;
    }

    @Override
    public void jump( final SapphirePart part,
                      final SapphireRenderingContext context,
                      final IModelElement modelElement,
                      final ValueProperty property )
    {
        try
        {
            final String typeName 
                = ( (Value<?>) property.invokeGetterMethod( modelElement ) ).getText( true );
            
            if( typeName != null )
            {
                final IType type = getType( typeName, getProject( modelElement ) );
                
                if( type != null )
                {
                    JavaUI.openInEditor( type );
                }
                else 
                {
                    final String message = NLS.bind( Resources.couldNotFindTypeDialogMessage, typeName );
                    MessageDialog.openInformation( context.getShell(), Resources.couldNotFindTypeDialogTitle, message );
                }
            }
        } 
        catch( CoreException e ) 
        {
            SapphireUiFrameworkPlugin.log( e );
        }
    }
    
    private IProject getProject( final IModelElement modelElement ) 
    {
        IProject project = (IProject) Platform.getAdapterManager().loadAdapter( modelElement.getModel(), IProject.class.getName() );
        
        if( project == null )
        {
            project = modelElement.getModel().getEclipseProject();
        }

        return project;
    }
    
    private static IType getType( String fullyQualifiedType, IProject project )
    {
        assert fullyQualifiedType != null : "Fully qualified type should not be null."; //$NON-NLS-1$
        
        // the JDT returns a non-null anonymous class IType 
        // for empty string and package names that end with a dot
        // if the type starts with a dot, the JDT helpfully removes it 
        // and returns the type referenced without the dot
        // short circuit here for perf and so validation results make sense
        // e.g. if the valid type is "Thing", then ".Thing" and "Thing." should not be valid
        if ( fullyQualifiedType.trim().length() == 0 
                || fullyQualifiedType.startsWith(".") //$NON-NLS-1$
                        || fullyQualifiedType.endsWith(".")) //$NON-NLS-1$
            return null;
        
        IJavaProject javaProject = JavaCore.create( project );
        
        if (( javaProject == null ) || ( !javaProject.exists() ))
        {
            return null;
        }

        IType type = null;
        try
        {
            type = javaProject.findType( fullyQualifiedType );
            if ( type != null && ( !type.exists() || type.isAnonymous() ) )
            {
                type = null;
            }
        }
        catch ( JavaModelException ce )
        {
            SapphireUiFrameworkPlugin.log( ce );
        }
        return type;
    }

    private static final class Resources extends NLS 
    {
        public static String couldNotFindTypeDialogTitle;
        public static String couldNotFindTypeDialogMessage;

        static 
        {
            initializeMessages( JavaClassValueJumpHandler.class.getName(), Resources.class );
        }
    }
    
}