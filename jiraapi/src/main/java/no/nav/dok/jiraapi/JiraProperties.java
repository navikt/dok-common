package no.nav.dok.jiraapi;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

/**
 * @param jiraServiceUser jira brukernavn og accessToken
 * @param url             jira url
 */

@Builder
public record JiraProperties(JiraServiceUser jiraServiceUser, @NotEmpty String url) {

	/**
	 * @param username applikasjonens eller brukerens brukernavn
	 * @param accessToken applikasjonens eller brukerens accessToken
	 */
	public record JiraServiceUser(@NotEmpty String username, @NotEmpty String accessToken) {
	}
}
