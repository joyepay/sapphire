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

package org.eclipse.sapphire.tests.modeling.xml.binding.t0006;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.tests.SapphireTestCase;

/**
 * Tests reporting of unresolvable namespace usage in list property binding.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TestXmlBinding0006

    extends SapphireTestCase
    
{
    private TestXmlBinding0006( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "TestXmlBinding0006" );

        suite.addTest( new TestXmlBinding0006( "testInMapping" ) );
        suite.addTest( new TestXmlBinding0006( "testInPath" ) );
        
        return suite;
    }
    
    public void testInMapping() throws Exception
    {
        final XmlResourceStore xmlResourceStore = new XmlResourceStore();
        final TestXmlBinding0006A element = TestXmlBinding0006A.TYPE.instantiate( new RootXmlResource( xmlResourceStore ) );
        
        try
        {
            element.getTestProperty().insert();
            fail( "Did not catch the expected exception." );
        }
        catch( Exception e )
        {
            assertEquals( e.getMessage(), "TestXmlBinding0006A.TestProperty : Could not resolve namespace for foo:abc node name." );
        }
    }

    public void testInPath() throws Exception
    {
        final XmlResourceStore xmlResourceStore = new XmlResourceStore();
        final TestXmlBinding0006B element = TestXmlBinding0006B.TYPE.instantiate( new RootXmlResource( xmlResourceStore ) );
        
        try
        {
            element.getTestProperty().insert();
            fail( "Did not catch the expected exception." );
        }
        catch( Exception e )
        {
            assertEquals( e.getMessage(), "TestXmlBinding0006B.TestProperty : Could not resolve namespace for foo:abc node name." );
        }
    }

}
