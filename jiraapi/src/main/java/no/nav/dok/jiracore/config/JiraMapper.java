package no.nav.dok.jiracore.config;

import no.nav.dok.jiraapi.JiraRequest;
import no.nav.dok.jiracore.exception.FileParseException;
import no.nav.dok.jiracore.interndomain.BasicInputFields;
import no.nav.dok.jiracore.interndomain.IssueInput;
import no.nav.dok.jiracore.interndomain.IssueType;
import no.nav.dok.jiracore.interndomain.Project;
import no.nav.dok.jiracore.interndomain.Reporter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static no.nav.dok.jiracore.config.JiraConstant.ISSUE_TYPE_OPPGAVE;

/**
 *  JiraMapper strukturt til å tilpasse jira interndomain
 */
public class JiraMapper {
	public static IssueInput map(JiraRequest jiraRequest, Project project) {

		Reporter reporter = new Reporter(null, jiraRequest.reporterName(), null);

		Project newProject = Project.builder()
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


	public static byte[] convertFileToByteArray(File file) {
		try (FileInputStream fis = new FileInputStream(file);
			 BufferedInputStream bis = new BufferedInputStream(fis);
			 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

			byte[] buffer = new byte[1024];
			int bytesRead;

			while ((bytesRead = bis.read(buffer)) != -1) {
				baos.write(buffer, 0, bytesRead);
			}

			return baos.toByteArray();  // Return byte array of file content
		} catch (IOException e) {
			throw new FileParseException("IO Error - klarer ikke parse fil", e);
		}
	}

}
