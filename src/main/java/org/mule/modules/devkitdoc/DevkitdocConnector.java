/**
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 **/
        
/**
 * This file was automatically generated by the Mule Development Kit
 */
package org.mule.modules.devkitdoc;

import java.util.List;
import java.util.Map;

import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.annotations.Connect;
import org.mule.api.annotations.ConnectionIdentifier;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Disconnect;
import org.mule.api.annotations.InvalidateConnectionOn;
import org.mule.api.annotations.MetaDataKeyRetriever;
import org.mule.api.annotations.MetaDataRetriever;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.Query;
import org.mule.api.annotations.QueryOperator;
import org.mule.api.annotations.QueryTranslator;
import org.mule.api.annotations.ValidateConnection;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.MetaDataKeyParam;
import org.mule.common.metadata.MetaData;
import org.mule.common.metadata.MetaDataKey;
import org.mule.common.query.DsqlQuery;
import org.mule.modules.devkitdoc.client.DevkitdocClient;
import org.mule.modules.devkitdoc.exception.DevkitdocConnectorException;
import org.mule.modules.devkitdoc.exception.DevkitdocConnectorSessionException;
import org.mule.modules.devkitdoc.transformer.DevkitdocQueryVisitor;

/**
 * Devkit Documentation Connector
 * <p>
 * This connector was made to demonstrate some of the vast features of devkit studio in a real case scenario
 * whit a custom service created for this connector
 * <p>
 * The service can be found in: https://github.com/mulesoft/devkit-documentation-service
 *
 * @author MuleSoft, Inc.
 */
@Connector(name="devkitdoc", schemaVersion="1.0", minMuleVersion="3.5", friendlyName="DevkitDoc")
public class DevkitdocConnector
{
    private boolean connected;
    private Object connectionLock;
    private DevkitdocClient clients1;
    
    public DevkitdocConnector() {
    	connected = false;
    	connectionLock = new Object();
    }

    @Connect
    public void connect(@ConnectionKey @Default("http://ec2-50-16-35-4.compute-1.amazonaws.com:8080") String host) throws ConnectionException  {
    	synchronized(connectionLock) {
    		if (!connected) {
    			// Initialize client
    			clients1 = new DevkitdocClient(host, "s1");
    			
    			/* 
    			 * Try to perform an operation to really test that the service is working
    			 * 
    			 * Note: this method can be invoked also from Studio in the Global Configuration with the "Test Connection"
    			 * 		 so we have to hit the service in this method to know if it is really working
    			 */
    			try {
    				metadataKeys();
    			} catch (DevkitdocConnectorException e) {
    				throw new ConnectionException(ConnectionExceptionCode.UNKNOWN, "", "Can not connect to the service", e);
    			}
    			
    			connected = true;
    		}
    	}
    }
    
    @Disconnect
    public void disconnect() {
    	synchronized(connectionLock) {
    		if (connected) {
    			// Release client
    			connected = false;
    		}
    	}
    }
    
    @MetaDataKeyRetriever
    public List<MetaDataKey> metadataKeys() {
    	return clients1.metadataKeys();
    }
    
    @MetaDataRetriever
    public MetaData metadata(MetaDataKey key) {
    	return clients1.metadata(key.getId());
    }
    
    @ValidateConnection
    public boolean isConnected() {
    	synchronized(connectionLock) {
    		return connected;
    	}
    }
    
    @ConnectionIdentifier
    public String connectionIdentifier() {
    	return "devkitdoc-";
    }
    
    @Processor
    @InvalidateConnectionOn(exception = DevkitdocConnectorSessionException.class)
    public Map<String, Object> create(@MetaDataKeyParam String entity, @Default("#[payload]") Map<String, Object> properties)
    	throws DevkitdocConnectorSessionException, DevkitdocConnectorException {
    	
    	return clients1.create(entity, properties);
    }
    
    @Processor
    @InvalidateConnectionOn(exception = DevkitdocConnectorSessionException.class)
    public Map<String, Object> update(@MetaDataKeyParam String entity, @Default("#[payload]") Map<String, Object> properties)
    	throws DevkitdocConnectorSessionException, DevkitdocConnectorException {
    	
    	return clients1.update(entity, properties);
    }
    
    @Processor
    @InvalidateConnectionOn(exception = DevkitdocConnectorSessionException.class)
    public Map<String, Object> retrieve(@MetaDataKeyParam String entity, @Default("#[payload]") Map<String, Object> properties)
    	throws DevkitdocConnectorSessionException, DevkitdocConnectorException {
    	
    	return clients1.retrieve(entity, properties);
    }
    
    @Processor
    @InvalidateConnectionOn(exception = DevkitdocConnectorSessionException.class)
    public Map<String, Object> delete(@MetaDataKeyParam String entity, @Default("#[payload]") Map<String, Object> properties)
    	throws DevkitdocConnectorSessionException, DevkitdocConnectorException {
    	
    	return clients1.delete(entity, properties);
    }
    
    @Processor
    @InvalidateConnectionOn(exception = DevkitdocConnectorSessionException.class)
    public List<Map<String, Object>> query(@Query(disabledOperators = {QueryOperator.OR}, orderBy = false) String query) 
			throws DevkitdocConnectorSessionException, DevkitdocConnectorException {
    	
    	return clients1.query(query);
    }
    
    @QueryTranslator
    public String translateDSQLToNativeQueryLanguage(DsqlQuery dsql) {
    	
    	// Transform the DsqlQuery in a String (Native Query)
    	DevkitdocQueryVisitor devkitdocQueryVisitor = new DevkitdocQueryVisitor();
    	dsql.accept(devkitdocQueryVisitor);
    	return devkitdocQueryVisitor.toString();
    }
}
