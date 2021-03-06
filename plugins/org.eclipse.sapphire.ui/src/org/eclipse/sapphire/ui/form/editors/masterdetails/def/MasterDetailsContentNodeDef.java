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

package org.eclipse.sapphire.ui.form.editors.masterdetails.def;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.el.Function;
import org.eclipse.sapphire.modeling.localization.Localizable;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.ui.def.FormComponentDef;
import org.eclipse.sapphire.ui.def.IPropertiesViewContributorDef;
import org.eclipse.sapphire.ui.def.SectionDef;
import org.eclipse.sapphire.ui.def.SectionRef;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@Label( standard = "content outline node" )
@XmlBinding( path = "node" )

public interface MasterDetailsContentNodeDef extends MasterDetailsContentNodeChildDef, IPropertiesViewContributorDef
{
    ElementType TYPE = new ElementType( MasterDetailsContentNodeDef.class );
    
    // *** Property ***
    
    @Label( standard = "property" )
    @XmlBinding( path = "property" )
    
    ValueProperty PROP_PROPERTY = new ValueProperty( TYPE, "Property" );
    
    Value<String> getProperty();
    void setProperty( String property );
    
    // *** Label ***
    
    @Type( base = Function.class )
    @Label( standard = "label" )
    @Localizable
    @XmlBinding( path = "label" )
    
    ValueProperty PROP_LABEL = new ValueProperty( TYPE, "Label" );
    
    Value<Function> getLabel();
    void setLabel( String value );
    void setLabel( Function value );
    
    // *** Image ***
    
    @Type( base = Function.class )
    @Label( standard = "image" )
    @XmlBinding( path = "image" )
    
    ValueProperty PROP_IMAGE = new ValueProperty( TYPE, "Image" );
    
    Value<Function> getImage();
    void setImage( String value );
    void setImage( Function value );
    
    // *** Sections ***
    
    @Label( standard = "sections" )
    @Type( base = FormComponentDef.class, possible = { SectionDef.class, SectionRef.class } )
    @XmlListBinding( path = "" )
    
    ListProperty PROP_SECTIONS = new ListProperty( TYPE, "Sections" );
    
    ElementList<FormComponentDef> getSections();
    
    // *** ChildNodes ***
    
    @Label( standard = "child nodes" )
    @XmlListBinding( path = "" )
    
    @Type
    ( 
        base = MasterDetailsContentNodeChildDef.class, 
        possible = 
        { 
            MasterDetailsContentNodeDef.class,
            MasterDetailsContentNodeFactoryDef.class,
            MasterDetailsContentNodeInclude.class
        } 
    )
    
    ListProperty PROP_CHILD_NODES = new ListProperty( TYPE, "ChildNodes" );
    
    ElementList<MasterDetailsContentNodeChildDef> getChildNodes();
    
}
