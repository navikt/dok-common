package no.nav.dok.jiracore.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dok.jiraapi.JiraRequest;
import no.nav.dok.jiracore.interndomain.AnsvarligTeam;
import no.nav.dok.jiracore.interndomain.BeroertTjeneste;
import no.nav.dok.jiracore.interndomain.CompleteJiraIssue;
import no.nav.dok.jiracore.interndomain.CustomField;
import no.nav.dok.jiracore.interndomain.IssueType;
import no.nav.dok.jiracore.interndomain.Project;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class JiraMapperTest {

	@Test
	void mapAndSerializeWithExtraProperties() throws JsonProcessingException {
		JiraRequest jiraInternRequest = JiraRequest.builder()
				.description("description")
				.reporterName("Tim Dokumentløsninger")
				.summary("an issue must be resolved")
				.labels(emptyList())
				.build();
		Project project = Project.builder()
				.name("project")
				.key("PRO")
				.issueTypes(List.of(new IssueType("self", "issu", "a type of issue", "ISSUEEEE", false)))
				.build();

		var mappedJiraRequest = JiraMapper.map(jiraInternRequest, project, x -> true,
				Stream.of(
						new AnsvarligTeam("Custom property 1"),
						new BeroertTjeneste("Custom property 2"),
						new HypotheticalComplicatedCustomField()
				));

		var objectMapper = new ObjectMapper();
		String mappedJson = objectMapper.writeValueAsString(mappedJiraRequest);

		assertThat(mappedJson).contains(
				"\"name\":\"Tim Dokumentløsninger\"",
				"\"key\":\"PRO\"",
				"\"key\":\"Custom property 1\"",
				"\"key\":\"Custom property 2\"",
				"\"customfield_1\":[{"
		);
	}

	@Test
	void mapAndSerializeWithExtraPropertiesNoReporter() throws JsonProcessingException {
		JiraRequest jiraInternRequest = JiraRequest.builder()
				.description("description")
				.reporterName(null)
				.summary("an issue must be resolved")
				.labels(emptyList())
				.build();
		Project project = Project.builder()
				.name("project")
				.key("PRO")
				.issueTypes(List.of(new IssueType("self", "issu", "a type of issue", "ISSUEEEE", false)))
				.build();

		var mappedJiraRequest = JiraMapper.map(jiraInternRequest, project, x -> true,
				Stream.of(
						new AnsvarligTeam("Custom property 1"),
						new BeroertTjeneste("Custom property 2"),
						new HypotheticalComplicatedCustomField()
				));

		var objectMapper = new ObjectMapper();
		String mappedJson = objectMapper.writeValueAsString(mappedJiraRequest);

		assertThat(mappedJson).doesNotContain("reporter");
	}

	@Test
	void mapAndSerializeWithExtraPropertiesAndSuccessfullyDeserialize() throws JsonProcessingException {
		JiraRequest jiraInternRequest = JiraRequest.builder()
				.description("description")
				.reporterName("Tim Dokumentløsninger")
				.summary("an issue must be resolved")
				.labels(emptyList())
				.build();
		Project project = Project.builder()
				.name("project")
				.key("PRO")
				.issueTypes(List.of(new IssueType("self", "issu", "a type of issue", "ISSUEEEE", false)))
				.build();

		var mappedJiraRequest = JiraMapper.map(jiraInternRequest, project, x -> true,
				Stream.of(
						new AnsvarligTeam("Custom property 1"),
						new BeroertTjeneste("Custom property 2"),
						new HypotheticalComplicatedCustomField()
				));

		var objectMapper = new ObjectMapper();
		String mappedJson = objectMapper.writeValueAsString(mappedJiraRequest);

		assertThat(mappedJson).contains(
				"\"name\":\"Tim Dokumentløsninger\"",
				"\"key\":\"PRO\"",
				"\"key\":\"Custom property 1\"",
				"\"key\":\"Custom property 2\"",
				"\"customfield_1\":[{");

		CompleteJiraIssue completeJiraIssue = objectMapper.readValue(mappedJson, CompleteJiraIssue.class);
		assertThat(completeJiraIssue.getFields().getJiraCustomProperties()).hasSize(9);
	}

	public static final class HypotheticalComplicatedCustomField extends CustomField {

		HypotheticalComplicatedCustomField() {
			super("customfield_1", null);
		}

		@Override
		public List<Map<String, String>> asRawInput() {
			return List.of(Map.of("key", "key", "id", "id", "label", "complicated"));
		}
	}
}