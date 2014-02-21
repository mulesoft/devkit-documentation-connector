/**
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.modules.devkitdoc.exception;

public class DevkitdocConnectorException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private int statusCode;

	public DevkitdocConnectorException() {
		super();
	}

	public DevkitdocConnectorException(int statusCode, String message, Throwable cause) {
		super(String.format("Status[%d]: %s", statusCode, message), cause);
		this.statusCode = statusCode;
	}
	
	public DevkitdocConnectorException(int statusCode, String message) {
		super(String.format("Status[%d]: %s", statusCode, message));
		this.statusCode = statusCode;
	}
	
	public DevkitdocConnectorException(int statusCode, Throwable cause) {
		super(cause);
		this.statusCode = statusCode;
	}

	public int getStatusCode() {
		return statusCode;
	}
}
