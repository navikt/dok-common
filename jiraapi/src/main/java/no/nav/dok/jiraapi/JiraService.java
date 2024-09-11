package no.nav.dok.jiraapi;

public interface JiraService {

	JiraResponse opprettJira(JiraRequest jiraRequest);

	JiraResponse opprettJiraVedVedlegg(JiraRequest jiraRequest);
}
