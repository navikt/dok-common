package no.nav.dok.jiraapi;

import jakarta.validation.ValidationException;
import no.nav.dok.jiraapi.client.JiraClient;
import no.nav.dok.jiracore.exception.IkkeFinneJiraFilException;
import no.nav.dok.jiracore.interndomain.Issue;
import no.nav.dok.jiracore.interndomain.JiraInternRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;

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
		JiraInternRequest jiraInternRequest = mapJiraRequest(jiraRequest);

		Issue issue = jiraClient.opprettJira(jiraInternRequest);

		jiraClient.oppdaterJiraStatus(issue.key());

		return JiraResponse.builder().jiraIssueKey(issue.key())
				.message(responseUrl(issue.self(), issue.key()))
				.httpStatusCode(OK_STATUS_CODE)
				.build();
	}

	/**
	 * @param jiraRequest jira requesten mapper JiraInternRequest og requesten bruker til å opprette jira sak.
	 * @return metoden opprette jira oppgave ved vedlegg og returnerer jira key, melding og httpstatus
	 */
	@Override
	public JiraResponse opprettJiraOppgaveMedVedlegg(JiraRequest jiraRequest) {

		JiraInternRequest jiraInternRequest = mapJiraRequest(jiraRequest);

		if (nonNull(jiraInternRequest) && !jiraInternRequest.vedlegg().exists()) {
			return JiraResponse.builder()
					.message("Kan ikke opprette Jira-sak. Fant ingen vedlegg fil")
					.httpStatusCode(NO_CONTENT_STATUS_CODE)
					.build();
		}

		Issue issue = jiraClient.opprettJira(jiraInternRequest);

		assertNotNullOrEmpty("key", issue.key());
		assertNotNull("file", jiraInternRequest.vedlegg());
		jiraClient.leggTilVedlegg(issue.key(), jiraInternRequest);

		Issue oppdaterOppgave = jiraClient.oppdaterJiraStatus(issue.key());

		return JiraResponse.builder().jiraIssueKey(issue.key())
				.message(responseUrl(issue.self(), issue.key()))
				.status(getStatus(oppdaterOppgave))
				.httpStatusCode(OK_STATUS_CODE)
				.build();
	}

	private JiraInternRequest mapJiraRequest(JiraRequest jiraRequest) {
		File vedleggFil = jiraRequest.vedlegg() == null ? null : createFile(jiraRequest.vedlegg(), jiraRequest.avstemmingsfilDato());
		return JiraInternRequest.builder()
				.reporterName(jiraRequest.reporterName())
				.description(jiraRequest.description())
				.summary(jiraRequest.summary())
				.vedlegg(vedleggFil)
				.labels(jiraRequest.labels())
				.build();
	}

	private File createFile(byte[] csvByte, LocalDate avstemmingsfilDato) {
		try {
			File tempFile = File.createTempFile("skanmotreferansenr-feilende-avstemming-" + avstemmingsfilDato, ".csv");
			try (FileOutputStream fs = new FileOutputStream(tempFile)) {
				fs.write(csvByte);
			}
			return tempFile;
		} catch (IOException ex) {
			throw new IkkeFinneJiraFilException("I/O feil med feilmelding=" + ex.getMessage(), ex);
		}
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
