module jiraapi {
	requires jakarta.validation;
	requires lombok;
	requires org.slf4j;
	requires java.net.http;
	requires spring.web;
	requires com.fasterxml.jackson.databind;
	requires org.apache.commons.lang3;
	exports no.nav.dok.jiraapi;
}