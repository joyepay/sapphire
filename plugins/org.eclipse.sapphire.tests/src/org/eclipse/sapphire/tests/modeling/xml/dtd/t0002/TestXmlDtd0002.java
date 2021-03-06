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

package org.eclipse.sapphire.tests.modeling.xml.dtd.t0002;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.modeling.xml.dtd.DtdParser;
import org.eclipse.sapphire.modeling.xml.schema.XmlDocumentSchema;
import org.eclipse.sapphire.tests.SapphireTestCase;

/**
 * Tests entity resolution during DTD parsing.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TestXmlDtd0002 extends SapphireTestCase
{
    private TestXmlDtd0002( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "TestXmlDtd0002" );

        suite.addTest( new TestXmlDtd0002( "test" ) );
        
        return suite;
    }
    
    public void test() throws Exception
    {
        final XmlDocumentSchema schema = DtdParser.parse( loadResource( "input.dtd" ) );
        assertEqualsIgnoreNewLineDiffs( loadResource( "output.txt" ), schema.toString() );
    }

}
