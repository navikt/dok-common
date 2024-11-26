package no.nav.dok.jiraapi;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

/**
 * @param jiraServiceUser jira brukernavn og passord
 * @param url             jira url
 */

@Builder
public record JiraProperties(JiraServiceUser jiraServiceUser, @NotEmpty String url) {

	/**
	 * @param username applikasjonceller bruker jira brukernavn
	 * @param password applikasjonen eller brukeren jira passord
	 */
	public record JiraServiceUser(@NotEmpty String username, @NotEmpty String password) {
	}
}
