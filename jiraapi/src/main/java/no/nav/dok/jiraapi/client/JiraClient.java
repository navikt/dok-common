package no.nav.dok.jiraapi.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dok.jiraapi.JiraProperties;
import no.nav.dok.jiraapi.JiraRequest;
import no.nav.dok.jiracore.config.JiraMapper;
import no.nav.dok.jiracore.config.JsonBodyHandler;
import no.nav.dok.jiracore.exception.JiraClientException;
import no.nav.dok.jiracore.interndomain.AnsvarligTeam;
import no.nav.dok.jiracore.interndomain.CompleteJiraIssue;
import no.nav.dok.jiracore.interndomain.CustomField;
import no.nav.dok.jiracore.interndomain.Issue;
import no.nav.dok.jiracore.interndomain.IssueInput;
import no.nav.dok.jiracore.interndomain.IssueType;
import no.nav.dok.jiracore.interndomain.JiraTransition;
import no.nav.dok.jiracore.interndomain.Project;
import no.nav.dok.jiracore.interndomain.SaksKategori;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static no.nav.dok.jiracore.config.JiraConstant.ATTACHMENT;
import static no.nav.dok.jiracore.config.JiraConstant.ISSUE_PATH;
import static no.nav.dok.jiracore.config.JiraConstant.ISSUE_TYPE_IKT_INCIDENT;
import static no.nav.dok.jiracore.config.JiraConstant.ISSUE_TYPE_MMA_OPPGAVE;
import static no.nav.dok.jiracore.config.JiraConstant.PROJECT_KEY_IKT;
import static no.nav.dok.jiracore.config.JiraConstant.PROJECT_KEY_TDH;
import static no.nav.dok.jiracore.config.JiraConstant.PROJECT_PATH;
import static no.nav.dok.jiracore.config.JiraConstant.TRANSITION;
import static no.nav.dok.jiracore.config.JiraConstant.TRANSITION_ID_KLAR_TIL_ARBEID;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

/**
 * Jira api client bruker Java HttpClient til å gjøre kall mot jira
 */

@Slf4j
public class JiraClient {

	private final HttpClient httpClient;
	private final RestClient restClient;
	private final JiraProperties jiraProperties;
	private final ObjectMapper objectMapper = new ObjectMapper();

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

	/**
	 * Opprett et Issue i MMA-prosjektet med type Oppgave
	 *
	 * @param request en request med de nødvendige feltene
	 * @return det nye issuet
	 */
	public Issue opprettMMAOppgaveJira(JiraRequest request) {
		return opprettJira(request, PROJECT_KEY_TDH, ISSUE_TYPE_MMA_OPPGAVE, Issue.class, Stream.empty());
	}

	/**
	 * Opprett et Issue i IKT-prosjektet med type Incident
	 *
	 * @param request       en request med de nødvendige feltene
	 * @param saksKategori  kategori av issue i IKT-prosjektet
	 * @param ansvarligTeam team som issuet skal tildeles til
	 * @param customFields  ytteligere CustomField som skal legges til issuet
	 * @return det nye issuet
	 */
	public CompleteJiraIssue opprettIKTOppgaveJira(JiraRequest request, SaksKategori saksKategori, AnsvarligTeam ansvarligTeam, CustomField... customFields) {
		return opprettJira(request, PROJECT_KEY_IKT, ISSUE_TYPE_IKT_INCIDENT, CompleteJiraIssue.class,
				Stream.concat(Stream.of(saksKategori, ansvarligTeam), Stream.of(customFields)));
	}

	/**
	 * Opprett et issue i jira med valgt prosjekt osv.
	 *
	 * @param request            en request med de nødvendige feltene
	 * @param projectKey         key for prosjektet issuet skal opprettes i
	 * @param issueTypePredicate et predikat for å finne riktig issue-type i prosjektet
	 * @param responseType       en klasse å mappe responsen inn i
	 * @param customFields       en stream av CustomField som legges til jira-issuet
	 * @return det nye issuet, mappet til responseType typen
	 */
	public <T> T opprettJira(JiraRequest request, String projectKey, Predicate<IssueType> issueTypePredicate, Class<T> responseType, Stream<CustomField> customFields) {
		Project project = hentProject(projectKey);
		IssueInput issueInput = JiraMapper.map(request, project, issueTypePredicate, customFields);
		String issueInputAsString = serialize(issueInput);
		log.trace("transmit to jira: {}", issueInputAsString);

		HttpRequest httpRequest = httpRequestBuilder()
				.uri(URI.create(jiraProperties.url() + ISSUE_PATH))
				.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
				.POST(HttpRequest.BodyPublishers.ofString(issueInputAsString))
				.build();

		try {
			HttpResponse<T> response = httpClient.send(httpRequest, new JsonBodyHandler<>(responseType));

			if (response.statusCode() != HTTP_CREATED) {
				throw new JiraClientException(format("opprettJira feilet med status=%s feilmelding=%s", response.statusCode(), response.body()));
			}
			return response.body();
		} catch (IOException | InterruptedException e) {
			throw new JiraClientException(format("opprettJira feilet med feilmelding=%s", e.getMessage()), e);
		}
	}

	private static ByteArrayResource vedleggFraByteArray(JiraRequest jiraRequest) {
		return new ByteArrayResource(jiraRequest.vedlegg());
	}

	public void leggTilVedlegg(String key, JiraRequest request) {
		MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
		multipartBodyBuilder
				.part("file", vedleggFraByteArray(request))
				.filename(request.filnavn() + ".csv");
		restClient.post()
				.uri(uriBuilder -> uriBuilder.path(ISSUE_PATH + "/" + key + ATTACHMENT)
						.build())
				.header(CONTENT_TYPE, MULTIPART_FORM_DATA_VALUE)
				.body(multipartBodyBuilder.build())
				.exchange((req, res) -> {
					if (!res.getStatusCode().is2xxSuccessful()) {
						throw new JiraClientException(format("leggTilVedlegg feilet med status=%s og feilmelding=%s", res.getStatusCode(), res.getBody()));
					}
					return res.getStatusCode().value();
				});
	}

	private Project hentProject(String projectKey) {
		try {
			HttpRequest httpRequest = httpRequestBuilder()
					.uri(URI.create(jiraProperties.url() + PROJECT_PATH + projectKey))
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

	/**
	 * oppdater status på et issue (i MMA-prosjektet) til "Klar til Arbeid"
	 *
	 * @param key key for issuet som skal oppdateres
	 * @return issuet slik det ser ut etter endring
	 */
	public Issue oppdaterJiraStatusTilKlarTilArbeid(final String key) {
		JiraTransition transition = new JiraTransition(new JiraTransition.Transition(TRANSITION_ID_KLAR_TIL_ARBEID));
		HttpRequest httpRequest = httpRequestBuilder().uri(URI.create(jiraProperties.url() + ISSUE_PATH + "/" + key + TRANSITION))
				.header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
				.POST(HttpRequest.BodyPublishers.ofString(serialize(transition)))
				.build();

		try {
			HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() >= HTTP_BAD_REQUEST) {
				throw new JiraClientException(format("oppdaterJiraStatus feilet med status=%s feilmelding=%s", response.statusCode(), response.body()));
			}

			return hentIssue(key);
		} catch (IOException | InterruptedException e) {
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
		} catch (IOException | InterruptedException e) {
			throw new JiraClientException(format("hentIssue feilet med feilmelding=%s", e.getMessage()), e.getCause());
		}
	}

	private String getBasicAuthenticationHeader() {
		String valueToEncode = jiraProperties.jiraServiceUser().username() + ":" + jiraProperties.jiraServiceUser().password();
		return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
	}

	private String serialize(Object object) {
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException(e);
		}
	}

	private HttpRequest.Builder httpRequestBuilder() {
		return HttpRequest.newBuilder()
				.header(AUTHORIZATION, getBasicAuthenticationHeader())
				.header("X-Atlassian-Token", "no-check");
	}
}
