package no.nav.dok.jiracore.interndomain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IssueType(
		String self,
		String id,
		String description,
		String name,
		Boolean subtask) {
}
