package com.camunda.report.camundabackuputility.services;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

@Component
public class RestCallService {

	@Value("${camundaHosturl}")
	private String camundaHostName;

	@Value("${camundaUsername}")
	private String username;

	@Value("${camundaPassword}")
	private String password;

	public String makePOSTCallToFetchCookie(WebClient.Builder builder) {

		String url = camundaHostName + "/api/login?username=" + username + "&password=" + password;

		ResponseEntity<Map> response1 = builder.build().post().uri(url).retrieve().toEntity(Map.class).block();

		String cookie = response1.getHeaders().get("Set-Cookie").get(0).split(";")[0];

		return cookie;

	}

	public String makePOSTCall(WebClient.Builder builder, String payload, String url) {

		String response = builder.build().post().uri(url).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
				.header(HttpHeaders.COOKIE, makePOSTCallToFetchCookie(builder)).body(BodyInserters.fromValue(payload))
				.retrieve().bodyToMono(String.class).block();

		return response;

	}

}
