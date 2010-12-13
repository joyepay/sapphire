/******************************************************************************
 * Copyright (c) 2010 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.ui.def.internal;

import org.eclipse.sapphire.modeling.annotations.ReferenceResolverImpl;
import org.eclipse.sapphire.ui.def.ISapphireUiDef;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ClassReferenceResolver

    extends ReferenceResolverImpl<Class<?>>

{
    @Override
    public Class<?> resolve( final String reference )
    {
        final ISapphireUiDef def = (ISapphireUiDef) getModelElement().getModel();
        return def.resolveClass( reference );
    }
    
}