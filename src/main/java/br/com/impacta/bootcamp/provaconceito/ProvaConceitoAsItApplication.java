package br.com.impacta.bootcamp.provaconceito;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProvaConceitoAsItApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProvaConceitoAsItApplication.class, args);

		System.out.println("Páginas do teste:");
		System.out.println("As it: http://localhost:8081/asit");
		System.out.println("To be: http://localhost:8081/tobe");
	}

}
