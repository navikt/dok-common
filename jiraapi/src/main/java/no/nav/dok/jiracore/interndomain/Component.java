package no.nav.dok.jiracore.interndomain;

import lombok.Builder;

@Builder
public record Component(
		String self,
		String id,
		String name,
		Boolean isAssigneeTypeValid) {
}
