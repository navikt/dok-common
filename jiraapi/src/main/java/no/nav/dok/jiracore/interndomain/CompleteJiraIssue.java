package no.nav.dok.jiracore.interndomain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class CompleteJiraIssue {
	private String id;
	private String self;
	private String key;
	private FlexibleOutputFields fields;

	public CompleteJiraIssue() {
		this.fields = new FlexibleOutputFields();
	}
}
