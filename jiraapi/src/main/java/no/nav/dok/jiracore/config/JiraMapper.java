package no.nav.dok.jiracore.config;

import no.nav.dok.jiracore.interndomain.BasicInputFields;
import no.nav.dok.jiracore.interndomain.CustomField;
import no.nav.dok.jiracore.interndomain.IssueInput;
import no.nav.dok.jiracore.interndomain.IssueType;
import no.nav.dok.jiracore.interndomain.JiraInternRequest;
import no.nav.dok.jiracore.interndomain.Project;
import no.nav.dok.jiracore.interndomain.Reporter;

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
	public static IssueInput map(JiraInternRequest jiraInternRequest, Project project, Predicate<IssueType> issueTypePredicate, CustomField... customFields) {
		Map<String, Object> extraProperties = new HashMap<>();

		Optional<Reporter> reporter = Optional.ofNullable(jiraInternRequest.reporterName()).map(name -> new Reporter(null, name, null));
		reporter.ifPresent(reporter1 -> extraProperties.put("reporter", reporter1));
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
				.description(jiraInternRequest.description())
				.labels(jiraInternRequest.labels().toArray(String[]::new))
				.customProperties(extraProperties)
				.build();

		return new IssueInput(basicInputFields);
	}

	public static Map<String, List<Map<String, String>>> mapCustomFields(CustomField[] customFields) {
		return Stream.of(customFields).collect(Collectors.toMap(CustomField::getCustomFieldKey, CustomField::asRawInput));
	}
}
