package no.nav.dok.jiraapi;

import jakarta.validation.constraints.NotEmpty;

/**
 *
 * @param jiraServieUser jira brukernavn og passord
 * @param url  jira url
 */
public record JiraProperties(JiraServieUser jiraServieUser, @NotEmpty String url) {

	/**
	 * @param username applikasjonen eller brukeren brukernavn
	 * @param password applikasjonen eller brukeren jira passord
	 */
	public record JiraServieUser(@NotEmpty String username, @NotEmpty String password) {

	}
}
