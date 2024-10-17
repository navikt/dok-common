package no.nav.dok.jiraapi;

import jakarta.validation.ValidationException;
import no.nav.dok.jiraapi.client.JiraClient;
import no.nav.dok.jiracore.interndomain.Issue;

import java.net.URI;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dok.jiracore.config.JiraConstant.BROWSE;
import static no.nav.dok.jiracore.config.JiraConstant.NO_CONTENT_STATUS_CODE;
import static no.nav.dok.jiracore.config.JiraConstant.OK_STATUS_CODE;
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

		return JiraResponse.builder().jiraIssueKey(issue.key())
				.message(responseUrl(issue.self(), issue.key()))
				.httpStatusCode(OK_STATUS_CODE)
				.build();
	}

	/**
	 * @param jiraRequest jira requesten som bruker til å opprette jira sak.
	 * @return metoden opprette jira oppgave ved vedlegg og returnerer jira key, melding og httpstatus
	 */
	@Override
	public JiraResponse opprettJiraOppgaveVedVedlegg(JiraRequest jiraRequest) {

		if (nonNull(jiraRequest) && !jiraRequest.file().exists()) {
			return JiraResponse.builder()
					.message("Kan ikke opprette Jira-sak. Fant ingen vedlegg fil")
					.httpStatusCode(NO_CONTENT_STATUS_CODE)
					.build();
		}

		Issue issue = jiraClient.opprettJira(jiraRequest);

		assertNotNullOrEmpty("key", issue.key());
		assertNotNull("file", jiraRequest.file());
		int vedleggStatus = jiraClient.leggTilVedlegg(issue.id(), jiraRequest);

		Issue oppdaterOppgave = jiraClient.oppdaterJiraStatus(issue.key());

		return JiraResponse.builder().jiraIssueKey(issue.key())
				.message(responseUrl(issue.self(), issue.key()))
				.status(getStatus(oppdaterOppgave))
				.httpStatusCode(OK_STATUS_CODE)
				.vedleggStatusCode(vedleggStatus)
				.build();
	}

	public static void assertNotNullOrEmpty(String field, String value) {
		if (isBlank(value)) {
			throw new ValidationException(format("Feltet %s kan ikke være null eller tomt. Fikk %s=%s", field, field, value));
		}
	}

	public static void assertNotNull(String field, Object value) {
		if (isNull(value)) {
			throw new ValidationException(format("Feltet %s kan ikke være null eller tomt. Fikk %s=null", field, field));
		}
	}

	private String responseUrl(String self, String key) {
		URI uri = URI.create(self);
		return "https://" + uri.getHost() + BROWSE + key;
	}

	private String getStatus(Issue issue) {
		if (isNull(issue.fields()) || isNull(issue.fields().status())) {
			return null;
		}
		return issue.fields().status().name();
	}
}
