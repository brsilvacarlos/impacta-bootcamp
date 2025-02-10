package br.com.impacta.bootcamp.prova_conceito_as_it;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

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

		private final String wiremockUrl;

		public ServicoController(@Value("${WIREMOCK_URL}") String wiremockUrl) {
			// Configura o WebClient com a URL do WireMock
			this.wiremockUrl = wiremockUrl;
		}

		@GetMapping({"/", "/home"})
		public String home(Model model) {
			List<ResponseBFFVO.ServiceResponse> requestsBff = new ArrayList<>();

			StopWatch swTotal = new StopWatch("total");
			swTotal.start();

			// Realiza as requisições de maneira assíncrona para cada serviço mockado
			IntStream.rangeClosed(1, 4).boxed().forEach(i -> {
				StopWatch swBFF = getStopWatch(i);

				// Realiza as requisições para os serviços mockados
				String url = getUrl(i);
				String resposta = new RestTemplate().getForObject(url, String.class);
				swBFF.stop();

				requestsBff.add(new ResponseBFFVO.ServiceResponse(
											"Servico " + i,
											resposta,
											swBFF.getTotalTimeMillis()));
			});

			swTotal.stop();
			model.addAttribute("responseBFFVO", new ResponseBFFVO(requestsBff));  // Passa o VO para o template Thymeleaf
			model.addAttribute("total",  swTotal.getTotalTimeMillis());

			return "home";
		}

		private StopWatch getStopWatch(Integer i) {
			StopWatch swBFF = new StopWatch(getStopWatchID(i));
			swBFF.start();
			return swBFF;
		}

		private static String getStopWatchID(int i) {
			return "bff" + i;
		}

		private String getUrl(Integer i) {
			String url = this.wiremockUrl + "/servico-" + i + "/resposta";
			return url;
		}
	}

}
