package no.nav.dok.jiraapi;

import org.springframework.http.HttpStatusCode;

public record JiraResponse (String jiraIssueKey,
							String message, HttpStatusCode httpStatusCode) {
}
