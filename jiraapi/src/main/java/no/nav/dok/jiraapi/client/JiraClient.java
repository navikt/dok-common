package no.nav.dok.jiraapi.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dok.jiraapi.JiraProperties;
import no.nav.dok.jiracore.config.JiraMapper;
import no.nav.dok.jiracore.config.JsonBodyHandler;
import no.nav.dok.jiracore.exception.JiraClientException;
import no.nav.dok.jiracore.interndomain.Issue;
import no.nav.dok.jiracore.interndomain.IssueInput;
import no.nav.dok.jiracore.interndomain.JiraInternRequest;
import no.nav.dok.jiracore.interndomain.JiraTransition;
import no.nav.dok.jiracore.interndomain.Project;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.client.RestClient;

import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

import static java.lang.String.format;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static no.nav.dok.jiracore.config.JiraConstant.ATTACHMENT;
import static no.nav.dok.jiracore.config.JiraConstant.ISSUE_PATH;
import static no.nav.dok.jiracore.config.JiraConstant.PROJECT_KEY;
import static no.nav.dok.jiracore.config.JiraConstant.PROJECT_PATH;
import static no.nav.dok.jiracore.config.JiraConstant.TRANSITION;
import static no.nav.dok.jiracore.config.JiraConstant.TRANSITION_ID;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

/**
 * Jira api client bruker Java HttpClient til å gjøre kall mot jira
 */

public class JiraClient {

	private final HttpClient httpClient;
	private final RestClient restClient;
	private final JiraProperties jiraProperties;

	public JiraClient(JiraProperties jiraProperties) {

		this.jiraProperties = jiraProperties;

		this.httpClient = HttpClient.newBuilder()
				.proxy(ProxySelector.getDefault())
				.connectTimeout(Duration.ofSeconds(15))
				.build();

		this.restClient = RestClient.builder()
				.baseUrl(jiraProperties.url())
				.defaultHeaders(httpHeaders -> {
					httpHeaders.setBasicAuth(jiraProperties.jiraServiceUser().username(), jiraProperties.jiraServiceUser().password());
					httpHeaders.set("X-Atlassian-Token", "no-check");
				})
				.build();
	}

	public Issue opprettJira(JiraInternRequest request) {
		Project project = hentProject();
		IssueInput issueInput = JiraMapper.map(request, project);
		try {
			String issueInputAsString = serialize(issueInput);

			HttpRequest httpRequest = httpRequestBuilder()
					.uri(URI.create(jiraProperties.url() + ISSUE_PATH))
					.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
					.POST(HttpRequest.BodyPublishers.ofString(issueInputAsString))
					.build();

			HttpResponse<Issue> response = httpClient.send(httpRequest, new JsonBodyHandler<>(Issue.class));

			if (response.statusCode() != HTTP_CREATED) {
				throw new JiraClientException(format("opprettJira feilet med status=%s", response.statusCode()));
			}
			return response.body();
		} catch (Exception e) {
			throw new JiraClientException(format("opprettJira feilet med feilmelding=%s", e.getMessage()));
		}
	}

	public int leggTilVedlegg(String key, JiraInternRequest request) {
		MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
		multipartBodyBuilder.part("file", new FileSystemResource(request.vedlegg()));
		return restClient.post()
				.uri(uriBuilder -> uriBuilder.path(ISSUE_PATH + "/" + key + ATTACHMENT)
						.build())
				.header(CONTENT_TYPE, MULTIPART_FORM_DATA_VALUE)
				.body(multipartBodyBuilder.build())
				.exchange((req, res) -> {
					if (!res.getStatusCode().is2xxSuccessful()) {
						throw new JiraClientException(format("leggTilVedlegg feilet med feilmelding=%s og status=%s", res.getBody(), res.getStatusCode()));
					}
					return res.getStatusCode().value();
				});
	}

	private Project hentProject() {
		try {
			HttpRequest httpRequest = httpRequestBuilder()
					.uri(URI.create(jiraProperties.url() + PROJECT_PATH + PROJECT_KEY))
					.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
					.timeout(Duration.ofSeconds(30))
					.GET()
					.build();

			HttpResponse<Project> response = httpClient.send(httpRequest, new JsonBodyHandler<>(Project.class));

			if (response.statusCode() != HTTP_OK) {
				throw new JiraClientException(format("hentProject feilet med status=%s, feilmelding=%s", response.statusCode(), response.headers()));
			}
			return response.body();
		} catch (Exception e) {
			throw new JiraClientException(format("hentProject feilet med feilmelding=%s", e.getMessage()), e);
		}
	}

	public Issue oppdaterJiraStatus(final String key) {
		try {
			JiraTransition transition = new JiraTransition(new JiraTransition.Transition(TRANSITION_ID));
			HttpRequest httpRequest = httpRequestBuilder().uri(URI.create(jiraProperties.url() + ISSUE_PATH + "/" + key + TRANSITION))
					.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
					.POST(HttpRequest.BodyPublishers.ofString(serialize(transition)))
					.build();
			httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			return hentIssue(key);

		} catch (Exception e) {
			throw new JiraClientException(format("oppdaterJiraStatus feilet med feilmelding=%s", e.getMessage()), e.getCause());
		}
	}

	private Issue hentIssue(final String key) {
		HttpRequest httpRequest = httpRequestBuilder().uri(URI.create(jiraProperties.url() + ISSUE_PATH + "/" + key))
				.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
				.GET()
				.build();
		try {
			HttpResponse<Issue> response = httpClient.send(httpRequest, new JsonBodyHandler<>(Issue.class));
			if (response.statusCode() != HTTP_OK) {
				throw new JiraClientException(format("hentIssue feilet med status=%s, feilmelding=%s", response.statusCode(), response.headers()));
			}
			return response.body();
		} catch (Exception e) {
			throw new JiraClientException(format("hentIssue feilet med feilmelding=%s", e.getMessage()), e.getCause());
		}
	}

	private String getBasicAuthenticationHeader() {
		String valueToEncode = jiraProperties.jiraServiceUser().username() + ":" + jiraProperties.jiraServiceUser().password();
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

	private HttpRequest.Builder httpRequestBuilder() {
		return HttpRequest.newBuilder()
				.header(AUTHORIZATION, getBasicAuthenticationHeader())
				.header("X-Atlassian-Token", "no-check");
	}
}
