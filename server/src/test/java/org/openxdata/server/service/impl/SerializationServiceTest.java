/*
 *  Licensed to the OpenXdata Foundation (OXDF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The OXDF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, 
 *  software distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and limitations under the License.
 *
 *  Copyright 2010 http://www.openxdata.org.
 */
package org.openxdata.server.service.impl;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.openxdata.server.dao.SettingDAO;
import org.openxdata.server.serializer.DefaultXformSerializer;
import org.openxdata.server.serializer.XformSerializer;
import org.openxdata.test.BaseContextSensitiveTest;

/**
 * @author Angel
 *
 */
public class SerializationServiceTest extends BaseContextSensitiveTest {

	private SettingDAO settingDAOMock;	
	private SerializationServiceImpl serializationService;
	
	@Before
	public void createMocks() {
		settingDAOMock = mock(SettingDAO.class);
		serializationService = new SerializationServiceImpl(settingDAOMock);
	}
	
	@Test
	public void testGetFormSerializerShouldReturnDefaultSerializer() {
		XformSerializer ser = serializationService.getFormSerializer("XXX");
		
		assertSame(ser.getClass(), DefaultXformSerializer.class);
	}
}
