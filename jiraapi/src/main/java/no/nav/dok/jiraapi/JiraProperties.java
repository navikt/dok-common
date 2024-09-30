package no.nav.dok.jiraapi;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

/**
 *
 * @param jiraServieUser jira brukernavn og passord
 * @param url  jira url
 */

@Builder
public record JiraProperties(JiraServieUser jiraServieUser, @NotEmpty String url, Proxy proxy) {

	/**
	 * @param username applikasjonceller bruker jira brukernavn
	 * @param password applikasjonen eller brukeren jira passord
	 */
	public record JiraServieUser(@NotEmpty String username, @NotEmpty String password) {

	}

	public record Proxy(String host, @Min(0) int port) {

	}
}
