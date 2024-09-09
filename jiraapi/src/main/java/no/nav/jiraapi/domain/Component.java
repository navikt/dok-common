package no.nav.dok.jiraapi.domain;

import lombok.Builder;

@Builder
public record Component(String self,
						String id,
						String name,
						Boolean isAssigneeTypeValid) {

}
