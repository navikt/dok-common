package no.nav.dok.jiracore.client;

import lombok.extern.slf4j.Slf4j;
import no.nav.dok.jiraapi.JiraRequest;
import no.nav.dok.jiracore.config.JiraProperties;
import no.nav.dok.jiracore.exception.JiraClientException;
import no.nav.dok.jiracore.exception.JiraTechnicalException;
import no.nav.dok.jiracore.interndomain.Issue;
import no.nav.dok.jiracore.interndomain.IssueInput;
import no.nav.dok.jiracore.interndomain.JiraTransition;
import no.nav.dok.jiracore.interndomain.Project;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.io.File;
import java.util.LinkedHashMap;

import static java.lang.String.format;
import static no.nav.dok.jiracore.config.JiraConstant.ATTACHMENT;
import static no.nav.dok.jiracore.config.JiraConstant.ISSUE;
import static no.nav.dok.jiracore.config.JiraConstant.JIRA_PATH;
import static no.nav.dok.jiracore.config.JiraConstant.PROJECT;
import static no.nav.dok.jiracore.config.JiraConstant.PROJECT_KEY;
import static no.nav.dok.jiracore.config.JiraConstant.TRANSITION;
import static no.nav.dok.jiracore.config.JiraConstant.TRANSITION_ID;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;


@Slf4j
public class JiraClient {

	private final RestClient restClient;

	public JiraClient(JiraProperties jiraProperties,
					  RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder
				.baseUrl(jiraProperties.jiraEndpoint().url())
				.defaultHeaders(httpHeaders ->
						httpHeaders.setBasicAuth(jiraProperties.serviceUser().username(), jiraProperties.serviceUser().password()))
				.build();
	}

	public Issue opprettJira(JiraRequest jiraRequest) {
		try {

			Project project = hentProject();
			IssueInput issueInput = JiraMapper.map(jiraRequest, project);

			return restClient.post()
					.uri(uriBuilder -> uriBuilder
							.path(JIRA_PATH + ISSUE)
							.build())
					.accept(APPLICATION_JSON)
					.body(issueInput)
					.retrieve()
					.onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
						throw new JiraClientException(format("opprettJira feilt funksjonelt med status=%s, feilmelding=%s",
								response.getStatusCode(), response.getHeaders()));
					})
					.body(Issue.class);
		} catch (HttpServerErrorException e) {
			throw new JiraTechnicalException(format("opprettJira feilet teknisk med med status=%s, feilmelding=%s", e.getStatusCode(), e.getMessage()), e);
		}
	}

	public void leggTilVedlegg(String key, File file) {
		try {
			LinkedHashMap<String, File> map = new LinkedHashMap<>();
			map.put("file", file);
			restClient.post()
					.uri(uriBuilder -> uriBuilder
							.pathSegment(JIRA_PATH, key, ATTACHMENT)
							.build())
					.accept(MULTIPART_FORM_DATA)
					.retrieve()
					.onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
						throw new JiraClientException(format("leggTilVedlegg (%s) feilet funksjonelt med status=%s, feilmelding=%s",
								file.getName(), response.getStatusCode(), response.getHeaders()));
					}).body(String.class);

		} catch (HttpServerErrorException e) {
			throw new JiraTechnicalException(
					format("opprettJiraVedVedlegg feilet teknisk med statusKode=%s, feilmelding=%s", e.getStatusCode(), e.getMessage()), e);
		}
	}

	private Project hentProject() {
		try {
			return restClient.get()
					.uri(uriBuilder -> uriBuilder.pathSegment(JIRA_PATH, PROJECT, PROJECT_KEY)
							.build())
					.accept(APPLICATION_JSON)
					.retrieve()
					.onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
						throw new JiraClientException(format("hentProject feilt funksjonelt med status=%s, feilmelding=%s",
								response.getStatusCode(), response.getHeaders()));

					})
					.body(Project.class);
		} catch (HttpServerErrorException e) {
			throw new JiraTechnicalException(format("hentProject feilet teknisk med med status=%s, feilmelding=%s",
					e.getStatusCode(), e.getMessage()), e);
		}
	}

	public void oppdaterJiraStatus(final String key) {
		try {
			JiraTransition transition = new JiraTransition(new JiraTransition.Transition(TRANSITION_ID));
			restClient.post()
					.uri(uriBuilder -> uriBuilder
							.pathSegment(JIRA_PATH, key, TRANSITION)
							.build())
					.accept(APPLICATION_JSON)
					.body(transition)
					.retrieve()
					.onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
						throw new JiraClientException(format("oppdaterJiraStatus feilt funksjonelt med status=%s, feilmelding=%s",
								response.getStatusCode(), response.getHeaders()));
					}))
					.body(String.class);
		} catch (HttpServerErrorException e) {
			throw new JiraTechnicalException(format("oppdaterJiraStatus feilet teknisk med med status=%s, feilmelding=%s",
					e.getStatusCode(), e.getMessage()), e);
		}

	}
}
