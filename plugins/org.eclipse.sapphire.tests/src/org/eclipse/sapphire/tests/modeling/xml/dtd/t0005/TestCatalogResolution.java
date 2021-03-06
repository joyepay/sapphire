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

package org.eclipse.sapphire.tests.modeling.xml.dtd.t0005;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.modeling.xml.schema.XmlDocumentSchema;
import org.eclipse.sapphire.modeling.xml.schema.XmlDocumentSchemasCache;
import org.eclipse.sapphire.tests.SapphireTestCase;

/**
 * Tests reading DTDs from the local catalog.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TestCatalogResolution extends SapphireTestCase
{
    private TestCatalogResolution( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "TestCatalogResolution" );

        suite.addTest( new TestCatalogResolution( "testSystemContribution" ) );
        suite.addTest( new TestCatalogResolution( "testPublicContribution1" ) );
        suite.addTest( new TestCatalogResolution( "testPublicContribution2" ) );
        
        return suite;
    }
    
    public void testSystemContribution() throws Exception
    {
        final XmlDocumentSchema schema = XmlDocumentSchemasCache.getSchema( null, null, "http://www.eclipse.org/sapphire/tests/xml/dtd/0005s.dtd" );
        
        assertNotNull( schema );
        assertNotNull( schema.getElement( "system" ) );
        
        final XmlResourceStore store = new XmlResourceStore( loadResource( "System.xml" ) );
        final XmlElement element = new XmlElement( store, store.getDomDocument().getDocumentElement() );
        
        assertNotNull( element.getContentModel() );
    }

   public void testPublicContribution1() throws Exception
    {
        final XmlDocumentSchema schema = XmlDocumentSchemasCache.getSchema( null, "-//Sapphire//TestCatalogResolution1//EN", "http://www.eclipse.org/sapphire/tests/xml/dtd/0005p.dtd" );
        
        assertNotNull( schema );
        assertNotNull( schema.getElement( "public" ) );
        
        final XmlResourceStore store = new XmlResourceStore( loadResource( "Public1.xml" ) );
        final XmlElement element = new XmlElement( store, store.getDomDocument().getDocumentElement() );
        
        assertNotNull( element.getContentModel() );
    }

    public void testPublicContribution2() throws Exception
    {
        final XmlDocumentSchema schema = XmlDocumentSchemasCache.getSchema( null, "-//Sapphire//TestCatalogResolution2//EN", "http://www.eclipse.org/sapphire/tests/xml/dtd/0005p.dtd" );
        
        assertNotNull( schema );
        assertNotNull( schema.getElement( "public" ) );
        
        final XmlResourceStore store = new XmlResourceStore( loadResource( "Public2.xml" ) );
        final XmlElement element = new XmlElement( store, store.getDomDocument().getDocumentElement() );
        
        assertNotNull( element.getContentModel() );
    }

}
