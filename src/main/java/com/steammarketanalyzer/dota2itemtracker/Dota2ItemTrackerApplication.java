package com.steammarketanalyzer.dota2itemtracker;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class Dota2ItemTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(Dota2ItemTrackerApplication.class, args);
	}

	@Bean
	public WebClient webClient(WebClient.Builder builder) {
		return builder.baseUrl("https://steamcommunity.com/market")
				.defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0")
				.build();
	}
}
