package no.nav.dok.jiracore.interndomain;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;

import java.util.Map;

@Builder
public record BasicInputFields(
		Project project,
		String summary,
		IssueType issuetype,
		String description,
		String[] labels,
		@JsonIgnore Map<String, ?> customProperties
) {

	@JsonAnyGetter
	public Map<String, ?> jiraCustomProperties() {
		return customProperties;
	}
}
