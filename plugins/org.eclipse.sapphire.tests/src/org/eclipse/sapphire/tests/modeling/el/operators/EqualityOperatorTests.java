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

package org.eclipse.sapphire.tests.modeling.el.operators;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests for the equality operator.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class EqualityOperatorTests extends OperatorTests
{
    private EqualityOperatorTests( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "EqualityOperatorTests" );
        
        for( int i = 1; i <= 15; i++ )
        {
            suite.addTest( new EqualityOperatorTests( "testEqualityOperator" + String.valueOf( i ) ) );
        }
        
        return suite;
    }
    
    public void testEqualityOperator1()
    {
        test( "${ 3 == 3 }", true );
    }

    public void testEqualityOperator2()
    {
        test( "${ 3 == 5 }", false );
    }

    public void testEqualityOperator3()
    {
        test( "${ 3.2 == 3.2 }", true );
    }

    public void testEqualityOperator4()
    {
        test( "${ 3.2 == 5 }", false );
    }
    
    public void testEqualityOperator5()
    {
        test( "${ 'abc' == 'abc' }", true );
    }

    public void testEqualityOperator6()
    {
        test( "${ 'abc' == 'xyz' }", false );
    }
    
    public void testEqualityOperator7()
    {
        test( "${ 3 eq 3 }", true );
    }

    public void testEqualityOperator8()
    {
        test( "${ 3 eq 5 }", false );
    }

    public void testEqualityOperator9()
    {
        test( "${ 3.2 eq 3.2 }", true );
    }

    public void testEqualityOperator10()
    {
        test( "${ 3.2 eq 5 }", false );
    }
    
    public void testEqualityOperator11()
    {
        test( "${ 'abc' eq 'abc' }", true );
    }

    public void testEqualityOperator12()
    {
        test( "${ 'abc' eq 'xyz' }", false );
    }

    public void testEqualityOperator13()
    {
        test( "${ Integer3 == 7 }", false );
    }

    public void testEqualityOperator14()
    {
        test( "${ 7 == Integer5 }", false );
    }
    
    public void testEqualityOperator15()
    {
        test( "${ Integer3 == Integer5 }", false );
    }

}

