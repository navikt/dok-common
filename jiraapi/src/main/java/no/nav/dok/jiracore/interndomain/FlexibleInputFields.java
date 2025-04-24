package no.nav.dok.jiracore.interndomain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public record FlexibleInputFields(@JsonIgnore Map<String, ?> customProperties) {

	@JsonAnyGetter
	public Map<String, ?> jiraCustomProperties() {
		return customProperties;
	}

}
