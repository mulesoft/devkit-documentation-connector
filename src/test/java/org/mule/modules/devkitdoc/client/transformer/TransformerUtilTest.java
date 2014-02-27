/**
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.modules.devkitdoc.client.transformer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mule.modules.devkitdoc.transformer.DevkitdocTransformerUtils;

public class TransformerUtilTest {
	
	private DevkitdocTransformerUtils transformerUtils;
	
	@Before
	public void initialize() {
		transformerUtils = new DevkitdocTransformerUtils();
	}
	
	@SuppressWarnings("unchecked")
	@Test	
	public void transformJsonToTypeTest() {
		String json = "{\"names\": {\"first\":\"gustavo\",\"last\":\"alberola\"}, \"age\": 27}";
		
		Map<String, Object> map = (Map<String, Object>) transformerUtils.transformJsonToType(json, Map.class);
		
		assertNotNull(map);
		assertEquals(2, map.keySet().size());
		assertEquals(27, map.get("age"));
		assertTrue(map.get("names") instanceof Map);
		
		Map<String, Object> names = (Map<String, Object>) map.get("names");
		
		assertEquals("gustavo", names.get("first"));
		assertEquals("alberola", names.get("last"));
	}
}
