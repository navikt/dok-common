package no.nav.dok.jiracore.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ResponseStatus(INTERNAL_SERVER_ERROR)
public class JiraTechnicalException extends RuntimeException {
	public JiraTechnicalException(String message, Throwable cause) {
		super(message, cause);
	}
}
