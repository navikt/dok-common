package no.nav.dok.jiraapi.domain;

import jakarta.validation.constraints.NotEmpty;

public record JiraTransition(Transition transition) {
	public record Transition(@NotEmpty String id) {
	}
}
