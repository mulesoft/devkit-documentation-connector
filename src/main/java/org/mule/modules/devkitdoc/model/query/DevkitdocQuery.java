/**
 *
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.modules.devkitdoc.model.query;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import org.apache.cxf.common.util.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.mule.modules.devkitdoc.transformer.DevkitdocTransformerUtils;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({ "query", "limit", "skip", "fields" })
public class DevkitdocQuery {

	@JsonProperty("entity")
	private String entity;	
	@JsonProperty("criteria")
	private Map<String, Object> criteria;
	@JsonProperty("limit")
	private Integer limit;
	@JsonProperty("skip")
	private Integer skip;
	@JsonProperty("fields")	
	private String fields;
	
	public DevkitdocQuery() {
		criteria = new HashMap<String, Object>();
	}
	
	public DevkitdocQuery addCriteria(String field, DevkitdocQuerySelectorComparisonType comparison, Object value) {
		if (StringUtils.isEmpty(field) || comparison == null || value == null) {
			return this;
		}
	
		if (DevkitdocQuerySelectorComparisonType.EQUALS.equals(comparison)) {
			// If the criteria is an equals for the field, the result must be a simple value and not an object.
			// Ex. { "firstname" : "gustavo" }
			criteria.put(field, value);
		} else {
			if (criteria.containsKey(field) && criteria.get(field) instanceof Map) {
				// If there is already a Map for that field it means that is a compose query
				// Ex. { "username" : { "$gt" : "a", "$lt" : "f" } }
				@SuppressWarnings("unchecked")
				Map<Object, Object> m = (HashMap<Object, Object>) criteria.get(field);
				m.put(comparison, value);
			} else {
				// Or is the new criteria for the fields, or there was an equals first, which is incompatible with the rest
				Map<Object, Object> m = new HashMap<Object, Object>(1);
				m.put(comparison, value);
				criteria.put(field, m);
			}
		}		
		
		return this;
	}
	
	public DevkitdocQuery addField(String field) {
		if (StringUtils.isEmpty(field)) {
			return this;
		}
		
		if (StringUtils.isEmpty(fields)) {
			fields = field;
		} else {
			fields = fields + " " + field;
		}
		
		return this;
	}

	@JsonProperty("criteria")
	public Map<String, Object> getCriteria() {
		return criteria;
	}

	@JsonProperty("criteria")
	public DevkitdocQuery setCriteria(Map<String, Object> query) {
		this.criteria = query;
		return this;
	}

	@JsonProperty("limit")
	public Integer getLimit() {
		return limit;
	}

	@JsonProperty("limit")
	public DevkitdocQuery setLimit(Integer limit) {
		this.limit = limit;
		return this;
	}

	@JsonProperty("skip")
	public Integer getSkip() {
		return skip;
	}

	@JsonProperty("skip")
	public DevkitdocQuery setSkip(Integer skip) {
		this.skip = skip;
		return this;
	}

	@JsonProperty("fields")
	public String getFields() {
		return fields;
	}

	@JsonProperty("fields")
	public DevkitdocQuery setFields(String
			fields) {
		this.fields = fields;
		return this;
	}

	@JsonProperty("entity")
	public String getEntity() {
		return entity;
	}

	@JsonProperty("entity")
	public DevkitdocQuery setEntity(String entity) {
		this.entity = entity;
		return this;
	}
	
	@JsonIgnore
	public String toJson() {
		DevkitdocTransformerUtils transformerUtils = new DevkitdocTransformerUtils();
		return transformerUtils.transformObjectToJson(this);
	}
}