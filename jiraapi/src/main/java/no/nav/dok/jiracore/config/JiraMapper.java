package no.nav.dok.jiracore.config;

import no.nav.dok.jiraapi.JiraRequest;
import no.nav.dok.jiracore.interndomain.BasicInputFields;
import no.nav.dok.jiracore.interndomain.CustomField;
import no.nav.dok.jiracore.interndomain.Description;
import no.nav.dok.jiracore.interndomain.IssueInput;
import no.nav.dok.jiracore.interndomain.IssueType;
import no.nav.dok.jiracore.interndomain.Project;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JiraMapper strukturt til å tilpasse jira interndomain
 */
public class JiraMapper {
	public static IssueInput map(JiraRequest jiraInternRequest, Project project, Predicate<IssueType> issueTypePredicate, Stream<CustomField> customFields) {
		Map<String, Object> extraProperties = new HashMap<>();

		Optional.ofNullable(jiraInternRequest.reporter()).ifPresent(reporter -> extraProperties.put("reporter", reporter));
		extraProperties.putAll(mapCustomFields(customFields));

		Project newProject = Project.builder()
				.key(project.key())
				.name(project.name())
				.build();

		IssueType newIssueType = project.issueTypes().stream()
				.filter(issueTypePredicate)
				.findFirst()
				.orElse(null);

		BasicInputFields basicInputFields = BasicInputFields.builder()
				.project(newProject)
				.issuetype(newIssueType)
				.summary(jiraInternRequest.summary())
				.description(mapDescription(jiraInternRequest.description()))
				.labels(jiraInternRequest.labels().toArray(String[]::new))
				.customProperties(extraProperties)
				.build();

		return new IssueInput(basicInputFields);
	}

	public static Description mapDescription(String description) {
		Description.ContentItem contentItem = new Description.ContentItem(
				"text",
				description
		);

		Description.Content content = new Description.Content(
				"paragraph",
				List.of(contentItem)
		);

		return new Description("doc", 1, List.of(content)
		);
	}

	public static Map<String, List<Map<String, String>>> mapCustomFields(Stream<CustomField> customFields) {
		return customFields.collect(Collectors.toMap(CustomField::getCustomFieldKey, CustomField::asRawInput));
	}
}
