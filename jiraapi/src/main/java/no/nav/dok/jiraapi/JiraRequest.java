package no.nav.dok.jiraapi;

import java.io.File;
import java.util.List;

/**
 * @param reporterName   applikasjonen eller personnavn som opprettes jira-saken.
 * @param description    detailert jira-saken beskrivelse
 * @param summary        sammendrag eller tittle for jira-saken
 * @param labels
 * @param file           filer som kan legge til jira-saken
 * @param jiraServieUser inneholder jira url, brukernavn og passord
 */

public record JiraRequest(
		String reporterName,
		String description,
		String summary,
		List<String> labels,
		File file,
		JiraServieUser jiraServieUser) {

	/**
	 * @param username applikasjonen eller brukeren brukernavn
	 * @param password applikasjonen eller brukeren jira passord
	 * @param url      Jira url
	 */
	public record JiraServieUser(String username, String password, String url) {

	}
}
