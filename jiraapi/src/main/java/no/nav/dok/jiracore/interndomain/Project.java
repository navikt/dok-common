package no.nav.dok.jiracore.interndomain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record Project(
		String expand,
		String self,
		String id,
		String key,
		String description,
		String name,
		String url,
		List<Component> components,
		List<IssueType> issueTypes,
		List<Version> versions) {
}
