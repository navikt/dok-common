module jiraapi {
	requires jakarta.validation;
	requires lombok;
	requires java.net.http;
	requires com.fasterxml.jackson.databind;
	requires org.apache.commons.lang3;
	requires org.slf4j;
	exports no.nav.dok.jiraapi;
}