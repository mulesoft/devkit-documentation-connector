/**
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.modules.devkitdoc.model.query;

import org.apache.commons.lang.StringUtils;


public enum DevkitdocQuerySelectorComparisonType {
	GREATER("$gt"),
	GREATER_OR_EQUALS("$gte"),
	IN("$in"),
	LESS("$lt"),
	LESS_OR_EQUALS("$lte"),
	NOT_EQUALS("$ne"),
	EQUALS("$eq");
	
	private String representation;
	
	private DevkitdocQuerySelectorComparisonType(String representation) {
		this.representation = representation;
	}
	
	static public DevkitdocQuerySelectorComparisonType getFrom(String representation) {
		if (StringUtils.isNotEmpty(representation)) {
			for (DevkitdocQuerySelectorComparisonType key : DevkitdocQuerySelectorComparisonType.values()) {
				if (key.representation.equals(representation)) {
					return key;
				}
			}
		}
		
		return null;
	}
	
	public String getRepresentation() {
		return representation;
	}

	@Override
	public String toString() {
		return representation;
	}
	
}
