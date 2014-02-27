/**
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.modules.devkitdoc.model;

import org.mule.common.metadata.datatype.DataType;

public enum DevkitdocServiceDataType {
	STRING,
	NUMBER,
	DATE,
	BOOLEAN,
	OBJECTID;
	
	static public DevkitdocServiceDataType getTypeFrom(String type) {
		if (type == null || type.isEmpty()) return null;
		
		for (DevkitdocServiceDataType dst : DevkitdocServiceDataType.values()) {
			if(dst.toString().equalsIgnoreCase(type)) {
				return dst;
			}
		}
		
		return null;
	}
	
	static public DataType getMuleTypeFrom(DevkitdocServiceDataType type) {
		switch (type) {
			case STRING:
				return DataType.STRING;
			case BOOLEAN:
				return DataType.BOOLEAN;
			case DATE:
				return DataType.DATE_TIME;
			case NUMBER:
				return DataType.DECIMAL;
			case OBJECTID:
				return DataType.STRING;
			default:
				return null;
		}
	}
	
	static public DataType getMuleTypeFrom(String type) {
		return getMuleTypeFrom(getTypeFrom(type));
	}
}
