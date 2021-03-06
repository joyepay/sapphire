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

package org.eclipse.sapphire.tests.workspace.t0002;

import java.util.SortedSet;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.services.FactsAggregationService;
import org.eclipse.sapphire.tests.SapphireTestCase;

/**
 * Tests operation of FactsService implementations.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TestWorkspace0002 extends SapphireTestCase
{
    private TestWorkspace0002( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "Workspace0002" );

        suite.addTest( new TestWorkspace0002( "testProjectRelativePath" ) );
        suite.addTest( new TestWorkspace0002( "testWorkspaceRelativePath" ) );
        
        return suite;
    }
    
    public void testProjectRelativePath() throws Exception
    {
        test( TestRootElement.PROP_PROJECT_RELATIVE_PATH, "Must be a project relative path." );
    }

    public void testWorkspaceRelativePath() throws Exception
    {
        test( TestRootElement.PROP_WORKSPACE_RELATIVE_PATH, "Must be a workspace relative path." );
    }

    private static void test( final PropertyDef property,
                              final String... factsExpected )
    {
        test( TestRootElement.TYPE.instantiate(), property, factsExpected );
    }
    
    private static void test( final Element element,
                              final PropertyDef property,
                              final String... factsExpected )
    {
        final SortedSet<String> factsActual = element.property( property ).service( FactsAggregationService.class ).facts();
        
        assertEquals( set( factsExpected ), factsActual );
    }

}
