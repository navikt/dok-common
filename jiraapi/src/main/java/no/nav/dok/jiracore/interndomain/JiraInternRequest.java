package no.nav.dok.jiracore.interndomain;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.io.File;
import java.util.List;

/**
 * @param reporterName applikasjonen eller personnavn som opprettes jira-saken.
 * @param description  detailert jira-saken beskrivelse
 * @param summary      sammendrag eller tittle for jira-saken
 * @param labels
 * @param vedlegg      vedlegg fil som kan legge til jira-saken
 */

@Builder
public record JiraInternRequest(
		@NotEmpty String reporterName,
		@NotEmpty String description,
		@NotEmpty String summary,
		List<String> labels,
		File vedlegg) {
}
