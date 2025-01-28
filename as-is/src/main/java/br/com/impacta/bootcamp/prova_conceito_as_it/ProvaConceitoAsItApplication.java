package br.com.impacta.bootcamp.prova_conceito_as_it;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class ProvaConceitoAsItApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProvaConceitoAsItApplication.class, args);
	}

	// Criação do controlador com o endpoint que realiza as requisições
	@RestController
	public class ServicoController {

		@GetMapping({"/", "/home"})
		public List<String> consultarServicos() {
			// Pega o URL do WireMock a partir da variável de ambiente
			String wiremockUrl = System.getenv("WIREMOCK_URL");

			StopWatch stopWatch = new StopWatch();
			stopWatch.start();

			// Lista para armazenar as respostas
			List<String> respostas = new ArrayList<>();

			// Realiza as requisições para os serviços mockados
			for (int i = 1; i <= 5; i++) {
				String url = wiremockUrl + "/servico-" + i + "/resposta";
				String resposta = new RestTemplate().getForObject(url, String.class);
				respostas.add("Resposta " + i + ": " + resposta);
			}

			stopWatch.stop();
			System.out.println(stopWatch.shortSummary());
			System.out.println(stopWatch.prettyPrint(TimeUnit.SECONDS));
			System.out.println(stopWatch.getTotalTime(TimeUnit.SECONDS));
			respostas.add(String.format("Tempo total: %s", stopWatch.getTotalTime(TimeUnit.SECONDS)));

			return respostas; // Retorna as respostas como lista
		}
	}

}
