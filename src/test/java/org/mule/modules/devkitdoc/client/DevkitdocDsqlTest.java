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
import org.mule.common.query.DsqlQuery;
import org.mule.common.query.dsql.parser.MuleDsqlParser;
import org.mule.modules.devkitdoc.DevkitdocConnector;
import org.mule.modules.devkitdoc.client.credentials.Credentials;
import org.mule.modules.devkitdoc.client.credentials.CredentialsUtils;
import org.mule.modules.devkitdoc.model.query.DevkitdocQuery;
import org.mule.modules.devkitdoc.model.query.DevkitdocQuerySelectorComparisonType;

public class DevkitdocDsqlTest {

	static final private Log LOG = LogFactory.getLog(DevkitdocDsqlTest.class); 
	
	static private Credentials credentials;
	static private List<Map<String, Object>> listOfProperties;
	static private String entity;
	static private String idField;
	static private DevkitdocConnector connector;
	static private MuleDsqlParser muleDsqlParser;
	
	@BeforeClass
	static public void initialize() throws IllegalArgumentException, IOException, IllegalAccessException, ConnectionException {
		credentials = new CredentialsUtils().readCredentials();
		
		listOfProperties = new LinkedList<Map<String, Object>>();
		connector = new DevkitdocConnector();
		
		connector.connect(credentials.getHost(), "bla", "ble");
		
		muleDsqlParser = new MuleDsqlParser();
		
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
		String dsqlStringQuery = String.format("select * from %s", entity);
		DsqlQuery dsqlQuery = muleDsqlParser.parse(dsqlStringQuery);
		String jsonQuery = connector.translateDSQLToNativeQueryLanguage(dsqlQuery);
		
		LOG.debug("Query All: " + jsonQuery);
		
		List<Map<String,Object>> queryResultList = connector.query(jsonQuery);
		
		assertNotNull(queryResultList);
		assertTrue(queryResultList.size() >= 3); // 3 is the number of elements that we created in the @Before
	}
	
	@Test
	public void queryByIdTest() {
		// Query first id ===========================================================================================
		String dsqlStringQuery = String.format("select * from %s where %s = '%s'", entity, idField, listOfProperties.get(0).get(idField));
		DsqlQuery dsqlQuery = muleDsqlParser.parse(dsqlStringQuery);
		String jsonQuery = connector.translateDSQLToNativeQueryLanguage(dsqlQuery);
		
		LOG.debug("Query first id: " + jsonQuery);
		
		List<Map<String,Object>> queryResultList = connector.query(jsonQuery);
		
		assertNotNull(queryResultList);
		assertEquals(1, queryResultList.size());
		assertEquals(listOfProperties.get(0).get(idField), queryResultList.get(0).get(idField));
	}
	
	@Test
	public void queryNotEquals() {
		// Query not equals =====================================================================================
		String dsqlStringQuery = String.format("select * from %s where email <> '%s'", entity, listOfProperties.get(0).get("email"));
		DsqlQuery dsqlQuery = muleDsqlParser.parse(dsqlStringQuery);
		String jsonQuery = connector.translateDSQLToNativeQueryLanguage(dsqlQuery);
		
		
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
		String dsqlStringQuery = String.format("select firstname,lastname from %s where %s <> '%s'", entity, idField, listOfProperties.get(0).get(idField));
		DsqlQuery dsqlQuery = muleDsqlParser.parse(dsqlStringQuery);
		String jsonQuery = connector.translateDSQLToNativeQueryLanguage(dsqlQuery);

		
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
		String dsqlStringQuery = String.format("select * from %s limit 1", entity);
		DsqlQuery dsqlQuery = muleDsqlParser.parse(dsqlStringQuery);
		String jsonQuery = connector.translateDSQLToNativeQueryLanguage(dsqlQuery);
		
		LOG.debug("Query limit: " + jsonQuery);
		
		List<Map<String,Object>> queryResultList = connector.query(jsonQuery);
		
		assertNotNull(queryResultList);
		assertEquals(1, queryResultList.size());
		assertEquals(listOfProperties.get(0).get(idField), queryResultList.get(0).get(idField));
	}
	
	@Test
	public void queryLimitAndOffsetTest() {		
		// Query limit and offset ==========================================================================
		String dsqlStringQuery = String.format("select * from %s limit 1 offset 1", entity);
		DsqlQuery dsqlQuery = muleDsqlParser.parse(dsqlStringQuery);
		String jsonQuery = connector.translateDSQLToNativeQueryLanguage(dsqlQuery);
		
		LOG.debug("Query limit and offset: " + jsonQuery);
		
		List<Map<String,Object>> queryResultList = connector.query(jsonQuery);
		
		assertNotNull(queryResultList);
		assertEquals(1, queryResultList.size());
		assertEquals(listOfProperties.get(1).get(idField), queryResultList.get(0).get(idField));
	}
}
