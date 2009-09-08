/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.xtext.scoping.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopedElement;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;


/**
 * @author Sven Efftinge - Initial contribution and API
 *
 */
public abstract class AbstractScope implements IScope {

	public Iterable<IScopedElement> getAllContents() {
		final Set<String> identifiers = new HashSet<String>();
		return Iterables.concat(Iterables.transform(getContents(), new Function<IScopedElement, IScopedElement>() {
			public IScopedElement apply(IScopedElement param) {
				identifiers.add(param.name());
				return param;
			}
		}), Iterables.filter(getOuterScope().getAllContents(), new Predicate<IScopedElement>() {
			public boolean apply(IScopedElement param) {
				return !identifiers.contains(param.name());
			}
		}));
	}

	public IScopedElement getContentByEObject(EObject object) {
		Iterator<IScopedElement> allContents = getAllContents().iterator();
		URI uri = EcoreUtil.getURI(object);
		while (allContents.hasNext()) {
			IScopedElement element = allContents.next();
			URI elementsUri = EcoreUtil.getURI(element.element());
			if (uri.equals(elementsUri))
				return element;
		}
		return null;
	}

	public IScopedElement getContentByName(String name) {
		Iterator<IScopedElement> allContents = getAllContents().iterator();
		while (allContents.hasNext()) {
			IScopedElement element = allContents.next();
			if (element.name().equals(name)) 
				return element;
		}
		return null;
	}
}
