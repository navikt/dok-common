package no.nav.dok.jiracore.exception;

public class JiraClientException extends RuntimeException {
	public JiraClientException(String message) {
		super(message);
	}

	public JiraClientException(String message, Throwable cause) {
		super(message, cause);
	}
}
