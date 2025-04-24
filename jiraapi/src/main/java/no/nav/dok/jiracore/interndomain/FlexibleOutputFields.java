package no.nav.dok.jiracore.interndomain;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public class FlexibleOutputFields {
	@JsonIgnore
	private final Map<String, Object> customProperties;

	public FlexibleOutputFields() {
		this.customProperties = new HashMap<>();
	}

	@JsonAnySetter
	public void setJiraCustomProperties(String key, Object value) {
		customProperties.put(key, value);
	}

	@JsonIgnore
	public Map<String, Object> getJiraCustomProperties() {
		return customProperties;
	}
}
