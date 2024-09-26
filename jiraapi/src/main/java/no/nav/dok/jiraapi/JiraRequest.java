package no.nav.dok.jiraapi;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.io.File;
import java.util.List;

/**
 * @param reporterName   applikasjonen eller personnavn som opprettes jira-saken.
 * @param description    detailert jira-saken beskrivelse
 * @param summary        sammendrag eller tittle for jira-saken
 * @param labels
 * @param file           filer som kan legge til jira-saken

 */

@Builder
public record JiraRequest(
		@NotEmpty String reporterName,
		@NotEmpty String description,
		@NotEmpty String summary,
		List<String> labels,
		File file) {
}
