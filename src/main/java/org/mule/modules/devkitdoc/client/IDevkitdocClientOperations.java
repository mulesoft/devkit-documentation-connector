/**
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.modules.devkitdoc.client;

import java.util.List;
import java.util.Map;

import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataKey;
import org.mule.modules.devkitdoc.exception.DevkitdocConnectorException;
import org.mule.modules.devkitdoc.exception.DevkitdocConnectorSessionException;

public interface IDevkitdocClientOperations {
	/**
	 * Get all the metadata types (IDs) supported by the host.
	 * <p>
	 * The response is used to call the metadata method.
	 * @return {@link List} of {@link MetaDataKey} with all the IDs
	 */
	public List<MetaDataKey> metadataKeys();
	
	/**
	 * Get all the composition of the metadata for a certain entity (type)
	 * <p>
	 * <b>Note:</b> if it is a composed object Ex.
	 * <p>
	 * <pre>
	 * { "name": { 
	 * 			"first": "gustavo",
	 * 		  	"last: "alberola"
	 * 		}
	 * }
	 * </pre>
	 * It will read as two paths:
	 * <ul>
	 * <li>name.first</li>
	 * <li>name.last</li>
	 * </ul>
	 * 
	 * @param type The ID of the entity to get the metadata from
	 * @return {@link MetaData} with the MetaData structure that Studio will interpret
	 * @throws DevkitdocConnectorException If some error occurred in the service
	 */
	public MetaData metadata(String type) 
			throws DevkitdocConnectorException;
	
	/**
	 * Performs the Query operation through the service.
	 * <p>
	 * This will call internally query(entity, jsonQuery, 20, 0); Meaning get the first 20 elements of the first page.
	 * 
	 * @param entity The ID of the entity
	 * @param jsonQuery The Query to be performed in JSON format
	 * @return {@link List} of {@link Map} with the entities. Each Map will be interpreted in Studio using the type generated from the MetaData
	 * @throws DevkitdocConnectorSessionException If the service responds with a 401 (Unauthorized) it will throw this exception. <b>Note:</b> 403 is not considered as SessionException
	 * @throws DevkitdocConnectorException If some error occurred in the service
	 */
	public List<Map<String, Object>> query(String entity, String jsonQuery) 
			throws DevkitdocConnectorSessionException, DevkitdocConnectorException;
	
	
	/**
	 * Persists the entity in the service passed as parameter
	 * 
	 * @param entity The ID of the entity
	 * @param properties The {@link Map} with the key/values of the object data
	 * @return {@link Map} with the persisted entity. The Map will be interpreted in Studio using the type generated from the MetaData
	 * @throws DevkitdocConnectorSessionException If the service responds with a 401 (Unauthorized) it will throw this exception. <b>Note:</b> 403 is not considered as SessionException
	 * @throws DevkitdocConnectorException If some error occurred in the service
	 */
	public Map<String, Object> create(String entity, Map<String, Object> properties) 
			throws DevkitdocConnectorSessionException, DevkitdocConnectorException;
	
	/**
	 * Retrieves an already persisted entity from the service.
	 * 
	 * @param entity The ID of the entity
	 * @param properties The ID value of the entity. For easy usage it can receive the full MAP, but it will only use the ID field
	 * @return {@link Map} with the retrieved entity. The Map will be interpreted in Studio using the type generated from the MetaData
	 */
	public Map<String, Object> retrieve(String entity, Map<String, Object> properties) 
			throws DevkitdocConnectorSessionException, DevkitdocConnectorException;
	
	/**
	 * Update the entity in the service. The entity must have also the ID fields setted
	 * <p>
	 * <b>Important:</b> this operation is an <b>UPDATE</b> not an <b>UPSERT</b>. Meaning that if the entity does not exists in the service it will fail
	 * <b>Note:</b> it will only modify the fields passed as parameters.
	 * 
	 * @param entity The ID of the entity
	 * @param properties The modified values to be modified plus (+) IDs of the entity
	 * @return {@link Map} with the fully modified entity. The Map will be interpreted in Studio using the type generated from the MetaData
	 * @throws DevkitdocConnectorSessionException If the service responds with a 401 (Unauthorized) it will throw this exception. <b>Note:</b> 403 is not considered as SessionException
	 * @throws DevkitdocConnectorException If some error occurred in the service
	 */
	public Map<String, Object> update(String entity, Map<String, Object> properties) 
			throws DevkitdocConnectorSessionException, DevkitdocConnectorException;
	
	/**
	 * Deletes an entity from the service.
	 * 
	 * @param entity The ID of the entity
	 * @param properties The ID value of the entity. For easy usage it can receive the full MAP, but it will only use the ID field
	 * @return {@link Map} with the deleted entity. The Map will be interpreted in Studio using the type generated from the MetaData
	 * @throws DevkitdocConnectorSessionException If the service responds with a 401 (Unauthorized) it will throw this exception. <b>Note:</b> 403 is not considered as SessionException
	 * @throws DevkitdocConnectorException If some error occurred in the service
	 */
	public Map<String, Object> delete(String entity, Map<String, Object> properties) 
			throws DevkitdocConnectorSessionException, DevkitdocConnectorException;
}
