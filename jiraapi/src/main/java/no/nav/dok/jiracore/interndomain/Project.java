package no.nav.dok.jiracore.interndomain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record Project(
		String self,
		String id,
		String key,
		String name,
		String description,
		List<Component> components,
		List<IssueType> issueTypes) {
}
