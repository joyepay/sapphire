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

package org.eclipse.sapphire.modeling.el;

import static org.eclipse.sapphire.modeling.el.internal.FunctionUtils.isDecimalString;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.util.NLS;

/**
 * Arithmetic unary minus function. 
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ArithmeticNegationFunction extends Function
{
    public static ArithmeticNegationFunction create( final Function operand )
    {
        final ArithmeticNegationFunction function = new ArithmeticNegationFunction();
        function.init( operand );
        return function;
    }
    
    @Override
    public String name()
    {
        return "-";
    }

    @Override
    public boolean operator()
    {
        return true;
    }

    @Override
    public int precedence()
    {
        return 2;
    }

    @Override
    public FunctionResult evaluate( final FunctionContext context )
    {
        return new FunctionResult( this, context )
        {
            @Override
            protected Object evaluate()
            {
                Object a = operand( 0 );
                
                if( a instanceof Value<?> )
                {
                    a = ( (Value<?>) a ).content();
                }
                
                if( a == null )
                {
                    return (long) 0;
                }
                else if( a instanceof BigDecimal )
                {
                    return ( (BigDecimal) a ).negate();
                }
                else if( a instanceof BigInteger )
                {
                    return ( (BigInteger) a ).negate();
                }
                else if( a instanceof String )
                {
                    if( isDecimalString( a ) )
                    {
                        return -( cast( a, Double.class ) );
                    }
                    else
                    {
                        return -( cast( a, Long.class ) );
                    }
                }
                else if( a instanceof Byte )
                {
                    return -( (Byte) a );
                }
                else if( a instanceof Short )
                {
                    return -( (Short) a );
                }
                else if( a instanceof Integer )
                {
                    return -( (Integer) a );
                }
                else if( a instanceof Long )
                {
                    return -( (Long) a );
                }
                else if( a instanceof Float )
                {
                    return -( (Float) a );
                }
                else if( a instanceof Double )
                {
                    return -( (Double) a );
                }
                else
                {
                    throw new FunctionException( NLS.bind( Resources.cannotApplyMessage, a.getClass().getName() ) );
                }
            }
        };
    }

    private static final class Resources extends NLS
    {
        public static String cannotApplyMessage;
        
        static
        {
            initializeMessages( ArithmeticNegationFunction.class.getName(), Resources.class );
        }
    }

}
