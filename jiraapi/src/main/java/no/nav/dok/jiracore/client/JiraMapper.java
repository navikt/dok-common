package no.nav.dok.jiracore.client;

import no.nav.dok.jiraapi.JiraRequest;
import no.nav.dok.jiracore.interndomain.BasicInputFields;
import no.nav.dok.jiracore.interndomain.IssueInput;
import no.nav.dok.jiracore.interndomain.IssueType;
import no.nav.dok.jiracore.interndomain.Project;
import no.nav.dok.jiracore.interndomain.Reporter;

import static no.nav.dok.jiracore.config.JiraConstant.ISSUE_TYPE_OPPGAVE;

public class JiraMapper {
	public static IssueInput map(JiraRequest jiraRequest, Project project) {

		Reporter reporter = new Reporter(null, jiraRequest.reporterName(), null);

		Project newProject = project.builder()
				.key(project.key())
				.name(project.name())
				.build();

		IssueType newIssueType = project.issueTypes().stream()
				.filter(issueType1 -> ISSUE_TYPE_OPPGAVE.equals(issueType1.name()))
				.findFirst()
				.orElse(null);

		BasicInputFields basicInputFields = BasicInputFields.builder()
				.project(newProject)
				.issuetype(newIssueType)
				.summary(jiraRequest.summary())
				.reporter(reporter)
				.description(jiraRequest.description())
				.labels(jiraRequest.labels().toArray(String[]::new))
				.build();

		return new IssueInput(basicInputFields);
	}
}
