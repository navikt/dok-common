package no.nav.dok.jiracore.interndomain;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record JiraTransition(Transition transition) {
	public record Transition(@NotEmpty String id) {
	}
}
