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

package org.eclipse.sapphire.tests.modeling.misc.t0007;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.tests.SapphireTestCase;

/**
 * Tests de-duplication of validation messages.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TestModelingMisc0007 extends SapphireTestCase
{
    private TestModelingMisc0007( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "TestModelingMisc0007" );

        suite.addTest( new TestModelingMisc0007( "test" ) );
        
        return suite;
    }
    
    public void test() throws Exception
    {
        final TestRootElement root = TestRootElement.TYPE.instantiate();
        final ElementList<TestChildElement> children = root.getChildren();
        
        final TestChildElement x = children.insert();
        x.setId( "123" );
        
        final TestChildElement y = children.insert();
        y.setId( "123" );
        
        final Status status = root.validation();
        
        assertEquals( "Unique id required. Another occurrence of \"123\" was found.", status.message() );
        assertEquals( 0, status.children().size() );
    }

}
