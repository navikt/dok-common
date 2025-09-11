package no.nav.dok.jiraapi;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

/**
 * @param reporterName applikasjonen eller personnavn som opprettes jira-saken.
 * @param description  detailert jira-saken beskrivelse
 * @param summary      sammendrag eller tittle for jira-saken
 * @param labels
 * @param vedlegg      byte data som kan legge til jira-saken
 */

@Builder
public record JiraRequest(
		@NotEmpty String reporterName,
		@NotEmpty String description,
		@NotEmpty String summary,
		String filnavn,
		List<String> labels,
		byte[] vedlegg,
		LocalDate avstemmingsfilDato) {

	public String filnavn() {
		if (filnavn == null) {
			return this.labels().getFirst() + "-" + this.avstemmingsfilDato() + ".csv";
		}
		return filnavn;
	}
}
