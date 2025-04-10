package no.nav.dok.jiracore.config;

import no.nav.dok.jiracore.interndomain.BasicInputFields;
import no.nav.dok.jiracore.interndomain.IssueInput;
import no.nav.dok.jiracore.interndomain.IssueType;
import no.nav.dok.jiracore.interndomain.JiraInternRequest;
import no.nav.dok.jiracore.interndomain.Project;
import no.nav.dok.jiracore.interndomain.Reporter;

import java.util.function.Predicate;

/**
 * JiraMapper strukturt til å tilpasse jira interndomain
 */
public class JiraMapper {
	public static IssueInput map(JiraInternRequest jiraInternRequest, Project project, Predicate<IssueType> issueTypePredicate) {

		Reporter reporter = new Reporter(null, jiraInternRequest.reporterName(), null);

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
				.reporter(reporter)
				.description(jiraInternRequest.description())
				.labels(jiraInternRequest.labels().toArray(String[]::new))
				.build();

		return new IssueInput(basicInputFields);
	}
}
