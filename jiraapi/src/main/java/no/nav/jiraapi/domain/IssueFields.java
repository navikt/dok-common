package no.nav.dok.jiraapi.domain;

public record IssueFields(Project project,
		String summary,
		IssueType issuetype,
		Reporter creator,
		Reporter assignee,
		Reporter reporter,
		Priority priority,
		String description,
		String[] labels,
		Status status) {
}
