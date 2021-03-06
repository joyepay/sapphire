/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and Other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.sapphire.ui.swt.renderer.internal.formtext;

import java.util.Hashtable;

import org.eclipse.swt.graphics.Rectangle;

/**
 * @author IBM Corporation
 */

@SuppressWarnings(value = { "rawtypes" })
public interface IFocusSelectable {
    boolean isFocusSelectable(Hashtable resourceTable);
    boolean setFocus(Hashtable resourceTable, boolean direction);
    Rectangle getBounds();
}
