module jiraapi {
	requires jakarta.validation;
	requires lombok;
	requires java.net.http;
	requires com.fasterxml.jackson.databind;
	requires org.apache.commons.lang3;
	requires spring.web;
	requires spring.core;
	exports no.nav.dok.jiraapi;
}