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

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mule.api.ConnectionException;
import org.mule.modules.devkitdoc.DevkitdocConnector;
import org.mule.modules.devkitdoc.client.credentials.Credentials;
import org.mule.modules.devkitdoc.client.credentials.CredentialsUtils;
import org.mule.modules.devkitdoc.model.query.DevkitdocQuery;
import org.mule.modules.devkitdoc.model.query.DevkitdocQuerySelectorComparisonType;

public class DevkitdocQueryTest {
	
	static final private Log LOG = LogFactory.getLog(DevkitdocQueryTest.class); 
	
	static private Credentials credentials;
	static private List<Map<String, Object>> listOfProperties;
	static private String entity;
	static private String idField;
	static private DevkitdocConnector connector;
	
	@BeforeClass
	static public void initialize() throws IllegalArgumentException, IOException, IllegalAccessException, ConnectionException {
		credentials = new CredentialsUtils().readCredentials();
		
		listOfProperties = new LinkedList<Map<String, Object>>();
		connector = new DevkitdocConnector();
		
		connector.connect(credentials.getHost());
		
		entity = "employee";
		idField = "_id";
		
		// TODO: Query by email and remove if exists. Email is a unique field, so there can not be more than one entity with the same email address
		
		// Required values
		Map<String, Object> properties = new HashMap<String, Object>(5); 
		
		properties.put("firstname", "gustavo");
		properties.put("lastname", "alberola");
		properties.put("email", "gustavo.alberola@mulesoft.com");
		properties.put("company", "MuleSoft");
		properties.put("job", "Cloud Connectors Dev");
				
		// Create
		Map<String, Object> resultProperties = connector.create(entity, properties);
		listOfProperties.add(resultProperties);
		
		properties.put("email", "gustavo.alberola2@mulesoft.com");
		resultProperties = connector.create(entity, properties);
		listOfProperties.add(resultProperties);
		
		properties.put("email", "gustavo.alberola3@mulesoft.com");
		resultProperties = connector.create(entity, properties);
		listOfProperties.add(resultProperties);
	}
	
	@AfterClass
	static public void dispose() {
		// If the properties is loaded
		if (listOfProperties != null) {
			for (Map<String, Object> properties : listOfProperties) {
				if (properties != null && properties.size() > 0) {
					try {
						connector.delete(entity, properties);
					} catch (Throwable e) {}
				}
			}
		}
	}
	
	@Test
	public void queryAllTest() {
		// Query All ================================================================================================
		DevkitdocQuery queryModel = new DevkitdocQuery();
		
		queryModel.setEntity(entity);
		
		String jsonQuery = queryModel.toJson();
		
		LOG.debug("Query All: " + jsonQuery);
		
		List<Map<String,Object>> queryResultList = connector.query(jsonQuery);
		
		assertNotNull(queryResultList);
		assertTrue(queryResultList.size() >= 3); // 3 is the number of elements that we created in the @Before
	}
	
	@Test
	public void queryByIdTest() {
		// Query first id ===========================================================================================
		DevkitdocQuery queryModel = new DevkitdocQuery();
		
		queryModel
			.setEntity(entity)
			.addCriteria(idField, DevkitdocQuerySelectorComparisonType.EQUALS, listOfProperties.get(0).get(idField));

		String jsonQuery = queryModel.toJson();
		
		LOG.debug("Query first id: " + jsonQuery);
		
		List<Map<String,Object>> queryResultList = connector.query(jsonQuery);
		
		assertNotNull(queryResultList);
		assertEquals(1, queryResultList.size());
		assertEquals(listOfProperties.get(0).get(idField), queryResultList.get(0).get(idField));
	}
	
	@Test
	public void queryNotEquals() {
		// Query not equals =====================================================================================
		DevkitdocQuery queryModel = new DevkitdocQuery();
		
		// Tell the criteria to skip the two firsts items created. It should match the index 3
		queryModel
			.setEntity(entity)
			.addCriteria("email", DevkitdocQuerySelectorComparisonType.NOT_EQUALS, listOfProperties.get(0).get("email"));

		String jsonQuery = queryModel.toJson();
		
		LOG.debug("Query not equals: " + jsonQuery);
		
		List<Map<String,Object>> queryResultList = connector.query(jsonQuery);
		
		assertNotNull(queryResultList);
		assertEquals(2, queryResultList.size());
		// We skipped the first element so the second and the third must match
		assertEquals(listOfProperties.get(1).get(idField), queryResultList.get(0).get(idField));
		assertEquals(listOfProperties.get(2).get(idField), queryResultList.get(1).get(idField));
	}
	
	public void queryByIdAndFilterFieldsTest() {
		// Query By Id And filter fields =====================================================================================
		DevkitdocQuery queryModel = new DevkitdocQuery();
		
		queryModel
			.setEntity(entity)
			.addCriteria(idField, DevkitdocQuerySelectorComparisonType.EQUALS, listOfProperties.get(0).get(idField))
			.addField("firstname")
			.addField("lastname");

		String jsonQuery = queryModel.toJson();
		
		LOG.debug("Query id with filter fields: " + jsonQuery);
		
		List<Map<String,Object>> queryResultList = connector.query(jsonQuery);
		
		assertNotNull(queryResultList);
		// _id field is always added
		assertEquals(3, queryResultList.get(0).keySet().size());
		
		Set<String> keySet = queryResultList.get(0).keySet();
		
		assertTrue(keySet.contains("firstname"));
		assertTrue(keySet.contains("lastname"));
		assertTrue(keySet.contains("_id"));
	}
	
	@Test
	public void queryLimitTest() {
		// Query limit =====================================================================================
		DevkitdocQuery queryModel = new DevkitdocQuery();
		
		queryModel
			.setEntity(entity)
			.setLimit(1);

		String jsonQuery = queryModel.toJson();
		
		LOG.debug("Query limit: " + jsonQuery);
		
		List<Map<String,Object>> queryResultList = connector.query(jsonQuery);
		
		assertNotNull(queryResultList);
		assertEquals(1, queryResultList.size());
		assertEquals(listOfProperties.get(0).get(idField), queryResultList.get(0).get(idField));
	}
	
	@Test
	public void queryLimitAndOffsetTest() {		
		// Query limit and offset ==========================================================================
		DevkitdocQuery queryModel = new DevkitdocQuery();
		
		queryModel
			.setEntity(entity)
			.setLimit(1)
			.setSkip(1);

		String jsonQuery = queryModel.toJson();
		
		LOG.debug("Query limit and offset: " + jsonQuery);
		
		List<Map<String,Object>> queryResultList = connector.query(jsonQuery);
		
		assertNotNull(queryResultList);
		assertEquals(1, queryResultList.size());
		assertEquals(listOfProperties.get(1).get(idField), queryResultList.get(0).get(idField));
	}
}
