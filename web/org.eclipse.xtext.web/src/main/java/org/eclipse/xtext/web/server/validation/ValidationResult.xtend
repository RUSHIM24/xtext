/*******************************************************************************
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.web.server.validation

import java.util.List
import org.eclipse.xtend.lib.annotations.Data
import org.eclipse.xtend.lib.annotations.ToString
import org.eclipse.xtext.web.server.IServiceResult

/**
 * Result object returned by the validation service.
 */
@Data
class ValidationResult implements IServiceResult {
	
	val List<Issue> issues = newArrayList
	
	@Data
	@ToString(skipNulls = true)
	static class Issue {
		String description
		String severity 
		Integer line
		Integer offset
		Integer length
	}
	
}
