package no.nav.dok.jiraapi;

/**
 * @param jiraIssueKey   jira sak nøkkel som bruker til å åpne saken eller opprette andre oppgaves
 * @param message
 * @param httpStatusCode
 */

public record JiraResponse(
		String jiraIssueKey,
		String message,
		String httpStatusCode) {
}
