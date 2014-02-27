/**
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.modules.devkitdoc.transformer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.mule.modules.devkitdoc.exception.DevkitdocConnectorException;

public class DevkitdocTransformerUtils {

	private ObjectMapper objectMapper;
	
	public DevkitdocTransformerUtils() {
		objectMapper = new ObjectMapper();
	}
	
	public DevkitdocTransformerUtils(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	public TypeFactory getTypefactory() {		
		return objectMapper.getTypeFactory();
	}
	
	public <T> T transformJsonToType(String json, Class<T> type) throws DevkitdocConnectorException {
		try {
			return objectMapper.readValue(json, type);
		} catch (JsonParseException e) {
			throw new DevkitdocConnectorException(400, e);
		} catch (JsonMappingException e) {
			throw new DevkitdocConnectorException(400, e);
		} catch (IOException e) {
			throw new DevkitdocConnectorException(400, e);
		}
	}
	
	public <T> T transformJsonToType(String json, JavaType type) throws DevkitdocConnectorException {
		try {
			return objectMapper.readValue(json, type);
		} catch (JsonParseException e) {
			throw new DevkitdocConnectorException(400, e);
		} catch (JsonMappingException e) {
			throw new DevkitdocConnectorException(400, e);
		} catch (IOException e) {
			throw new DevkitdocConnectorException(400, e);
		}
	}
	
	public String transformObjectToJson(Object value) throws DevkitdocConnectorException {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (JsonGenerationException e) {
			throw new DevkitdocConnectorException(400, e);
		} catch (JsonMappingException e) {
			throw new DevkitdocConnectorException(400, e);
		} catch (IOException e) {
			throw new DevkitdocConnectorException(400, e);
		}
	}
	
	public <T> T transformMapToType(Map<?, ?> map, Class<T> type) throws DevkitdocConnectorException {
		try {
			return objectMapper.convertValue(map, type);
		} catch (IllegalArgumentException e) {
			throw new DevkitdocConnectorException(400, e);
		}
	}
	
	public String transformMapToJson(Map<String, Object> map) throws DevkitdocConnectorException  {
		StringWriter sw = new StringWriter();
		
		try {
			objectMapper.writeValue(sw, map);
			return sw.toString();
		} catch (JsonGenerationException e) {
			throw new DevkitdocConnectorException(400, e);
		} catch (JsonMappingException e) {
			throw new DevkitdocConnectorException(400, e);
		} catch (IOException e) {
			throw new DevkitdocConnectorException(400, e);
		}
	}
}
