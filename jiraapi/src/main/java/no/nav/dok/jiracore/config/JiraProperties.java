package no.nav.dok.jiracore.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("jira")
public record JiraProperties(@NotNull ServiceUser serviceUser, @NotNull JiraEndpoint jiraEndpoint) {

	public record ServiceUser (
			@NotEmpty String username,
			@NotEmpty String password) {
	}

	public record JiraEndpoint (String url) {
	}
}
