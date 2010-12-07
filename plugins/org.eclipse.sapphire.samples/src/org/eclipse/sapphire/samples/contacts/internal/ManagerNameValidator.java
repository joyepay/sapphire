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

package org.eclipse.sapphire.samples.contacts.internal;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class ManagerNameValidator

    extends ConnectionNameValidator
    
{
    protected IStatus createErrorStatus()
    {
        return createErrorStatus( Resources.cannotBeYourOwnManager );
    }
    
    private static final class Resources extends NLS
    {
        public static String cannotBeYourOwnManager;
        
        static
        {
            initializeMessages( ManagerNameValidator.class.getName(), Resources.class );
        }
    }
    
}
