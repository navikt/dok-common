package no.nav.dok.jiraapi;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

/**
 * @param reporter	applikasjonen eller person som opprettes jira-saken.
 * @param description	detaljert beskrivelse for Jira-saken
 * @param summary	sammendrag eller tittel for jira-saken
 * @param labels
 * @param vedlegg bytedata som vedlegg for Jira-saken
 */

@Builder
public record JiraRequest(
		@NotNull Reporter reporter,
		@NotEmpty String description,
		@NotEmpty String summary,
		String filnavn,
		List<String> labels,
		byte[] vedlegg,
		LocalDate avstemmingsfilDato) {

	public record Reporter(@NotEmpty String accountId, @NotEmpty String displayName) {
	}

	public String filnavn() {
		if (filnavn == null) {
			return this.labels().getFirst() + "-" + this.avstemmingsfilDato() + ".csv";
		}
		return filnavn;
	}
}
