/**
 * Copyright (c) 2015 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtext.web.server.test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.util.Collections;
import org.eclipse.xtext.web.server.IServiceResult;
import org.eclipse.xtext.web.server.XtextServiceDispatcher;
import org.eclipse.xtext.web.server.persistence.ResourceContentResult;
import org.eclipse.xtext.web.server.test.AbstractWebServerTest;
import org.eclipse.xtext.web.server.test.HashMapSession;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function0;
import org.eclipse.xtext.xbase.lib.Pair;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("all")
public class ResourcePersistenceTest extends AbstractWebServerTest {
  @Test
  public void testLoadFile() {
    final String resourceContent = "state foo end";
    final File file = this.createFile(resourceContent);
    Pair<String, String> _mappedTo = Pair.<String, String>of("serviceType", "load");
    String _name = file.getName();
    Pair<String, String> _mappedTo_1 = Pair.<String, String>of("resource", _name);
    final XtextServiceDispatcher.ServiceDescriptor load = this.getService(Collections.<String, String>unmodifiableMap(CollectionLiterals.<String, String>newHashMap(_mappedTo, _mappedTo_1)));
    Assert.assertFalse(load.isHasSideEffects());
    Function0<? extends IServiceResult> _service = load.getService();
    IServiceResult _apply = _service.apply();
    final ResourceContentResult result = ((ResourceContentResult) _apply);
    Assert.assertEquals(resourceContent, result.getFullText());
    Assert.assertFalse(result.isDirty());
  }
  
  @Test
  public void testLoadDummy() {
    final HashMapSession session = new HashMapSession();
    final String resourceContent = "state foo end";
    Pair<String, String> _mappedTo = Pair.<String, String>of("serviceType", "update");
    Pair<String, String> _mappedTo_1 = Pair.<String, String>of("resource", "dummy.statemachine");
    Pair<String, String> _mappedTo_2 = Pair.<String, String>of("fullText", resourceContent);
    XtextServiceDispatcher.ServiceDescriptor update = this.getService(
      Collections.<String, String>unmodifiableMap(CollectionLiterals.<String, String>newHashMap(_mappedTo, _mappedTo_1, _mappedTo_2)), session);
    Function0<? extends IServiceResult> _service = update.getService();
    _service.apply();
    Pair<String, String> _mappedTo_3 = Pair.<String, String>of("serviceType", "load");
    Pair<String, String> _mappedTo_4 = Pair.<String, String>of("resource", "dummy.statemachine");
    XtextServiceDispatcher.ServiceDescriptor load = this.getService(Collections.<String, String>unmodifiableMap(CollectionLiterals.<String, String>newHashMap(_mappedTo_3, _mappedTo_4)), session);
    Function0<? extends IServiceResult> _service_1 = load.getService();
    IServiceResult _apply = _service_1.apply();
    ResourceContentResult result = ((ResourceContentResult) _apply);
    Assert.assertEquals(resourceContent, result.getFullText());
    update = this.getService(
      Collections.<String, String>unmodifiableMap(CollectionLiterals.<String, String>newHashMap(Pair.<String, String>of("serviceType", "update"), Pair.<String, String>of("resource", "dummy.statemachine"), Pair.<String, String>of("deltaText", "bar"), Pair.<String, String>of("deltaOffset", "6"), Pair.<String, String>of("deltaReplaceLength", "3"))), session);
    Function0<? extends IServiceResult> _service_2 = update.getService();
    _service_2.apply();
    load = this.getService(Collections.<String, String>unmodifiableMap(CollectionLiterals.<String, String>newHashMap(Pair.<String, String>of("serviceType", "load"), Pair.<String, String>of("resource", "dummy.statemachine"))), session);
    Function0<? extends IServiceResult> _service_3 = load.getService();
    IServiceResult _apply_1 = _service_3.apply();
    result = ((ResourceContentResult) _apply_1);
    Assert.assertEquals("state bar end", result.getFullText());
  }
  
  @Test
  public void testRevertFile() {
    final String resourceContent = "state foo end";
    final File file = this.createFile(resourceContent);
    final HashMapSession session = new HashMapSession();
    Pair<String, String> _mappedTo = Pair.<String, String>of("serviceType", "load");
    String _name = file.getName();
    Pair<String, String> _mappedTo_1 = Pair.<String, String>of("resource", _name);
    final XtextServiceDispatcher.ServiceDescriptor load = this.getService(Collections.<String, String>unmodifiableMap(CollectionLiterals.<String, String>newHashMap(_mappedTo, _mappedTo_1)), session);
    Function0<? extends IServiceResult> _service = load.getService();
    _service.apply();
    Pair<String, String> _mappedTo_2 = Pair.<String, String>of("serviceType", "update");
    String _name_1 = file.getName();
    Pair<String, String> _mappedTo_3 = Pair.<String, String>of("resource", _name_1);
    Pair<String, String> _mappedTo_4 = Pair.<String, String>of("deltaText", "bar");
    Pair<String, String> _mappedTo_5 = Pair.<String, String>of("deltaOffset", "6");
    Pair<String, String> _mappedTo_6 = Pair.<String, String>of("deltaReplaceLength", "3");
    final XtextServiceDispatcher.ServiceDescriptor update = this.getService(
      Collections.<String, String>unmodifiableMap(CollectionLiterals.<String, String>newHashMap(_mappedTo_2, _mappedTo_3, _mappedTo_4, _mappedTo_5, _mappedTo_6)), session);
    Function0<? extends IServiceResult> _service_1 = update.getService();
    _service_1.apply();
    Pair<String, String> _mappedTo_7 = Pair.<String, String>of("serviceType", "revert");
    String _name_2 = file.getName();
    Pair<String, String> _mappedTo_8 = Pair.<String, String>of("resource", _name_2);
    final XtextServiceDispatcher.ServiceDescriptor revert = this.getService(Collections.<String, String>unmodifiableMap(CollectionLiterals.<String, String>newHashMap(_mappedTo_7, _mappedTo_8)), session);
    Assert.assertTrue(revert.isHasSideEffects());
    Function0<? extends IServiceResult> _service_2 = revert.getService();
    IServiceResult _apply = _service_2.apply();
    final ResourceContentResult result = ((ResourceContentResult) _apply);
    Assert.assertEquals(resourceContent, result.getFullText());
  }
  
  @Test
  public void testSaveFile() {
    try {
      final File file = this.createFile("state foo end");
      final HashMapSession session = new HashMapSession();
      Pair<String, String> _mappedTo = Pair.<String, String>of("serviceType", "load");
      String _name = file.getName();
      Pair<String, String> _mappedTo_1 = Pair.<String, String>of("resource", _name);
      final XtextServiceDispatcher.ServiceDescriptor load = this.getService(Collections.<String, String>unmodifiableMap(CollectionLiterals.<String, String>newHashMap(_mappedTo, _mappedTo_1)), session);
      Function0<? extends IServiceResult> _service = load.getService();
      _service.apply();
      Pair<String, String> _mappedTo_2 = Pair.<String, String>of("serviceType", "update");
      String _name_1 = file.getName();
      Pair<String, String> _mappedTo_3 = Pair.<String, String>of("resource", _name_1);
      Pair<String, String> _mappedTo_4 = Pair.<String, String>of("deltaText", "bar");
      Pair<String, String> _mappedTo_5 = Pair.<String, String>of("deltaOffset", "6");
      Pair<String, String> _mappedTo_6 = Pair.<String, String>of("deltaReplaceLength", "3");
      final XtextServiceDispatcher.ServiceDescriptor update = this.getService(
        Collections.<String, String>unmodifiableMap(CollectionLiterals.<String, String>newHashMap(_mappedTo_2, _mappedTo_3, _mappedTo_4, _mappedTo_5, _mappedTo_6)), session);
      Function0<? extends IServiceResult> _service_1 = update.getService();
      _service_1.apply();
      Pair<String, String> _mappedTo_7 = Pair.<String, String>of("serviceType", "save");
      String _name_2 = file.getName();
      Pair<String, String> _mappedTo_8 = Pair.<String, String>of("resource", _name_2);
      final XtextServiceDispatcher.ServiceDescriptor save = this.getService(Collections.<String, String>unmodifiableMap(CollectionLiterals.<String, String>newHashMap(_mappedTo_7, _mappedTo_8)), session);
      Assert.assertTrue(save.isHasSideEffects());
      Function0<? extends IServiceResult> _service_2 = save.getService();
      _service_2.apply();
      final String resourceContent = Files.toString(file, Charsets.UTF_8);
      Assert.assertEquals("state bar end", resourceContent);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
