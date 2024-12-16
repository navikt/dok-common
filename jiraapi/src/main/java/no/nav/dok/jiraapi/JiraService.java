package no.nav.dok.jiraapi;

public interface JiraService {

	JiraResponse opprettJiraOppgave(JiraRequest jiraRequest);

	JiraResponse opprettJiraOppgaveMedVedlegg(JiraRequest jiraRequest);
}
