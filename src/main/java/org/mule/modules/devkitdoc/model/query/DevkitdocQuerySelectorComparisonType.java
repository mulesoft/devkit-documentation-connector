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
