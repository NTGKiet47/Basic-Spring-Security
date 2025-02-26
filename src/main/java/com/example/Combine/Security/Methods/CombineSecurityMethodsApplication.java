package com.example.Combine.Security.Methods;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CombineSecurityMethodsApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();
		for(String name : dotenv.entries().stream().map(e -> e.getKey()).toList()){
			System.setProperty(name, dotenv.get(name));
		}
		SpringApplication.run(CombineSecurityMethodsApplication.class, args);
	}

}
