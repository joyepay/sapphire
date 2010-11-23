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

package org.eclipse.sapphire.ui.def;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.BasePathsProvider;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.MustExist;
import org.eclipse.sapphire.modeling.annotations.NonNullValue;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.ValidFileExtensions;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.ui.def.internal.ProjectRootBasePathsProvider;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "image reference" )
@GenerateImpl

public interface ISapphireActionImage

    extends IModelElement
    
{
    ModelElementType TYPE = new ModelElementType( ISapphireActionImage.class );
    
    // *** Image ***
    
    @Reference( target = ImageDescriptor.class )
    @Label( standard = "image" )
    @NonNullValue
    @BasePathsProvider( ProjectRootBasePathsProvider.class )
    @ValidFileSystemResourceType( FileSystemResourceType.FILE )
    @ValidFileExtensions( { "gif", "png" } )
    @MustExist
    @XmlBinding( path = "" )
    
    ValueProperty PROP_IMAGE = new ValueProperty( TYPE, "Image" );
    
    ReferenceValue<ImageDescriptor> getImage();
    void setImage( String value );
    
}