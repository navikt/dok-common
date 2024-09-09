package no.nav.dok.jiraapi.domain;

public record StatusTransition(Transition transition){
	public record Transition(String id) {};
}
