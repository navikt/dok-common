package no.nav.dok.jiraapi.domain;

public record IssueType(String self,
						String id,
						String description,
						String name,
						Boolean subtask) {

	public IssueType withDescription(String description) {
		return new IssueType(self(), id(), description, name(), subtask());
	}
}
