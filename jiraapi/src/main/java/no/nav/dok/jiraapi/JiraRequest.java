package no.nav.dok.jiraapi;

import java.io.File;
import java.util.List;

public record JiraRequest(
		String reporterName,
		String description,
		String summary,
		List<String> labels,

		 File file) {
}
