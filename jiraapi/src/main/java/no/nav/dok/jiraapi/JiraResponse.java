package no.nav.dok.jiraapi;

import lombok.Builder;

/**
 * @param jiraIssueKey   jira sak nøkkel som bruker til å åpne saken eller opprette andre oppgaves
 * @param message
 * @param httpStatusCode
 */

@Builder
public record JiraResponse(
		String jiraIssueKey,
		String message,
		String httpStatusCode) {
}
