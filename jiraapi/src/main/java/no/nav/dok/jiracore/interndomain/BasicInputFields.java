package no.nav.dok.jiracore.interndomain;


import lombok.Builder;

@Builder
public record BasicInputFields(
		Project project,
		String summary,
		IssueType issuetype,
		Reporter reporter,
		String description,
		String[] labels) {

}
