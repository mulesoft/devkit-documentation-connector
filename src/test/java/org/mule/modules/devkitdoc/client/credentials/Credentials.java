/**
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.modules.devkitdoc.client.credentials;

public class Credentials {
	
	private String host;
	private String clientPath;
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getClientPath() {
		return clientPath;
	}
	
	public void setClientPath(String clientPath) {
		this.clientPath = clientPath;
	}
}
