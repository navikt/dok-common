package no.nav.dok.jiracore.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dok.jiraapi.JiraRequest;
import no.nav.dok.jiracore.exception.JiraClientException;
import no.nav.dok.jiracore.exception.JiraServerException;
import no.nav.dok.jiracore.interndomain.Issue;
import no.nav.dok.jiracore.interndomain.IssueInput;
import no.nav.dok.jiracore.interndomain.JiraTransition;
import no.nav.dok.jiracore.interndomain.Project;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import static java.lang.String.format;
import static no.nav.dok.jiracore.config.JiraConstant.ATTACHMENT;
import static no.nav.dok.jiracore.config.JiraConstant.ISSUE;
import static no.nav.dok.jiracore.config.JiraConstant.JIRA_PATH;
import static no.nav.dok.jiracore.config.JiraConstant.PROJECT;
import static no.nav.dok.jiracore.config.JiraConstant.PROJECT_KEY;
import static no.nav.dok.jiracore.config.JiraConstant.TRANSITION;
import static no.nav.dok.jiracore.config.JiraConstant.TRANSITION_ID;

/**
 *
 */

@Slf4j
public class JiraClient {

	public static String CONTENT_TYPE = "Content-Type";
	public static final String AUTHORIZATION = "Authorization";
	public static String APPLICATION_JSON = "application/json";
	public static final String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";
	private final HttpClient httpClient;


	public JiraClient() {
		this.httpClient = HttpClient.newBuilder()
				.build();
	}

	public Issue opprettJira(JiraRequest request) {

		Project project = hentProject(request.jiraServieUser().url(), request.jiraServieUser().username(), request.jiraServieUser().password());
		IssueInput issueInput = JiraMapper.map(request, project);

		try {

			String issueInputAsString = serialize(issueInput);

			HttpRequest httpRequest = HttpRequest.newBuilder()
					.uri(URI.create(request.jiraServieUser().url() + JIRA_PATH + ISSUE))
					.header(CONTENT_TYPE, APPLICATION_JSON)
					.header("Authorization", getBasicAuthenticationHeader(request.jiraServieUser().username(), request.jiraServieUser().password()))
					.POST(HttpRequest.BodyPublishers.ofString(issueInputAsString))
					.build();

			HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() != 200) {
				throw new JiraClientException(format("opprettJira feilt med status=%s", response.statusCode()));
			}
			return deserialize(response.body(), Issue.class);
		} catch (IOException e) {
			throw new JiraClientException(format("opprettJira feilt funksjonelt med feilmelding=%s", e.getMessage()));
		} catch (InterruptedException e) {
			throw new JiraServerException(format("opprettJira feilet teknisk med feilmelding=%s", e.getMessage()), e);
		}
	}

	public void leggTilVedlegg(String key, JiraRequest request) {


		try {
			byte[] bytes = new byte[(int) request.file().length()];

			HttpRequest httpRequest = HttpRequest.newBuilder()
					.uri(URI.create(request.jiraServieUser().url() + JIRA_PATH + key + ATTACHMENT))
					.header(CONTENT_TYPE, MULTIPART_FORM_DATA_VALUE)
					.header(AUTHORIZATION, getBasicAuthenticationHeader(request.jiraServieUser().username(), request.jiraServieUser().password()))
					.POST(HttpRequest.BodyPublishers.ofByteArray(bytes))
					.build();
			httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();

		} catch (IOException e) {
			throw new JiraClientException(format("opprettJiraVedVedlegg feilet funksjonelt med feilmelding=%s", e.getMessage()), e.getCause());
		} catch (InterruptedException e) {
			throw new JiraServerException(format("opprettJira feilet teknisk  med feilmelding=%s", e.getMessage()), e);
		}
	}

	private Project hentProject(String url, String username, String password) {
		try {
			HttpRequest httpRequest = HttpRequest.newBuilder()
					.uri(URI.create(url + PROJECT + PROJECT_KEY))
					.header(CONTENT_TYPE, APPLICATION_JSON)
					.header(AUTHORIZATION, getBasicAuthenticationHeader(username, password))
					.GET()
					.build();

			String response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString()).body();

			return deserialize(response, Project.class);
		} catch (IOException e) {
			throw new JiraClientException(format("hentProject feilet funksjonelt med feilmelding=%s", e.getMessage()), e.getCause());
		} catch (InterruptedException e) {
			throw new JiraServerException(format("hentProject feilet teknisk med feilmelding=%s", e.getMessage()), e);
		}
	}

	public void oppdaterJiraStatus(final String key, String url, String username, String password) {
		try {
			JiraTransition transition = new JiraTransition(new JiraTransition.Transition(TRANSITION_ID));
			HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(url + JIRA_PATH + key + TRANSITION))
					.header(CONTENT_TYPE, APPLICATION_JSON)
					.header(AUTHORIZATION, getBasicAuthenticationHeader(username, password))
					.POST(HttpRequest.BodyPublishers.ofString(serialize(transition)))
					.build();
			httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
		} catch (IOException e) {
			throw new JiraClientException(format("oppdaterJiraStatus feilet teknisk med med feilmelding=%s", e.getMessage()), e.getCause());
		} catch (InterruptedException e) {
			throw new JiraClientException(format("oppdaterJiraStatus feilet teknisk med med feilmelding=%s", e.getMessage()), e);
		}
	}

	private String getBasicAuthenticationHeader(String username, String password) {
		String valueToEncode = username + ":" + password;
		return Base64.getEncoder().encodeToString(valueToEncode.getBytes());
	}

	private <T> T deserialize(String jsonPayload, Class<T> tClass) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(jsonPayload, tClass);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}

	private String serialize(Object object) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(objectMapper);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}
}
