package com.ap.greenpole;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import co.elastic.apm.attach.ElasticApmAttacher;

@SpringBootApplication
public class GreenpoleStockbrokerApplication {

	public static void main(String[] args) {
		ElasticApmAttacher.attach();
		SpringApplication.run(GreenpoleStockbrokerApplication.class, args);
	}

}
