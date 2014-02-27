package org.mule.modules.devkitdoc.transformer;

import org.mule.common.query.DefaultOperatorVisitor;
import org.mule.modules.devkitdoc.model.query.DevkitdocQuerySelectorComparisonType;

public class DevkitdocOperatorVisitor extends DefaultOperatorVisitor {
	
	@Override
	public String equalsOperator() {
		return DevkitdocQuerySelectorComparisonType.EQUALS.getRepresentation();
	}

	@Override
	public String greaterOperator() {
		return DevkitdocQuerySelectorComparisonType.GREATER.getRepresentation();
	}

	@Override
	public String greaterOrEqualsOperator() {
		return DevkitdocQuerySelectorComparisonType.GREATER_OR_EQUALS.getRepresentation();
	}

	@Override
	public String lessOperator() {
		return DevkitdocQuerySelectorComparisonType.LESS.getRepresentation();
	}

	@Override
	public String lessOrEqualsOperator() {
		return DevkitdocQuerySelectorComparisonType.LESS_OR_EQUALS.getRepresentation();
	}

	@Override
	public String likeOperator() {
		return "";
	}

	@Override
	public String notEqualsOperator() {
		return DevkitdocQuerySelectorComparisonType.NOT_EQUALS.getRepresentation();
	}
	
}
