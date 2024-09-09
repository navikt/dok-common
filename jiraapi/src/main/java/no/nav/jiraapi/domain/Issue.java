package no.nav.dok.jiraapi.domain;

public record Issue (String expand,
		String id,
		String self,
		String key,
		IssueFields fields,
		Status status){
}
