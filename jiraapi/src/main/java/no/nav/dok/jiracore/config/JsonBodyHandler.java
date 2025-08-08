package no.nav.dok.jiracore.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dok.jiracore.exception.JiraClientException;

import java.net.http.HttpResponse;

import static com.fasterxml.jackson.core.JsonGenerator.Feature.IGNORE_UNKNOWN;
import static java.nio.charset.StandardCharsets.UTF_8;

public class JsonBodyHandler<T> implements HttpResponse.BodyHandler<T> {
	private Class<T> tClass;

	public JsonBodyHandler(Class<T> tClass) {
		this.tClass = tClass;
	}

	@Override
	public HttpResponse.BodySubscriber<T> apply(HttpResponse.ResponseInfo responseInfo) {
		HttpResponse.BodySubscriber<String> upstream = HttpResponse.BodySubscribers.ofString(UTF_8);
		return HttpResponse.BodySubscribers.mapping(upstream,
				(String body) -> {
					try {
						ObjectMapper mapper = new ObjectMapper().configure(IGNORE_UNKNOWN, true);
						return mapper.readValue(body, tClass);
					} catch (JsonProcessingException e) {
						throw new JiraClientException("Unable to map response! Response statuscode: " + responseInfo.statusCode() + " Exception: " + e.getMessage(), e);
					}
				});
	}

}
