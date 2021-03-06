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

package org.eclipse.sapphire.tests.services.t0001;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.services.FactsAggregationService;
import org.eclipse.sapphire.tests.SapphireTestCase;

/**
 * Tests operation of FactsService implementations.
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class TestServices0001 extends SapphireTestCase
{
    private TestServices0001( final String name )
    {
        super( name );
    }
    
    public static Test suite()
    {
        final TestSuite suite = new TestSuite();
        
        suite.setName( "TestServices0001" );

        suite.addTest( new TestServices0001( "testPlain" ) );
        suite.addTest( new TestServices0001( "testSensitive" ) );
        suite.addTest( new TestServices0001( "testDefaultValue" ) );
        suite.addTest( new TestServices0001( "testDefaultValueSensitive" ) );
        suite.addTest( new TestServices0001( "testNumericRangeMin" ) );
        suite.addTest( new TestServices0001( "testNumericRangeMax" ) );
        suite.addTest( new TestServices0001( "testNumericRangeMinMax" ) );
        suite.addTest( new TestServices0001( "testRequiredValue" ) );
        suite.addTest( new TestServices0001( "testRequiredValueWithDefault" ) );
        suite.addTest( new TestServices0001( "testRequiredElement" ) );
        suite.addTest( new TestServices0001( "testReadOnly" ) );
        suite.addTest( new TestServices0001( "testCountConstraintAtLeastOne" ) );
        suite.addTest( new TestServices0001( "testCountConstraintMin" ) );
        suite.addTest( new TestServices0001( "testCountConstraintMax" ) );
        suite.addTest( new TestServices0001( "testCountConstraintMinMax" ) );
        suite.addTest( new TestServices0001( "testAbsolutePath" ) );
        suite.addTest( new TestServices0001( "testMustExist" ) );
        suite.addTest( new TestServices0001( "testMustExistAbsolutePath" ) );
        suite.addTest( new TestServices0001( "testNoDuplicates" ) );
        suite.addTest( new TestServices0001( "testFileExtensionsOne" ) );
        suite.addTest( new TestServices0001( "testFileExtensionsTwo" ) );
        suite.addTest( new TestServices0001( "testFileExtensionsThree" ) );
        suite.addTest( new TestServices0001( "testFileExtensionsMany" ) );
        suite.addTest( new TestServices0001( "testValidFileSystemResourceTypeFile" ) );
        suite.addTest( new TestServices0001( "testValidFileSystemResourceTypeFolder" ) );
        suite.addTest( new TestServices0001( "testDeprecated" ) );
        suite.addTest( new TestServices0001( "testStatic" ) );
        
        return suite;
    }
    
    public void testPlain() throws Exception
    {
        test( TestRootElement.PROP_PLAIN );
    }

    public void testSensitive() throws Exception
    {
        test( TestRootElement.PROP_SENSITIVE );
    }

    public void testDefaultValue() throws Exception
    {
        test( TestRootElement.PROP_DEFAULT_VALUE, "Default value is \"123\"." );
    }

    public void testDefaultValueSensitive() throws Exception
    {
        test( TestRootElement.PROP_DEFAULT_VALUE_SENSITIVE, "Has default value." );
    }

    public void testNumericRangeMin() throws Exception
    {
        test( TestRootElement.PROP_NUMERIC_RANGE_MIN, "Minimum value is 1." );
    }
    
    public void testNumericRangeMax() throws Exception
    {
        test( TestRootElement.PROP_NUMERIC_RANGE_MAX, "Maximum value is 100." );
    }

    public void testNumericRangeMinMax() throws Exception
    {
        test( TestRootElement.PROP_NUMERIC_RANGE_MIN_MAX, "Minimum value is 1.", "Maximum value is 100." );
    }

    public void testRequiredValue() throws Exception
    {
        test( TestRootElement.PROP_REQUIRED_VALUE, "Must be specified." );
    }

    public void testRequiredValueWithDefault() throws Exception
    {
        test( TestRootElement.PROP_REQUIRED_VALUE_WITH_DEFAULT, "Default value is \"123\"." );
    }
    
    public void testRequiredElement() throws Exception
    {
        test( TestRootElement.PROP_REQUIRED_ELEMENT, "Must be specified." );
    }
    
    public void testReadOnly() throws Exception
    {
        test( TestRootElement.PROP_READ_ONLY, "Cannot be modified." );
    }
    
    public void testCountConstraintAtLeastOne() throws Exception
    {
        test( TestRootElement.PROP_COUNT_CONSTRAINT_AT_LEAST_ONE, "Must have at least one." );
    }
    
    public void testCountConstraintMin() throws Exception
    {
        test( TestRootElement.PROP_COUNT_CONSTRAINT_MIN, "Must have at least 2 items." );
    }
    
    public void testCountConstraintMax() throws Exception
    {
        test( TestRootElement.PROP_COUNT_CONSTRAINT_MAX, "Must have at most 200 items." );
    }
    
    public void testCountConstraintMinMax() throws Exception
    {
        test( TestRootElement.PROP_COUNT_CONSTRAINT_MIN_MAX, "Must have at least 2 items.", "Must have at most 200 items." );
    }
    
    public void testAbsolutePath() throws Exception
    {
        test( TestRootElement.PROP_ABSOLUTE_PATH, "Must be an absolute path." );
    }

    public void testMustExist() throws Exception
    {
        test( TestRootElement.PROP_MUST_EXIST, "Must exist." );
    }
    
    public void testMustExistAbsolutePath() throws Exception
    {
        test( TestRootElement.PROP_MUST_EXIST_ABSOLUTE_PATH, "Must be an absolute path.", "Must exist." );
    }
    
    public void testNoDuplicates() throws Exception
    {
        final TestRootElement root = TestRootElement.TYPE.instantiate();
        final TestNoDuplicatesChildElement child = root.getNoDuplicates().insert();
                
        test( child.property( TestNoDuplicatesChildElement.PROP_NO_DUPLICATES ), "Must be unique." );
    }

    public void testFileExtensionsOne() throws Exception
    {
        test( TestRootElement.PROP_FILE_EXTENSIONS_ONE, "Must have \"xml\" file extension." );
    }
    
    public void testFileExtensionsTwo() throws Exception
    {
        test( TestRootElement.PROP_FILE_EXTENSIONS_TWO, "Must have either \"xml\" or \"java\" file extension." );
    }
    
    public void testFileExtensionsThree() throws Exception
    {
        test( TestRootElement.PROP_FILE_EXTENSIONS_THREE, "Must have either \"xml\", \"java\" or \"jsp\" file extension." );
    }
    
    public void testFileExtensionsMany() throws Exception
    {
        test( TestRootElement.PROP_FILE_EXTENSIONS_MANY, "Must have one of these file extensions: \"xml\", \"java\", \"jsp\", \"jspx\"." );
    }

    public void testValidFileSystemResourceTypeFile() throws Exception
    {
        test( TestRootElement.PROP_VALID_FILE_SYSTEM_RESOURCE_TYPE_FILE, "Must be a file." );
    }
    
    public void testValidFileSystemResourceTypeFolder() throws Exception
    {
        test( TestRootElement.PROP_VALID_FILE_SYSTEM_RESOURCE_TYPE_FOLDER, "Must be a folder." );
    }
    
    @SuppressWarnings( "deprecation" )
    
    public void testDeprecated() throws Exception
    {
        test( TestRootElement.PROP_DEPRECATED, "Deprecated." );
    }
    
    public void testStatic() throws Exception
    {
        test( TestRootElement.PROP_STATIC_FACT, "First static fact.", "Second static fact.", "Third static fact." );
    }

    private static void test( final PropertyDef property,
                              final String... factsExpected )
    {
        test( TestRootElement.TYPE.instantiate().property( property ), factsExpected );
    }
    
    private static void test( final Property property,
                              final String... factsExpected )
    {
        assertEquals( set( factsExpected ), property.service( FactsAggregationService.class ).facts() );
    }

}
