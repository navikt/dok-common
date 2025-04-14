package no.nav.dok.jiracore.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dok.jiracore.interndomain.IssueType;
import no.nav.dok.jiracore.interndomain.JiraInternRequest;
import no.nav.dok.jiracore.interndomain.Project;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class JiraMapperTest {

	@Test
	void mapAndSerializeWithExtraProperties() throws JsonProcessingException {
		JiraInternRequest jiraInternRequest = JiraInternRequest.builder()
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
		Map.of("customprop_1", "Custom property 1",
				"customprop_2", "Custom property 2",
				"customprop_3", Map.of("id", "id", "key", "key")));

		var objectMapper = new ObjectMapper();
		String mappedJson = objectMapper.writeValueAsString(mappedJiraRequest);

		assertThat(mappedJson).contains(
				"\"name\":\"Tim Dokumentløsninger\"",
				"\"key\":\"PRO\"",
				"\"customprop_1\":\"Custom property 1\"",
				"\"customprop_2\":\"Custom property 2\"",
				"\"customprop_3\":{");
	}
}