package no.nav.dok.jiracore.interndomain;

import java.util.List;

public record Description(
		String type,
		int version,
		List<Content> content
) {

	public record ContentItem(
			String type,
			String text
	) {
	}

	public record Content(
			String type,
			List<ContentItem> content
	) {
	}
}


