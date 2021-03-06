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

package org.eclipse.sapphire.services;

import java.util.List;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.util.NLS;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public abstract class PathValidationService extends ValidationService
{
    protected boolean resourceMustExist;
    protected FileSystemResourceType validResourceType;
    private FileExtensionsService fileExtensionsService;
    
    @Override
    protected void init()
    {
        super.init();
        
        final Property property = context( Property.class );
        
        this.resourceMustExist = property.definition().hasAnnotation( MustExist.class );
        
        final ValidFileSystemResourceType validResourceTypeAnnotation = property.definition().getAnnotation( ValidFileSystemResourceType.class );
        this.validResourceType = ( validResourceTypeAnnotation != null ? validResourceTypeAnnotation.value() : null );
        
        this.fileExtensionsService = property.service( FileExtensionsService.class );
        
        if( this.fileExtensionsService != null )
        {
            this.fileExtensionsService.attach
            (
                new Listener()
                {
                    @Override
                    public void handle( final Event event )
                    {
                        property.refresh();
                    }
                }
            );
        }
    }
    
    protected final Status validateExtensions( final Path path )
    {
        if( this.fileExtensionsService != null )
        {
            final String fileName = path.lastSegment();
            
            if( fileName != null )
            {
                final List<String> extensions = this.fileExtensionsService.extensions();
                final int count = ( extensions == null ? 0 : extensions.size() );
                
                if( count > 0 )
                {
                    final String trimmedFileName = fileName.trim();
                    final int lastdot = trimmedFileName.lastIndexOf( '.' );
                    final String extension;
                    
                    if( lastdot == -1 )
                    {
                        extension = "";
                    }
                    else
                    {
                        extension = trimmedFileName.substring( lastdot + 1 );
                    }
                    
                    boolean match = false;
                    
                    if( extension != null && extension.length() != 0 )
                    {
                        for( String ext : extensions )
                        {
                            if( extension.equalsIgnoreCase( ext ) )
                            {
                                match = true;
                                break;
                            }
                        }
                    }
                    
                    if( ! match )
                    {
                        final String message;
                        
                        if( count == 1 )
                        {
                            message = NLS.bind( Resources.invalidFileExtensionOne, trimmedFileName, extensions.get( 0 ) );
                        }
                        else if( count == 2 )
                        {
                            message = NLS.bind( Resources.invalidFileExtensionTwo, trimmedFileName, extensions.get( 0 ), extensions.get( 1 ) );
                        }
                        else
                        {
                            final StringBuilder buf = new StringBuilder();
                            
                            for( String ext : extensions )
                            {
                                if( buf.length() != 0 )
                                {
                                    buf.append( ", " );
                                }
                                
                                buf.append( ext );
                            }
                            
                            message = NLS.bind( Resources.invalidFileExtensionMultiple, trimmedFileName, buf.toString() ); 
                        }
                        
                        return Status.createErrorStatus( message );
                    }
                }
            }
        }
        
        return Status.createOkStatus();
    }
    
    protected static final class Resources extends NLS
    {
        public static String folderMustExist;
        public static String fileMustExist;
        public static String resourceMustExist;
        public static String pathIsNotFile;
        public static String pathIsNotFolder;
        public static String invalidFileExtensionOne;
        public static String invalidFileExtensionTwo;
        public static String invalidFileExtensionMultiple;
        
        static
        {
            initializeMessages( PathValidationService.class.getName(), Resources.class );
        }
    }
    
}
