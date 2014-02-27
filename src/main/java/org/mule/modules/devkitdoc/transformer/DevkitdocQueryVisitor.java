package org.mule.modules.devkitdoc.transformer;

import java.util.List;

import org.mule.common.query.DefaultQueryVisitor;
import org.mule.common.query.Field;
import org.mule.common.query.Type;
import org.mule.common.query.expression.Direction;
import org.mule.common.query.expression.OperatorVisitor;
import org.mule.common.query.expression.Value;
import org.mule.modules.devkitdoc.model.query.DevkitdocQuery;
import org.mule.modules.devkitdoc.model.query.DevkitdocQuerySelectorComparisonType;

public class DevkitdocQueryVisitor extends DefaultQueryVisitor {
	
	private DevkitdocQuery query;
		
	public DevkitdocQueryVisitor() {
		super();
		query = new DevkitdocQuery();
	}

	@Override
	public OperatorVisitor operatorVisitor() {
		return new DevkitdocOperatorVisitor();
	}

	@Override
	public void visitAnd() {
		// Dummy method
	}

	@Override
	public void visitBeginExpression() {
		// Dummy method
	}

	@Override
	public void visitComparison(String operator, Field field, Value<?> value) {
		DevkitdocQuerySelectorComparisonType devkitdocOperator = DevkitdocQuerySelectorComparisonType.getFrom(operator);
		
		query.addCriteria(field.getName(), devkitdocOperator, (value != null ? value.getValue() : null));
	}

	@Override
	public void visitEndPrecedence() {
		// Dummy method
	}

	@Override
	public void visitFields(List<Field> fields) {
		for (Field field : fields) {
			query.addField(field.getName());
		}
	}

	@Override
	public void visitInitPrecedence() {
		// Dummy method
	}

	@Override
	public void visitLimit(int limit) {
		query.setLimit(limit);
	}

	@Override
	public void visitOR() {
		// Dummy method
	}

	@Override
	public void visitOffset(int offset) {
		query.setSkip(offset);
	}

	@Override
	public void visitOrderByFields(List<Field> orderByFields, Direction direction) {		
		// Dummy method
	}

	@Override
	public void visitTypes(List<Type> types) {
		// This will be only one all the time. If there is more than one (the user edited the query by hand) can make the query fail
		for (Type type : types) {
			query.setEntity(type.getName());
		}
	}

	@Override
	public String toString() {
		return query.toJson();
	}
}
