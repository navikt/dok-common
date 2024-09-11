package no.nav.dok.jiracore.interndomain;


public record IssueType(String self,
						String id,
						String description,
						String name,
						Boolean subtask) {
}
