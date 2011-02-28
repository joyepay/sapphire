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

import org.eclipse.sapphire.ui.SapphirePartListener;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 */

public abstract class SapphireDiagramPartListener extends SapphirePartListener 
{
    public void handleNodeUpdateEvent(final DiagramNodeEvent event)
    {
        // The default implementation doesn't do anything.
    }

    public void handleNodeAddEvent(final DiagramNodeEvent event)
    {
    	// The default implementation doesn't do anything.
    }

    public void handleNodeDeleteEvent(final DiagramNodeEvent event)
    {
    	// The default implementation doesn't do anything.
    }

    public void handleConnectionUpdateEvent(final DiagramConnectionEvent event)
    {
        // The default implementation doesn't do anything.
    }

    public void handleConnectionEndpointEvent(final DiagramConnectionEvent event)
    {
    	// The default implementation doesn't do anything.
    }
    
    public void handleConnectionAddEvent(final DiagramConnectionEvent event)
    {
    	// The default implementation doesn't do anything.
    }

    public void handleConnectionDeleteEvent(final DiagramConnectionEvent event)
    {
    	// The default implementation doesn't do anything.
    }
}