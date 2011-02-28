/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.editor;

import java.util.List;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.el.FunctionResult;
import org.eclipse.sapphire.modeling.el.RootPropertyAccessFunction;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public class FunctionUtil 
{
    public static ValueProperty getFunctionProperty(IModelElement modelElement, FunctionResult functionResult)
    {
    	if (functionResult.function() instanceof RootPropertyAccessFunction)
    	{
    		if (functionResult.operand(0).value() instanceof String)
    		{
    			String propName = (String)functionResult.operand(0).value();
    	        final ModelElementType type = modelElement.getModelElementType();
    	        final ModelProperty property = type.getProperty(propName);
    			if (property instanceof ValueProperty)
    			{
    				return (ValueProperty)property;
    			}
    		}
    	}
    	else 
    	{
    		List<FunctionResult> subFuncs = functionResult.operands();
    		for (FunctionResult subFunc : subFuncs)
    		{
    			ValueProperty property = getFunctionProperty(modelElement, subFunc);
    			if (property != null)
    			{
    				return property;
    			}
    		}
    	}
    	return null;
    }

}