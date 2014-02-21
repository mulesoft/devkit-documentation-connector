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
import org.mule.common.metadata.DefaultDefinedMapMetaDataModel;
import org.mule.common.metadata.DefaultMetaDataField;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataField;
import org.mule.common.metadata.MetaDataKey;
import org.mule.modules.devkitdoc.client.credentials.Credentials;
import org.mule.modules.devkitdoc.client.credentials.CredentialsUtils;

public class DevkitdocClientTest {
	
	static private DevkitdocClient clients1;
	static private Credentials credentials;
	
	@BeforeClass
	static public void staticInitialize() throws IllegalArgumentException, IOException, IllegalAccessException {
		credentials = new CredentialsUtils().readCredentials();
		
		clients1 = new DevkitdocClient(credentials.getHost(), credentials.getClientPath());
	}
	
	@Test
	public void metadataKeysTest() {
		List<MetaDataKey> metadataKeys = clients1.metadataKeys();
		
		assertNotNull(metadataKeys);
		assertTrue(metadataKeys.size() > 0);
		assertEquals("employee", metadataKeys.get(0).getId());
	}
	
	@Test
	public void metadataTest() {
		MetaData metadata = clients1.metadata("employee");
		
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
