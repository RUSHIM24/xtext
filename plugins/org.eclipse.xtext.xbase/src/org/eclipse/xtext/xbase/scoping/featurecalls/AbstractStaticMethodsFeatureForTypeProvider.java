/*******************************************************************************
 * Copyright (c) 2011 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.xbase.scoping.featurecalls;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import static java.util.Collections.*;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.common.types.JvmDeclaredType;
import org.eclipse.xtext.common.types.JvmFeature;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.util.TypeReferences;
import org.eclipse.xtext.xbase.typing.XbaseTypeConformanceComputer;

import com.google.common.base.Predicate;
import com.google.inject.Inject;

/**
 * @author Sven Efftinge - Initial contribution and API
 * @author Sebastian Zarnekow
 */
public abstract class AbstractStaticMethodsFeatureForTypeProvider implements IFeaturesForTypeProvider {

	@Inject
	private XbaseTypeConformanceComputer conformanceComputer;
	
	@Inject
	private TypeReferences typeRefs;

	private Resource context;

	public Iterable<? extends JvmFeature> getFeaturesForType(final JvmTypeReference reference) {
		final Iterable<String> staticTypes = getVisibleTypesContainingStaticMethods(reference);
		Iterable<JvmOperation> staticMethods = emptySet();
		for (String typeName : staticTypes) {
			JvmTypeReference typeReference = typeRefs.getTypeForName(typeName, context);
			if (typeReference != null) {
				final JvmDeclaredType type = (JvmDeclaredType) typeReference.getType();
				Iterable<JvmOperation> operations = type.getDeclaredOperations();
				staticMethods = concat(staticMethods, filter(operations, new Predicate<JvmOperation>() {
					public boolean apply(JvmOperation input) {
						if (input.isStatic()) {
							if (reference == null)
								return true;
							if (input.getParameters().size() > 0) {
								JvmFormalParameter firstParam = input.getParameters().get(0);
								return conformanceComputer.isConformant(firstParam.getParameterType(),
										reference, true);
							}
						}
						return false;
					}
				}));
			}
		}
		return newArrayList(staticMethods);
	}

	protected abstract Iterable<String> getVisibleTypesContainingStaticMethods(JvmTypeReference reference);

	public void setContext(Resource context) {
		this.context = context;
	}
	
	protected Resource getContext() {
		return context;
	}
	
	protected XbaseTypeConformanceComputer getConformanceComputer() {
		return conformanceComputer;
	}
	
	protected TypeReferences getTypeRefs() {
		return typeRefs;
	}
}
