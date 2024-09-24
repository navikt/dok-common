package no.nav.dok.jiracore.exception;

public class JiraServerException extends   RuntimeException {
	public JiraServerException(String message) {
		super(message);
	}

	public JiraServerException(String message, Throwable cause) {
		super(message, cause);
	}
}
