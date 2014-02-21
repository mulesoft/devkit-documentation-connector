/**
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.modules.devkitdoc.exception;

public class DevkitdocConnectorSessionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DevkitdocConnectorSessionException() {
		super();
	}

	public DevkitdocConnectorSessionException(String message, Throwable cause) {
		super(message, cause);
	}

	public DevkitdocConnectorSessionException(String message) {
		super(message);
	}

	public DevkitdocConnectorSessionException(Throwable cause) {
		super(cause);
	}
}
