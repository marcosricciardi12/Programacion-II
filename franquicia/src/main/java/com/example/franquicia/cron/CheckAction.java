package com.example.franquicia.cron;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.franquicia.model.Loggin;

import lombok.Data;
import reactor.core.publisher.Mono;

@Data
@Component
public class CheckAction {
	
	@Value("${environments.loggin.user}")
	private String user;
	@Value("${environments.loggin.pass}")
	private String pass;
	private String tokenid;
	
	@Scheduled(cron = "${environments.cron.expression}")
	public void checkactiontask() {
		
		System.out.println("Chequeando accion");
		if(this.tokenid == null) {
			
			WebClient webClient = WebClient.builder()
			        .baseUrl("http://10.101.102.1:8080/api/authenticate")
			        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			        .build();
			
			String cuerpo = "{\n"
					+ "\"username\": " + "\"" + this.user + "\"" + ",\n"
					+ "\"password\": " + "\"" + this.pass + "\"" + "\n"
					+ "}";
			
			Mono<Loggin> responseJson = webClient.post()
			                               .uri("")
			                               .contentType(MediaType.APPLICATION_JSON)
			                               .accept(MediaType.APPLICATION_JSON)
			                               .body(BodyInserters.fromValue(cuerpo))
			                               .retrieve()
			                               .bodyToMono(Loggin.class);
			this.tokenid = responseJson.block().getId_token();
		}
		
		String cuerpo = "{\n"
				+ "\"accion\": \"consulta\",\n"
				+ "\"franquiciaID\": \"5d73ab28-db0d-4813-8b8c-03643be194af\"\n"
				+ "}";
		
		WebClient webClient = WebClient.builder()
		        .baseUrl("http://10.101.102.1:8080/api/accion")
		        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
		        .build();
		
		String responseJson = webClient.post()
		                               .uri("")
		                               .contentType(MediaType.APPLICATION_JSON)
		                               .accept(MediaType.APPLICATION_JSON)
		                               .header(HttpHeaders.AUTHORIZATION, "Bearer " + this.tokenid + "\"")
		                               .body(BodyInserters.fromValue(cuerpo))
		                               .retrieve()
		                               .bodyToMono(String.class)
		                               .block();
		System.out.println(responseJson);
	}
}
