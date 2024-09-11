package no.nav.dok.jiraapi;

import no.nav.dok.jiracore.client.JiraClient;
import no.nav.dok.jiracore.interndomain.Issue;

import static java.util.Objects.nonNull;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

public class JiraServiceImp implements JiraService {

	private final JiraClient jiraClient;

	public JiraServiceImp(JiraClient jiraClient) {
		this.jiraClient = jiraClient;
	}

	@Override
	public JiraResponse opprettJira(JiraRequest jiraRequest) {

		Issue issue = jiraClient.opprettJira(jiraRequest);

		jiraClient.oppdaterJiraStatus(issue.key());

		return new JiraResponse(issue.key(), null, OK);
	}

	@Override
	public JiraResponse opprettJiraVedVedlegg(JiraRequest jiraRequest) {
		if (!(nonNull(jiraRequest) && jiraRequest.file().exists())) {
			return new JiraResponse(null,
					"Kan ikke opprette Jira-sak. Fant ingen vedlegg fil", NO_CONTENT);
		}

		Issue issue = jiraClient.opprettJira(jiraRequest);

		jiraClient.leggTilVedlegg(issue.key(), jiraRequest.file());

		jiraClient.oppdaterJiraStatus(issue.key());
		return new JiraResponse(issue.key(), null, OK);
	}
}
