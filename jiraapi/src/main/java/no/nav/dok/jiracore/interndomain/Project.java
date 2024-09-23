package no.nav.dok.jiracore.interndomain;

import lombok.Builder;

import java.util.List;

@Builder
public record Project(
		String expand,
		String self,
		String id,
		String key,
		String description,
		String name,
		String url,
		List<IssueType> issueTypes) {
}
