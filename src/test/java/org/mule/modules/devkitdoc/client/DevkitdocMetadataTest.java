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
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mule.api.ConnectionException;
import org.mule.common.metadata.DefaultDefinedMapMetaDataModel;
import org.mule.common.metadata.DefaultMetaDataField;
import org.mule.common.metadata.DefaultMetaDataKey;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataField;
import org.mule.common.metadata.MetaDataKey;
import org.mule.modules.devkitdoc.DevkitdocConnector;
import org.mule.modules.devkitdoc.client.credentials.Credentials;
import org.mule.modules.devkitdoc.client.credentials.CredentialsUtils;

public class DevkitdocMetadataTest {
	
	static private DevkitdocConnector connector;
	static private Credentials credentials;
	static private String entity;
	
	
	@BeforeClass
	static public void staticInitialize() throws IllegalArgumentException, IOException, IllegalAccessException, ConnectionException {
		credentials = new CredentialsUtils().readCredentials();
		connector = new DevkitdocConnector();
		connector.connect(credentials.getHost());
		
		entity = "employee";
	}
	
	@Test
	public void metadataKeysTest() {
		List<MetaDataKey> metadataKeys = connector.metadataKeys();
		
		assertNotNull(metadataKeys);
		assertTrue(metadataKeys.size() > 0);
		assertEquals("employee", metadataKeys.get(0).getId());
	}
	
	@Test
	public void metadataTest() {
		MetaData metadata = connector.metadata(new DefaultMetaDataKey(entity, entity));
		
		assertNotNull(metadata);
		assertEquals("MAP", metadata.getPayload().getDataType().toString());
		
		DefaultDefinedMapMetaDataModel payload = (DefaultDefinedMapMetaDataModel) metadata.getPayload();
		List<MetaDataField> fields = payload.getFields();
		
		assertNotNull(fields);
		
		DefaultMetaDataField metaDataField = (DefaultMetaDataField) fields.get(0);
		
		assertEquals("firstname", metaDataField.getName());
		assertEquals("STRING", metaDataField.getMetaDataModel().getDataType().toString());
	}
}
