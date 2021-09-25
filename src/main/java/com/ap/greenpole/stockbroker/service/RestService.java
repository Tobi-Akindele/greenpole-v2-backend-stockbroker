package com.ap.greenpole.stockbroker.service;

import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public interface RestService {

	ResponseEntity<?> request(String url, Map<String, String> headers, HttpMethod requestMethod, Object requestBody, Class<?> returnType);
}
