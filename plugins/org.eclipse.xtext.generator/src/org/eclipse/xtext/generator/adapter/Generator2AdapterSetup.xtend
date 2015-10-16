/*******************************************************************************
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.generator.adapter

import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Module
import org.eclipse.xpand2.XpandExecutionContext
import org.eclipse.xtend.lib.annotations.Accessors
import org.eclipse.xtext.Grammar
import org.eclipse.xtext.generator.LanguageConfig
import org.eclipse.xtext.generator.Naming
import org.eclipse.xtext.xtext.generator.CodeConfig
import org.eclipse.xtext.xtext.generator.DefaultGeneratorModule
import org.eclipse.xtext.xtext.generator.ILanguageConfig
import org.eclipse.xtext.xtext.generator.LanguageConfig2
import org.eclipse.xtext.xtext.generator.WizardConfig
import org.eclipse.xtext.xtext.generator.XtextGeneratorNaming
import org.eclipse.xtext.xtext.generator.XtextProjectConfig

import static org.eclipse.xtext.generator.Generator.*

/**
 * @since 2.9
 */
class Generator2AdapterSetup {
	
	val LanguageConfig languageConfig
	val XpandExecutionContext xpandContext
	val Naming naming
	
	@Accessors
	val Injector injector
	
	new(LanguageConfig languageConfig, XpandExecutionContext xpandContext, Naming naming) {
		this.languageConfig = languageConfig
		this.xpandContext = xpandContext
		this.naming = naming
		this.injector = createInjector()
	}
	
	private def Injector createInjector() {
		val generatorModule = new DefaultGeneratorModule => [
			project = createProjectConfig()
			code = createCodeConfig()
		]
		val generatorInjector = Guice.createInjector(generatorModule)
		generatorModule.project.initialize(generatorInjector)
		generatorModule.code.initialize(generatorInjector)
		val language = createLanguage(generatorInjector)
		val Module languageModule = [
			bind(ILanguageConfig).toInstance(language)
			bind(Grammar).toInstance(language.grammar)
			bind(XtextGeneratorNaming).toInstance(new NamingAdapter(naming))
		]
		return generatorInjector.createChildInjector(languageModule)
	}
	
	private def XtextProjectConfig createProjectConfig() {
		new WizardConfig => [
			createEclipseMetaData = true
			baseName = naming.projectNameRt
			val runtimeRoot = xpandContext.output.getOutlet(PLUGIN_RT).path
			val projectNameIndex = runtimeRoot.lastIndexOf(baseName)
			if (projectNameIndex >= 0)
				rootPath = runtimeRoot.substring(0, projectNameIndex)
			else
				rootPath = runtimeRoot
			runtime.name = baseName
			runtime.root = runtimeRoot
			runtime.src = xpandContext.output.getOutlet(SRC).path
			runtime.srcGen = xpandContext.output.getOutlet(SRC_GEN).path
			runtime.ecoreModel = xpandContext.output.getOutlet(MODEL).path
			eclipsePlugin.enabled = true
			eclipsePlugin.name = naming.projectNameUi
			eclipsePlugin.root = xpandContext.output.getOutlet(PLUGIN_UI).path
			eclipsePlugin.src = xpandContext.output.getOutlet(SRC_UI).path
			eclipsePlugin.srcGen = xpandContext.output.getOutlet(SRC_GEN_UI).path
			genericIde.enabled = true
			genericIde.name = naming.projectNameIde
			genericIde.root = xpandContext.output.getOutlet(PLUGIN_IDE).path
			genericIde.src = xpandContext.output.getOutlet(SRC_IDE).path
			genericIde.srcGen = xpandContext.output.getOutlet(SRC_GEN_IDE).path
		]
	}
	
	private def CodeConfig createCodeConfig() {
		new CodeConfig => [
			lineDelimiter = naming.lineDelimiter
			fileHeader = naming.fileHeader
		]
	}
	
	private def ILanguageConfig createLanguage(Injector generatorInjector) {
		new LanguageConfig2 => [
			uri = languageConfig.grammar.eResource.URI.toString
			resourceSet = languageConfig.grammar.eResource.resourceSet
			fileExtensions = languageConfig.getFileExtensions(languageConfig.grammar).join(',')
			generatorInjector.injectMembers(it)
			initialize(languageConfig.grammar)
		]
	}
	
}