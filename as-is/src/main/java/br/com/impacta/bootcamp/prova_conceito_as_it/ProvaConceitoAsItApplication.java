package br.com.impacta.bootcamp.prova_conceito_as_it;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootApplication
public class ProvaConceitoAsItApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProvaConceitoAsItApplication.class, args);
	}

	// Criação do controlador com o endpoint que realiza as requisições
	@Controller
	public class ServicoController {

		private final WebClient webClient;

		public ServicoController(WebClient.Builder webClientBuilder
								 ,@Value("${WIREMOCK_URL}") String wiremockUrl) {
			// Configura o WebClient com a URL do WireMock
			this.webClient = webClientBuilder.baseUrl(wiremockUrl).build();
		}

		@GetMapping({"/", "/home"})
		public String home(Model model) {
			List<Mono<ResponseBFFVO.ServiceResponse>> requestsBff = new ArrayList<>();

			StopWatch swTotal = new StopWatch("total");
			swTotal.start();

			// Realiza as requisições de maneira assíncrona para cada serviço mockado
			IntStream.rangeClosed(1, 4).boxed().forEach(i -> {
				StopWatch swBFF = getStopWatch(i);

				requestsBff.add(
						webClient.get()
								.uri(getUrl(i))
								.retrieve()
								.bodyToMono(String.class)
								.map(resposta -> {
									swBFF.stop();

									return new ResponseBFFVO.ServiceResponse(
											"Servico " + i,
											resposta,
											swBFF.getTotalTimeMillis()
									);
								})
				);
			});

			// Retorna a tabela formatada assim que todas as requisições forem completadas
			Mono<List<ResponseBFFVO.ServiceResponse>> serviceResponses = Mono.zip(requestsBff, respostas -> {
				List<ResponseBFFVO.ServiceResponse> responseList = new ArrayList<>();
				for (Object resposta : respostas) {
					responseList.add((ResponseBFFVO.ServiceResponse) resposta);
				}
				return responseList;
			});

			// Monta o VO ResponseBFFVO com os dados das respostas
			return serviceResponses.map(responseList -> {
				ResponseBFFVO responseBFFVO = new ResponseBFFVO(responseList);
				model.addAttribute("responseBFFVO", responseBFFVO);  // Passa o VO para o template Thymeleaf
				swTotal.stop();
				model.addAttribute("total",  swTotal.getTotalTimeMillis());
				return "home";  // Nome do template Thymeleaf
			}).block();  // Aguarda a resposta para continuar com o fluxo
		}

		private StopWatch getStopWatch(Integer i) {
			StopWatch swBFF = new StopWatch(getStopWatchID(i));
			swBFF.start();
			return swBFF;
		}

		private static String getStopWatchID(int i) {
			return "bff" + i;
		}

		private static String getUrl(Integer i) {
			String url = "/servico-" + i + "/resposta";
			return url;
		}
	}

}
