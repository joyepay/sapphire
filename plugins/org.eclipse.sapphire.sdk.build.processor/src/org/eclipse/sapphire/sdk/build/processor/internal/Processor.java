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

package org.eclipse.sapphire.sdk.build.processor.internal;

import java.util.Collection;
import java.util.Set;

import org.eclipse.sapphire.modeling.annotations.GenerateImpl;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.declaration.AnnotationMirror;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.Declaration;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class Processor 

    implements AnnotationProcessor
    
{
    private final AnnotationProcessorEnvironment _env;
    private final GenerateImplProcessor generateImplProcessor;
    private final ExternalizeStringResourcesProcessor generateLabelResourcesProcessor;

    public Processor( final Set<AnnotationTypeDeclaration> atds,
                      final AnnotationProcessorEnvironment env ) 
    {
        this._env = env;
        this.generateImplProcessor = new GenerateImplProcessor();
        this.generateLabelResourcesProcessor = new ExternalizeStringResourcesProcessor();
    }

    public void process() 
    {
        process( GenerateImpl.class.getName(), this.generateImplProcessor );
        
        try
        {
            this.generateLabelResourcesProcessor.process( this._env );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }
    
    private void process( final String annotationTypeName,
                          final SapphireAnnotationsProcessor processor ) 
    {
        try
        {
            final AnnotationTypeDeclaration annotationDeclaration 
                = (AnnotationTypeDeclaration) this._env.getTypeDeclaration( annotationTypeName );
            
            if( annotationDeclaration == null ) 
            {
                return;                         
            }
            
            final Collection<Declaration> annotatedDeclarations 
                = this._env.getDeclarationsAnnotatedWith( annotationDeclaration );
            
            if( annotatedDeclarations == null ) 
            {
                return;
            }
            
            for( Declaration decl : annotatedDeclarations ) 
            {
                final Collection<AnnotationMirror> annotations = decl.getAnnotationMirrors();
                
                for( AnnotationMirror annotation : annotations ) 
                {
                    if( annotation.getAnnotationType().getDeclaration().getQualifiedName().equals( annotationTypeName ) ) 
                    {
                        processor.process( this._env, decl, annotation );
                    }
                }
            }
        }
        catch( Throwable e )
        {
            e.printStackTrace();
        }
    }
    
}