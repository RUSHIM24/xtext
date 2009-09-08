/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.xtext.scoping.impl;

import org.eclipse.xtext.scoping.IScopedElement;


/**
 * @author Sven Efftinge - Initial contribution and API
 *
 */
public abstract class AbstractScopedElement implements IScopedElement {
	@Override
	public String toString() {
		return name();
	}
	
	public Object additionalInformation() {
		return null;
	}
}
