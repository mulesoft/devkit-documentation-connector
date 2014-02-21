/**
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.modules.devkitdoc.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.type.JavaType;
import org.mule.api.annotations.param.MetaDataKeyParam;
import org.mule.common.metadata.DefaultMetaData;
import org.mule.common.metadata.DefaultMetaDataKey;
import org.mule.common.metadata.DefinedMapMetaDataModel;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataKey;
import org.mule.common.metadata.builder.DefaultMetaDataBuilder;
import org.mule.common.metadata.builder.DynamicObjectBuilder;
import org.mule.common.metadata.builder.PropertyCustomizableMetaDataBuilder;
import org.mule.modules.devkitdoc.exception.DevkitdocConnectorException;
import org.mule.modules.devkitdoc.exception.DevkitdocConnectorSessionException;
import org.mule.modules.devkitdoc.model.DevkitdocMetadata;
import org.mule.modules.devkitdoc.model.DevkitdocServiceDataType;
import org.mule.modules.devkitdoc.transformer.TransformerUtils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

public class DevkitdocClient implements IDevkitdocClientOperations {
	
	private Client jerseyClient;
	private TransformerUtils transformerUtils;
	private String host;
	private String endpoint;
	private String idField;
	
	public DevkitdocClient(String host, String endpoint) {
		jerseyClient = new Client();
		transformerUtils = new TransformerUtils();
		this.host = host;
		this.endpoint = endpoint;
		idField = "_id";
	}
	
	private void checkStatusResponse(ClientResponse response) 
			throws DevkitdocConnectorSessionException, DevkitdocConnectorException {
		
		if (response != null) {
			int status = response.getStatus();
			
			if (status == 401) { // Unauthorized
				throw new DevkitdocConnectorSessionException("Server returned an unauthorized");
			} else if (status >= 400) {
				String errMsg = response.getEntity(String.class);
				throw new DevkitdocConnectorException(status, errMsg);
			}
		}
	}
	
	private UriBuilder getBaseUriBuilder() {
		return UriBuilder.fromPath(host);
	}
	
	private WebResource getBaseWebResource(URI uri) {
		WebResource resource = jerseyClient.resource(uri);
				
		return resource;
	}
	
	private Builder getBuilder(WebResource resource) {
		return resource.type(MediaType.APPLICATION_JSON_TYPE).accept(MediaType.APPLICATION_JSON_TYPE);
	}
	
	private <T> T getResponseMapped(Class<T> type, ClientResponse clientResponse) 
			throws DevkitdocConnectorSessionException, DevkitdocConnectorException {
		// Check if the response code is valid
		checkStatusResponse(clientResponse);
		// Get the response body
		String response = clientResponse.getEntity(String.class);
		// Transform the JSON representation to the specified type
		return transformerUtils.transformJsonToType(response, type);
	}
	
	/*
	 * This operation is used when the type is from a Generic typed class. Ex. List<String> or Map<String, MyObject>
	 * To send the correct type for conversion you have to previously create a JavaType. See TransformerUtils
	 */
	private <T> T getResponseMapped(Class<T> type, JavaType jType, ClientResponse clientResponse) 
			throws DevkitdocConnectorSessionException, DevkitdocConnectorException {
		// Check if the response code is valid
		checkStatusResponse(clientResponse);
		// Get the response body
		String response = clientResponse.getEntity(String.class);
		// Transform the JSON representation to the specified type
		return transformerUtils.transformJsonToType(response, jType);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MetaDataKey> metadataKeys() {
		URI uri = getBaseUriBuilder().path("/{endpoint}/metadatatypes").build(endpoint);
		WebResource resource = getBaseWebResource(uri);
		
		List<String> responseMapped = getResponseMapped(List.class, resource.get(ClientResponse.class));
		List<MetaDataKey> keys = new ArrayList<MetaDataKey>(responseMapped.size());
		
		for (String k : responseMapped) {
			keys.add(new DefaultMetaDataKey(k, k));
		}
		
		return keys;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public MetaData metadata(String type) 
			throws DevkitdocConnectorException {
		URI uri = getBaseUriBuilder().path("/{endpoint}/metadata").build(endpoint);
		WebResource resource = getBaseWebResource(uri);
		
		resource = resource.queryParam("type", type);
		
		// The structure of the JSON is represented as a Map of String, DevkitdocMetadata. Jackson needs a JavaType specified for this purpose		 
		JavaType javaType = transformerUtils.getTypefactory().constructMapType(Map.class, String.class, DevkitdocMetadata.class);
		
		// Get the structure of the object
		Map<String, DevkitdocMetadata> metadataMap = (Map<String, DevkitdocMetadata>) getResponseMapped(Map.class, javaType, getBuilder(resource).get(ClientResponse.class));
		
		// Use the objectBuilder (mule-commons) to build the Studio MetaData representation
		DynamicObjectBuilder<?> dynObj = metadataTransform(type, metadataMap);
		
		// And then create the MetaData object
		DefinedMapMetaDataModel build = dynObj.build();
		
		return new DefaultMetaData(build);
	}
	
	public DynamicObjectBuilder<?> metadataTransform(String type, Map<String, DevkitdocMetadata> metadataMap) throws DevkitdocConnectorException {
		
		DynamicObjectBuilder<?> dynObj = new DefaultMetaDataBuilder().createDynamicObject(type);
		
		DevkitdocMetadata metadata = null;
		for (String key : metadataMap.keySet()) {
			metadata = metadataMap.get(key);
			
			String fieldType = null;
			if (StringUtils.isNotEmpty(metadata.getInstance())) {
				fieldType = metadata.getInstance();
			} else if (metadata.getOptions() != null && StringUtils.isNotEmpty(metadata.getOptions().getType())) {
				fieldType = metadata.getOptions().getType();
			} else {
				throw new DevkitdocConnectorException(400, String.format("Failed to create MetaData. The filed %s of the type %s does not have a type defined", key, type));
			}
			
			PropertyCustomizableMetaDataBuilder<?> simpleField = dynObj.addSimpleField(key, DevkitdocServiceDataType.getMuleTypeFrom(fieldType));
			
			// Configure the abilities available for each filed if DSQL is used
			simpleField
				.isOrderByCapable(false) // TODO: The isOrderByCapable must be checked agains the index property of the metadata
				.isSelectCapable(true)
				.isWhereCapable(true);
		}
		
		return dynObj;
	}

	@Override
	public List<Map<String, Object>> query(String entity, String jsonQuery) 
			throws DevkitdocConnectorSessionException, DevkitdocConnectorException {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> create(@MetaDataKeyParam String entity, Map<String, Object> properties) throws DevkitdocConnectorSessionException,
			DevkitdocConnectorException {
		
		if (properties == null) {
			throw new DevkitdocConnectorException(400, "properties can not be null");
		}
		
		URI uri = getBaseUriBuilder().path("/{endpoint}/{entity}").build(endpoint, entity);
		WebResource resource = getBaseWebResource(uri);
		
		String json = transformerUtils.transformMapToJson(properties);
		
		return getResponseMapped(Map.class, getBuilder(resource).post(ClientResponse.class, json));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> retrieve(@MetaDataKeyParam String entity, Map<String, Object> properties) throws DevkitdocConnectorSessionException,
			DevkitdocConnectorException {
		
		if (properties == null) {
			throw new DevkitdocConnectorException(400, "properties can not be null");
		} else if (!properties.containsKey(idField)) {
			throw new DevkitdocConnectorException(400, "properties does not contain a key");
		}
			
		URI uri = getBaseUriBuilder().path("/{endpoint}/{entity}/{id}").build(endpoint, entity, properties.get(idField));
		WebResource resource = getBaseWebResource(uri);
		
		return getResponseMapped(Map.class, getBuilder(resource).get(ClientResponse.class));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> update(@MetaDataKeyParam String entity, Map<String, Object> properties) throws DevkitdocConnectorSessionException,
			DevkitdocConnectorException {
		
		if (properties == null) {
			throw new DevkitdocConnectorException(400, "properties can not be null");
		} else if (!properties.containsKey(idField)) {
			throw new DevkitdocConnectorException(400, "properties does not contain a key");
		}
		
		URI uri = getBaseUriBuilder().path("/{endpoint}/{entity}/{id}").build(endpoint, entity, properties.get(idField));
		properties.remove(idField);
		WebResource resource = getBaseWebResource(uri);
		
		String json = transformerUtils.transformMapToJson(properties);
		
		return getResponseMapped(Map.class, getBuilder(resource).put(ClientResponse.class, json));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> delete(@MetaDataKeyParam String entity, Map<String, Object> properties) throws DevkitdocConnectorSessionException,
			DevkitdocConnectorException {
		
		if (properties == null) {
			throw new DevkitdocConnectorException(400, "properties can not be null");
		} else if (!properties.containsKey(idField)) {
			throw new DevkitdocConnectorException(400, "properties does not contain a key");
		}
			
		URI uri = getBaseUriBuilder().path("/{endpoint}/{entity}/{id}").build(endpoint, entity, properties.get(idField));
		WebResource resource = getBaseWebResource(uri);
		
		return getResponseMapped(Map.class, getBuilder(resource).delete(ClientResponse.class));
	}	
}
