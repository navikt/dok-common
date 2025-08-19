module jiraapi {
	requires jakarta.validation;
	requires lombok;
	requires java.net.http;
	requires com.fasterxml.jackson.databind;
	requires org.apache.commons.lang3;
	requires spring.web;
	requires spring.core;
	requires org.slf4j;
	exports no.nav.dok.jiraapi;
	exports no.nav.dok.jiracore.interndomain;
}