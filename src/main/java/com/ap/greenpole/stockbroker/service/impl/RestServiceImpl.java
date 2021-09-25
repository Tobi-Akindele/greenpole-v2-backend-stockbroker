package com.ap.greenpole.stockbroker.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ap.greenpole.stockbroker.service.RestService;

@Service
public class RestServiceImpl implements RestService {
	
	private static Logger logger = LoggerFactory.getLogger(RestServiceImpl.class);
	
	@Autowired
	private RestTemplate restTemplate;

	@Override
	public ResponseEntity<?> request(String requestUrl, Map<String, String> headers, HttpMethod requestMethod,
			Object requestBody, Class<?> returnType) {
		
		ResponseEntity<?> responseEntity = null;
		try {
			URI uri = getUri(requestUrl);
			HttpHeaders httpHeaders = getHeaders(headers);
			HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
			
			responseEntity = restTemplate.exchange(uri, requestMethod, requestEntity, returnType);
		} catch(Exception ex) {
			logger.info("[-] Exception happened while calling Notification service {}", ex);
		}
		
		return responseEntity;
	}

	private HttpHeaders getHeaders(Map<String, String> headers) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		if(headers != null && !headers.isEmpty()) {
			for(Map.Entry<String, String> entrySet : headers.entrySet()) {
				httpHeaders.add(entrySet.getKey(), entrySet.getValue());
			}
		}
		return httpHeaders;
	}

	private URI getUri(String requestUrl) throws URISyntaxException {
		return new URI(requestUrl);
	}

}
