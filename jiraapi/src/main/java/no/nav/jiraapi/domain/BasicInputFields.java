package no.nav.dok.jiraapi.domain;

import lombok.Builder;

@Builder
public record BasicInputFields(Project project,
		String summary,
		IssueType issuetype,
		Reporter reporter,
		String description,
		String[] labels) {
}
