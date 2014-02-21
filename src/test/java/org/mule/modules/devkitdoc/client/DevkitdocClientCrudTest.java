/**
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.modules.devkitdoc.client;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.modules.devkitdoc.client.credentials.Credentials;
import org.mule.modules.devkitdoc.client.credentials.CredentialsUtils;
import org.mule.modules.devkitdoc.exception.DevkitdocConnectorException;

public class DevkitdocClientCrudTest {
	
	private DevkitdocClient clients1;
	private Credentials credentials;
	private Map<String, Object> properties;
	private String entity;
	private String idField;
	
	@Before
	public void initialize() throws IllegalArgumentException, IOException, IllegalAccessException {
		credentials = new CredentialsUtils().readCredentials();
		clients1 = new DevkitdocClient(credentials.getHost(), credentials.getClientPath());
		
		entity = "employee";
		idField = "_id";
	}
	
	@After
	public void dispose() {
		// If the properties is loaded
		if (properties != null && properties.size() > 0) {
			try {
				clients1.delete(entity, properties);
			} catch (Throwable e) {}
		}
	}
	
	@Test
	public void crudTest() {
		// Required values =======================================================================================
		Map<String, Object> properties = new HashMap<String, Object>(5); 
		
		properties.put("firstname", "gustavo");
		properties.put("lastname", "alberola");
		properties.put("email", "gustavo.alberola@mulesoft.com");
		properties.put("company", "MuleSoft");
		properties.put("job", "Cloud Connectors Dev");
		
		// Create ================================================================================================
		properties = clients1.create(entity, properties);
		
		assertNotNull(properties);
		assertTrue(properties.size() > 0);
		assertTrue(properties.containsKey(idField));
		
		this.properties = properties; // Save the properties for a delete in case of fail in next operations
		
		// Update ================================================================================================
		Map<String, Object> modifyProperties = new HashMap<String, Object>();
		modifyProperties.put(idField, properties.get(idField));
		modifyProperties.put("firstname", "gustavious III");
		
		properties = clients1.update(entity, modifyProperties);
		
		assertNotNull(properties);
		assertTrue(properties.size() > 0);
		assertTrue(properties.containsKey(idField));
		
		// Retrieve ==============================================================================================
		properties = clients1.retrieve(entity, properties);
		
		assertNotNull(properties);
		assertTrue(properties.size() > 0);
		assertTrue(properties.containsKey(idField));
		assertEquals(modifyProperties.get("firstname"), properties.get("firstname"));
		assertEquals("alberola", properties.get("lastname"));
		
		// Delete ================================================================================================
		properties = clients1.delete(entity, properties);
		
		assertNotNull(properties);
		assertTrue(properties.size() > 0);
		assertTrue(properties.containsKey(idField));
		
		// Retrieve should fail ==================================================================================
		try {
			clients1.retrieve(entity, properties);
			fail("Retrieve of an unexistent entity should throw an exception");
		} catch (DevkitdocConnectorException e) {
			assertEquals(404, e.getStatusCode());
		}
	}
}
