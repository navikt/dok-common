package no.nav.dok.jiraapi.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dok.jiraapi.JiraProperties;
import no.nav.dok.jiraapi.JiraRequest;
import no.nav.dok.jiracore.config.JiraMapper;
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
import java.nio.file.Path;
import java.util.Base64;

import static java.lang.String.format;
import static no.nav.dok.jiracore.config.JiraConstant.ATTACHMENT;
import static no.nav.dok.jiracore.config.JiraConstant.ISSUE_PATH;
import static no.nav.dok.jiracore.config.JiraConstant.JIRA_PATH;
import static no.nav.dok.jiracore.config.JiraConstant.PROJECT;
import static no.nav.dok.jiracore.config.JiraConstant.PROJECT_KEY;
import static no.nav.dok.jiracore.config.JiraConstant.TRANSITION;
import static no.nav.dok.jiracore.config.JiraConstant.TRANSITION_ID;

/**
 * Jira api client bruker Java HttpClient til å gjøre kall mot jira
 */

public class JiraClient {

	public static String CONTENT_TYPE = "content-type";
	public static final String AUTHORIZATION = "Authorization";
	public static String APPLICATION_JSON = "application/json";
	private final HttpClient httpClient;


	private final JiraProperties jiraProperties;

	public JiraClient(JiraProperties jiraProperties) {
		this.httpClient = HttpClient.newHttpClient();
		this.jiraProperties = jiraProperties;
	}

	public Issue opprettJira(JiraRequest request) {
		Project project = hentProject(jiraProperties.url());
		IssueInput issueInput = JiraMapper.map(request, project);

		try {
			String issueInputAsString = serialize(issueInput);

			HttpRequest httpRequest = HttpRequest.newBuilder()
					.uri(URI.create(jiraProperties.url() + ISSUE_PATH))
					.header(CONTENT_TYPE, APPLICATION_JSON)
					.header("Authorization", getBasicAuthenticationHeader())
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
			HttpRequest httpRequest = HttpRequest.newBuilder()
					.uri(URI.create(jiraProperties.url() + ISSUE_PATH + "/" + key + ATTACHMENT))
					.header(AUTHORIZATION, getBasicAuthenticationHeader())
					.POST(HttpRequest.BodyPublishers.ofFile(Path.of(request.file().getPath())))
					.build();
			httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
		} catch (IOException e) {
			throw new JiraClientException(format("opprettJiraVedVedlegg feilet funksjonelt med feilmelding=%s", e.getMessage()), e.getCause());
		} catch (InterruptedException e) {
			throw new JiraServerException(format("opprettJira feilet teknisk  med feilmelding=%s", e.getMessage()), e);
		}
	}

	private Project hentProject(String url) {
		try {
			HttpRequest httpRequest = HttpRequest.newBuilder()
					.uri(URI.create(url + JIRA_PATH + PROJECT + PROJECT_KEY))
					.header(CONTENT_TYPE, APPLICATION_JSON)
					.header("Accept", APPLICATION_JSON)
					.header(AUTHORIZATION, getBasicAuthenticationHeader())
					.GET()
					.build();

			HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() != 200) {
				throw new JiraClientException(format("hentProject feilet med status=%s, feilmelding=%s", response.statusCode(), response.headers()));
			}
			return deserialize(response.body(), Project.class);

		} catch (IOException e) {
			throw new JiraClientException(format("hentProject feilet funksjonelt med feilmelding=%s", e.getMessage()), e.getCause());
		} catch (InterruptedException e) {
			throw new JiraServerException(format("hentProject feilet teknisk med feilmelding=%s", e.getMessage()), e);
		}
	}

	public Issue oppdaterJiraStatus(final String key) {
		try {
			JiraTransition transition = new JiraTransition(new JiraTransition.Transition(TRANSITION_ID));
			HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(jiraProperties.url() + ISSUE_PATH + "/" + key + TRANSITION))
					.header(CONTENT_TYPE, APPLICATION_JSON)
					.header(AUTHORIZATION, getBasicAuthenticationHeader())
					.POST(HttpRequest.BodyPublishers.ofString(serialize(transition)))
					.build();
			httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			return hentIssue(key);

		} catch (IOException e) {
			throw new JiraClientException(format("oppdaterJiraStatus feilet teknisk med med feilmelding=%s", e.getMessage()), e.getCause());
		} catch (InterruptedException e) {
			throw new JiraClientException(format("oppdaterJiraStatus feilet teknisk med med feilmelding=%s", e.getMessage()), e);
		}
	}

	private Issue hentIssue(final String key) {
		HttpRequest httpRequest = HttpRequest.newBuilder(URI.create(jiraProperties.url() + ISSUE_PATH + "/" + key))
				.header(CONTENT_TYPE, APPLICATION_JSON)
				.header(AUTHORIZATION, getBasicAuthenticationHeader())
				.GET()
				.build();
		try {
			HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() != 200) {
				throw new JiraClientException(format("hentIssue feilet med status=%s, feilmelding=%s", response.statusCode(), response.headers()));
			}
			return deserialize(response.body(), Issue.class);
		} catch (IOException e) {
			throw new JiraClientException(format("hentIssue feilet med feilmelding=%s", e.getMessage()), e.getCause());
		} catch (InterruptedException e) {
			throw new JiraClientException(format("hentIssue feilet teknisk med feilmelding=%s", e.getMessage()), e);
		}
	}

	private String getBasicAuthenticationHeader() {
		String valueToEncode = jiraProperties.jiraServieUser().username() + ":" + jiraProperties.jiraServieUser().username();
		return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
	}

	private String serialize(Object object) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}

	private <T> T deserialize(String jsonString, Class<T> tClass) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(jsonString, tClass);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}
}
