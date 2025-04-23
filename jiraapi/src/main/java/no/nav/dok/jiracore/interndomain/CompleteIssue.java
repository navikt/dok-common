package no.nav.dok.jiracore.interndomain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CompleteIssue(
		String id,
		String self,
		String key,
		FlexibleInputFields fields) {
}
