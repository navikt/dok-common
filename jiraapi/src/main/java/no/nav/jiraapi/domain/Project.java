package no.nav.dok.jiraapi.domain;

import java.util.List;

public record Project(String expand,
					  String self,
					  String id,
					  String key,
					  String description,
					  String name,
					  String url,
					  List<Component> components,
					  List<IssueType> issueTypes,
					  List<Version> versions) {
}
