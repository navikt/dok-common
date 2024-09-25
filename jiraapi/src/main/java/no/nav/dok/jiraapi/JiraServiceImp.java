package no.nav.dok.jiraapi;

import jakarta.validation.ValidationException;
import no.nav.dok.jiraapi.client.JiraClient;
import no.nav.dok.jiracore.interndomain.Issue;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;


public class JiraServiceImp implements JiraService {

	private final JiraClient jiraClient;

	public JiraServiceImp(JiraClient jiraClient) {
		this.jiraClient = jiraClient;
	}

	@Override
	public JiraResponse opprettJiraOppgave(JiraRequest jiraRequest) {
		Issue issue = jiraClient.opprettJira(jiraRequest);

		jiraClient.oppdaterJiraStatus(issue.key());

		return new JiraResponse(issue.key(), null, "OK");
	}

	/**
	 * @param jiraRequest jira requesten som bruker til å opprette jira sak.
	 * @return metoden opprette jira oppgave ved vedlegg og returnerer jira key, melding og httpstatus
	 */
	@Override
	public JiraResponse opprettJiraOppgaveVedVedlegg(JiraRequest jiraRequest) {

		if (nonNull(jiraRequest) && !jiraRequest.file().exists()) {
			return new JiraResponse(null,
					"Kan ikke opprette Jira-sak. Fant ingen vedlegg fil", "NO_CONTENT");
		}

		Issue issue = jiraClient.opprettJira(jiraRequest);

		assertNotNullOrEmpty("key", issue.key());
		assertNull("file", jiraRequest.file());
		jiraClient.leggTilVedlegg(issue.key(), jiraRequest);

		jiraClient.oppdaterJiraStatus(issue.key());
		return new JiraResponse(issue.key(), null, "OK");
	}

	public static void assertNotNullOrEmpty(String field, String value) {
		if (isBlank(value)) {
			throw new ValidationException(format("Feltet %s kan ikke være null eller tomt. Fikk %s=%s", field, field, value));
		}
	}

	public static void assertNull(String field, Object value) {
		if (isNull(value)) {
			throw new ValidationException(format("Feltet %s kan ikke være null eller tomt. Fikk %s=null", field, field));
		}
	}
}
