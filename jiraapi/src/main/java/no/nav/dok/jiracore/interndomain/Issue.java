package no.nav.dok.jiracore.interndomain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Issue(
		String id,
		String self,
		String key,
		Fields fields) {

	@JsonIgnoreProperties
	public record Fields(Status status, List<Attachment> attachment) {}

	@JsonIgnoreProperties
	public record Status(String name) {}

	@JsonIgnoreProperties
	public record Attachment(
		 String id,
		 String filename,
		 String mimeType){
	}
}
