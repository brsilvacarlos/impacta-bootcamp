package br.com.impacta.bootcamp.provaconceito.controller;

import br.com.impacta.bootcamp.provaconceito.ResponseBFFVO;
import br.com.impacta.bootcamp.provaconceito.model.Produto;
import br.com.impacta.bootcamp.provaconceito.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

@Controller
public class HomeController {

    private final String wiremockUrl;
    private final ProdutoRepository produtoRepository;

    public HomeController(@Value("${WIREMOCK_URL}") String wiremockUrl, ProdutoRepository produtoRepository) {
        // Configura o WebClient com a URL do WireMock
        this.wiremockUrl = wiremockUrl;
        this.produtoRepository = produtoRepository;
    }

    @GetMapping({"as-it", "/asit"})
    public String asIt(Model model) {
        List<ResponseBFFVO.ServiceResponse> requestsBff = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();

        StopWatch swTotal = new StopWatch("total");
        swTotal.start();

        // Realiza as requisições de maneira assíncrona para cada serviço mockado
        IntStream.rangeClosed(1, 4).boxed().forEach(i -> {
            StopWatch swBFF = getStopWatch(i);

            // Realiza as requisições para os serviços mockados
            String url = getUrl(i);
            String resposta = restTemplate.getForObject(url, String.class);
            swBFF.stop();

            requestsBff.add(new ResponseBFFVO.ServiceResponse(
                    "Servico " + i,
                    resposta,
                    swBFF.getTotalTimeMillis()));
        });

        swTotal.stop();
        model.addAttribute("responseBFFVO", new ResponseBFFVO(requestsBff));  // Passa o VO para o template Thymeleaf
        model.addAttribute("total", swTotal.getTotalTimeMillis());

        return "home";
    }

    @GetMapping({"to-be", "/tobe"})
    public String toBe(Model model) {

        StopWatch swTotal = new StopWatch("total");
        swTotal.start();

        List<Long> idsProdutos = LongStream.rangeClosed(1L, 4L).boxed().collect(Collectors.toList());

        StopWatch swDb = new StopWatch("db");
        swDb.start();
        List<Produto> produtos = produtoRepository.findAllById(idsProdutos);
        swDb.stop();

        List<ResponseBFFVO.ServiceResponse> requestsBff = produtos.stream().map(p -> new ResponseBFFVO.ServiceResponse(
                "Servico " + p.getId(),
                "Resposta produto "+ p.getNome(),
                swDb.getTotalTimeMillis())).toList();

        swTotal.stop();
        model.addAttribute("responseBFFVO", new ResponseBFFVO(requestsBff));
        model.addAttribute("total", swTotal.getTotalTimeMillis());

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
        return this.wiremockUrl + "/servico-" + i + "/resposta";
    }
}